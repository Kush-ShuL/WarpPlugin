package top.mc_plfd_host.warpPlugin.Utils;

import org.bukkit.Bukkit;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class VersionChecker {
    private static final URL VERSION_URL;
    private static final URL FALLBACK_VERSION_URL;
    private static final URL UPDATE_URL;
    private static final String CURRENT_VERSION;

    static {
        URL tempVersionUrl = null;
        URL tempFallbackVersionUrl = null;
        URL tempUpdateUrl = null;
        
        try {
            String versionApiUrl = WarpPlugin.getProperty("GITHUB-VERSION-API");
            if (!versionApiUrl.equals("error!")) {
                tempVersionUrl = new URL(versionApiUrl);
            }
        } catch (MalformedURLException e) {
            Bukkit.getLogger().warning("[WP-VersionChecker] Invalid version API URL!");
        }

        try {
            String fallbackVersionApiUrl = WarpPlugin.getProperty("FALLBACK-VERSION-API");
            if (!fallbackVersionApiUrl.equals("error!") && !fallbackVersionApiUrl.isEmpty()) {
                tempFallbackVersionUrl = new URL(fallbackVersionApiUrl);
            }
        } catch (MalformedURLException e) {
            Bukkit.getLogger().warning("[WP-VersionChecker] Invalid fallback version API URL!");
        }

        try {
            String updateUrlStr = WarpPlugin.getProperty("update-url");
            if (!updateUrlStr.equals("error!")) {
                tempUpdateUrl = new URL(updateUrlStr);
            }
        } catch (MalformedURLException e) {
            Bukkit.getLogger().warning("[WP-VersionChecker] Invalid update URL!");
        }
        
        VERSION_URL = tempVersionUrl;
        FALLBACK_VERSION_URL = tempFallbackVersionUrl;
        UPDATE_URL = tempUpdateUrl;
        CURRENT_VERSION = WarpPlugin.version;
    }

    public static void checkUpdate() {
        if (VERSION_URL == null && FALLBACK_VERSION_URL == null) {
            Bukkit.getLogger().warning("[WP-VersionChecker] Version API URL is not configured, skipping update check.");
            return;
        }
        
        Bukkit.getScheduler().runTaskAsynchronously(WarpPlugin.getInstance(), () -> {
            // 首先尝试主 API
            String latestVersion = fetchVersionFromAPI(VERSION_URL, "GitHub");
            
            // 如果主 API 失败，尝试后备 API
            if (latestVersion == null && FALLBACK_VERSION_URL != null) {
                Bukkit.getLogger().info("[WP-VersionChecker] Primary API failed, trying fallback API...");
                latestVersion = fetchVersionFromAPI(FALLBACK_VERSION_URL, "Fallback");
            }
            
            // 比较版本并通知
            if (latestVersion != null && !latestVersion.isEmpty()) {
                compareVersionsAndNotify(latestVersion);
            } else {
                Bukkit.getLogger().warning("[WP-VersionChecker] Could not fetch version from any API.");
            }
        });
    }

    private static String fetchVersionFromAPI(URL apiUrl, String apiName) {
        if (apiUrl == null) {
            return null;
        }
        
        try {
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("User-Agent", "WarpPlugin-VersionChecker");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                Bukkit.getLogger().warning("[WP-VersionChecker] Failed to fetch version info from " + apiName + ". Response code: " + responseCode);
                return null;
            }
            
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                
                // 根据 API 类型解析版本号
                if ("GitHub".equals(apiName)) {
                    return extractTagName(response.toString());
                } else if ("Fallback".equals(apiName)) {
                    return extractModrinthVersion(response.toString());
                }
            }
        } catch (javax.net.ssl.SSLException e) {
            Bukkit.getLogger().warning("[WP-VersionChecker] SSL certificate verification failed for " + apiName + ". Error: " + e.getMessage());
        } catch (IOException e) {
            Bukkit.getLogger().warning("[WP-VersionChecker] Error fetching version from " + apiName + ": " + e.getMessage());
        }
        
        return null;
    }

    private static String extractTagName(String jsonResponse) {
        int tagIndex = jsonResponse.indexOf("\"tag_name\"");
        if (tagIndex == -1) {
            return null;
        }
        
        int colonIndex = jsonResponse.indexOf(':', tagIndex);
        if (colonIndex == -1) {
            return null;
        }
        
        int quoteStart = jsonResponse.indexOf('"', colonIndex + 1);
        if (quoteStart == -1) {
            return null;
        }
        
        int quoteEnd = jsonResponse.indexOf('"', quoteStart + 1);
        if (quoteEnd == -1) {
            return null;
        }
        
        return jsonResponse.substring(quoteStart + 1, quoteEnd);
    }

    private static String extractModrinthVersion(String jsonResponse) {
        // Modrinth API 返回的字段是 version_number
        int versionIndex = jsonResponse.indexOf("\"version_number\"");
        if (versionIndex == -1) {
            return null;
        }
        
        int colonIndex = jsonResponse.indexOf(':', versionIndex);
        if (colonIndex == -1) {
            return null;
        }
        
        int quoteStart = jsonResponse.indexOf('"', colonIndex + 1);
        if (quoteStart == -1) {
            return null;
        }
        
        int quoteEnd = jsonResponse.indexOf('"', quoteStart + 1);
        if (quoteEnd == -1) {
            return null;
        }
        
        return jsonResponse.substring(quoteStart + 1, quoteEnd);
    }

    private static void compareVersionsAndNotify(String latestVersion) {
        try {
            int[] latest = parseVersion(latestVersion);
            int[] current = parseVersion(CURRENT_VERSION);
            
            if (latest == null || current == null) {
                Bukkit.getLogger().warning("[WP-VersionChecker] Could not parse version strings.");
                return;
            }
            
            int comparison = compareVersions(latest, current);
            
            if (comparison > 0) {
                logUpdateAvailable(latestVersion);
            } else if (comparison < 0) {
                logUsingNewerVersion();
            } else {
                Bukkit.getLogger().info("[VersionChecker] You are using the latest version.");
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("[WP-VersionChecker] Error comparing versions: " + e.getMessage());
        }
    }

    private static int[] parseVersion(String version) {
        if (version == null || version.isEmpty()) {
            return null;
        }
        
        String cleanVersion = version.replaceFirst("^[vV]", "");
        
        try {
            String[] parts = cleanVersion.split("\\.");
            if (parts.length < 3) {
                return null;
            }
            
            int major = Integer.parseInt(parts[0].replaceAll("[^0-9]", ""));
            int minor = Integer.parseInt(parts[1].replaceAll("[^0-9]", ""));
            int patch = Integer.parseInt(parts[2].replaceAll("[^0-9]", ""));
            
            return new int[]{major, minor, patch};
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private static int compareVersions(int[] latest, int[] current) {
        if (latest[0] > current[0]) return 1;
        if (latest[0] < current[0]) return -1;
        
        if (latest[1] > current[1]) return 1;
        if (latest[1] < current[1]) return -1;
        
        return Integer.compare(latest[2], current[2]);
    }

    private static void logUpdateAvailable(String version) {
        Bukkit.getLogger().info("[WP-VersionChecker] A new version is available: " + version);
        Bukkit.getLogger().info("[WP-VersionChecker] Please update your plugin to the latest version.");
        if (UPDATE_URL != null) {
            Bukkit.getLogger().info("[WP-VersionChecker] Download link: " + UPDATE_URL);
        }
    }

    private static void logUsingNewerVersion() {
        Bukkit.getLogger().info("[WP-VersionChecker] You are using a newer version than the latest.");
        if (UPDATE_URL != null) {
            Bukkit.getLogger().info("[WP-VersionChecker] You can get our official version from this: " + UPDATE_URL);
        }
    }
}
