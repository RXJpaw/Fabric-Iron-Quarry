package pw.rxj.iron_quarry.util;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import pw.rxj.iron_quarry.block.QuarryBlock;

public class BlockEntry<T extends Block, S extends BlockItem> implements ItemConvertible {
    private T block;
    private S blockItem;
    private Identifier id;

    protected BlockEntry() {}
    public static <T extends Block, S extends BlockItem> BlockEntry<T, S> build() {
        return new BlockEntry<>();
    }
    public static <T extends Block> BlockEntry<T, BlockItem> buildInherit() {
        return new BlockEntry<>();
    }
    public static BlockEntry<QuarryBlock, BlockItem> buildQuarryBlock() {
        return new BlockEntry<>();
    }

    public BlockEntry<T, S> withId(String namespace, String path) {
        this.id = Identifier.of(namespace, path);

        return this;
    }
    public BlockEntry<T, S> withBlock(T block) {
        this.block = block;

        return this;
    }
    public BlockEntry<T, S> withBlockItem(S blockItem) {
        this.blockItem = blockItem;

        return this;
    }
    @SuppressWarnings("unchecked")
    public BlockEntry<T, S> withBlockItemSettings(Item.Settings settings) {
        if(this.block == null) throw new IllegalStateException();

        this.blockItem = (S) new BlockItem(this.block, settings);

        return this;
    }

    public T getBlock() {
        return block;
    }
    @Override
    public S asItem() {
        return blockItem;
    }
    public S getBlockItem() {
        return blockItem;
    }
    public Identifier getId() {
        return id;
    }
}