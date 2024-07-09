package pw.rxj.iron_quarry.resource.config.server;

import lombok.EqualsAndHashCode;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;
import pw.rxj.iron_quarry.resource.config.AbstractInnerConfigHandler;
import pw.rxj.iron_quarry.resource.config.ConfigHandler;

import java.util.Optional;

@EqualsAndHashCode
public class SilkTouchAugmentConfig {
    public final String villagerProfession = "minecraft:toolsmith";
    public final byte villagerLevel = 3;
    public final boolean wanderingVillager = true;
    public final byte wanderingVillagerLevel = 2;

    public static class Handler extends AbstractInnerConfigHandler {
        private Handler(ConfigHandler handler) {
            super(handler);
        }
        public static Handler of(ConfigHandler handler) {
            return new Handler(handler);
        }

        public Optional<VillagerProfession> getVillagerProfession() {
            Identifier villagerProfession = Identifier.tryParse(this.server.silkTouchAugment.villagerProfession);

            return Registry.VILLAGER_PROFESSION.getOrEmpty(villagerProfession);
        }
        public byte getVillagerLevel() {
            return this.server.silkTouchAugment.villagerLevel;
        }
        public boolean isWanderingVillagerEnabled() {
            return this.server.silkTouchAugment.wanderingVillager;
        }
        public byte getWanderingVillagerLevel() {
            return this.server.silkTouchAugment.wanderingVillagerLevel;
        }
    }
}
