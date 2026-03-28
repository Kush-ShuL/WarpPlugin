package top.mc_plfd_host.warpPlugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

public class PublicWarp implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Bukkit.getLogger().warning(WarpPlugin.getMessages("player_only"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(WarpPlugin.getMessages("no_name"));
            return true;
        }

        if (args.length == 1) {
            sender.sendMessage(WarpPlugin.getMessages("no_get_perm"));
            return true;
        }

        if (!args[1].contains("true") && !args[1].contains("false")) {
            sender.sendMessage(WarpPlugin.getMessages("perm_not_boolean"));
            return true;
        }

        if (!player.hasPermission("warpplugin.warpall")) {
            sender.sendMessage(WarpPlugin.getMessages("no_perm"));
            return true;
        }

        String warpName = args[0];
        boolean isPublic = Boolean.parseBoolean(args[1]);

        if (!WarpPlugin.checkData("warps." + warpName)) {
            sender.sendMessage(WarpPlugin.getMessages("not_find"));
            return true;
        }

        if (!isPublic && !WarpPlugin.getData("warps." + warpName + ".creator").equals(player.getName())) {
            sender.sendMessage(WarpPlugin.getMessages("no_perm"));
            return true;
        }

        WarpPlugin.saveData(isPublic, "warps." + warpName + ".public");
        sender.sendMessage(WarpPlugin.getMessages("success"));
        return true;
    }
}