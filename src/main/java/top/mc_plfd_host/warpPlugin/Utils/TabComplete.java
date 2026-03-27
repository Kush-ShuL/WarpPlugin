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
            case "setwarp", "publicwarp" -> completeSetWarp(args);
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

    private @NotNull List<String> completeSetWarp(String[] args) {
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
            List<String> suggestions = new ArrayList<>();
            String input = args[1].toLowerCase();
            
            // 当输入完全匹配 "true" 时，只显示 true
            if (input.equals("true")) {
                suggestions.add("true");
                return suggestions;
            }

            // 当输入完全匹配 "false" 时，只显示 false
            if (input.equals("false")) {
                suggestions.add("false");
                return suggestions;
            }
            
            // 参数为空或输入其他字母时，显示 true 和 false
            suggestions.add("true");
            suggestions.add("false");
            return suggestions;
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