package top.mc_plfd_host.warpPlugin.Utils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import top.mc_plfd_host.warpPlugin.WarpPlugin;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter{

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      String[] args) {

        String commandName = command.getName().toLowerCase();

        if (commandName.equals("warpadmin")) {
            return completeAdminSubcommand(args);
        }

        if (commandName.equals("warp") ||
                commandName.equals("setwarp") ||
                commandName.equals("delwarp")) {
            return completeWarpName(args);
        }

        return new ArrayList<>();
    }

    private List<String> completeAdminSubcommand(String @NotNull [] args) {
        if (args.length == 1) {
            List<String> subcommands = List.of("list", "del", "setowner", "reload");
            List<String> suggestions = new ArrayList<>();

            for (String sub : subcommands) {
                if (sub.toLowerCase().startsWith(args[0].toLowerCase())) {
                    suggestions.add(sub);
                }
            }
            return suggestions;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("del")) {
            return completeWarpName(args);
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("setowner")) {
            return completePlayerName(args);
        }

        return new ArrayList<>();
    }

    private @NotNull List<String> completeWarpName(String[] args) {
        List<String> warps = WarpPlugin.lookupData("warps");
        List<String> suggestions = new ArrayList<>();

        for (String warp : warps) {
            if (warp.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                suggestions.add(warp);
            }
        }
        return suggestions;
    }

    private @NotNull @Unmodifiable List<String> completePlayerName(String[] args) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}