package pw.rxj.iron_quarry.types;

import pw.rxj.iron_quarry.resource.config.server.AugmentStatsConfig;
import pw.rxj.iron_quarry.util.MathUtil;

import java.util.List;

public enum AugmentType {
    EMPTY(0, "empty", "energy", 1, 0.0F, 0.0F, false),
    SPEED(1, "speed", "energy", 1000, 1194.1878449451583F, 0.5F, false),
    FORTUNE(2, "fortune", "energy", 1500, 135.73132372061485F, 1.5F, false),
    SILK_TOUCH(3, "silk_touch", "energy", 0, 0.0F, 180.0F, false),
    CHEST_LOOTING(4, "chest_looting", "time", 0, 0.0F, 0.0F, false);

    private final int id;
    private final String name;
    private final String drawbackKey;
    private int baseAmount;
    private float multiplier;
    private float inefficiency;
    private boolean disabled;

    private static final List<AugmentType> ALL = List.of(EMPTY, SPEED, FORTUNE, SILK_TOUCH);

    private AugmentType(int id, String name, String drawbackKey, int baseAmount, float multiplier, float inefficiency, boolean disabled) {
        this.id = id;
        this.name = name;
        this.drawbackKey = drawbackKey;
        this.baseAmount = baseAmount;
        this.multiplier = multiplier;
        this.inefficiency = inefficiency;
        this.disabled = disabled;
    }
    public void override(AugmentStatsConfig.Entry augmentStats) {
        this.disabled = augmentStats.disabled;
        this.baseAmount = augmentStats.baseAmount;
        this.multiplier = augmentStats.multiplier;
        this.inefficiency = augmentStats.inefficiency;
    }

    public String getName(){
        return this.name;
    }
    public String getDrawbackKey() {
        return drawbackKey;
    }
    public int getId(){
        return this.id;
    }
    public boolean isDisabled() {
        return this.disabled;
    }
    public int getBaseAmount() { return this.baseAmount; }
    public float getMultiplier() { return this.multiplier; }
    public float getInefficiency() { return this.inefficiency; }

    public boolean isEmpty() { return this.equals(AugmentType.EMPTY); }
    public boolean isPresent() { return !this.isEmpty(); }

    /**
     *  C(U) = B * (1 + 0.25 * U * (U + 1))
     */
    public int getCapacity(int capacityUpgrades) {
        return Math.round(this.getBaseAmount() * (1 + 0.56F * capacityUpgrades * (capacityUpgrades + 1)));
    }

    /**
     *  R(A) = log_2(log_2(2 + A / 10 000)) * M
     */
    public float multiply(int amount) {
        if(this.multiplier == 0) return 0.0F;

        return (float) (MathUtil.log2(MathUtil.log2(2 + (double) amount / 10_000)) * (double) this.multiplier);
    }
    public float ineff(float amount) {
        if(this.multiplier == 0) return this.inefficiency;

        return amount * this.inefficiency;
    }

    public static AugmentType from(String name){
        for (AugmentType augmentType : ALL) {
            if(augmentType.getName().equalsIgnoreCase(name)){
                return augmentType;
            }
        }

        return null;
    }
    public static AugmentType from(int id){
        return ALL.get(id);
    }
}
