package me.basiqueevangelist.fastworldactions.action;

import it.unimi.dsi.fastutil.longs.LongCollection;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface WorldAction {
    LongCollection chunkSections();

    void forSection(long sectionPos, BiConsumer<BlockPos, BlockState> changeConsumer);
}
