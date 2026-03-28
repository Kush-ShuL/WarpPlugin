package top.mc_plfd_host.warpPlugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
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
    private static boolean isFolia = false;

    public static String version;

    @Override
    public void onEnable() {
        instance = this;
        
        try {
            Class.forName("io.papermc.paper.threadedregions.ThreadedRegionizer");
            isFolia = true;
            getLogger().info("[WarpPlugin] Folia detected! Running in threaded regions mode.");
        } catch (ClassNotFoundException e) {
            isFolia = false;
            getLogger().info("[WarpPlugin] Running in standard Bukkit/Paper mode.");
        }
        
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

        if (WarpPlugin.reload()) {
            getLogger().info("[WarpPlugin] Configuration loaded successfully!");
        }

        Bukkit.getPluginManager().registerEvents(new ReloadListener(), this);
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

    public static boolean isFolia() {
        return isFolia;
    }

    public static void runTask(Runnable task) {
        if (isFolia) {
            // Folia - 使用反射调用 getGlobalRegionScheduler()
            try {
                Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                java.lang.reflect.Method getSchedulerMethod = bukkitClass.getMethod("getGlobalRegionScheduler");
                Object scheduler = getSchedulerMethod.invoke(null);
                Class<?> schedulerClass = scheduler.getClass();
                java.lang.reflect.Method runMethod = schedulerClass.getMethod("run", JavaPlugin.class, java.util.function.Consumer.class);
                runMethod.invoke(scheduler, instance, (java.util.function.Consumer<Object>) plugin -> task.run());
            } catch (Exception e) {
                // 如果反射失败，回退到 Bukkit 调度器
                Bukkit.getScheduler().runTask(instance, task);
            }
        } else {
            // Bukkit/Paper - 使用 BukkitScheduler
            Bukkit.getScheduler().runTask(instance, task);
        }
    }

    /**
     * 在 Folia 或 Bukkit 上延迟运行任务
     * @param task 要执行的任务
     * @param delay 延迟（tick）
     */
    public static void runTaskLater(Runnable task, long delay) {
        if (isFolia) {
            try {
                Class<?> bukkitClass = Class.forName("org.bukkit.Bukkit");
                java.lang.reflect.Method getSchedulerMethod = bukkitClass.getMethod("getGlobalRegionScheduler");
                Object scheduler = getSchedulerMethod.invoke(null);
                Class<?> schedulerClass = scheduler.getClass();
                java.lang.reflect.Method runDelayedMethod = schedulerClass.getMethod("runDelayed", JavaPlugin.class, java.util.function.Consumer.class, long.class);
                runDelayedMethod.invoke(scheduler, instance, (java.util.function.Consumer<Object>) plugin -> task.run(), delay);
            } catch (Exception e) {
                Bukkit.getScheduler().runTaskLater(instance, task, delay);
            }
        } else {
            Bukkit.getScheduler().runTaskLater(instance, task, delay);
        }
    }
}

class ReloadListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerLoad(ServerLoadEvent event) {
        boolean success = WarpPlugin.reload();
        if (success) {
            WarpPlugin.getInstance().getLogger().info("[WarpPlugin] Reloaded successfully!");
        } else {
            WarpPlugin.getInstance().getLogger().warning("[WarpPlugin] Failed to reload! Please check configuration files.");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String command = event.getMessage().toLowerCase();
        if (command.equals("/reload") || command.startsWith("/reload ")) {
            WarpPlugin.runTaskLater(() -> WarpPlugin.getInstance().getLogger().info("Detected /reload command. Configuration will be reloaded on plugin reload."), 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onServerCommand(ServerCommandEvent event) {
        String command = event.getCommand().toLowerCase();
        if (command.equals("reload") || command.startsWith("reload ")) {
            WarpPlugin.runTaskLater(() -> WarpPlugin.getInstance().getLogger().info("Detected /reload command from console. Configuration will be reloaded on plugin reload."), 1L);
        }
    }
}
