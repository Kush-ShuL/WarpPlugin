package top.mc_plfd_host.warpPlugin.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

public class DelWarp implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args){
        if (!(sender instanceof Player player)){
            Bukkit.getLogger().warning(WarpPlugin.getMessages("player_only"));
            return true;
        }

        if (args.length==0){
            sender.sendMessage(WarpPlugin.getMessages("no_name"));
            return true;
        }

        if (!(WarpPlugin.checkData("warps."+args[0]))){
            sender.sendMessage(WarpPlugin.getMessages("not_find"));
            return true;
        }

        if (!player.hasPermission("warpplugin.delwarp")){
            sender.sendMessage(WarpPlugin.getMessages("no_perm"));
            return true;
        }

        if (!(WarpPlugin.getData("warps."+args[0]+".creator").equals(player.getName()))) {
            sender.sendMessage(WarpPlugin.getMessages("not_yours"));
            return true;
        }

        WarpPlugin.saveData(null, "warps."+args[0]);
        sender.sendMessage(WarpPlugin.getMessages("success"));

        return  true;
    }
}
