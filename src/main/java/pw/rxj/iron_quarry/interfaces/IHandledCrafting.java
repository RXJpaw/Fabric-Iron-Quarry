package pw.rxj.iron_quarry.interfaces;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import pw.rxj.iron_quarry.recipe.HandledCraftingRecipe;

public interface IHandledCrafting {
    ItemStack getCraftingOutput(HandledCraftingRecipe handler, CraftingInventory craftingInventory, DynamicRegistryManager dynamicRegistryManager);

    default ItemStack getCraftingOutputPreview(CraftingRecipe recipe) {
        return null;
    }
}
