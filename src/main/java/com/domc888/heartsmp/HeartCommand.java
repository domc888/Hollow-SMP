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
import java.util.concurrent.ThreadLocalRandom;

public final class HeartCommand implements TabExecutor {

    private record Parsed(String name, int amount) {
    }

    private static final List<String> GIVE_AMOUNTS =
            List.of("1", "8", "16", "32", "64");

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
            sendUsage(sender);
            return;
        }

        Parsed parsed = parse(args[1], args[2]);

        if (parsed == null) {
            sendUsage(sender);
            return;
        }

        int max = lives.getMaxLives();

        if (parsed.amount() < 0 || parsed.amount() > max) {
            sender.sendMessage(
                    Component.text(
                            "Amount must be between 0 and " + max + "."
                    )
            );
            return;
        }

        OfflinePlayer target = findPlayer(parsed.name());

        if (target == null) {
            sender.sendMessage(Component.text("Player not found."));
            return;
        }

        lives.setLives(target.getUniqueId(), parsed.amount());

        Player online = target.getPlayer();

        if (online != null) {
            if (parsed.amount() == 0) {
                lives.deathBan(online);
            } else {
                lives.applyState(online);

                online.sendMessage(
                        Component.text(
                                "Your lives were set to "
                                        + parsed.amount()
                                        + "/"
                                        + max
                                        + "."
                        )
                );
            }
        }

        sender.sendMessage(
                Component.text(
                        "Set "
                                + parsed.name()
                                + " to "
                                + parsed.amount()
                                + "/"
                                + max
                                + " lives."
                )
        );
    }

    private void give(CommandSender sender, String[] args) {
        if (args.length < 3 || args.length > 4) {
            sendUsage(sender);
            return;
        }

        Parsed parsed = parse(args[1], args[2]);

        if (parsed == null) {
            sendUsage(sender);
            return;
        }

        if (parsed.amount() < 1 || parsed.amount() > 64) {
            sender.sendMessage(
                    Component.text("Amount must be between 1 and 64.")
            );
            return;
        }

        Player target = Bukkit.getPlayerExact(parsed.name());

        if (target == null) {
            sender.sendMessage(
                    Component.text("Player not found or not online.")
            );
            return;
        }

        RevivalToken fixed = null;

        if (args.length == 4) {
            fixed = RevivalToken.byId(args[3]);

            if (fixed == null) {
                sender.sendMessage(
                        Component.text(
                                "Unknown token. Use tab completion to see all 25."
                        )
                );
                return;
            }
        }

        if (fixed != null) {
            ItemStack stack = tokens.create(fixed);
            stack.setAmount(parsed.amount());
            giveItem(target, stack);
        } else {
            RevivalToken[] all = RevivalToken.values();

            for (int i = 0; i < parsed.amount(); i++) {
                RevivalToken random =
                        all[ThreadLocalRandom.current().nextInt(all.length)];

                giveItem(target, tokens.create(random));
            }
        }

        sender.sendMessage(
                Component.text(
                        "Gave "
                                + parsed.amount()
                                + " Revival Token(s) to "
                                + target.getName()
                                + "."
                )
        );
    }

    private void giveItem(Player target, ItemStack stack) {
        Map<Integer, ItemStack> leftovers =
                target.getInventory().addItem(stack);

        for (ItemStack left : leftovers.values()) {
            target.getWorld().dropItemNaturally(
                    target.getLocation(),
                    left
            );
        }
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(
                Component.text(
                        "/heartsmp setlives <player> <amount>"
                )
        );

        sender.sendMessage(
                Component.text(
                        "/heartsmp give <amount> <player> [token]"
                )
        );
    }

    private Parsed parse(String first, String second) {
        Integer a = toInt(first);
        Integer b = toInt(second);

        if (a != null && b == null) {
            return new Parsed(second, a);
        }

        if (a == null && b != null) {
            return new Parsed(first, b);
        }

        return null;
    }

    private Integer toInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private OfflinePlayer findPlayer(String name) {
        Player online = Bukkit.getPlayerExact(name);

        if (online != null) {
            return online;
        }

        return Bukkit.getOfflinePlayerIfCached(name);
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

        String sub = args[0].toLowerCase(Locale.ROOT);

        boolean isSet = sub.equals("setlives");
        boolean isGive = sub.equals("give");

        if (!isSet && !isGive) {
            return List.of();
        }

        if (args.length == 2) {
            return isSet
                    ? filter(onlineNames(), args[1])
                    : filter(GIVE_AMOUNTS, args[1]);
        }

        if (args.length == 3) {
            if (toInt(args[1]) != null) {
                return filter(onlineNames(), args[2]);
            }

            return filter(
                    isSet ? lifeAmounts() : GIVE_AMOUNTS,
                    args[2]
            );
        }

        if (args.length == 4 && isGive) {
            return filter(
                    RevivalToken.ids(),
                    args[3]
            );
        }

        return List.of();
    }

    private List<String> lifeAmounts() {
        List<String> out = new ArrayList<>();

        for (int i = 0; i <= lives.getMaxLives(); i++) {
            out.add(String.valueOf(i));
        }

        return out;
    }

    private List<String> onlineNames() {
        List<String> out = new ArrayList<>();

        for (Player player : Bukkit.getOnlinePlayers()) {
            out.add(player.getName());
        }

        return out;
    }

    private List<String> filter(
            List<String> options,
            String prefix
    ) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();

        for (String option : options) {
            if (option.toLowerCase(Locale.ROOT).startsWith(lower)) {
                out.add(option);
            }
        }

        return out;
    }
}
