package com.domc888.heartsmp;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum RevivalToken {
    EMERALD("Emerald Revival Token", Material.EMERALD),
    DIAMOND("Diamond Revival Token", Material.DIAMOND),
    GOLD("Gold Revival Token", Material.GOLD_INGOT),
    IRON("Iron Revival Token", Material.IRON_INGOT),
    COPPER("Copper Revival Token", Material.COPPER_INGOT),
    NETHERITE("Netherite Revival Token", Material.NETHERITE_INGOT),
    AMETHYST("Amethyst Revival Token", Material.AMETHYST_SHARD),
    ECHO("Echo Revival Token", Material.ECHO_SHARD),
    PRISMARINE_SHARD("Prismarine Shard Revival Token", Material.PRISMARINE_SHARD),
    PRISMARINE_CRYSTAL("Prismarine Crystal Revival Token", Material.PRISMARINE_CRYSTALS),
    QUARTZ("Quartz Revival Token", Material.QUARTZ),
    LAPIS("Lapis Revival Token", Material.LAPIS_LAZULI),
    GLOWSTONE("Glowstone Revival Token", Material.GLOWSTONE_DUST),
    BLAZE("Blaze Revival Token", Material.BLAZE_POWDER),
    GHAST("Ghast Revival Token", Material.GHAST_TEAR),
    NAUTILUS("Nautilus Revival Token", Material.NAUTILUS_SHELL),
    HEART_OF_THE_SEA("Heart of the Sea Revival Token", Material.HEART_OF_THE_SEA),
    NETHER_STAR("Nether Star Revival Token", Material.NETHER_STAR),
    PHANTOM("Phantom Revival Token", Material.PHANTOM_MEMBRANE),
    RABBIT("Rabbit Revival Token", Material.RABBIT_FOOT),
    FEATHER("Feather Revival Token", Material.FEATHER),
    SLIME("Slime Revival Token", Material.SLIME_BALL),
    MAGMA("Magma Revival Token", Material.MAGMA_CREAM),
    BREEZE("Breeze Revival Token", Material.BREEZE_ROD),
    DISC_FRAGMENT("Disc Fragment Revival Token", Material.DISC_FRAGMENT_5);

    private final String displayName;
    private final Material material;

    RevivalToken(String displayName, Material material) {
        this.displayName = displayName;
        this.material = material;
    }

    public String id() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String displayName() {
        return displayName;
    }

    public Material material() {
        return material;
    }

    public static RevivalToken byId(String id) {
        if (id == null) {
            return null;
        }
        try {
            return valueOf(id.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static List<String> ids() {
        List<String> out = new ArrayList<>();
        for (RevivalToken token : values()) {
            out.add(token.id());
        }
        return out;
    }
}
