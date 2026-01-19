package top.mc_plfd_host.warpPlugin.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

import java.util.List;

import static top.mc_plfd_host.warpPlugin.WarpPlugin.getData;

public class AdminCommands implements CommandExecutor {
    private static String getPos(String warpName) {
        double x = WarpPlugin.getDataDouble("warps." + warpName + ".x");
        double y = WarpPlugin.getDataDouble("warps." + warpName + ".y");
        double z = WarpPlugin.getDataDouble("warps." + warpName + ".z");
        return x + " " + y + " " + z;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (!sender.hasPermission("warpplugin.warpadmin")) {
            sender.sendMessage(WarpPlugin.getMessages("no_perm"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(WarpPlugin.getMessages("admin_help"));
            return true;
        }

        switch (args[0]) {
            case "list":
                List<String> warps = WarpPlugin.lookupData("warps");
                if (warps.isEmpty()) {
                    sender.sendMessage(WarpPlugin.getMessages("no_data"));
                    return true;
                }
                for (String warpName : warps) {
                    String creator = getData("warps." + warpName + ".creator");
                    if ("error!".equals(creator)) creator = "null";
                    String message = WarpPlugin.getMessages("list_warps")
                            .replace("%warp%", warpName)
                            .replace("%name%", warpName)
                            .replace("%owner%", creator)
                            .replace("%world%", getData("warps." + warpName + ".world"))
                            .replace("%pos%", getPos(warpName));
                    sender.sendMessage(message);
                }
                return true;

            case "del":
                if (args.length < 2) {
                    sender.sendMessage(WarpPlugin.getMessages("no_name"));
                    return true;
                }
                if (!WarpPlugin.checkData("warps." + args[1])) {
                    sender.sendMessage(WarpPlugin.getMessages("not_find"));
                    return true;
                }
                WarpPlugin.saveData(null, "warps."+args[1]);
                sender.sendMessage(WarpPlugin.getMessages("success"));
                return true;

            case "setowner":
                if (args.length < 2) {
                    sender.sendMessage(WarpPlugin.getMessages("no_name"));
                    return true;
                }
                if (!WarpPlugin.checkData("warps." + args[1])) {
                    sender.sendMessage(WarpPlugin.getMessages("not_find"));
                    return true;
                }
                String new_creator = args[2];
                WarpPlugin.saveData(new_creator, "warps." + args[1] + ".creator");
                if (getData("warps." + args[1] + ".creator").equals(new_creator)) {
                    sender.sendMessage(WarpPlugin.getMessages("success"));
                } else {
                    sender.sendMessage(WarpPlugin.getMessages("error"));
                }
                return true;

            case "reload":
                if (WarpPlugin.reload()){
                    sender.sendMessage(WarpPlugin.getMessages("success"));
                }else{
                    sender.sendMessage(WarpPlugin.getMessages("error"));
                }
                return true;

            default:
                sender.sendMessage(WarpPlugin.getMessages("admin_help"));
                return true;
        }
    }
}