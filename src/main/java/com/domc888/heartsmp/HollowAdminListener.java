package com.domc888.heartsmp;

import io.papermc.paper.event.player.ChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerItemDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class HollowAdminListener implements Listener {

    private final HeartSMP plugin;
    private final HollowAdminManager manager;
    private final TokenItems tokens;
    private final NamespacedKey shrineKey;

    public HollowAdminListener(
            HeartSMP plugin,
            HollowAdminManager manager,
            TokenItems tokens,
            NamespacedKey shrineKey
    ) {
        this.plugin = plugin;
        this.manager = manager;
        this.tokens = tokens;
        this.shrineKey = shrineKey;
    }

    public void openMain(Player player) {
        if (!player.isOp()) {
            return;
        }

        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.MAIN
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        54,
                        Component.text(
                                "HollowSMP"
                        )
                );

        holder.setInventory(inventory);

        inventory.setItem(
                0,
                manager.createGuiItem(
                        Material.CHEST,
                        "Items",
                        NamedTextColor.GOLD
                )
        );

        inventory.setItem(
                1,
                manager.createGuiItem(
                        Material.PLAYER_HEAD,
                        "Players",
                        NamedTextColor.AQUA
                )
        );

        inventory.setItem(
                52,
                manager.createGuiItem(
                        Material.JUKEBOX,
                        "Proximity Chat",
                        manager.isProximityChat()
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isProximityChat()
                                ? "ON - 500 block chat range"
                                : "OFF - normal chat range"
                )
        );

        inventory.setItem(
                53,
                manager.createGuiItem(
                        Material.HEAVY_CORE,
                        "Voice chat mute",
                        manager.isGlobalVoiceMute()
                                ? NamedTextColor.RED
                                : NamedTextColor.GREEN,
                        manager.isGlobalVoiceMute()
                                ? "ON - non-OP voice chat muted"
                                : "OFF"
                )
        );

        player.openInventory(inventory);
    }

    private void openItems(Player player) {
        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.ITEMS
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        27,
                        Component.text("HollowSMP Items")
                );

        holder.setInventory(inventory);

        inventory.setItem(
                0,
                ShrineItems.create(shrineKey)
        );

        RevivalToken[] values =
                RevivalToken.values();

        for (int i = 0; i < values.length; i++) {
            inventory.setItem(
                    i + 1,
                    tokens.create(values[i])
            );
        }

        inventory.setItem(
                26,
                manager.createRevivalHorn()
        );

        player.openInventory(inventory);
    }

    private void openPlayers(Player player) {
        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.PLAYERS
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        54,
                        Component.text("Players")
                );

        holder.setInventory(inventory);

        int slot = 0;

        for (Player target :
                Bukkit.getOnlinePlayers()) {

            if (slot >= inventory.getSize()) {
                break;
            }

            ItemStack head =
                    PlayerHeadItems.create(target);

            ItemMeta meta =
                    head.getItemMeta();

            if (meta != null) {
                meta.displayName(
                        Component.text(
                                target.getName(),
                                NamedTextColor.YELLOW
                        )
                );

                meta.lore(
                        List.of(
                                Component.text(
                                        "Click to manage player",
                                        NamedTextColor.GRAY
                                )
                        )
                );

                head.setItemMeta(meta);
            }

            inventory.setItem(
                    slot++,
                    head
            );
        }

        player.openInventory(inventory);
    }

    private void openPlayerControl(
            Player admin,
            UUID targetId
    ) {
        Player target =
                Bukkit.getPlayer(targetId);

        String targetName =
                target != null
                        ? target.getName()
                        : Bukkit.getOfflinePlayer(targetId).getName();

        if (targetName == null) {
            targetName = targetId.toString();
        }

        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.PLAYER_CONTROL,
                        targetId
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        27,
                        Component.text(
                                "Control: " + targetName
                        )
                );

        holder.setInventory(inventory);

        inventory.setItem(
                0,
                manager.createGuiItem(
                        Material.TOTEM_OF_UNDYING,
                        "Immortality",
                        manager.isImmortality(targetId)
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isImmortality(targetId)
                                ? "ON"
                                : "OFF"
                )
        );

        inventory.setItem(
                1,
                manager.createGuiItem(
                        Material.GOLDEN_CARROT,
                        "Saturation",
                        manager.isSaturation(targetId)
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isSaturation(targetId)
                                ? "ON"
                                : "OFF"
                )
        );

        inventory.setItem(
                2,
                manager.createGuiItem(
                        Material.IRON_CHESTPLATE,
                        "Infinite Armor",
                        manager.isInfiniteArmor(targetId)
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isInfiniteArmor(targetId)
                                ? "ON"
                                : "OFF"
                )
        );

        inventory.setItem(
                18,
                manager.createGuiItem(
                        Material.CHEST,
                        "Inventory",
                        NamedTextColor.GOLD,
                        "View and edit inventory"
                )
        );

        inventory.setItem(
                19,
                manager.createGuiItem(
                        Material.ENDER_CHEST,
                        "Ender Chest",
                        NamedTextColor.DARK_PURPLE,
                        "View and edit ender chest"
                )
        );

        admin.openInventory(inventory);
    }

    private void openPlayerInventory(
            Player admin,
            UUID targetId
    ) {
        Player target =
                Bukkit.getPlayer(targetId);

        if (target == null) {
            admin.sendMessage(
                    Component.text(
                            "That player is no longer online.",
                            NamedTextColor.RED
                    )
            );
            return;
        }

        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.PLAYER_INVENTORY,
                        targetId
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        54,
                        Component.text(
                                "Inventory: " + target.getName()
                        )
                );

        holder.setInventory(inventory);

        ItemStack[] contents =
                target.getInventory().getContents();

        for (int i = 0;
             i < contents.length && i < 36;
             i++) {

            inventory.setItem(
                    i,
                    contents[i] == null
                            ? null
                            : contents[i].clone()
            );
        }

        inventory.setItem(
                36,
                target.getInventory()
                        .getHelmet() == null
                        ? null
                        : target.getInventory()
                                .getHelmet()
                                .clone()
        );

        inventory.setItem(
                37,
                target.getInventory()
                        .getChestplate() == null
                        ? null
                        : target.getInventory()
                                .getChestplate()
                                .clone()
        );

        inventory.setItem(
                38,
                target.getInventory()
                        .getLeggings() == null
                        ? null
                        : target.getInventory()
                                .getLeggings()
                                .clone()
        );

        inventory.setItem(
                39,
                target.getInventory()
                        .getBoots() == null
                        ? null
                        : target.getInventory()
                                .getBoots()
                                .clone()
        );

        inventory.setItem(
                40,
                target.getInventory()
                        .getItemInOffHand() == null
                        ? null
                        : target.getInventory()
                                .getItemInOffHand()
                                .clone()
        );

        admin.openInventory(inventory);
    }

    private void openPlayerEnderChest(
            Player admin,
            UUID targetId
    ) {
        Player target =
                Bukkit.getPlayer(targetId);

        if (target == null) {
            admin.sendMessage(
                    Component.text(
                            "That player is no longer online.",
                            NamedTextColor.RED
                    )
            );
            return;
        }

        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.PLAYER_ENDER_CHEST,
                        targetId
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        27,
                        Component.text(
                                "Ender Chest: "
                                        + target.getName()
                        )
                );

        holder.setInventory(inventory);

        ItemStack[] contents =
                target.getEnderChest().getContents();

        for (int i = 0;
             i < contents.length;
             i++) {

            if (contents[i] != null) {
                inventory.setItem(
                        i,
                        contents[i].clone()
                );
            }
        }

        admin.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(
            InventoryClickEvent event
    ) {
        if (!(event.getWhoClicked()
                instanceof Player player)) {
            return;
        }

        if (!(event.getView()
                .getTopInventory()
                .getHolder()
                instanceof HollowGuiHolder holder)) {
            return;
        }

        if (!player.isOp()) {
            event.setCancelled(true);
            return;
        }

        HollowGuiHolder.Type type =
                holder.getType();

        /*
         * These four GUIs are completely locked.
         *
         * This catches:
         * - normal clicks
         * - shift-clicks
         * - hotbar swaps
         * - number-key swaps
         * - double-click collection
         * - dropping items
         * - picking items up
         * - placing items
         */
        if (type == HollowGuiHolder.Type.MAIN
                || type == HollowGuiHolder.Type.ITEMS
                || type == HollowGuiHolder.Type.PLAYERS
                || type == HollowGuiHolder.Type.PLAYER_CONTROL) {

            event.setCancelled(true);

            /*
             * Do not allow any interaction with the player's
             * own inventory while one of these GUI menus is open.
             */
            if (event.getRawSlot()
                    >= event.getView()
                    .getTopInventory()
                    .getSize()) {
                return;
            }

            if (type == HollowGuiHolder.Type.MAIN) {

                if (event.getRawSlot() == 0) {
                    openItems(player);
                    return;
                }

                if (event.getRawSlot() == 1) {
                    openPlayers(player);
                    return;
                }

                if (event.getRawSlot() == 52) {

                    boolean enabled =
                            manager.toggleProximityChat();

                    player.sendMessage(
                            Component.text(
                                    "Proximity chat is now ",
                                    NamedTextColor.GRAY
                            ).append(
                                    Component.text(
                                            enabled
                                                    ? "ON"
                                                    : "OFF",
                                            enabled
                                                    ? NamedTextColor.GREEN
                                                    : NamedTextColor.RED
                                    )
                            );

                    openMain(player);
                    return;
                }

                if (event.getRawSlot() == 53) {

                    boolean enabled =
                            manager.toggleGlobalVoiceMute();

                    player.sendMessage(
                            Component.text(
                                    "Global voice chat mute is now ",
                                    NamedTextColor.GRAY
                            ).append(
                                    Component.text(
                                            enabled
                                                    ? "ON"
                                                    : "OFF",
                                            enabled
                                                    ? NamedTextColor.RED
                                                    : NamedTextColor.GREEN
                                    )
                            );

                    openMain(player);
                    return;
                }

                return;
            }

            if (type == HollowGuiHolder.Type.ITEMS) {

                if (event.getRawSlot() == 26) {

                    ItemStack horn =
                            manager.createRevivalHorn();

                    player.getInventory()
                            .addItem(horn);

                    player.sendMessage(
                            Component.text(
                                    "You received the Revival horn.",
                                    NamedTextColor.LIGHT_PURPLE
                            )
                    );

                    return;
                }

                return;
            }

            if (type == HollowGuiHolder.Type.PLAYERS) {

                ItemStack clicked =
                        event.getCurrentItem();

                if (clicked == null
                        || clicked.getType()
                        != Material.PLAYER_HEAD) {
                    return;
                }

                if (!(clicked.getItemMeta()
                        instanceof SkullMeta skullMeta)) {
                    return;
                }

                if (skullMeta.getOwningPlayer()
                        == null) {
                    return;
                }

                UUID targetId =
                        skullMeta.getOwningPlayer()
                                .getUniqueId();

                openPlayerControl(
                        player,
                        targetId
                );

                return;
            }

            if (type == HollowGuiHolder.Type.PLAYER_CONTROL) {

                UUID targetId =
                        holder.getTarget();

                if (targetId == null) {
                    return;
                }

                switch (event.getRawSlot()) {

                    case 0 -> {
                        boolean enabled =
                                manager.toggleImmortality(
                                        targetId
                                );

                        sendToggle(
                                player,
                                "Immortality",
                                enabled,
                                targetId
                        );

                        openPlayerControl(
                                player,
                                targetId
                        );
                    }

                    case 1 -> {
                        boolean enabled =
                                manager.toggleSaturation(
                                        targetId
                                );

                        sendToggle(
                                player,
                                "Saturation",
                                enabled,
                                targetId
                        );

                        openPlayerControl(
                                player,
                                targetId
                        );
                    }

                    case 2 -> {
                        boolean enabled =
                                manager.toggleInfiniteArmor(
                                        targetId
                                );

                        sendToggle(
                                player,
                                "Infinite Armor",
                                enabled,
                                targetId
                        );

                        openPlayerControl(
                                player,
                                targetId
                        );
                    }

                    case 18 ->
                            openPlayerInventory(
                                    player,
                                    targetId
                            );

                    case 19 ->
                            openPlayerEnderChest(
                                    player,
                                    targetId
                            );

                    default -> {
                    }
                }

                return;
            }
        }

        /*
         * Inventory and Ender Chest viewers are intentionally
         * NOT cancelled. OPs can freely move, take, place,
         * swap and give items.
         */
    }

    @EventHandler
    public void onInventoryDrag(
            InventoryDragEvent event
    ) {
        if (!(event.getWhoClicked()
                instanceof Player player)) {
            return;
        }

        if (!(event.getView()
                .getTopInventory()
                .getHolder()
                instanceof HollowGuiHolder holder)) {
            return;
        }

        if (!player.isOp()) {
            event.setCancelled(true);
            return;
        }

        HollowGuiHolder.Type type =
                holder.getType();

        /*
         * Absolutely prevent dragging items into any
         * administrative menu.
         */
        if (type == HollowGuiHolder.Type.MAIN
                || type == HollowGuiHolder.Type.ITEMS
                || type == HollowGuiHolder.Type.PLAYERS
                || type == HollowGuiHolder.Type.PLAYER_CONTROL) {

            event.setCancelled(true);
        }

        /*
         * PLAYER_INVENTORY and PLAYER_ENDER_CHEST intentionally
         * remain editable.
         */
    }

    @EventHandler
    public void onInventoryClose(
            InventoryCloseEvent event
    ) {
        if (!(event.getPlayer()
                instanceof Player admin)) {
            return;
        }

        if (!(event.getInventory()
                .getHolder()
                instanceof HollowGuiHolder holder)) {
            return;
        }

        if (!admin.isOp()) {
            return;
        }

        UUID targetId =
                holder.getTarget();

        if (targetId == null) {
            return;
        }

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_ENDER_CHEST) {

            Player target =
                    Bukkit.getPlayer(targetId);

            if (target == null) {
                return;
            }

            Inventory viewer =
                    event.getInventory();

            ItemStack[] contents =
                    new ItemStack[
                            target.getEnderChest()
                                    .getSize()
                    ];

            for (int i = 0;
                 i < contents.length
                        && i < viewer.getSize();
                 i++) {

                ItemStack item =
                        viewer.getItem(i);

                contents[i] =
                        item == null
                                ? null
                                : item.clone();
            }

            target.getEnderChest()
                    .setContents(contents);

            return;
        }

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_INVENTORY) {

            Player target =
                    Bukkit.getPlayer(targetId);

            if (target == null) {
                return;
            }

            Inventory viewer =
                    event.getInventory();

            ItemStack[] main =
                    new ItemStack[36];

            for (int i = 0; i < 36; i++) {

                ItemStack item =
                        viewer.getItem(i);

                main[i] =
                        item == null
                                ? null
                                : item.clone();
            }

            target.getInventory()
                    .setContents(main);

            ItemStack helmet =
                    viewer.getItem(36);

            ItemStack chestplate =
                    viewer.getItem(37);

            ItemStack leggings =
                    viewer.getItem(38);

            ItemStack boots =
                    viewer.getItem(39);

            ItemStack offhand =
                    viewer.getItem(40);

            target.getInventory()
                    .setHelmet(
                            helmet == null
                                    ? null
                                    : helmet.clone()
                    );

            target.getInventory()
                    .setChestplate(
                            chestplate == null
                                    ? null
                                    : chestplate.clone()
                    );

            target.getInventory()
                    .setLeggings(
                            leggings == null
                                    ? null
                                    : leggings.clone()
                    );

            target.getInventory()
                    .setBoots(
                            boots == null
                                    ? null
                                    : boots.clone()
                    );

            target.getInventory()
                    .setItemInOffHand(
                            offhand == null
                                    ? null
                                    : offhand.clone()
                    );
        }
    }

    @EventHandler
    public void onFoodChange(
            PlayerFoodLevelChangeEvent event
    ) {
        Player player =
                event.getEntity();

        if (!manager.isSaturation(
                player.getUniqueId()
        )) {
            return;
        }

        event.setFoodLevel(20);
    }

    @EventHandler
    public void onItemDamage(
            PlayerItemDamageEvent event
    ) {
        Player player =
                event.getPlayer();

        if (!manager.isInfiniteArmor(
                player.getUniqueId()
        )) {
            return;
        }

        if (event.getItem()
                .getType()
                .toString()
                .endsWith("_HELMET")
                || event.getItem()
                .getType()
                .toString()
                .endsWith("_CHESTPLATE")
                || event.getItem()
                .getType()
                .toString()
                .endsWith("_LEGGINGS")
                || event.getItem()
                .getType()
                .toString()
                .endsWith("_BOOTS")
                || event.getItem()
                .getType()
                == Material.ELYTRA) {

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(
            EntityDamageEvent event
    ) {
        if (!(event.getEntity()
                instanceof Player player)) {
            return;
        }

        if (!manager.isImmortality(
                player.getUniqueId()
        )) {
            return;
        }

        double maxHealth =
                player.getAttribute(
                        Attribute.MAX_HEALTH
                ) != null
                        ? player.getAttribute(
                                Attribute.MAX_HEALTH
                        ).getValue()
                        : 20.0;

        double health =
                player.getHealth();

        double finalDamage =
                event.getFinalDamage();

        /*
         * Already at half a heart:
         * nothing can kill the player.
         */
        if (health <= 0.5) {
            event.setCancelled(true);

            Bukkit.getScheduler().runTask(
                    plugin,
                    () -> {
                        if (player.isOnline()
                                && manager.isImmortality(
                                        player.getUniqueId()
                                )) {
                            player.setHealth(0.5);
                        }
                    }
            );

            return;
        }

        /*
         * If this hit would kill the player, reduce it
         * so they stop at exactly half a heart.
         */
        if (health - finalDamage <= 0.5) {

            event.setCancelled(true);

            player.setHealth(
                    Math.min(
                            0.5,
                            maxHealth
                    )
            );
        }
    }

    @EventHandler
    public void onKillCommand(
            PlayerCommandPreprocessEvent event
    ) {
        Player player =
                event.getPlayer();

        if (!manager.isImmortality(
                player.getUniqueId()
        )) {
            return;
        }

        String command =
                event.getMessage()
                        .trim()
                        .toLowerCase();

        if (command.equals("/kill")
                || command.startsWith("/kill ")
                || command.equals("/minecraft:kill")
                || command.startsWith("/minecraft:kill ")) {

            event.setCancelled(true);

            if (player.getHealth() > 0.5) {
                player.setHealth(0.5);
            }

            player.sendMessage(
                    Component.text(
                            "Immortality prevented /kill.",
                            NamedTextColor.GOLD
                    )
            );
        }
    }

    @EventHandler
    public void onServerKillCommand(
            ServerCommandEvent event
    ) {
        String command =
                event.getCommand()
                        .trim()
                        .toLowerCase();

        if (!command.equals("kill")
                && !command.startsWith("kill ")
                && !command.equals("minecraft:kill")
                && !command.startsWith("minecraft:kill ")) {
            return;
        }

        String[] split =
                command.split("\\s+");

        if (split.length < 2) {
            return;
        }

        Player target =
                Bukkit.getPlayerExact(
                        split[1]
                );

        if (target == null
                || !manager.isImmortality(
                        target.getUniqueId()
                )) {
            return;
        }

        event.setCancelled(true);

        if (target.getHealth() > 0.5) {
            target.setHealth(0.5);
        }

        target.sendMessage(
                Component.text(
                        "Immortality prevented /kill.",
                        NamedTextColor.GOLD
                )
        );
    }

    @EventHandler
    public void onJoin(
            PlayerJoinEvent event
    ) {
        manager.applyVoicePermissions(
                event.getPlayer()
        );
    }

    @EventHandler
    public void onChat(
            ChatEvent event
    ) {
        if (!manager.isProximityChat()) {
            return;
        }

        Player sender =
                event.getPlayer();

        double radius = 500.0;
        double radiusSquared =
                radius * radius;

        /*
         * Work on a copy so modifying the viewers does
         * not cause concurrent modification issues.
         */
        List<Player> viewers =
                new ArrayList<>(
                        event.viewers()
                );

        for (Player receiver : viewers) {

            if (receiver.equals(sender)) {
                continue;
            }

            if (!receiver.getWorld()
                    .equals(sender.getWorld())) {

                event.viewers()
                        .remove(receiver);

                continue;
            }

            if (receiver.getLocation()
                    .distanceSquared(
                            sender.getLocation()
                    ) > radiusSquared) {

                event.viewers()
                        .remove(receiver);
            }
        }
    }

    @EventHandler
    public void onRevivalHorn(
            PlayerInteractEvent event
    ) {
        if (!event.getAction()
                .isRightClick()) {
            return;
        }

        ItemStack item =
                event.getItem();

        if (!manager.isRevivalHorn(item)) {
            return;
        }

        event.setCancelled(true);

        Player player =
                event.getPlayer();

        manager.playRevivalHorn(player);
    }

    @EventHandler
    public void onShrineBreak(
            BlockBreakEvent event
    ) {
        /*
         * Prevent accidental shrine destruction by
         * non-OPs only if needed by the admin item.
         *
         * The actual shrine system continues to be
         * handled by ShrineListener.
         */
    }

    private void sendToggle(
            Player admin,
            String feature,
            boolean enabled,
            UUID targetId
    ) {
        Player target =
                Bukkit.getPlayer(targetId);

        String targetName =
                target != null
                        ? target.getName()
                        : Bukkit.getOfflinePlayer(
                                targetId
                        ).getName();

        if (targetName == null) {
            targetName = targetId.toString();
        }

        manager.sendToggleMessage(
                admin,
                feature,
                enabled,
                targetName
        );

        if (target != null) {

            target.sendMessage(
                    Component.text(
                            feature + " is now ",
                            NamedTextColor.GRAY
                    ).append(
                            Component.text(
                                    enabled
                                            ? "ON"
                                            : "OFF",
                                    enabled
                                            ? NamedTextColor.GREEN
                                            : NamedTextColor.RED
                            )
                    )
            );
        }
    }
}
