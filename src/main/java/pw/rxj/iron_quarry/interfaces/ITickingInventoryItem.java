package pw.rxj.iron_quarry.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public interface ITickingInventoryItem {
    void tick(ServerPlayerEntity playerEntity, ServerWorld world, ItemStack stack);
}
