package pw.rxj.iron_quarry.interfaces;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import pw.rxj.iron_quarry.recipe.HandledCraftingRecipe;

public interface IHandledCrafting {
    ItemStack getCraftingOutput(HandledCraftingRecipe handler, RecipeInputInventory recipeInputInventory, DynamicRegistryManager dynamicRegistryManager);

    default ItemStack getCraftingOutputPreview(CraftingRecipe recipe) {
        return null;
    }
}
