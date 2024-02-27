package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

import java.util.Map;
import java.util.function.BiConsumer;

public record SectionUpdateInfo(long pos, WorldChunk chunk, ChunkSection section,
                                BlockState[] changes, BlockState[] previous) {
    public static SectionUpdateInfo create(long pos, WorldChunk chunk, ChunkSection section) {
        return new SectionUpdateInfo(pos, chunk, section, new BlockState[4096], new BlockState[4096]);
    }

    private int toIndex(int sx, int sy, int sz) {
        return (sx * 16 + sy) * 16 + sz;
    }

    public void setChange(int sx, int sy, int sz, BlockState oldState, BlockState newState) {
        int idx = toIndex(sx, sy, sz);

        changes[idx] = newState;
        previous[idx] = oldState;
    }

    public void forEachChange(ChangeConsumer consumer) {
        var start = new BlockPos(
            ChunkSectionPos.unpackX(pos) * 16,
            ChunkSectionPos.unpackY(pos) * 16,
            ChunkSectionPos.unpackZ(pos) * 16
        );
        var pos = new BlockPos.Mutable();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int idx = toIndex(x, y, z);

                    if (changes[idx] == null) continue;

                    pos.set(start.getX() + x, start.getY() + y, start.getZ() + z);
                    consumer.onChange(pos, previous[idx], changes[idx]);
                }
            }
        }
    }

    public interface ChangeConsumer {
        void onChange(BlockPos pos, BlockState oldState, BlockState newState);
    }
}
