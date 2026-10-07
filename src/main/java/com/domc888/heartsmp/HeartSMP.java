package com.domc888.heartsmp;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class HeartSMP extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        int max = Math.max(
                1,
                getConfig().getInt("max-lives", 3)
        );

        int start = Math.min(
                max,
                Math.max(
                        1,
                        getConfig().getInt("starting-lives", max)
                )
        );

        LivesManager lives = new LivesManager(
                this,
                max,
                start
        );

        TokenItems tokens = new TokenItems(this);

        getServer().getPluginManager().registerEvents(
                new LifeListener(
                        this,
                        lives,
                        tokens
                ),
                this
        );

        HeartCommand heartCommand = new HeartCommand(
                lives,
                tokens
        );

        PluginCommand heart = Objects.requireNonNull(
                getCommand("heartsmp")
        );

        heart.setExecutor(heartCommand);
        heart.setTabCompleter(heartCommand);

        LivesCommand livesCommand = new LivesCommand(lives);

        PluginCommand livesCommandPlugin = Objects.requireNonNull(
                getCommand("lives")
        );

        livesCommandPlugin.setExecutor(livesCommand);
        livesCommandPlugin.setTabCompleter(livesCommand);

        /*
         * Revival Shrine
         */
        NamespacedKey shrineKey = new NamespacedKey(
                this,
                "revival_shrine"
        );

        ShrineManager shrineManager = new ShrineManager(this);

        getServer().getPluginManager().registerEvents(
                new ShrineListener(
                        this,
                        lives,
                        shrineManager,
                        shrineKey
                ),
                this
        );

        registerShrineRecipe(
                shrineKey,
                tokens
        );
    }

    private void registerShrineRecipe(
            NamespacedKey shrineKey,
            TokenItems tokens
    ) {
        ItemStack result = ShrineItems.create(shrineKey);

        ShapedRecipe recipe = new ShapedRecipe(
                new NamespacedKey(
                        this,
                        "revival_shrine"
                ),
                result
        );

        /*
         * Revival Shrine recipe:
         *
         * D G D
         * G T G
         * D R D
         *
         * D = Diamond Block
         * G = Gold Block
         * T = Revival Token
         * R = Totem of Undying
         */

        recipe.shape(
                "DGD",
                "GTG",
                "DRD"
        );

        recipe.setIngredient(
                'D',
                Material.DIAMOND_BLOCK
        );

        recipe.setIngredient(
                'G',
                Material.GOLD_BLOCK
        );

        recipe.setIngredient(
                'R',
                Material.TOTEM_OF_UNDYING
        );

        /*
         * The token can be any of the 25 valid
         * HeartSMP revival tokens.
         *
         * Every token is physically a Nether Star
         * and is identified by the HeartSMP PDC.
         */
        List<ItemStack> tokenChoices = new ArrayList<>();

        for (RevivalToken token : RevivalToken.values()) {
            tokenChoices.add(
                    tokens.create(token)
            );
        }

        recipe.setIngredient(
                'T',
                new RecipeChoice.ExactChoice(
                        tokenChoices
                )
        );

        getServer().addRecipe(recipe);
    }
}
