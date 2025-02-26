package pw.rxj.iron_quarry.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class ComplexOption {
    private ComplexOption() { }


    public static SimpleOption<String> rotatingOptionFrom(String key, boolean autoTooltip, Class<? extends Enum<?>> clazz, String defaultValue, Consumer<String> write) {
        return rotatingOptionFrom(
                key,
                autoTooltip ? SimpleOption.constantTooltip(ReadableString.translatable(key + ".tooltip")) : SimpleOption.emptyTooltip(),
                clazz.isEnum() ? Arrays.stream(clazz.getEnumConstants()).map(anEnum -> anEnum.toString().toLowerCase()).toList() : List.of(),
                defaultValue,
                write
        );
    }
    public static SimpleOption<String> rotatingOptionFrom(String key, SimpleOption.TooltipFactoryGetter<String> tooltipFactory, List<String> optionList, String defaultValue, Consumer<String> write) {
        return new SimpleOption<>(
                key,
                tooltipFactory,
                (optionText, value) -> Text.translatable(key + "." + value),
                new SimpleOption.LazyCyclingCallbacks<>(() -> optionList, Optional::of, Codec.STRING),
                defaultValue,
                write
        );
    }

    public static SimpleOption<Integer> sliderOptionFrom(String key, ValueFormatter<Integer> valueDivider, boolean autoTooltip, int min, int max, int defaultValue, Consumer<Integer> write) {
        return sliderOptionFrom(
                key,
                valueDivider,
                autoTooltip ? SimpleOption.constantTooltip(ReadableString.translatable(key + ".tooltip")) : SimpleOption.emptyTooltip(),
                min, max,
                defaultValue,
                write
        );
    }
    public static SimpleOption<Integer> sliderOptionFrom(String key, ValueFormatter<Integer> valueDivider, SimpleOption.TooltipFactoryGetter<Integer> tooltipFactory, int min, int max, int defaultValue, Consumer<Integer> write) {
        return new SimpleOption<>(
                key,
                tooltipFactory,
                valueDivider.apply(key),
                new SimpleOption.ValidatingIntSliderCallbacks(min, max),
                Codec.intRange(min, max),
                defaultValue,
                write
        );
    }

    public static <T> SimpleOption.TooltipFactoryGetter<T> emptyTooltip() {
        return (client) -> (value) -> ImmutableList.of();
    }

    public static <T extends Number> ValueFormatter<T> valueDivider(String formatter, double divider) {
        return (key -> (optionText, value) -> {
            Object castValue;

            try {
                castValue = String.format(formatter, value.doubleValue() / divider);
            } catch (IllegalFormatException ignored) {
                castValue = "NaN";
            }

            return ReadableString.translatable(key, castValue);
        });
    }

    public interface ValueFormatter<T extends Number> extends Function<String, SimpleOption.ValueTextGetter<T>> { }
}
