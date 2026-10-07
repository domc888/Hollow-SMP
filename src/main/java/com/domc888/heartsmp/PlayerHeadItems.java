package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;

public final class PlayerHeadItems {

    private PlayerHeadItems() {
    }

    public static ItemStack create(Player player) {
        ItemStack head = new ItemStack(
                org.bukkit.Material.PLAYER_HEAD
        );

        SkullMeta meta = (SkullMeta) head.getItemMeta();

        /*
         * Copy the player's actual profile.
         * This makes the dropped head use their skin.
         */
        PlayerProfile profile = player.getPlayerProfile();
        meta.setPlayerProfile(profile);

        /*
         * The head is named after the player.
         */
        meta.displayName(
                Component.text(player.getName())
                        .decoration(TextDecoration.ITALIC, false)
        );

        head.setItemMeta(meta);

        return head;
    }
}
