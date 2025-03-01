package pw.rxj.iron_quarry.recipe;

import com.google.gson.JsonObject;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.SmithingTransformRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import pw.rxj.iron_quarry.interfaces.IHandledSmithing;
import pw.rxj.iron_quarry.util.ZUtil;

public class HandledSmithingRecipe extends SmithingTransformRecipe {
    public static final RecipeSerializer<HandledSmithingRecipe> SERIALIZER = new RecipeSerializer<>() {
        private ItemStack appendSmithingPreview(Ingredient base, Ingredient addition, SmithingRecipe recipe) {
            ItemStack output = recipe.getOutput(DynamicRegistryManager.EMPTY);

            if(ZUtil.getBlockOrItem(output) instanceof IHandledSmithing smithing) {
                ItemStack outputPreview = smithing.getSmithingOutputPreview(base, addition, output);
                if(outputPreview != null) return outputPreview;
            }

            return output;
        }

        @Override
        public HandledSmithingRecipe read(Identifier recipeId, JsonObject json) {
            SmithingRecipe recipe = RecipeSerializer.SMITHING_TRANSFORM.read(recipeId, json);

            Ingredient base = Ingredient.fromJson(JsonHelper.getObject(json, "base"));
            Ingredient addition = Ingredient.fromJson(JsonHelper.getObject(json, "addition"));
            ItemStack output = this.appendSmithingPreview(base, addition, recipe);

            return new HandledSmithingRecipe(recipeId, base, addition, output);
        }

        @Override
        public HandledSmithingRecipe read(Identifier recipeId, PacketByteBuf buffer) {
            SmithingRecipe recipe = RecipeSerializer.SMITHING_TRANSFORM.read(recipeId, buffer);

            Ingredient base = Ingredient.fromPacket(buffer);
            Ingredient addition = Ingredient.fromPacket(buffer);
            ItemStack output = this.appendSmithingPreview(base, addition, recipe);

            return new HandledSmithingRecipe(recipeId, base, addition, output);
        }

        @Override
        public void write(PacketByteBuf buffer, HandledSmithingRecipe recipe) {
            RecipeSerializer.SMITHING_TRANSFORM.write(buffer, recipe);
        }
    };

    public HandledSmithingRecipe(Identifier id, Ingredient base, Ingredient addition, ItemStack result) {
        super(id, Ingredient.EMPTY, base, addition, result);
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack output = getOutput(dynamicRegistryManager).copy();

        if(ZUtil.getBlockOrItem(output) instanceof IHandledSmithing handledSmithing) {
            return handledSmithing.getSmithingOutput(this, inventory, dynamicRegistryManager);
        }

        return super.craft(inventory, dynamicRegistryManager);
    }
}
