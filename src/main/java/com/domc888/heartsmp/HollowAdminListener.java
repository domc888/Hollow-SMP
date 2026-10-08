package com.domc888.heartsmp;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
import org.bukkit.event.block.Action;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public final class HollowAdminListener implements Listener {

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
                        "Open HeartSMP items"
                )
        );

        inventory.setItem(
                1,
                manager.createGuiItem(
                        Material.PLAYER_HEAD,
                        "Players",
                        NamedTextColor.AQUA,
                        "Manage individual players"
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
                                ? "500 block chat radius: ON"
                                : "500 block chat radius: OFF"
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
                                : "Non-OP voice chat: ALLOWED"
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

        inventory.setItem(
                0,
                ShrineItems.create(shrineKey)
        );

        int slot = 1;

        for (RevivalToken token :
                RevivalToken.values()) {

            if (slot >= 26) {
                break;
            }

            inventory.setItem(
                    slot++,
                    tokens.create(token)
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
                        )
                );

                meta.lore(
                        List.of(
                                Component.text(
                                        "Click to manage this player.",
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

        inventory.setItem(
                0,
                manager.createGuiItem(
                        Material.TOTEM_OF_UNDYING,
                        "Immortality",
                        manager.isImmortality(
                                target.getUniqueId()
                        )
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isImmortality(
                                target.getUniqueId()
                        )
                                ? "ON"
                                : "OFF"
                )
        );

        inventory.setItem(
                1,
                manager.createGuiItem(
                        Material.GOLDEN_CARROT,
                        "Saturation",
                        manager.isSaturation(
                                target.getUniqueId()
                        )
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isSaturation(
                                target.getUniqueId()
                        )
                                ? "ON - hunger locked at full"
                                : "OFF"
                )
        );

        inventory.setItem(
                2,
                manager.createGuiItem(
                        Material.IRON_CHESTPLATE,
                        "Infinite Armor",
                        manager.isInfiniteArmor(
                                target.getUniqueId()
                        )
                                ? NamedTextColor.GREEN
                                : NamedTextColor.RED,
                        manager.isInfiniteArmor(
                                target.getUniqueId()
                        )
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
                                target.getName() + "'s Inventory",
                                NamedTextColor.GOLD
                        )
                );

        holder.setInventory(inventory);

        PlayerInventory targetInventory =
                target.getInventory();

        for (int i = 0; i < 36; i++) {
            ItemStack item =
                    targetInventory.getItem(i);

            if (item != null) {
                inventory.setItem(
                        i,
                        item.clone()
                );
            }
        }

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
                                target.getName() + "'s Ender Chest",
                                NamedTextColor.DARK_PURPLE
                        )
                );

        holder.setInventory(inventory);

        ItemStack[] contents =
                target.getEnderChest()
                        .getContents();

        for (int i = 0; i < contents.length; i++) {
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

                if (event.getRawSlot() == 0) {
                    openItems(admin);
                } else if (event.getRawSlot() == 1) {
                    openPlayers(admin);
                } else if (event.getRawSlot() == 25) {
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
                            );

                    openMain(admin);
                } else if (event.getRawSlot() == 26) {
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
                            );

                    openMain(admin);
                }
            }

            case ITEMS -> {
                /*
                 * Items menu is intentionally editable.
                 * OPs can take the items out.
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
                        instanceof SkullMeta skull)) {
                    return;
                }

                OfflinePlayer offline =
                        skull.getOwningPlayer();

                if (offline == null) {
                    return;
                }

                Player target =
                        Bukkit.getPlayer(
                                offline.getUniqueId()
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
                scheduleInventorySync(
                        top,
                        holder.getTarget()
                );
            }

            case PLAYER_ENDER_CHEST -> {
                scheduleEnderChestSync(
                        top,
                        holder.getTarget()
                );
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(
            InventoryDragEvent event
    ) {
        if (!(event.getWhoClicked()
                instanceof Player admin)
                || !admin.isOp()) {
            return;
        }

        Inventory top =
                event.getView().getTopInventory();

        if (!(top.getHolder()
                instanceof HollowGuiHolder holder)) {
            return;
        }

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_INVENTORY) {

            scheduleInventorySync(
                    top,
                    holder.getTarget()
            );
        }

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_ENDER_CHEST) {

            scheduleEnderChestSync(
                    top,
                    holder.getTarget()
            );
        }
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

    private void scheduleInventorySync(
            Inventory inventory,
            UUID target
    ) {
        plugin.getServer()
                .getScheduler()
                .runTask(
                        plugin,
                        () -> syncPlayerInventory(
                                inventory,
                                target
                        )
                );
    }

    private void scheduleEnderChestSync(
            Inventory inventory,
            UUID target
    ) {
        plugin.getServer()
                .getScheduler()
                .runTask(
                        plugin,
                        () -> syncEnderChest(
                                inventory,
                                target
                        )
                );
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
         * If a real totem is available, allow vanilla
         * totem behavior to happen normally.
         */
        if (hasUsableTotem(player)) {
            return;
        }

        double health =
                player.getHealth();

        /*
         * Never allow damage to bring the player
         * below half a heart.
         */
        if (health <= 0.5) {
            event.setCancelled(true);
            return;
        }

        double maximumDamage =
                health - 0.5;

        if (event.getFinalDamage()
                >= maximumDamage) {

            event.setDamage(
                    Math.max(
                            0.0,
                            maximumDamage
                    )
            );
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFoodChange(
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

        if (command.equals("/kill")
                || command.startsWith("/kill ")) {

            if (!hasUsableTotem(player)
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
    }

    @EventHandler
    public void onInteract(
            PlayerInteractEvent event
    ) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
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
         * Permission attachments are cleaned up by
         * the manager when the plugin disables.
         */
    }

    @EventHandler
    public void onChat(
            AsyncChatEvent event
    ) {
        if (!manager.isProximityChat()) {
            return;
        }

        Player sender =
                event.getPlayer();

        /*
         * AsyncChatEvent is asynchronous, so use the
         * player's current location only for the
         * filtering operation and do not modify
         * Bukkit state.
         */
        org.bukkit.Location senderLocation =
                sender.getLocation();

        Iterator<Audience> iterator =
                event.viewers().iterator();

        while (iterator.hasNext()) {
            Audience audience =
                    iterator.next();

            if (!(audience instanceof Player receiver)) {
                continue;
            }

            if (receiver.equals(sender)) {
                continue;
            }

            if (!receiver.getWorld().equals(
                    sender.getWorld()
            )) {
                iterator.remove();
                continue;
            }

            if (receiver.getLocation()
                    .distanceSquared(
                            senderLocation
                    ) > 500.0 * 500.0) {

                iterator.remove();
            }
        }
    }

    private boolean hasUsableTotem(
            Player player
    ) {
        ItemStack main =
                player.getInventory()
                        .getItemInMainHand();

        ItemStack off =
                player.getInventory()
                        .getItemInOffHand();

        return isTotem(main)
                || isTotem(off);
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
