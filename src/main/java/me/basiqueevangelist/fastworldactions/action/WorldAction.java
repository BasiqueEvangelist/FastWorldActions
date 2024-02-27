package me.basiqueevangelist.fastworldactions.action;

import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.function.BiConsumer;

public interface WorldAction {
    LongCollection chunkSections();

    void forSection(long sectionPos, BiConsumer<BlockPos, BlockState> changeConsumer);
}
