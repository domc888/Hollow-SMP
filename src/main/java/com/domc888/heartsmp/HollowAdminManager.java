package com.domc888.heartsmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class HollowAdminManager {

    private final HeartSMP plugin;
    private boolean freezeBarrierActive = false;

    public HollowAdminManager(HeartSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isFreezeBarrierActive() {
        return freezeBarrierActive;
    }

    public void setFreezeBarrierActive(boolean active) {
        this.freezeBarrierActive = active;
    }

    /**
     * 27-slot chest GUI with slots 0 to 4 placed next to each other in row 0:
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
}
