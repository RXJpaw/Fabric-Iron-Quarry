package pw.rxj.iron_quarry.interfaces;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;

public interface IHandledCrafting {
    ItemStack getCraftingOutput(ShapedRecipe handler, RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager);

    default ItemStack getCraftingOutputPreview(CraftingRecipe recipe) {
        return null;
    }
}
