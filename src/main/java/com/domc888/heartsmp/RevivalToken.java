package com.domc888.heartsmp;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum RevivalToken {

    EMERALD("emerald", 1001),
    DIAMOND("diamond", 1002),
    GOLD("gold", 1003),
    IRON("iron", 1004),
    COPPER("copper", 1005),
    NETHERITE("netherite", 1006),
    AMETHYST("amethyst", 1007),
    ECHO("echo", 1008),
    PRISMARINE_SHARD("prismarine_shard", 1009),
    PRISMARINE_CRYSTAL("prismarine_crystal", 1010),
    QUARTZ("quartz", 1011),
    LAPIS("lapis", 1012),
    GLOWSTONE("glowstone", 1013),
    BLAZE("blaze", 1014),
    GHAST("ghast", 1015),
    NAUTILUS("nautilus", 1016),
    HEART_OF_THE_SEA("heart_of_the_sea", 1017),
    NETHER_STAR("nether_star", 1018),
    PHANTOM("phantom", 1019),
    RABBIT("rabbit", 1020),
    FEATHER("feather", 1021),
    SLIME("slime", 1022),
    MAGMA("magma", 1023),
    BREEZE("breeze", 1024),
    DISC_FRAGMENT("disc_fragment", 1025);

    private final String id;
    private final int customModelData;

    RevivalToken(String id, int customModelData) {
        this.id = id;
        this.customModelData = customModelData;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return "Revival Token";
    }

    public Material material() {
        return Material.NETHER_STAR;
    }

    public int customModelData() {
        return customModelData;
    }

    public static RevivalToken byId(String id) {
        if (id == null) {
            return null;
        }

        String normalized = id.toLowerCase(Locale.ROOT);

        for (RevivalToken token : values()) {
            if (token.id.equals(normalized)) {
                return token;
            }
        }

        return null;
    }

    public static List<String> ids() {
        List<String> out = new ArrayList<>();

        for (RevivalToken token : values()) {
            out.add(token.id());
        }

        return out;
    }
}
