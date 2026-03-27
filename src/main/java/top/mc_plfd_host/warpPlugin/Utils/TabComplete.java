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

    private CommandSender sender;

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender,
                                      @NotNull Command command,
                                      @NotNull String alias,
                                      String[] args) {
        this.sender = sender;

        String commandName = command.getName().toLowerCase();

        return switch (commandName) {
            case "warpadmin" -> completeAdminSubcommand(args);
            case "warp", "delwarp" -> completeWarpName(args);
            case "publicwarp" -> completePublicWarp(args);
            case "setwarp" -> completeSetWarp(args);
            default -> new ArrayList<>();
        };

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
                boolean isPublic = WarpPlugin.checkData("warps." + warp + ".public") &&
                                   Boolean.parseBoolean(WarpPlugin.getData("warps." + warp + ".public"));
                boolean isCreator = sender.getName().equals(WarpPlugin.getData("warps." + warp + ".creator"));
                
                if (isPublic) {
                    suggestions.add(warp);
                } else if (isCreator) {
                    suggestions.add(warp);
                }
            }
        }
        return suggestions;
    }

    private @NotNull List<String> completePublicWarp(String[] args) {
        if (args.length == 1) {
            List<String> warps = WarpPlugin.lookupData("warps");
            List<String> suggestions = new ArrayList<>();
            for (String warp : warps) {
                if (warp.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                    boolean isPublic = WarpPlugin.checkData("warps." + warp + ".public") &&
                            Boolean.parseBoolean(WarpPlugin.getData("warps." + warp + ".public"));
                    boolean isCreator = sender.getName().equals(WarpPlugin.getData("warps." + warp + ".creator"));

                    if (isPublic) {
                        suggestions.add(warp);
                    } else if (isCreator) {
                        suggestions.add(warp);
                    }
                }
            }
            return suggestions;
        }

        if (args.length == 2) {
            return List.of("true", "false");
        }

        return new ArrayList<>();
    }

    private @NotNull List<String> completeSetWarp(String[] args) {
        if (args.length == 1) {
            return List.of("WarpName");
        }

        if (args.length == 2) {
            return List.of("true", "false");
        }

        return new ArrayList<>();
    }

    private @NotNull @Unmodifiable List<String> completePlayerName(String[] args) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> name.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}