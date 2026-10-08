package com.domc888/heartsmp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

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

    public void openMainGUI(Player admin) {
        Inventory gui = Bukkit.createInventory(
                new HollowGuiHolder(HollowGuiHolder.Type.MAIN), 
                27, 
                ChatColor.DARK_GRAY + "HollowSMP"
        );

        // Slot 0: Custom Items GUI
        ItemStack itemsIcon = new ItemStack(Material.CHEST);
        ItemMeta itemsMeta = itemsIcon.getItemMeta();
        if (itemsMeta != null) {
            itemsMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Custom Items");
            itemsIcon.setItemMeta(itemsMeta);
        }

        // Slot 1: Player Management / Heads
        ItemStack playersIcon = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta playersMeta = playersIcon.getItemMeta();
        if (playersMeta != null) {
            playersMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Manage Players");
            playersIcon.setItemMeta(playersMeta);
        }

        // Slot 2: Jukebox / Audio Controls
        ItemStack musicIcon = new ItemStack(Material.JUKEBOX);
        ItemMeta musicMeta = musicIcon.getItemMeta();
        if (musicMeta != null) {
            musicMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Server Audio");
            musicIcon.setItemMeta(musicMeta);
        }

        // Slot 3: Shrine Settings
        ItemStack shrineIcon = new ItemStack(Material.RESPAWN_ANCHOR);
        ItemMeta shrineMeta = shrineIcon.getItemMeta();
        if (shrineMeta != null) {
            shrineMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Shrine Management");
            shrineIcon.setItemMeta(shrineMeta);
        }

        // Slot 4: Freeze Barrier Button
        ItemStack freezeIcon = new ItemStack(freezeBarrierActive ? Material.ICE : Material.PACKED_ICE);
        ItemMeta freezeMeta = freezeIcon.getItemMeta();
        if (freezeMeta != null) {
            freezeMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Freeze Barrier");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Freezes all non-OP players.");
            lore.add("");
            lore.add(ChatColor.GRAY + "Status: " + (freezeBarrierActive ? ChatColor.GREEN + "ACTIVE" : ChatColor.RED + "DISABLED"));
            lore.add(ChatColor.YELLOW + "Click to toggle!");
            freezeMeta.setLore(lore);
            freezeIcon.setItemMeta(freezeMeta);
        }

        gui.setItem(0, itemsIcon);
        gui.setItem(1, playersIcon);
        gui.setItem(2, musicIcon);
        gui.setItem(3, shrineIcon);
        gui.setItem(4, freezeIcon);

        admin.openInventory(gui);
    }

    public void openItemsGUI(Player admin) {
        Inventory gui = Bukkit.createInventory(
                new HollowGuiHolder(HollowGuiHolder.Type.ITEMS), 
                27, 
                ChatColor.DARK_GRAY + "HollowSMP Items"
        );

        // Place custom items consecutively starting at Slot 0
        gui.setItem(0, TokenItems.createRevivalToken());
        gui.setItem(1, TokenItems.createHeartContainer());
        gui.setItem(2, TokenItems.createShrineCore());

        // Slot 26: Return Arrow
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.RED + "Back to Main Menu");
            back.setItemMeta(backMeta);
        }
        gui.setItem(26, back);

        admin.openInventory(gui);
    }

    public void openPlayerControlGUI(Player admin, Player target) {
        Inventory gui = Bukkit.createInventory(
                new HollowGuiHolder(HollowGuiHolder.Type.PLAYER_CONTROL, target.getUniqueId()), 
                27, 
                ChatColor.DARK_GRAY + "Control: " + target.getName()
        );

        // Slot 0: Revive / Totem
        ItemStack revive = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta rMeta = revive.getItemMeta();
        if (rMeta != null) {
            rMeta.setDisplayName(ChatColor.GREEN + "Grant Life");
            revive.setItemMeta(rMeta);
        }

        // Slot 1: Take Heart
        ItemStack takeHeart = new ItemStack(Material.GOLDEN_CARROT);
        ItemMeta tMeta = takeHeart.getItemMeta();
        if (tMeta != null) {
            tMeta.setDisplayName(ChatColor.RED + "Revoke Life");
            takeHeart.setItemMeta(tMeta);
        }

        // Slot 2: Invsee
        ItemStack invsee = new ItemStack(Material.CHESTPLATE);
        ItemMeta iMeta = invsee.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName(ChatColor.YELLOW + "Inspect Inventory");
            invsee.setItemMeta(iMeta);
        }

        // Slot 3: Enderchest
        ItemStack ender = new ItemStack(Material.ENDER_CHEST);
        ItemMeta eMeta = ender.getItemMeta();
        if (eMeta != null) {
            eMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Inspect Enderchest");
            ender.setItemMeta(eMeta);
        }

        // Slot 4: Player Head Info
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta hMeta = (SkullMeta) head.getItemMeta();
        if (hMeta != null) {
            hMeta.setOwningPlayer(target);
            hMeta.setDisplayName(ChatColor.AQUA + target.getName());
            head.setItemMeta(hMeta);
        }

        gui.setItem(0, revive);
        gui.setItem(1, takeHeart);
        gui.setItem(2, invsee);
        gui.setItem(3, ender);
        gui.setItem(4, head);

        admin.openInventory(gui);
    }
}
