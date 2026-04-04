package top.mc_plfd_host.warpPlugin.Api;


import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import top.mc_plfd_host.warpPlugin.Models.Warp;
import top.mc_plfd_host.warpPlugin.Utils.WarpUnits;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

import java.util.ArrayList;
import java.util.Set;

public class WarpApi {
    public static @NotNull ArrayList<Warp> getAllWarps() {
        Set<String> allKeys;
        ArrayList<String> allNames = new ArrayList<>();
        try {
            allKeys = WarpPlugin.getAllKeys();
            for (String key : allKeys) {
                int first = key.indexOf('.');
                int second = key.indexOf('.', first + 1);
                if (first == -1 || second == -1) throw new RuntimeException("[WarpApi] Who changed my data.yml!");
                allNames.add(key.substring(first+1,second));
            }
        } catch (Exception ex) {
            Bukkit.getLogger().warning("[WarpApi] Could not get all Warps from API: " + ex.getMessage());
        }
        ArrayList<Warp> warps = new ArrayList<>();
        allNames.forEach(warpName -> {
            Warp warp = WarpUnits.getWarp(warpName);
            warps.add(warp);
        });
        return warps;
    }
}
