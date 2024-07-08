package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public record SectionUpdateInfo(long pos, LevelChunk chunk, LevelChunkSection section,
                                BlockState[] changes, BlockState[] previous) {
    public static SectionUpdateInfo create(long pos, LevelChunk chunk, LevelChunkSection section) {
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
            SectionPos.x(pos) * 16,
            SectionPos.y(pos) * 16,
            SectionPos.z(pos) * 16
        );
        var pos = new BlockPos.MutableBlockPos();

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
