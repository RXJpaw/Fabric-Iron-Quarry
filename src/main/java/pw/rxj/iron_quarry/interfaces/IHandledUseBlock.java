package pw.rxj.iron_quarry.interfaces;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public interface IHandledUseBlock {
    ActionResult useOnBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult);
}
