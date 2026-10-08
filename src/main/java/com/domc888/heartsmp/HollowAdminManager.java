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
    private boolean freezeBarrierActive = false;
    private final Set<UUID> voiceMutedPlayers = new HashSet<>();

    public HollowAdminManager(HeartSMP plugin) {
        this.plugin = plugin;
    }

    public boolean isFreezeBarrierActive() {
        return freezeBarrierActive;
    }

    public void setFreezeBarrierActive(boolean active) {
        this.freezeBarrierActive = active;
    }

    public void togglePersonalVoiceMute(UUID uuid) {
        if (voiceMutedPlayers.contains(uuid)) {
            voiceMutedPlayers.remove(uuid);
        } else {
            voiceMutedPlayers.add(uuid);
        }
    }

    public boolean isVoiceMuted(UUID uuid) {
        return voiceMutedPlayers.contains(uuid);
    }

    public void shutdown() {
        voiceMutedPlayers.clear();
        freezeBarrierActive = false;
    }

    public void openMainGUI(Player admin) {
        Inventory gui = Bukkit.createInventory(
                new HollowGuiHolder(HollowGuiHolder.Type.MAIN), 
                27, 
                ChatColor.DARK_GRAY + "HollowSMP"
        );

        ItemStack itemsIcon = new ItemStack(Material.CHEST);
        ItemMeta itemsMeta = itemsIcon.getItemMeta();
        if (itemsMeta != null) {
            itemsMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Custom Items");
            itemsIcon.setItemMeta(itemsMeta);
        }

        ItemStack playersIcon = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta playersMeta = playersIcon.getItemMeta();
        if (playersMeta != null) {
            playersMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Manage Players");
            playersIcon.setItemMeta(playersMeta);
        }

        ItemStack musicIcon = new ItemStack(Material.JUKEBOX);
        ItemMeta musicMeta = musicIcon.getItemMeta();
        if (musicMeta != null) {
            musicMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Server Audio");
            musicIcon.setItemMeta(musicMeta);
        }

        ItemStack shrineIcon = new ItemStack(Material.RESPAWN_ANCHOR);
        ItemMeta shrineMeta = shrineIcon.getItemMeta();
        if (shrineMeta != null) {
            shrineMeta.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + "Shrine Management");
            shrineIcon.setItemMeta(shrineMeta);
        }

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

        gui.setItem(0, plugin.getTokenItems().createRevivalToken());
        gui.setItem(1, plugin.getTokenItems().createHeartContainer());
        gui.setItem(2, plugin.getTokenItems().createShrineCore());

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

        ItemStack revive = new ItemStack(Material.TOTEM_OF_UNDYING);
        ItemMeta rMeta = revive.getItemMeta();
        if (rMeta != null) {
            rMeta.setDisplayName(ChatColor.GREEN + "Grant Life");
            revive.setItemMeta(rMeta);
        }

        ItemStack takeHeart = new ItemStack(Material.GOLDEN_CARROT);
        ItemMeta tMeta = takeHeart.getItemMeta();
        if (tMeta != null) {
            tMeta.setDisplayName(ChatColor.RED + "Revoke Life");
            takeHeart.setItemMeta(tMeta);
        }

        ItemStack invsee = new ItemStack(Material.NETHERITE_CHESTPLATE);
        ItemMeta iMeta = invsee.getItemMeta();
        if (iMeta != null) {
            iMeta.setDisplayName(ChatColor.YELLOW + "Inspect Inventory");
            invsee.setItemMeta(iMeta);
        }

        ItemStack ender = new ItemStack(Material.ENDER_CHEST);
        ItemMeta eMeta = ender.getItemMeta();
        if (eMeta != null) {
            eMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Inspect Enderchest");
            ender.setItemMeta(eMeta);
        }

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
