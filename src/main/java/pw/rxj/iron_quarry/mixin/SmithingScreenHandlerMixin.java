package pw.rxj.iron_quarry.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.LegacySmithingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.rxj.iron_quarry.interfaces.IHandledSmithing;
import pw.rxj.iron_quarry.util.ZUtil;



@SuppressWarnings("removal")
@Mixin(LegacySmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {
    public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Inject(method="onTakeOutput", at = @At(value = "HEAD"), cancellable = true)
    private void onTakeOutput(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        //TESTME: stack is "0 air" when taking output with shift (MC-267545)
        if(ZUtil.getBlockOrItem(this.input.getStack(0)) instanceof IHandledSmithing handledSmithing) {
            boolean result = handledSmithing.handleSmithingTakeOutput(player, this.input, this.output, this.context);
            if(result) ci.cancel();
        }
    }
}
