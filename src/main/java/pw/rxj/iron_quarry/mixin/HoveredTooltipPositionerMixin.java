package pw.rxj.iron_quarry.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import org.joml.Vector2ic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.rxj.iron_quarry.interfaces.IManipulateTooltipPositioner;

@Mixin(HoveredTooltipPositioner.class)
public abstract class HoveredTooltipPositionerMixin {
    @Inject(method = "getPosition", at = @At(value = "HEAD"), cancellable = true)
    private void getPosition(Screen screen, int x, int y, int width, int height, CallbackInfoReturnable<Vector2ic> cir) {
        if(screen instanceof IManipulateTooltipPositioner tooltipPositioner) {
            cir.setReturnValue(tooltipPositioner.getTooltipPosition(x, y, width, height));
        }
    }
}
