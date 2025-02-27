package pw.rxj.iron_quarry.types;

import net.minecraft.text.Text;
import net.minecraft.util.TranslatableOption;
import net.minecraft.util.math.MathHelper;

import java.util.Arrays;
import java.util.Comparator;

public enum AbsAlignment implements TranslatableOption {
    OFF(0),
    TOP_LEFT(1),
    TOP_RIGHT(2),
    BOTTOM_LEFT(3),
    BOTTOM_RIGHT(4);

    private static final AbsAlignment[] VALUES = Arrays.stream(values()).sorted(Comparator.comparingInt(AbsAlignment::getId)).toArray(AbsAlignment[]::new);

    private final int id;

    private AbsAlignment(int id) {
        this.id = id;
    }

    public static AbsAlignment byId(int id) {
        return VALUES[MathHelper.floorMod(id, VALUES.length)];
    }
    public static AbsAlignment byTranslationKey(String translationKey) {
        return Arrays.stream(VALUES).filter(absAlignment -> absAlignment.getTranslationKey().equals(translationKey)).findFirst().orElse(AbsAlignment.BOTTOM_LEFT);
    }

    @Override
    public int getId() {
        return this.id;
    }
    @Override
    public String getTranslationKey() {
        return this.toString().toLowerCase();
    }
    @Override
    public Text getText() {
        return Text.literal("Not yet implemented.");
    }
}
