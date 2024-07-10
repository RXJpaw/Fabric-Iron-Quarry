package pw.rxj.iron_quarry.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;

public class ItemEntry<T extends Item> implements ItemConvertible {
    private T item;
    private Identifier id;

    protected ItemEntry() {}
    public static <T extends Item> ItemEntry<T> build() {
        return new ItemEntry<>();
    }

    public ItemEntry<T> withId(String namespace, String path) {
        this.id = Identifier.of(namespace, path);

        return this;
    }
    public ItemEntry<T> withItem(T item) {
        this.item = item;

        return this;
    }

    @Override
    public T asItem() {
        return item;
    }
    public T getItem() { return item; }
    public Identifier getId() {
        return id;
    }

}
