package com.domc888.heartsmp;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class LifeListener implements Listener {

    private final HeartSMP plugin;
    private final LivesManager lives;
    private final TokenItems tokens;

    public LifeListener(HeartSMP plugin, LivesManager lives, TokenItems tokens) {
        this.plugin = plugin;
        this.lives = lives;
        this.tokens = tokens;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        UUID id = event.getEntity().getUniqueId();
        if (lives.isEliminated(id)) {
            return;
        }
        lives.loseLife(id);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID id = player.getUniqueId();

        if (lives.isEliminated(id)) {
            player.sendMessage(Component.text("You are out of lives and have been eliminated."));
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (player.isOnline()) {
                    lives.applyState(player);
                }
            });
        } else {
            player.sendMessage(Component.text("Lives left: " + lives.getLives(id) + "/" + lives.getMaxLives() + "."));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTask(plugin, () -> {
            if (player.isOnline()) {
                lives.applyState(player);
            }
        });
    }

    @EventHandler
    public void onUse(PlayerInteractEvent event) {
        Action action = event.getAction();
        if (action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (tokens.read(hand) == null) {
            return;
        }

        event.setCancelled(true);

        UUID id = player.getUniqueId();
        if (lives.isEliminated(id)) {
            return;
        }

        int current = lives.getLives(id);
        if (current >= lives.getMaxLives()) {
            player.sendMessage(Component.text("You already have the maximum number of lives."));
            return;
        }

        lives.setLives(id, current + 1);
        hand.setAmount(hand.getAmount() - 1);
        player.getInventory().setItemInMainHand(hand);
        player.sendMessage(Component.text("Revival Token used. Lives: " + lives.getLives(id) + "/" + lives.getMaxLives() + "."));
    }
}
