package com.domc888.heartsmp;

import io.papermc.paper.event.player.ChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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
                        Component.text("HollowSMP")
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

        int proximityDistance =
                plugin.getConfig().getInt(
                        "proximity-chat.distance",
                        500
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
                                ? "ON - " + proximityDistance + " block chat range"
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

        for (Player target : Bukkit.getOnlinePlayers()) {

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
                    slot,
                    head
            );

            slot++;
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

        for (int i = 0; i < 36; i++) {
            ItemStack item = contents[i];

            inventory.setItem(
                    i,
                    item == null
                            ? null
                            : item.clone()
            );
        }

        inventory.setItem(
                36,
                cloneItem(
                        target.getInventory().getHelmet()
                )
        );

        inventory.setItem(
                37,
                cloneItem(
                        target.getInventory().getChestplate()
                )
        );

        inventory.setItem(
                38,
                cloneItem(
                        target.getInventory().getLeggings()
                )
        );

        inventory.setItem(
                39,
                cloneItem(
                        target.getInventory().getBoots()
                )
        );

        inventory.setItem(
                40,
                cloneItem(
                        target.getInventory().getItemInOffHand()
                )
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
                                "Ender Chest: " + target.getName()
                        )
                );

        holder.setInventory(inventory);

        ItemStack[] contents =
                target.getEnderChest().getContents();

        for (int i = 0; i < contents.length; i++) {
            inventory.setItem(
                    i,
                    cloneItem(contents[i])
            );
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

        if (type == HollowGuiHolder.Type.MAIN
                || type == HollowGuiHolder.Type.ITEMS
                || type == HollowGuiHolder.Type.PLAYERS
                || type == HollowGuiHolder.Type.PLAYER_CONTROL) {

            event.setCancelled(true);

            if (event.getRawSlot()
                    >= event.getView()
                    .getTopInventory()
                    .getSize()) {
                return;
            }

            if (type == HollowGuiHolder.Type.MAIN) {

                switch (event.getRawSlot()) {

                    case 0 -> openItems(player);

                    case 1 -> openPlayers(player);

                    case 52 -> {
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
                                )
                        );

                        openMain(player);
                    }

                    case 53 -> {
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
                                )
                        );

                        openMain(player);
                    }

                    default -> {
                    }
                }

                return;
            }

            if (type == HollowGuiHolder.Type.ITEMS) {
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

                if (skullMeta.getOwningPlayer() == null) {
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
            }
        }
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

        if (type == HollowGuiHolder.Type.MAIN
                || type == HollowGuiHolder.Type.ITEMS
                || type == HollowGuiHolder.Type.PLAYERS
                || type == HollowGuiHolder.Type.PLAYER_CONTROL) {

            event.setCancelled(true);
        }
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
                            target.getEnderChest().getSize()
                    ];

            for (int i = 0;
                 i < contents.length;
                 i++) {

                contents[i] =
                        cloneItem(
                                viewer.getItem(i)
                        );
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
                main[i] =
                        cloneItem(
                                viewer.getItem(i)
                        );
            }

            target.getInventory()
                    .setContents(main);

            target.getInventory()
                    .setHelmet(
                            cloneItem(
                                    viewer.getItem(36)
                            )
                    );

            target.getInventory()
                    .setChestplate(
                            cloneItem(
                                    viewer.getItem(37)
                            )
                    );

            target.getInventory()
                    .setLeggings(
                            cloneItem(
                                    viewer.getItem(38)
                            )
                    );

            target.getInventory()
                    .setBoots(
                            cloneItem(
                                    viewer.getItem(39)
                            )
                    );

            target.getInventory()
                    .setItemInOffHand(
                            cloneItem(
                                    viewer.getItem(40)
                            )
                    );
        }
    }

    @EventHandler
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

        Material material =
                event.getItem().getType();

        if (material.name().endsWith("_HELMET")
                || material.name().endsWith("_CHESTPLATE")
                || material.name().endsWith("_LEGGINGS")
                || material.name().endsWith("_BOOTS")
                || material == Material.ELYTRA) {

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

        double health =
                player.getHealth();

        double finalDamage =
                event.getFinalDamage();

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

        if (health - finalDamage <= 0.5) {
            event.setCancelled(true);
            player.setHealth(0.5);
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

        double radius =
                plugin.getConfig().getDouble(
                        "proximity-chat.distance",
                        500.0
                );

        double radiusSquared =
                radius * radius;

        event.viewers().removeIf(
                viewer -> {

                    if (!(viewer instanceof Player receiver)) {
                        return false;
                    }

                    if (receiver.equals(sender)) {
                        return false;
                    }

                    if (!receiver.getWorld()
                            .equals(sender.getWorld())) {
                        return true;
                    }

                    return receiver.getLocation()
                            .distanceSquared(
                                    sender.getLocation()
                            ) > radiusSquared;
                }
        );
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

        manager.playRevivalHorn(
                event.getPlayer()
        );
    }

    @EventHandler
    public void onShrineBreak(
            BlockBreakEvent event
    ) {
        // Shrine breaking is handled by ShrineListener.
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

    private ItemStack cloneItem(ItemStack item) {
        return item == null
                ? null
                : item.clone();
    }
}
