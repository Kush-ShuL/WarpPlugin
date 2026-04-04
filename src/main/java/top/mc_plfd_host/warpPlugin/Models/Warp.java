package top.mc_plfd_host.warpPlugin.Models;

import org.bukkit.Location;
import org.bukkit.World;

public record Warp(
        Location loc,
        String name,
        boolean pub,
        String owner,
        World world
) {
}
