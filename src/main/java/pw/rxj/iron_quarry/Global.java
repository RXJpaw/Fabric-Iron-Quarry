package pw.rxj.iron_quarry;

import org.jetbrains.annotations.Nullable;
import pw.rxj.iron_quarry.types.DynamicText;
import pw.rxj.iron_quarry.util.ZUtil;

import java.awt.*;
import java.lang.reflect.Field;

public class Global {
    public static final int RGB_BENEFIT = Color.HSBtoRGB(ZUtil.normDeg(120), 0.4F, 0.5F);
    public static final int RGB_DRAWBACK = Color.HSBtoRGB(ZUtil.normDeg(0), 0.4F, 0.5F);
    public static final int RGB_DARK_GRAY = Color.HSBtoRGB(ZUtil.normDeg(0), 0.0F, 1.0F/3.0F);
    public static final int RGB_LIGHT_GRAY = Color.HSBtoRGB(ZUtil.normDeg(0), 0.0F, 0.6F);
    public static final int RGB_WEAK_HIGHLIGHT = Color.HSBtoRGB(ZUtil.normDeg(210), 0.4F, 0.5F);
    public static final int RGB_STRONG_HIGHLIGHT = Color.HSBtoRGB(ZUtil.normDeg(210), 0.6F, 0.5F);
    public static final int RGB_DARK_PURPLE = Color.HSBtoRGB(ZUtil.normDeg(300), 0.8F, 0.7F);
    public static final int RGB_WARNING =  Color.HSBtoRGB(ZUtil.normDeg(40), 0.8F, 0.99F);
    public static final int RGB_TAB_TITLE = 0xE1C92F;
    public static final int RGB_MC_NOPERMS = 0xFB5454;
    public static final int RGB_RF_PURPLE = 0x8A53AC;

    public static final DynamicText DYN_RAINBOW = DynamicText.RAINBOW;
    public static final DynamicText DYN_EMERALD = DynamicText.EMERALD;

    public static @Nullable Object get(String name){
        try {
            Field field = Global.class.getField(name);

            return field.get(null);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            return null;
        }
    }
}
