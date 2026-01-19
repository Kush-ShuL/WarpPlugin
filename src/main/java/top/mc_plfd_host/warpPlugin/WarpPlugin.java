package top.mc_plfd_host.warpPlugin;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import top.mc_plfd_host.warpPlugin.Commands.*;
import top.mc_plfd_host.warpPlugin.Utils.TabComplete;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.io.File;

public final class WarpPlugin extends JavaPlugin {
    private static YamlConfiguration message;
    private static YamlConfiguration data;
    private static File dataFile;
    private static File messageFile;

    @Override
    public void onEnable() {
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

        Objects.requireNonNull(getCommand("setwarp")).setExecutor(new SetWarp());
        Objects.requireNonNull(getCommand("warp")).setExecutor(new Warp());
        Objects.requireNonNull(getCommand("delwarp")).setExecutor(new DelWarp());
        Objects.requireNonNull(getCommand("warpadmin")).setExecutor(new AdminCommands());
        //TabComplete
        Objects.requireNonNull(getCommand("setwarp")).setTabCompleter(new TabComplete());
        Objects.requireNonNull(getCommand("warp")).setTabCompleter(new TabComplete());
        Objects.requireNonNull(getCommand("delwarp")).setTabCompleter(new TabComplete());
        Objects.requireNonNull(getCommand("warpadmin")).setTabCompleter(new TabComplete());
        getLogger().info("[WarpPlugin] Enabled!");
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
