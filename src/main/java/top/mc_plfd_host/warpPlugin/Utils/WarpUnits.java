package top.mc_plfd_host.warpPlugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;
import top.mc_plfd_host.warpPlugin.Models.Warp;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

import static top.mc_plfd_host.warpPlugin.WarpPlugin.getData;

public class WarpUnits {
    public static @Nullable Location getWarpLocation(String warpName, String worldName) {
        try {
            World world = Bukkit.getWorld(worldName);
            if (world == null) return null;

            double x = Double.parseDouble(getData("warps." + warpName + ".x"));
            double y = Double.parseDouble(getData("warps." + warpName + ".y"));
            double z = Double.parseDouble(getData("warps." + warpName + ".z"));
            float yaw = Float.parseFloat(getData("warps." + warpName + ".yaw"));
            float pitch = Float.parseFloat(getData("warps." + warpName + ".pitch"));

            return new Location(world, x, y, z, yaw, pitch);
        } catch (Exception e) {
            Bukkit.getLogger().warning("[WarpPlugin] " + e.getMessage());
            return null;
        }
    }
    public static @Nullable Warp getWarp(String warpName) {
        String path = "warps." + warpName;

        try {
            String worldNameStr = getData(path + ".world");
            if (worldNameStr == null || worldNameStr.isEmpty()) {
                return null;
            }
            World world = Bukkit.getWorld(worldNameStr);
            if (world == null) {
                Bukkit.getLogger().warning("[WarpPlugin] World '" + worldNameStr + "' not found for warp: " + warpName);
                return null;
            }
            double x = Double.parseDouble(getData(path + ".x"));
            double y = Double.parseDouble(getData(path + ".y"));
            double z = Double.parseDouble(getData(path + ".z"));
            float yaw = Float.parseFloat(getData(path + ".yaw"));
            float pitch = Float.parseFloat(getData(path + ".pitch"));

            Location loc = new Location(world, x, y, z, yaw, pitch);
            boolean isPublic = Boolean.parseBoolean(getData(path + ".public"));
            String ownerName = getData(path + ".creator");
            return new Warp(loc, warpName, isPublic, ownerName, world);

        } catch (NumberFormatException e) {
            Bukkit.getLogger().severe("[WarpPlugin] Invalid number format for warp '" + warpName + "': " + e.getMessage());
            return null;
        } catch (Exception e) {
            Bukkit.getLogger().warning("[WarpPlugin] Failed to load warp '" + warpName + "': " + e.getMessage());
            return null;
        }
    }
}
