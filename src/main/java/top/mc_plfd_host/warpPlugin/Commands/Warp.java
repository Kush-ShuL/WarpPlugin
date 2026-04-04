package top.mc_plfd_host.warpPlugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.mc_plfd_host.warpPlugin.Utils.WarpUnits;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

public class Warp implements CommandExecutor {



    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)){
            Bukkit.getLogger().warning(WarpPlugin.getMessages("player_only"));
            return true;
        }

        if (args.length == 0){
            sender.sendMessage(WarpPlugin.getMessages("no_name"));
            return true;
        }

        if (!(WarpPlugin.checkData("warps."+ args[0]))){
            sender.sendMessage(WarpPlugin.getMessages("not_find"));
            return true;
        }

        if (!WarpPlugin.checkData("warps."+ args[0] + ".public") && !sender.getName().contains(WarpPlugin.getData("warps."+ args[0] + ".creator"))) {
            sender.sendMessage(WarpPlugin.getMessages("no_perm"));
            return true;
        }

        String worldName = WarpPlugin.getData("warps." + args[0] + ".world");

        if (sender.hasPermission("warpplugin.warpall")){
            try {
                Location loc = WarpUnits.getWarpLocation(args[0], worldName);
                if (loc == null) {
                    sender.sendMessage(WarpPlugin.getMessages("error"));
                    return true;
                }
                if (WarpPlugin.isFolia()) {
                    final Player finalPlayer = player;
                    WarpPlugin.runTask(() -> {
                        finalPlayer.teleport(loc);
                        sender.sendMessage(WarpPlugin.getMessages("success"));
                    });
                } else {
                    player.teleport(loc);
                    sender.sendMessage(WarpPlugin.getMessages("success"));
                }
                return true;
            } catch (Exception e) {
                sender.sendMessage(WarpPlugin.getMessages("error"));
                Bukkit.getLogger().warning("[WarpPlugin] " + e.getMessage());
                return true;
            }
        }

        if (sender.hasPermission("warpplugin.warp." + args[0])){
            try {
                Location loc = WarpUnits.getWarpLocation(args[0], worldName);
                if (loc == null) {
                    sender.sendMessage(WarpPlugin.getMessages("error"));
                    return true;
                }
                if (WarpPlugin.isFolia()) {
                    final Player finalPlayer = player;
                    WarpPlugin.runTask(() -> {
                        finalPlayer.teleport(loc);
                        sender.sendMessage(WarpPlugin.getMessages("success"));
                    });
                } else {
                    player.teleport(loc);
                    sender.sendMessage(WarpPlugin.getMessages("success"));
                }
            } catch (Exception e) {
                sender.sendMessage(WarpPlugin.getMessages("error"));
                Bukkit.getLogger().warning("[WarpPlugin] " + e.getMessage());
                return true;
            }
        } else {
            sender.sendMessage(WarpPlugin.getMessages("no_perm"));
        }
        return true;
    }
}