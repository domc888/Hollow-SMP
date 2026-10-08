package com.domc888.heartsmp;

import io.papermc.paper.event.player.ChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public final class HollowAdminListener
        implements Listener {

    private final HeartSMP plugin;
    private final HollowAdminManager manager;
    private final TokenItems tokens;
    private final org.bukkit.NamespacedKey shrineKey;

    public HollowAdminListener(
            HeartSMP plugin,
            HollowAdminManager manager,
            TokenItems tokens,
            org.bukkit.NamespacedKey shrineKey
    ) {
        this.plugin = plugin;
        this.manager = manager;
        this.tokens = tokens;
        this.shrineKey = shrineKey;
    }

    public void openMain(Player player) {
        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.MAIN
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        27,
                        Component.text(
                                "HollowSMP",
                                NamedTextColor.DARK_PURPLE
                        )
                );

        holder.setInventory(inventory);

        inventory.setItem(
                0,
                manager.createGuiItem(
                        Material.CHEST,
                        "Items",
                        NamedTextColor.GOLD,
                        "Open plugin items"
                )
        );

        inventory.setItem(
                1,
                manager.createGuiItem(
                        Material.PLAYER_HEAD,
                        "Players",
                        NamedTextColor.AQUA,
                        "Manage online players"
                )
        );

        inventory.setItem(
                25,
                manager.createGuiItem(
                        Material.JUKEBOX,
                        "Proximity Chat",
                        manager.isProximityChat()
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isProximityChat()
                                ? "500 blocks: ON"
                                : "500 blocks: OFF"
                )
        );

        inventory.setItem(
                26,
                manager.createGuiItem(
                        Material.HEAVY_CORE,
                        "Voice chat mute",
                        manager.isGlobalVoiceMute()
                                ? NamedTextColor.RED
                                : NamedTextColor.GREEN,
                        manager.isGlobalVoiceMute()
                                ? "Non-OP voice chat: MUTED"
                                : "Non-OP voice chat: ON"
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
                        Component.text(
                                "Items",
                                NamedTextColor.GOLD
                        )
                );

        holder.setInventory(inventory);

        /*
         * Slot 1
         * Revival Shrine
         */
        inventory.setItem(
                0,
                ShrineItems.create(shrineKey)
        );

        /*
         * Slots 2-26
         * All 25 revival tokens.
         */
        int slot = 1;

        for (RevivalToken token :
                RevivalToken.values()) {

            inventory.setItem(
                    slot,
                    tokens.create(token)
            );

            slot++;
        }

        /*
         * Slot 27
         * Revival horn.
         */
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
                        27,
                        Component.text(
                                "Players",
                                NamedTextColor.AQUA
                        )
                );

        holder.setInventory(inventory);

        int slot = 0;

        for (Player target :
                Bukkit.getOnlinePlayers()) {

            if (slot >= 27) {
                break;
            }

            ItemStack head =
                    PlayerHeadItems.create(target);

            SkullMeta meta =
                    (SkullMeta) head.getItemMeta();

            if (meta != null) {
                meta.displayName(
                        Component.text(
                                target.getName(),
                                NamedTextColor.GOLD
                        ).decoration(
                                net.kyori.adventure.text.format.TextDecoration.ITALIC,
                                false
                        )
                );

                meta.lore(
                        List.of(
                                Component.text(
                                        "Click to manage.",
                                        NamedTextColor.GRAY
                                )
                        )
                );

                head.setItemMeta(meta);
            }

            inventory.setItem(
                    slot,
                    head
            );

            slot++;
        }

        player.openInventory(inventory);
    }

    private void openPlayerControl(
            Player admin,
            Player target
    ) {
        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.PLAYER_CONTROL,
                        target.getUniqueId()
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        27,
                        Component.text(
                                target.getName(),
                                NamedTextColor.GOLD
                        )
                );

        holder.setInventory(inventory);

        UUID uuid =
                target.getUniqueId();

        inventory.setItem(
                0,
                manager.createGuiItem(
                        Material.TOTEM_OF_UNDYING,
                        "Immortality",
                        manager.isImmortality(uuid)
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isImmortality(uuid)
                                ? "ON"
                                : "OFF"
                )
        );

        inventory.setItem(
                1,
                manager.createGuiItem(
                        Material.GOLDEN_CARROT,
                        "Saturation",
                        manager.isSaturation(uuid)
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isSaturation(uuid)
                                ? "ON"
                                : "OFF"
                )
        );

        inventory.setItem(
                2,
                manager.createGuiItem(
                        Material.IRON_CHESTPLATE,
                        "Infinite Armor",
                        manager.isInfiniteArmor(uuid)
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isInfiniteArmor(uuid)
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
                        "Open and edit inventory"
                )
        );

        inventory.setItem(
                19,
                manager.createGuiItem(
                        Material.ENDER_CHEST,
                        "Ender Chest",
                        NamedTextColor.DARK_PURPLE,
                        "Open and edit ender chest"
                )
        );

        admin.openInventory(inventory);
    }

    private void openPlayerInventory(
            Player admin,
            Player target
    ) {
        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.PLAYER_INVENTORY,
                        target.getUniqueId()
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        54,
                        Component.text(
                                target.getName()
                                        + "'s Inventory",
                                NamedTextColor.GOLD
                        )
                );

        holder.setInventory(inventory);

        PlayerInventory targetInventory =
                target.getInventory();

        /*
         * Main inventory:
         * 0-35
         */
        for (int i = 0; i < 36; i++) {
            inventory.setItem(
                    i,
                    cloneOrNull(
                            targetInventory.getItem(i)
                    )
            );
        }

        /*
         * Armor:
         * 45 helmet
         * 46 chestplate
         * 47 leggings
         * 48 boots
         *
         * 49 offhand
         */
        inventory.setItem(
                45,
                cloneOrNull(
                        targetInventory.getHelmet()
                )
        );

        inventory.setItem(
                46,
                cloneOrNull(
                        targetInventory.getChestplate()
                )
        );

        inventory.setItem(
                47,
                cloneOrNull(
                        targetInventory.getLeggings()
                )
        );

        inventory.setItem(
                48,
                cloneOrNull(
                        targetInventory.getBoots()
                )
        );

        inventory.setItem(
                49,
                cloneOrNull(
                        targetInventory.getItemInOffHand()
                )
        );

        admin.openInventory(inventory);
    }

    private void openEnderChest(
            Player admin,
            Player target
    ) {
        HollowGuiHolder holder =
                new HollowGuiHolder(
                        HollowGuiHolder.Type.PLAYER_ENDER_CHEST,
                        target.getUniqueId()
                );

        Inventory inventory =
                Bukkit.createInventory(
                        holder,
                        27,
                        Component.text(
                                target.getName()
                                        + "'s Ender Chest",
                                NamedTextColor.DARK_PURPLE
                        )
                );

        holder.setInventory(inventory);

        ItemStack[] contents =
                target.getEnderChest()
                        .getContents();

        for (int i = 0; i < contents.length; i++) {
            inventory.setItem(
                    i,
                    cloneOrNull(contents[i])
            );
        }

        admin.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(
            InventoryClickEvent event
    ) {
        if (!(event.getWhoClicked()
                instanceof Player admin)) {
            return;
        }

        if (!admin.isOp()) {
            return;
        }

        Inventory top =
                event.getView().getTopInventory();

        if (!(top.getHolder()
                instanceof HollowGuiHolder holder)) {
            return;
        }

        switch (holder.getType()) {

            case MAIN -> {
                event.setCancelled(true);

                switch (event.getRawSlot()) {
                    case 0 ->
                            openItems(admin);

                    case 1 ->
                            openPlayers(admin);

                    case 25 -> {
                        boolean enabled =
                                manager.toggleProximityChat();

                        admin.sendMessage(
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
                                )
                        );

                        openMain(admin);
                    }

                    case 26 -> {
                        boolean enabled =
                                manager.toggleGlobalVoiceMute();

                        admin.sendMessage(
                                Component.text(
                                        "Voice chat mute is now ",
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
                                )
                        );

                        openMain(admin);
                    }

                    default -> {
                    }
                }
            }

            case ITEMS -> {
                /*
                 * Items menu is intentionally not cancelled.
                 * OPs can take the items.
                 */
            }

            case PLAYERS -> {
                event.setCancelled(true);

                if (event.getRawSlot() < 0
                        || event.getRawSlot() >= 27) {
                    return;
                }

                ItemStack clicked =
                        event.getCurrentItem();

                if (clicked == null
                        || clicked.getType()
                        != Material.PLAYER_HEAD) {
                    return;
                }

                if (!(clicked.getItemMeta()
                        instanceof SkullMeta meta)) {
                    return;
                }

                OfflinePlayer owner =
                        meta.getOwningPlayer();

                if (owner == null) {
                    return;
                }

                Player target =
                        Bukkit.getPlayer(
                                owner.getUniqueId()
                        );

                if (target == null) {
                    admin.sendMessage(
                            Component.text(
                                    "That player is no longer online.",
                                    NamedTextColor.RED
                            )
                    );

                    openPlayers(admin);
                    return;
                }

                openPlayerControl(
                        admin,
                        target
                );
            }

            case PLAYER_CONTROL -> {
                event.setCancelled(true);

                UUID targetId =
                        holder.getTarget();

                if (targetId == null) {
                    return;
                }

                Player target =
                        Bukkit.getPlayer(targetId);

                if (target == null) {
                    admin.sendMessage(
                            Component.text(
                                    "That player is no longer online.",
                                    NamedTextColor.RED
                            )
                    );

                    openPlayers(admin);
                    return;
                }

                switch (event.getRawSlot()) {

                    case 0 -> {
                        boolean enabled =
                                manager.toggleImmortality(
                                        targetId
                                );

                        manager.sendToggleMessage(
                                admin,
                                "Immortality",
                                enabled,
                                target.getName()
                        );

                        openPlayerControl(
                                admin,
                                target
                        );
                    }

                    case 1 -> {
                        boolean enabled =
                                manager.toggleSaturation(
                                        targetId
                                );

                        manager.sendToggleMessage(
                                admin,
                                "Saturation",
                                enabled,
                                target.getName()
                        );

                        openPlayerControl(
                                admin,
                                target
                        );
                    }

                    case 2 -> {
                        boolean enabled =
                                manager.toggleInfiniteArmor(
                                        targetId
                                );

                        manager.sendToggleMessage(
                                admin,
                                "Infinite armor",
                                enabled,
                                target.getName()
                        );

                        openPlayerControl(
                                admin,
                                target
                        );
                    }

                    case 18 ->
                            openPlayerInventory(
                                    admin,
                                    target
                            );

                    case 19 ->
                            openEnderChest(
                                    admin,
                                    target
                            );

                    default -> {
                    }
                }
            }

            case PLAYER_INVENTORY -> {
                /*
                 * Only the top inventory is the target's
                 * inventory. The bottom is the admin's.
                 *
                 * We synchronize on close.
                 */
            }

            case PLAYER_ENDER_CHEST -> {
                /*
                 * Synchronize on close.
                 */
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(
            InventoryDragEvent event
    ) {
        if (!(event.getWhoClicked()
                instanceof Player admin)) {
            return;
        }

        if (!admin.isOp()) {
            return;
        }

        Inventory top =
                event.getView().getTopInventory();

        if (!(top.getHolder()
                instanceof HollowGuiHolder holder)) {
            return;
        }

        /*
         * Allow normal item movement.
         * The target is synchronized when the menu closes.
         */
    }

    @EventHandler
    public void onInventoryClose(
            InventoryCloseEvent event
    ) {
        Inventory top =
                event.getView().getTopInventory();

        if (!(top.getHolder()
                instanceof HollowGuiHolder holder)) {
            return;
        }

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_INVENTORY) {

            syncPlayerInventory(
                    top,
                    holder.getTarget()
            );
        }

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_ENDER_CHEST) {

            syncEnderChest(
                    top,
                    holder.getTarget()
            );
        }
    }

    private void syncPlayerInventory(
            Inventory inventory,
            UUID targetId
    ) {
        Player target =
                Bukkit.getPlayer(targetId);

        if (target == null) {
            return;
        }

        PlayerInventory targetInventory =
                target.getInventory();

        for (int i = 0; i < 36; i++) {
            targetInventory.setItem(
                    i,
                    cloneOrNull(
                            inventory.getItem(i)
                    )
            );
        }

        targetInventory.setHelmet(
                cloneOrNull(
                        inventory.getItem(45)
                )
        );

        targetInventory.setChestplate(
                cloneOrNull(
                        inventory.getItem(46)
                )
        );

        targetInventory.setLeggings(
                cloneOrNull(
                        inventory.getItem(47)
                )
        );

        targetInventory.setBoots(
                cloneOrNull(
                        inventory.getItem(48)
                )
        );

        targetInventory.setItemInOffHand(
                cloneOrNull(
                        inventory.getItem(49)
                )
        );
    }

    private void syncEnderChest(
            Inventory inventory,
            UUID targetId
    ) {
        Player target =
                Bukkit.getPlayer(targetId);

        if (target == null) {
            return;
        }

        Inventory ender =
                target.getEnderChest();

        for (int i = 0; i < 27; i++) {
            ender.setItem(
                    i,
                    cloneOrNull(
                            inventory.getItem(i)
                    )
            );
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

        UUID uuid =
                player.getUniqueId();

        if (!manager.isImmortality(uuid)) {
            return;
        }

        /*
         * Vanilla totems remain completely normal.
         */
        if (hasTotem(player)) {
            return;
        }

        /*
         * Already at half a heart:
         * absolutely nothing can damage the player.
         */
        if (player.getHealth() <= 0.5) {
            event.setCancelled(true);
            return;
        }

        /*
         * Reduce lethal damage to exactly half a heart.
         */
        double maximumAllowed =
                player.getHealth() - 0.5;

        if (event.getFinalDamage()
                >= maximumAllowed) {

            event.setDamage(
                    Math.max(
                            0.0,
                            maximumAllowed
                    )
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodLevelChange(
            FoodLevelChangeEvent event
    ) {
        if (!(event.getEntity()
                instanceof Player player)) {
            return;
        }

        if (!manager.isSaturation(
                player.getUniqueId()
        )) {
            return;
        }

        /*
         * Hunger bar stays full.
         * Saturation is NOT artificially restored.
         */
        event.setFoodLevel(20);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArmorDamage(
            PlayerItemDamageEvent event
    ) {
        if (!manager.isInfiniteArmor(
                event.getPlayer()
                        .getUniqueId()
        )) {
            return;
        }

        if (isArmor(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
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

        if (!command.equals("/kill")
                && !command.startsWith("/kill ")) {
            return;
        }

        if (!hasTotem(player)
                && player.getHealth() <= 0.5) {

            event.setCancelled(true);

            player.sendMessage(
                    Component.text(
                            "Immortality prevented /kill.",
                            NamedTextColor.GOLD
                    )
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onServerCommand(
            ServerCommandEvent event
    ) {
        String command =
                event.getCommand()
                        .trim();

        if (!command.toLowerCase()
                .startsWith("kill ")) {
            return;
        }

        String[] parts =
                command.split("\\s+");

        if (parts.length < 2) {
            return;
        }

        Player target =
                Bukkit.getPlayerExact(parts[1]);

        if (target == null) {
            return;
        }

        if (!manager.isImmortality(
                target.getUniqueId()
        )) {
            return;
        }

        if (hasTotem(target)) {
            return;
        }

        if (target.getHealth() <= 0.5) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onRevivalHorn(
            PlayerInteractEvent event
    ) {
        if (event.getAction()
                != Action.RIGHT_CLICK_AIR
                && event.getAction()
                != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        ItemStack item =
                event.getItem();

        if (!manager.isRevivalHorn(item)) {
            return;
        }

        event.setCancelled(true);

        manager.playRevivalHorn(
                event.getPlayer()
        );
    }

    @EventHandler
    public void onJoin(
            PlayerJoinEvent event
    ) {
        Player player =
                event.getPlayer();

        manager.applyVoicePermissions(
                player
        );

        if (manager.isSaturation(
                player.getUniqueId()
        )) {
            player.setFoodLevel(20);
        }
    }

    @EventHandler
    public void onQuit(
            PlayerQuitEvent event
    ) {
        /*
         * Persistent state remains in hollowsmp.yml.
         */
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

        double radiusSquared =
                500.0 * 500.0;

        for (Audience audience :
                List.copyOf(event.viewers())) {

            if (!(audience instanceof Player receiver)) {
                continue;
            }

            if (receiver.equals(sender)) {
                continue;
            }

            if (!receiver.getWorld().equals(
                    sender.getWorld()
            )
                    || receiver.getLocation()
                    .distanceSquared(
                            sender.getLocation()
                    ) > radiusSquared) {

                event.viewers().remove(
                        receiver
                );
            }
        }
    }

    private boolean hasTotem(Player player) {
        return isTotem(
                player.getInventory()
                        .getItemInMainHand()
        ) || isTotem(
                player.getInventory()
                        .getItemInOffHand()
        );
    }

    private boolean isTotem(ItemStack item) {
        return item != null
                && item.getType()
                == Material.TOTEM_OF_UNDYING
                && item.getAmount() > 0;
    }

    private boolean isArmor(ItemStack item) {
        if (item == null) {
            return false;
        }

        return switch (item.getType()) {
            case LEATHER_HELMET,
                 CHAINMAIL_HELMET,
                 IRON_HELMET,
                 GOLDEN_HELMET,
                 DIAMOND_HELMET,
                 NETHERITE_HELMET,
                 TURTLE_HELMET,
                 LEATHER_CHESTPLATE,
                 CHAINMAIL_CHESTPLATE,
                 IRON_CHESTPLATE,
                 GOLDEN_CHESTPLATE,
                 DIAMOND_CHESTPLATE,
                 NETHERITE_CHESTPLATE,
                 LEATHER_LEGGINGS,
                 CHAINMAIL_LEGGINGS,
                 IRON_LEGGINGS,
                 GOLDEN_LEGGINGS,
                 DIAMOND_LEGGINGS,
                 NETHERITE_LEGGINGS,
                 LEATHER_BOOTS,
                 CHAINMAIL_BOOTS,
                 IRON_BOOTS,
                 GOLDEN_BOOTS,
                 DIAMOND_BOOTS,
                 NETHERITE_BOOTS,
                 ELYTRA -> true;

            default -> false;
        };
    }

    private ItemStack cloneOrNull(ItemStack item) {
        return item == null
                ? null
                : item.clone();
    }
}
