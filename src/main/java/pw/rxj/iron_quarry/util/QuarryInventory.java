package pw.rxj.iron_quarry.util;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import pw.rxj.iron_quarry.blockentity.QuarryBlockEntity;
import pw.rxj.iron_quarry.item.AugmentItem;
import pw.rxj.iron_quarry.item.BlueprintItem;
import pw.rxj.iron_quarry.item.DrillItem;
import team.reborn.energy.api.EnergyStorage;

public class QuarryInventory {
    private static class ReferencedQuarryInventory extends ComplexInventory {
        private final @Nullable QuarryBlockEntity ref;

        private ReferencedQuarryInventory(int size, @Nullable QuarryBlockEntity ref) {
            super(size);

            this.ref = ref;
        }

        public @Nullable QuarryBlockEntity ref() {
            return this.ref;
        }

        @Override
        public void markDirty() {
            if(ref == null) return;

            this.ref.markDirty();
        }
    }


    public static class Output extends ReferencedQuarryInventory {
        private Output(@Nullable QuarryBlockEntity ref) {
            super(18, ref);
        }
        public static Output noRef() {
            return new Output(null);
        }
        public static Output ofRef(@Nullable QuarryBlockEntity ref) {
            return new Output(ref);
        }
    }
    public static class BatteryInput extends ReferencedQuarryInventory {
        private BatteryInput(@Nullable QuarryBlockEntity ref) {
            super(1, ref);
        }
        public static BatteryInput noRef() {
            return new BatteryInput(null);
        }
        public static BatteryInput ofRef(@Nullable QuarryBlockEntity ref) {
            return new BatteryInput(ref);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            EnergyStorage energyStorage = ContainerItemContext.withConstant(stack).find(EnergyStorage.ITEM);

            return super.canInsert(stack) && energyStorage != null && energyStorage.supportsExtraction();
        }
    }
    public static class MachineUpgrades extends ReferencedQuarryInventory {
        private MachineUpgrades(@Nullable QuarryBlockEntity ref) {
            super(6, ref);
        }
        public static MachineUpgrades noRef() {
            return new MachineUpgrades(null);
        }
        public static MachineUpgrades ofRef(@Nullable QuarryBlockEntity ref) {
            return new MachineUpgrades(ref);
        }


        @Override
        public int getMaxCountPerStack() {
            return 1;
        }
        @Override
        public boolean canInsert(ItemStack stack) {
            return super.canInsert(stack) && stack.getItem() instanceof AugmentItem;
        }
    }
    public static class Blueprint extends ReferencedQuarryInventory {
        private Blueprint(@Nullable QuarryBlockEntity ref) {
            super(1, ref);
        }
        public static Blueprint noRef() {
            return new Blueprint(null);
        }
        public static Blueprint ofRef(@Nullable QuarryBlockEntity ref) {
            return new Blueprint(ref);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return super.canInsert(stack) && stack.getItem() instanceof BlueprintItem;
        }
    }
    public static class Drill extends ReferencedQuarryInventory {
        private Drill(@Nullable QuarryBlockEntity ref) {
            super(1, ref);
        }
        public static Drill noRef() {
            return new Drill(null);
        }
        public static Drill ofRef(@Nullable QuarryBlockEntity ref) {
            return new Drill(ref);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return super.canInsert(stack) && stack.getItem() instanceof DrillItem;
        }
    }
}
