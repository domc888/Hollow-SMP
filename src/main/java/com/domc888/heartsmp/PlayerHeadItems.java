package com.domc888.heartsmp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

import java.util.UUID;

public final class PlayerHeadItems {

    private PlayerHeadItems() {
    }

    public static ItemStack create(UUID playerId, String playerName) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta == null) {
            return head;
        }

        PlayerProfile profile = Bukkit.createPlayerProfile(playerId, playerName);
        meta.setPlayerProfile(profile);

        head.setItemMeta(meta);
        return head;
    }
}
