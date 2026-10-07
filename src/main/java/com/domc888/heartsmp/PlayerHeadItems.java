package com.domc888.heartsmp;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

public final class PlayerHeadItems {

    private PlayerHeadItems() {
    }

    public static ItemStack create(Player player) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta == null) {
            return head;
        }

        PlayerProfile profile = player.getPlayerProfile();
        meta.setPlayerProfile(profile);

        head.setItemMeta(meta);
        return head;
    }
}
