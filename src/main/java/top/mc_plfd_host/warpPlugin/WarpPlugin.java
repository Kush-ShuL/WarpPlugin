package top.mc_plfd_host.warpPlugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import top.mc_plfd_host.warpPlugin.Commands.*;
import top.mc_plfd_host.warpPlugin.Utils.TabComplete;
import top.mc_plfd_host.warpPlugin.Utils.VersionChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.io.File;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Properties;

public final class WarpPlugin extends JavaPlugin {
    private static WarpPlugin instance;
    private static YamlConfiguration message;
    private static YamlConfiguration data;
    private static File dataFile;
    private static File messageFile;
    private static Properties settings;

    public static String version;

    @Override
    public void onEnable() {
        instance = this;
        
        try {
            version = instance.getDescription().getVersion();
        } catch (NullPointerException e) {
            getLogger().warning("[WarpPlugin] Failed to get version!");
            getLogger().warning(e.getMessage());
        }
        
        File themessageFile = new File(getDataFolder(), "message.yml");
        File thedataFile = new File(getDataFolder(), "data.yml");

        if (!(themessageFile.exists())) {
            saveResource("message.yml", false);
        }
        if (!(thedataFile.exists())) {
            saveResource("data.yml", false);
        }

        getLogger().info("[WarpPlugin] Loading configuration...");
        messageFile = new File((getDataFolder()), "message.yml");
        message = YamlConfiguration.loadConfiguration(messageFile);
        dataFile = new File(getDataFolder(), "data.yml");
        data = YamlConfiguration.loadConfiguration(dataFile);
        
        // Load settings.properties
        settings = new Properties();
        try (InputStream input = WarpPlugin.class.getClassLoader().getResourceAsStream("settings.properties")) {
            if (input != null) {
                settings.load(new InputStreamReader(input, java.nio.charset.StandardCharsets.UTF_8));
            } else {
                getLogger().warning("[WarpPlugin] settings.properties not found!");
            }
        } catch (IOException e) {
            getLogger().severe("[WarpPlugin] Failed to load settings.properties!");
            getLogger().warning(e.getMessage());
        }

        // CommandExecutor
        Objects.requireNonNull(getCommand("setwarp")).setExecutor(new SetWarp());
        Objects.requireNonNull(getCommand("warp")).setExecutor(new Warp());
        Objects.requireNonNull(getCommand("delwarp")).setExecutor(new DelWarp());
        Objects.requireNonNull(getCommand("warpadmin")).setExecutor(new AdminCommands());
        Objects.requireNonNull(getCommand("publicwarp")).setExecutor(new PublicWarp());
        // TabComplete
        Objects.requireNonNull(getCommand("setwarp")).setTabCompleter(new TabComplete());
        Objects.requireNonNull(getCommand("warp")).setTabCompleter(new TabComplete());
        Objects.requireNonNull(getCommand("delwarp")).setTabCompleter(new TabComplete());
        Objects.requireNonNull(getCommand("warpadmin")).setTabCompleter(new TabComplete());
        Objects.requireNonNull(getCommand("publicwarp")).setTabCompleter(new TabComplete());
        getLogger().info("[WarpPlugin] Enabled!");
        VersionChecker.checkUpdate();
    }

    @Override
    public void onDisable() {
        getLogger().info("[WarpPlugin] Disabled!");
        try {
            data.save(dataFile);
        } catch (IOException e) {
            getLogger().warning("[WarpPlugin] Failed to save data!");
        }
    }

    public static @NotNull String getMessages(String path) {
        String raw = message.getString(path);
        if (raw != null) {
            return raw.replace('&','§');
        }
        return "error!";
    }

    public static String getData(String path) {
        String raw = data.getString(path);
        return Objects.requireNonNullElse(raw, "error!");
    }

    public static double getDataDouble(String path) {
        return data.getDouble(path);
    }

    public static void saveData(Object object, String path) {
        try {
            data.set(path, object);
            data.save(dataFile);
        } catch (IOException ignored) {
            Bukkit.getLogger().warning("[WarpPlugin] Failed to save! There is a problem with the configuration file.");
        }
    }

    public static boolean checkData(String path) {
        return data.contains(path);
    }

    public static @NotNull ArrayList<String> lookupData(String path) {
        ArrayList<String> list = new ArrayList<>();
        ConfigurationSection section = data.getConfigurationSection(path);
        if (section != null) {
            list.addAll(section.getKeys(false));
        }
        return list;
    }

    public static @NotNull String getProperty(String path) {
        String value = settings.getProperty(path);
        return value != null ? value : "error!";
    }

    public static WarpPlugin getInstance() {
        return instance;
    }

    public static boolean reload() {
        try {
            data.load(dataFile);
            message.load(messageFile);
            return true;
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().warning("[WarpPlugin] Failed to reload! There is a problem with the configuration file.");
            Bukkit.getLogger().warning(e.getMessage());
            return false;
        }
    }
}
