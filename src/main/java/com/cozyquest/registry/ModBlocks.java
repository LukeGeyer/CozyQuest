package com.cozyquest.registry;

import com.cozyquest.CozyQuest;
import com.cozyquest.block.ShrineBlock;
import com.cozyquest.quest.QuestTier;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;

public class ModBlocks {

    public static final ShrineBlock SHRINE_TIER_1 = register("shrine_tier_1",
            props -> new ShrineBlock(props, QuestTier.TIER_1));

    public static final ShrineBlock SHRINE_TIER_2 = register("shrine_tier_2",
            props -> new ShrineBlock(props, QuestTier.TIER_2));

    public static final ShrineBlock SHRINE_TIER_3 = register("shrine_tier_3",
            props -> new ShrineBlock(props, QuestTier.TIER_3));

    private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> blockFactory) {
        Identifier id = Identifier.fromNamespaceAndPath(CozyQuest.MOD_ID, name);

        ResourceKey<Block> blockKey = ResourceKey.create(Registries.BLOCK, id);
        BlockBehaviour.Properties blockProps = BlockBehaviour.Properties.of()
                .strength(3.0f)
                .requiresCorrectToolForDrops()
                .setId(blockKey);

        T block = blockFactory.apply(blockProps);
        Registry.register(BuiltInRegistries.BLOCK, id, block);

        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, id);
        Registry.register(BuiltInRegistries.ITEM, id,
                new BlockItem(block, new Item.Properties().setId(itemKey)));

        return block;
    }

    public static void initialize() {
        // Triggers static field initialization / block registration
    }
}
