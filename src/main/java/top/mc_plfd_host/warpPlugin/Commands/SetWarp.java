package top.mc_plfd_host.warpPlugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetWarp implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().warning(WarpPlugin.getMessages("player_only"));
            return true;
        }

        if (!sender.hasPermission("warpplugin.set")) {
            sender.sendMessage(WarpPlugin.getMessages("no_perm"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(WarpPlugin.getMessages("no_name"));
            return true;
        }

        if (WarpPlugin.checkData("warps." + args[0])) {
            sender.sendMessage(WarpPlugin.getMessages("already_exists"));
            return true;
        }

        Location loc = player.getLocation();
        String warpName = args[0];
        String path = "warps." + warpName;

        WarpPlugin.saveData(sender.getName(), path + ".creator");
        WarpPlugin.saveData(player.getWorld().getName(), path + ".world");
        WarpPlugin.saveData(loc.getX(), path + ".x");
        WarpPlugin.saveData(loc.getY(), path + ".y");
        WarpPlugin.saveData(loc.getZ(), path + ".z");
        WarpPlugin.saveData(loc.getYaw(), path + ".yaw");
        WarpPlugin.saveData(loc.getPitch(), path + ".pitch");

        sender.sendMessage(WarpPlugin.getMessages("success"));
        return true;
    }
}