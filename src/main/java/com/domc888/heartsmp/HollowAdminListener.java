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
import org.bukkit.event.inventory.InventoryAction;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.event.block.Action;

import java.util.List;
import java.util.UUID;

public final class HollowAdminListener implements Listener {

    private final HeartSMP plugin;
    private final HollowAdminManager manager;
    private final TokenItems tokens;
    private final NamespacedKey shrineKey;

    private static final int SLOT_MAIN_ITEMS = 0;
    private static final int SLOT_MAIN_PLAYERS = 1;
    private static final int SLOT_MAIN_PROXIMITY = 52;
    private static final int SLOT_MAIN_VOICE = 53;
    private static final int SLOT_MAIN_EXIT = 49;

    private static final int SLOT_ITEMS_SHRINE = 0;
    private static final int SLOT_ITEMS_TOKEN = 1;
    private static final int SLOT_ITEMS_HORN = 2;
    private static final int SLOT_ITEMS_BACK = 20;
    private static final int SLOT_ITEMS_EXIT = 22;

    private static final int SLOT_PLAYERS_BACK = 52;
    private static final int SLOT_PLAYERS_EXIT = 53;

    private static final int SLOT_CONTROL_IMMORTALITY = 0;
    private static final int SLOT_CONTROL_SATURATION = 1;
    private static final int SLOT_CONTROL_ARMOR = 2;
    private static final int SLOT_CONTROL_INVENTORY = 3;
    private static final int SLOT_CONTROL_ENDER = 4;
    private static final int SLOT_CONTROL_BACK = 20;
    private static final int SLOT_CONTROL_EXIT = 22;

    private static final int SLOT_INV_HELMET = 36;
    private static final int SLOT_INV_CHESTPLATE = 37;
    private static final int SLOT_INV_LEGGINGS = 38;
    private static final int SLOT_INV_BOOTS = 39;
    private static final int SLOT_INV_OFFHAND = 40;

