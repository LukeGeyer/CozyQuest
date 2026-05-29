package com.cozyquest.block;

import com.cozyquest.QuestManager;
import com.cozyquest.quest.QuestTier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class ShrineBlock extends Block {

    private final QuestTier tier;

    public ShrineBlock(Properties properties, QuestTier tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            QuestManager.onShrinePlaced(serverLevel.getServer());
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            QuestManager.onShrineInteract(serverPlayer, this.tier);
        }
        return InteractionResult.SUCCESS;
    }

    public QuestTier getTier() {
        return tier;
    }
}
