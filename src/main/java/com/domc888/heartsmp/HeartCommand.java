package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class HeartCommand implements TabExecutor {

    private final LivesManager lives;
    private final TokenItems tokens;

    public HeartCommand(LivesManager lives, TokenItems tokens) {
        this.lives = lives;
        this.tokens = tokens;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "setlives" -> setLives(sender, args);
            case "give" -> give(sender, args);
            default -> sendUsage(sender);
        }

        return true;
    }

    private void setLives(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(
                    Component.text("/heartsmp setlives <player> <amount>")
            );
            return;
        }

        Player target = Bukkit.getPlayerExact(args[1]);

        if (target == null) {
            sender.sendMessage(Component.text("Player not found."));
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid amount."));
            return;
        }

        if (amount < 0 || amount > lives.getMaxLives()) {
            sender.sendMessage(
                    Component.text(
                            "Amount must be between 0 and "
                                    + lives.getMaxLives()
                                    + "."
                    )
            );
            return;
        }

        lives.setLives(target.getUniqueId(), amount);

        sender.sendMessage(
                Component.text(
                        "Set "
                                + target.getName()
                                + " to "
                                + amount
                                + "/"
                                + lives.getMaxLives()
                                + " lives."
                )
        );
    }

    private void give(CommandSender sender, String[] args) {
        /*
         * ONLY:
         *
         * /heartsmp give <amount> <player>
         */

        if (args.length != 3) {
            sender.sendMessage(
                    Component.text("/heartsmp give <amount> <player>")
            );
            return;
        }

        int amount;

        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("Invalid amount."));
            return;
        }

        if (amount < 1 || amount > 64) {
            sender.sendMessage(
                    Component.text("Amount must be between 1 and 64.")
            );
            return;
        }

        Player target = Bukkit.getPlayerExact(args[2]);

        if (target == null) {
            sender.sendMessage(
                    Component.text("Player not found or not online.")
            );
            return;
        }

        /*
         * The command now gives the standard Revival token.
         * It does not accept a token type.
         */
        ItemStack stack = tokens.create(RevivalToken.NETHER_STAR);
        stack.setAmount(amount);

        Map<Integer, ItemStack> leftovers =
                target.getInventory().addItem(stack);

        for (ItemStack leftover : leftovers.values()) {
            target.getWorld().dropItemNaturally(
                    target.getLocation(),
                    leftover
            );
        }

        sender.sendMessage(
                Component.text(
                        "Gave "
                                + amount
                                + " Revival token(s) to "
                                + target.getName()
                                + "."
                )
        );
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(
                Component.text("/heartsmp setlives <player> <amount>")
        );

        sender.sendMessage(
                Component.text("/heartsmp give <amount> <player>")
        );
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {
        if (args.length == 1) {
            return filter(
                    List.of("setlives", "give"),
                    args[0]
            );
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("give")) {
                return filter(
                        List.of("1", "8", "16", "32", "64"),
                        args[1]
                );
            }

            if (args[0].equalsIgnoreCase("setlives")) {
                return filter(
                        onlineNames(),
                        args[1]
                );
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give")) {
                return filter(
                        onlineNames(),
                        args[2]
                );
            }

            if (args[0].equalsIgnoreCase("setlives")) {
                List<String> amounts = new ArrayList<>();

                for (int i = 0; i <= lives.getMaxLives(); i++) {
                    amounts.add(String.valueOf(i));
                }

                return filter(amounts, args[2]);
            }
        }

        return List.of();
    }

    private List<String> onlineNames() {
        List<String> names = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }

        return names;
    }

    private List<String> filter(
            List<String> options,
            String prefix
    ) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();

        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(lower)) {
                result.add(option);
            }
        }

        return result;
    }
}