    private static final int SLOT_INV_BACK = 49;
    private static final int SLOT_INV_EXIT = 53;

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
                SLOT_MAIN_ITEMS,
                manager.createGuiItem(
                        Material.CHEST,
                        "Items",
                        NamedTextColor.GOLD
                )
        );

        inventory.setItem(
                SLOT_MAIN_PLAYERS,
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
                SLOT_MAIN_PROXIMITY,
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
                SLOT_MAIN_VOICE,
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

        inventory.setItem(
                SLOT_MAIN_EXIT,
                manager.createGuiItem(
                        Material.BARRIER,
                        "Out",
                        NamedTextColor.RED
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
                SLOT_ITEMS_SHRINE,
                ShrineItems.create(shrineKey)
        );

        RevivalToken[] values =
                RevivalToken.values();

        if (values.length > 0) {
            inventory.setItem(
                    SLOT_ITEMS_TOKEN,
                    tokens.create(values[0])
            );
        }

        inventory.setItem(
                SLOT_ITEMS_HORN,
                manager.createRevivalHorn()
        );

        inventory.setItem(
                SLOT_ITEMS_BACK,
                manager.createGuiItem(
                        Material.ARROW,
                        "Go Back",
                        NamedTextColor.YELLOW
                )
        );

        inventory.setItem(
                SLOT_ITEMS_EXIT,
                manager.createGuiItem(
                        Material.BARRIER,
                        "Out",
                        NamedTextColor.RED
                )
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
            if (slot >= 45) {
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

        inventory.setItem(
                SLOT_PLAYERS_BACK,
                manager.createGuiItem(
                        Material.ARROW,
                        "Go Back",
                        NamedTextColor.YELLOW
                )
        );

        inventory.setItem(
                SLOT_PLAYERS_EXIT,
                manager.createGuiItem(
                        Material.BARRIER,
                        "Out",
                        NamedTextColor.RED
                )
        );

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
                SLOT_CONTROL_IMMORTALITY,
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
                SLOT_CONTROL_SATURATION,
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
                SLOT_CONTROL_ARMOR,
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
                SLOT_CONTROL_INVENTORY,
                manager.createGuiItem(
                        Material.CHEST,
                        "Inventory",
                        NamedTextColor.GOLD,
                        "View and edit inventory"
                )
        );

        inventory.setItem(
                SLOT_CONTROL_ENDER,
                manager.createGuiItem(
                        Material.ENDER_CHEST,
                        "Ender Chest",
                        NamedTextColor.DARK_PURPLE,
                        "View and edit ender chest"
                )
        );

        inventory.setItem(
                SLOT_CONTROL_BACK,
                manager.createGuiItem(
                        Material.ARROW,
                        "Go Back",
                        NamedTextColor.YELLOW
                )
        );

        inventory.setItem(
                SLOT_CONTROL_EXIT,
                manager.createGuiItem(
                        Material.BARRIER,
                        "Out",
                        NamedTextColor.RED
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
            inventory.setItem(
                    i,
                    cloneItem(contents[i])
            );
        }

        inventory.setItem(
                SLOT_INV_HELMET,
                armorOrPane(
                        target.getInventory().getHelmet(),
                        Material.PURPLE_STAINED_GLASS_PANE,
                        "Helmet"
                )
        );

        inventory.setItem(
                SLOT_INV_CHESTPLATE,
                armorOrPane(
                        target.getInventory().getChestplate(),
                        Material.PURPLE_STAINED_GLASS_PANE,
                        "Chestplate"
                )
        );

        inventory.setItem(
                SLOT_INV_LEGGINGS,
                armorOrPane(
                        target.getInventory().getLeggings(),
                        Material.PURPLE_STAINED_GLASS_PANE,
                        "Leggings"
                )
        );

        inventory.setItem(
                SLOT_INV_BOOTS,
                armorOrPane(
                        target.getInventory().getBoots(),
                        Material.PURPLE_STAINED_GLASS_PANE,
                        "Boots"
                )
        );

        inventory.setItem(
                SLOT_INV_OFFHAND,
                armorOrPane(
                        target.getInventory().getItemInOffHand(),
                        Material.ORANGE_STAINED_GLASS_PANE,
                        "Offhand"
                )
        );

        inventory.setItem(
                SLOT_INV_BACK,
                manager.createGuiItem(
                        Material.ARROW,
                        "Go Back",
                        NamedTextColor.YELLOW
                )
        );

        inventory.setItem(
                SLOT_INV_EXIT,
                manager.createGuiItem(
                        Material.BARRIER,
                        "Out",
                        NamedTextColor.RED
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

        inventory.setItem(
                25,
                manager.createGuiItem(
                        Material.ARROW,
                        "Go Back",
                        NamedTextColor.YELLOW
                )
        );

        inventory.setItem(
                26,
                manager.createGuiItem(
                        Material.BARRIER,
                        "Out",
                        NamedTextColor.RED
                )
        );

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

        if (type == HollowGuiHolder.Type.PLAYER_INVENTORY
                || type == HollowGuiHolder.Type.PLAYER_ENDER_CHEST) {

            handleEditorClick(event, player, holder);
            return;
        }

        event.setCancelled(true);

        int slot = event.getRawSlot();

        if (slot < 0
                || slot >= event.getView()
                .getTopInventory()
                .getSize()) {
            return;
        }

        switch (type) {
            case MAIN -> handleMainClick(
                    event,
                    player,
                    slot
            );

            case ITEMS -> handleItemsClick(
                    player,
                    slot
            );

            case PLAYERS -> handlePlayersClick(
                    event,
                    player,
                    slot
            );

            case PLAYER_CONTROL -> handlePlayerControlClick(
                    player,
                    holder,
                    slot
            );

            default -> {
            }
        }
    }

    private void handleMainClick(
            InventoryClickEvent event,
            Player player,
            int slot
    ) {
        switch (slot) {
            case SLOT_MAIN_ITEMS ->
                    openItems(player);

            case SLOT_MAIN_PLAYERS ->
                    openPlayers(player);

            case SLOT_MAIN_PROXIMITY -> {
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

            case SLOT_MAIN_VOICE -> {
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

            case SLOT_MAIN_EXIT ->
                    player.closeInventory();

            default -> {
            }
        }
    }

    private void handleItemsClick(
            Player player,
            int slot
    ) {
        switch (slot) {
            case SLOT_ITEMS_SHRINE ->
                    giveItem(
                            player,
                            ShrineItems.create(shrineKey)
                    );

            case SLOT_ITEMS_TOKEN -> {
                RevivalToken[] values =
                        RevivalToken.values();

                if (values.length > 0) {
                    giveItem(
                            player,
                            tokens.create(values[0])
                    );
                }
            }

            case SLOT_ITEMS_HORN ->
                    giveItem(
                            player,
                            manager.createRevivalHorn()
                    );

            case SLOT_ITEMS_BACK ->
                    openMain(player);

            case SLOT_ITEMS_EXIT ->
                    player.closeInventory();

            default -> {
            }
        }
    }

    private void handlePlayersClick(
            InventoryClickEvent event,
            Player player,
            int slot
    ) {
        if (slot == SLOT_PLAYERS_BACK) {
            openMain(player);
            return;
        }

        if (slot == SLOT_PLAYERS_EXIT) {
            player.closeInventory();
            return;
        }

        ItemStack clicked =
                event.getCurrentItem();

        if (clicked == null
                || clicked.getType() != Material.PLAYER_HEAD) {
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
    }

    private void handlePlayerControlClick(
            Player player,
            HollowGuiHolder holder,
            int slot
    ) {
        UUID targetId =
                holder.getTarget();

        if (targetId == null) {
            return;
        }

        switch (slot) {
            case SLOT_CONTROL_IMMORTALITY -> {
                boolean enabled =
                        manager.toggleImmortality(targetId);

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

            case SLOT_CONTROL_SATURATION -> {
                boolean enabled =
                        manager.toggleSaturation(targetId);

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

            case SLOT_CONTROL_ARMOR -> {
                boolean enabled =
                        manager.toggleInfiniteArmor(targetId);

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

            case SLOT_CONTROL_INVENTORY ->
                    openPlayerInventory(
                            player,
                            targetId
                    );

            case SLOT_CONTROL_ENDER ->
                    openPlayerEnderChest(
                            player,
                            targetId
                    );

            case SLOT_CONTROL_BACK ->
                    openPlayers(player);

            case SLOT_CONTROL_EXIT ->
                    player.closeInventory();

            default -> {
            }
        }
    }

    private void handleEditorClick(
            InventoryClickEvent event,
            Player admin,
            HollowGuiHolder holder
    ) {
        int rawSlot = event.getRawSlot();
        Inventory top = event.getView().getTopInventory();

        if (rawSlot < 0 || rawSlot >= top.getSize()) {
            /*
             * Prevent shift-clicking items from the player's
             * own inventory into the admin editor.
             */
            if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                event.setCancelled(true);
            }

            return;
        }

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_INVENTORY) {

            if (rawSlot == SLOT_INV_BACK) {
                event.setCancelled(true);

                if (holder.getTarget() != null) {
                    openPlayerControl(
                            admin,
                            holder.getTarget()
                    );
                }

                return;
            }

            if (rawSlot == SLOT_INV_EXIT) {
                event.setCancelled(true);
                admin.closeInventory();
                return;
            }

            if (rawSlot >= SLOT_INV_HELMET
                    && rawSlot <= SLOT_INV_OFFHAND) {

                ItemStack current =
                        event.getCurrentItem();

                if (isPlaceholder(current)) {
                    event.setCancelled(true);
                    return;
                }

                return;
            }

            return;
        }

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_ENDER_CHEST) {

            if (rawSlot == 25) {
                event.setCancelled(true);

                if (holder.getTarget() != null) {
                    openPlayerControl(
                            admin,
                            holder.getTarget()
                    );
                }

                return;
            }

            if (rawSlot == 26) {
                event.setCancelled(true);
                admin.closeInventory();
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

        if (holder.getType()
                == HollowGuiHolder.Type.PLAYER_INVENTORY
                || holder.getType()
                == HollowGuiHolder.Type.PLAYER_ENDER_CHEST) {

            int topSize =
                    event.getView()
                            .getTopInventory()
                            .getSize();

            for (int rawSlot :
                    event.getRawSlots()) {

                if (rawSlot < topSize) {
                    if (holder.getType()
                            == HollowGuiHolder.Type.PLAYER_INVENTORY
                            && (rawSlot == SLOT_INV_BACK
                            || rawSlot == SLOT_INV_EXIT
                            || isArmorSlot(rawSlot))) {

                        event.setCancelled(true);
                        return;
                    }

                    if (holder.getType()
                            == HollowGuiHolder.Type.PLAYER_ENDER_CHEST
                            && (rawSlot == 25
                            || rawSlot == 26)) {

                        event.setCancelled(true);
                        return;
                    }
                }
            }

            return;
        }

        event.setCancelled(true);
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

            for (int i = 0; i < target.getEnderChest().getSize(); i++) {
                target.getEnderChest().setItem(
                        i,
                        cloneItem(viewer.getItem(i))
                );
            }

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

            /*
             * Only copy the 36 normal inventory slots.
             *
             * Armor and offhand are handled separately below.
             * This prevents Bukkit's setContents() from replacing
             * or wiping armor unexpectedly.
             */
            for (int i = 0; i < 36; i++) {
                target.getInventory().setItem(
                        i,
                        cloneItem(viewer.getItem(i))
                );
            }

            ItemStack helmet =
                    getEditableArmorItem(
                            viewer.getItem(SLOT_INV_HELMET)
                    );

            ItemStack chestplate =
                    getEditableArmorItem(
                            viewer.getItem(SLOT_INV_CHESTPLATE)
                    );

            ItemStack leggings =
                    getEditableArmorItem(
                            viewer.getItem(SLOT_INV_LEGGINGS)
                    );

            ItemStack boots =
                    getEditableArmorItem(
                            viewer.getItem(SLOT_INV_BOOTS)
                    );

            ItemStack offhand =
                    getEditableArmorItem(
                            viewer.getItem(SLOT_INV_OFFHAND)
                    );

            target.getInventory().setHelmet(helmet);
            target.getInventory().setChestplate(chestplate);
            target.getInventory().setLeggings(leggings);
            target.getInventory().setBoots(boots);
            target.getInventory().setItemInOffHand(offhand);
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
                Bukkit.getPlayerExact(split[1]);

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
        if (!event.getAction().isRightClick()) {
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

    private void giveItem(
            Player player,
            ItemStack item
    ) {
        if (item == null) {
            return;
        }

        MapAddItemResult result =
                giveItemSafely(player, item);

        if (!result.added) {
            player.getWorld().dropItemNaturally(
                    player.getLocation(),
                    result.item
            );
        }
    }

    private MapAddItemResult giveItemSafely(
            Player player,
            ItemStack item
    ) {
        java.util.HashMap<Integer, ItemStack> leftovers =
                player.getInventory().addItem(item);

        if (leftovers.isEmpty()) {
            return new MapAddItemResult(
                    true,
                    null
            );
        }

        ItemStack leftover =
                leftovers.values()
                        .iterator()
                        .next();

        return new MapAddItemResult(
                false,
                leftover
        );
    }

    private ItemStack armorOrPane(
            ItemStack item,
            Material pane,
            String name
    ) {
        if (item != null && !item.getType().isAir()) {
            return item.clone();
        }

        return manager.createGuiItem(
                pane,
                name,
                NamedTextColor.LIGHT_PURPLE
        );
    }

    private boolean isPlaceholder(ItemStack item) {
        if (item == null
                || !item.hasItemMeta()) {
            return false;
        }

        Material type =
                item.getType();

        return type == Material.PURPLE_STAINED_GLASS_PANE
                || type == Material.ORANGE_STAINED_GLASS_PANE;
    }

    private boolean isArmorSlot(int slot) {
        return slot == SLOT_INV_HELMET
                || slot == SLOT_INV_CHESTPLATE
                || slot == SLOT_INV_LEGGINGS
                || slot == SLOT_INV_BOOTS
                || slot == SLOT_INV_OFFHAND;
    }

    private ItemStack getEditableArmorItem(
            ItemStack item
    ) {
        if (isPlaceholder(item)) {
            return null;
        }

        return cloneItem(item);
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

    private static final class MapAddItemResult {

        private final boolean added;
        private final ItemStack item;

        private MapAddItemResult(
                boolean added,
                ItemStack item
        ) {
            this.added = added;
            this.item = item;
        }
    }
}
