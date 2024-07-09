package pw.rxj.iron_quarry.mixin;


import com.google.gson.JsonElement;
import net.fabricmc.api.EnvType;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.rxj.iron_quarry.Main;
import pw.rxj.iron_quarry.types.AugmentType;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/resource/ResourceManager;Lnet/minecraft/util/profiler/Profiler;)V", at = @At(value = "HEAD"))
    private void apply(Map<Identifier, JsonElement> map, ResourceManager resourceManager, Profiler profiler, CallbackInfo ci) {
        Main.CONFIG.read(EnvType.SERVER);
        Main.CONFIG.applyServerChanges();

        if(AugmentType.SPEED.isDisabled()) {
            map.remove(Identifier.of(Main.MOD_ID, "augment_speed_tier1"));
            map.remove(Identifier.of(Main.MOD_ID, "augment_speed_tier2"));
            map.remove(Identifier.of(Main.MOD_ID, "augment_speed_tier3"));
        }

        if(AugmentType.FORTUNE.isDisabled()) {
            map.remove(Identifier.of(Main.MOD_ID, "augment_fortune_tier1"));
            map.remove(Identifier.of(Main.MOD_ID, "augment_fortune_tier2"));
            map.remove(Identifier.of(Main.MOD_ID, "augment_fortune_tier3"));
        }
    }
}
