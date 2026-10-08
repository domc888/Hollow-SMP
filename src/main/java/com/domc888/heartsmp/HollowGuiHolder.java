package com.domc888.heartsmp;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

public final class HollowGuiHolder implements InventoryHolder {

    public enum Type {
        MAIN,
        ITEMS,
        PLAYERS,
        PLAYER_CONTROL,
        PLAYER_INVENTORY,
        PLAYER_ENDER_CHEST
    }

    private final Type type;
    private final UUID target;

    private Inventory inventory;

    public HollowGuiHolder(Type type) {
        this(type, null);
    }

    public HollowGuiHolder(Type type, UUID target) {
        this.type = type;
        this.target = target;
    }

    public Type getType() {
        return type;
    }

    public UUID getTarget() {
        return target;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
