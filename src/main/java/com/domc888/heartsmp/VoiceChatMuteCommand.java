package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public final class VoiceChatMuteCommand
        implements CommandExecutor, TabCompleter {

    private final HollowAdminManager manager;

    public VoiceChatMuteCommand(
            HollowAdminManager manager
    ) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!sender.isOp()) {
            sender.sendMessage(
                    Component.text(
                            "Only server operators can use this command.",
                            NamedTextColor.RED
                    )
            );
            return true;
        }

        if (args.length != 1) {
            sender.sendMessage(
                    Component.text(
                            "Usage: /voicechatmute <player>",
                            NamedTextColor.RED
                    )
            );
            return true;
        }

        OfflinePlayer target =
                Bukkit.getOfflinePlayer(args[0]);

        if (target.getName() == null) {
            sender.sendMessage(
                    Component.text(
                            "Player not found.",
                            NamedTextColor.RED
                    )
            );
            return true;
        }

        boolean newState =
                !manager.isVoiceMuted(
                        target.getUniqueId()
                );

        manager.setVoiceMuted(
                target.getUniqueId(),
                newState
        );

        sender.sendMessage(
                Component.text(
                        "Voice chat for ",
                        NamedTextColor.GRAY
                ).append(
                        Component.text(
                                target.getName(),
                                NamedTextColor.GOLD
                        )
                ).append(
                        Component.text(
                                " is now ",
                                NamedTextColor.GRAY
                        )
                ).append(
                        Component.text(
                                newState
                                        ? "MUTED"
                                        : "UNMUTED",
                                newState
                                        ? NamedTextColor.RED
                                        : NamedTextColor.GREEN
                        )
                )
        );

        if (target.isOnline()) {
            target.getPlayer().sendMessage(
                    Component.text(
                            "Your voice chat is now ",
                            NamedTextColor.GRAY
                    ).append(
                            Component.text(
                                    newState
                                            ? "MUTED"
                                            : "UNMUTED",
                                    newState
                                            ? NamedTextColor.RED
                                            : NamedTextColor.GREEN
                            )
                    )
            );
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (!sender.isOp()
                || args.length != 1) {
            return List.of();
        }

        String input =
                args[0].toLowerCase();

        List<String> result =
                new ArrayList<>();

        for (org.bukkit.entity.Player player :
                Bukkit.getOnlinePlayers()) {

            if (player.getName()
                    .toLowerCase()
                    .startsWith(input)) {

                result.add(
                        player.getName()
                );
            }
        }

        return result;
    }
}
