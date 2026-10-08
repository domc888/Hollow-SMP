package com.domc888.heartsmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class HollowAdminManager {

    private final HeartSMP plugin;

    /**
     * When active, every non-OP player is frozen in place.
     */
    private boolean freezeBarrierActive = false;

    private final Set<UUID> voiceChatMuted = new HashSet<>();

    public HollowAdminManager(HeartSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isFreezeBarrierActive() {
        return freezeBarrierActive;
    }

    public void setFreezeBarrierActive(boolean active) {
        this.freezeBarrierActive = active;

        if (active) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.isOp()) {
                    player.sendActionBar(net.kyori.adventure.text.Component.text(
                            "You have been frozen by an admin.",
                            net.kyori.adventure.text.format.NamedTextColor.AQUA
                    ));
                }
            }
        }
    }

    /**
     * Toggles the personal voice-chat mute flag for a player.
     * Returns true if the player is now muted.
     */
    public boolean togglePersonalVoiceMute(UUID id) {
        if (voiceChatMuted.contains(id)) {
            voiceChatMuted.remove(id);
            return false;
        }
        voiceChatMuted.add(id);
        return true;
    }

    public boolean isVoiceChatMuted(UUID id) {
        return voiceChatMuted.contains(id);
    }

    /**
     * 27-slot chest GUI with slots 0 to 4 placed directly next
     * to each other in the top row:
     * - Slot 0: Custom Items (CHEST)
     * - Slot 1: Manage Players (PLAYER_HEAD)
     * - Slot 2: Server Audio (JUKEBOX)
     * - Slot 3: Shrine Management (RESPAWN_ANCHOR)
     * - Slot 4: Freeze Barrier (ICE / PACKED_ICE)
     */
    public void openMainGUI(Player admin) {
        Inventory gui = Bukkit.createInventory(
                new HollowGuiHolder(HollowGuiHolder.Type.MAIN),
                27,
                ChatColor.DARK_GRAY + "HollowSMP"
        );

        // Slot 0: Custom Items
        ItemStack itemsIcon = new ItemStack(Material.CHEST);
        ItemMeta itemsMeta = itemsIcon.getItemMeta();
        if (itemsMeta != null) {
            itemsMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Custom Items");
            itemsIcon.setItemMeta(itemsMeta);
        }

        // Slot 1: Manage Players
        ItemStack playersIcon = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta playersMeta = playersIcon.getItemMeta();
        if (playersMeta != null) {
            playersMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Manage Players");
            playersIcon.setItemMeta(playersMeta);
        }

        // Slot 2: Server Audio
        ItemStack musicIcon = new ItemStack(Material.JUKEBOX);
        ItemMeta musicMeta = musicIcon.getItemMeta();
        if (musicMeta != null) {
            musicMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Server Audio");
            musicIcon.setItemMeta(musicMeta);
        }

        // Slot 3: Shrine Management
        ItemStack shrineIcon = new ItemStack(Material.RESPAWN_ANCHOR);
        ItemMeta shrineMeta = shrineIcon.getItemMeta();
        if (shrineMeta != null) {
            shrineMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Shrine Management");
            shrineIcon.setItemMeta(shrineMeta);
        }

        // Slot 4: Freeze Barrier (placed directly next to Slot 3)
        ItemStack freezeIcon = new ItemStack(freezeBarrierActive ? Material.ICE : Material.PACKED_ICE);
        ItemMeta freezeMeta = freezeIcon.getItemMeta();
        if (freezeMeta != null) {
            freezeMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Freeze Barrier");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Freezes all non-OP players in place.");
            lore.add("");
            lore.add(ChatColor.GRAY + "Status: " + (freezeBarrierActive ? ChatColor.GREEN + "ACTIVE" : ChatColor.RED + "DISABLED"));
            lore.add(ChatColor.YELLOW + "Click to toggle!");
            freezeMeta.setLore(lore);
            freezeIcon.setItemMeta(freezeMeta);
        }

        // Placed side-by-side next to each other in row 0:
        gui.setItem(0, itemsIcon);
        gui.setItem(1, playersIcon);
        gui.setItem(2, musicIcon);
        gui.setItem(3, shrineIcon);
        gui.setItem(4, freezeIcon);

        admin.openInventory(gui);
    }

    /**
     * 27-slot chest GUI showing the custom plugin items
     * side-by-side in the top row. Clicking one gives it to the admin.
     */
    public void openItemsGUI(Player admin) {
        Inventory gui = Bukkit.createInventory(
                new HollowGuiHolder(HollowGuiHolder.Type.ITEMS),
                27,
                ChatColor.DARK_GRAY + "HollowSMP Items"
        );

        gui.setItem(0, plugin.getTokenItems().createRevivalToken());
        gui.setItem(1, plugin.getTokenItems().createHeartContainer());
        gui.setItem(2, plugin.getTokenItems().createShrineCore());

        admin.openInventory(gui);
    }

    /**
     * 54-slot chest GUI listing every online player's head.
     */
    public void openPlayersListGUI(Player admin) {
        Inventory gui = Bukkit.createInventory(
                new HollowGuiHolder(HollowGuiHolder.Type.PLAYERS),
                54,
                ChatColor.DARK_GRAY + "HollowSMP Players"
        );

        int slot = 0;
        for (Player online : Bukkit.getOnlinePlayers()) {
            if (slot >= 54) {
                break;
            }

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = head.getItemMeta();
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(online);
                skullMeta.setDisplayName(ChatColor.YELLOW + online.getName());
                head.setItemMeta(skullMeta);
            }

            gui.setItem(slot++, head);
        }

        admin.openInventory(gui);
    }

    /**
     * Called from onDisable().
     */
    public void shutdown() {
        freezeBarrierActive = false;
        voiceChatMuted.clear();
    }
}
