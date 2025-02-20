package pw.rxj.iron_quarry.util;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;
import pw.rxj.iron_quarry.Global;
import pw.rxj.iron_quarry.render.Cuboid;
import pw.rxj.iron_quarry.types.DynamicText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadableString {
    public static Text ERROR = Text.of("<error>");

    public static DecimalFormat getDecimalFormatter(String pattern) {
        return new DecimalFormat(pattern, DecimalFormatSymbols.getInstance(Locale.ROOT));
    }
    public static <T extends Number> String intFrom(T number) {
        DecimalFormat formatter = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.ROOT));

        return formatter.format(number);
    }
    public static <T extends Number> String cIntFrom(T number) {
        NumberFormat formatter = NumberFormat.getCompactNumberInstance(Locale.ROOT, NumberFormat.Style.SHORT);
        formatter.setMinimumFractionDigits(1);

        return formatter.format(number).replace("K", "k");
    }

    public static String minPrecisionFloat(float number) {
        if(number == (int) number)
            return String.format("%d", (int) number);
        else
            return String.format("%s", number);
    }
    public static String minPrecisionDouble(double number) {
        if(number == (long) number)
            return String.format("%d", (long) number);
        else
            return String.format("%s", number);
    }

    public static MutableText space() {
        return Text.literal(" ");
    }

    public static Optional<String> from(BlockPos blockPos) {
        if(blockPos == null) return Optional.empty();

        return String.format("%s, %s, %s", blockPos.getX(), blockPos.getY(), blockPos.getZ()).describeConstable();
    }
    public static Optional<Text> textFrom(BlockPos blockPos) {
        return from(blockPos).map(Text::of);
    }

    public static Optional<String> from(Identifier identifier) {
        if(identifier == null) return Optional.empty();
        String path = identifier.getPath();

        StringBuilder readablePath = new StringBuilder();
        for (String part : path.split("[-_]")) {
            if(!readablePath.isEmpty()) readablePath.append(" ");
            readablePath.append(ZUtil.capitalizeFirstLetter(part));
        }

        return readablePath.toString().describeConstable();
    }
    public static Optional<Text> textFrom(Identifier identifier) {
        return from(identifier).map(Text::of);
    }

    public static Optional<String> from(Cuboid cuboid) {
        if(cuboid == null) return Optional.empty();

        Vec3d abs = cuboid.fullblock().abs();

        return String.format("%s × %s × %s", (int) abs.x, (int) abs.y, (int) abs.z).describeConstable();
    }
    public static Optional<Text> textFrom(Cuboid cuboid) {
        return from(cuboid).map(Text::of);
    }

    public static Text textFrom(@NotNull KeyBinding keyBinding) {
        InputUtil.Type type = keyBinding.boundKey.getCategory();
        int code = keyBinding.boundKey.getCode();

        if(type.equals(InputUtil.Type.MOUSE)) {
            return switch (code) {
                case 0 -> Text.translatable("short_key.mouse.c.0");
                case 1 -> Text.translatable("short_key.mouse.c.1");
                case 2 -> Text.translatable("short_key.mouse.c.2");

                default -> keyBinding.getBoundKeyLocalizedText();
            };
        } else {
            return keyBinding.getBoundKeyLocalizedText();
        }
    }

    public static MutableText toTimeAgo(long timestamp) {
        long seconds = (System.currentTimeMillis() - timestamp) / 1000;

        if(seconds < 10) {
            return Text.translatable("time.unit.c.a_moment");
        } else if (seconds < 60) {
            return Text.translatable("time.unit.c.second.plural", seconds);
        } else if (seconds < 120) {
            return Text.translatable("time.unit.c.minute.singular", seconds / 60);
        } else if (seconds < 3_600) {
            return Text.translatable("time.unit.c.minute.plural", seconds / 60);
        } else if (seconds < 7_200) {
            return Text.translatable("time.unit.c.hour.singular", seconds / 3_600);
        } else if (seconds < 86_400) {
            return Text.translatable("time.unit.c.hour.plural", seconds / 3_600);
        } else if (seconds < 172_800) {
            return Text.translatable("time.unit.c.day.singular", seconds / 86_400);
        } else {
            return Text.translatable("time.unit.c.day.plural", seconds / 86_400);
        }
    }

    public static MutableText translatable(String key, Object... args) {
        MutableText translation = Text.translatable(key, args);
        MutableText parsed = Text.empty();

        String string = translation.getString();
        Pattern pattern = Pattern.compile("(.*?)§\\{(.*?) (.*?)\\}");
        Matcher matcher = pattern.matcher(string);

        int offset = 0;
        int textOffset = 0;
        while (matcher.find()) {
            offset = matcher.end();
            String text = matcher.group(3);
            parsed.append(matcher.group(1));

            Object modifier = Global.get(matcher.group(2));
            if(modifier == null) {
                textOffset += text.length();
                parsed.append(text);
                continue;
            }

            if(modifier instanceof Integer rgb) {
                Style style = Style.EMPTY.withColor(rgb);

                parsed.append(Text.literal(text).setStyle(style));
            } else if(modifier instanceof DynamicText dynamicText) {
                MutableText dynamicResult = dynamicText.getText(Text.literal(text), textOffset);

                parsed.append(dynamicResult);
            }

            textOffset += text.length();
        }

        return parsed.append(string.substring(offset));
    }
}
