package com.domc888.heartsmp;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

public class HollowAdminListener implements Listener {

    private final HeartSMP plugin;

    public HollowAdminListener(HeartSMP plugin) {
        this.plugin = plugin;
    }

    public void openMain(Player player) {
        plugin.getAdminManager().openMainGUI(player);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player admin)) return;
        if (!(event.getInventory().getHolder() instanceof HollowGuiHolder holder)) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType().isAir()) return;

        int slot = event.getRawSlot();

        switch (holder.getType()) {
            case MAIN -> {
                switch (slot) {
                    case 0 -> plugin.getAdminManager().openItemsGUI(admin);
                    case 1 -> admin.sendMessage(ChatColor.YELLOW + "Player list selection opened.");
                    case 2 -> admin.sendMessage(ChatColor.GREEN + "Audio controls opened.");
                    case 3 -> admin.sendMessage(ChatColor.AQUA + "Shrine settings opened.");
                    case 4 -> {
                        boolean currentState = plugin.getAdminManager().isFreezeBarrierActive();
                        plugin.getAdminManager().setFreezeBarrierActive(!currentState);
                        String statusMsg = !currentState 
                                ? ChatColor.GREEN + "Freeze Barrier is now ACTIVE. All non-OP players are frozen." 
                                : ChatColor.RED + "Freeze Barrier is now DISABLED.";
                        admin.sendMessage(statusMsg);
                        plugin.getAdminManager().openMainGUI(admin);
                    }
                }
            }
            case ITEMS -> {
                if (slot == 26) {
                    plugin.getAdminManager().openMainGUI(admin);
                    return;
                }
                if (slot >= 0 && slot <= 2) {
                    admin.getInventory().addItem(clicked.clone());
                    admin.sendMessage(ChatColor.GREEN + "Gave " + clicked.getItemMeta().getDisplayName());
                }
            }
            case PLAYER_CONTROL -> {
                // Individual action clicks
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (plugin.getAdminManager().isFreezeBarrierActive() && !player.isOp()) {
            if (event.getFrom().getX() != event.getTo().getX() || 
                event.getFrom().getY() != event.getTo().getY() || 
                event.getFrom().getZ() != event.getTo().getZ()) {
                event.setTo(event.getFrom());
            }
        }
    }
}
