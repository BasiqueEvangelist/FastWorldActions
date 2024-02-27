package me.basiqueevangelist.fastworldactions.action;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.function.BiConsumer;

public class BoxFillWorldAction implements WorldAction {
    private final BlockBox box;
    private final BlockState targetState;
    private final LongList sections = new LongArrayList();

    public BoxFillWorldAction(BlockBox box, BlockState targetState) {
        this.box = box;
        this.targetState = targetState;

        for (int sx = box.getMinX() >> 4; sx <= (box.getMaxX() >> 4); sx++) {
            for (int sy = box.getMinY() >> 4; sy <= (box.getMaxY() >> 4); sy++) {
                for (int sz = box.getMinZ() >> 4; sz <= (box.getMaxZ() >> 4); sz++) {
                    sections.add(ChunkSectionPos.asLong(sx, sy, sz));
                }
            }
        }
    }

    public BlockBox box() {
        return box;
    }

    public BlockState targetState() {
        return targetState;
    }

    @Override
    public LongCollection chunkSections() {
        return sections;
    }

    @Override
    public void forSection(long sectionPos, BiConsumer<BlockPos, BlockState> changeConsumer) {
        var start = new BlockPos(
            ChunkSectionPos.unpackX(sectionPos) * 16,
            ChunkSectionPos.unpackY(sectionPos) * 16,
            ChunkSectionPos.unpackZ(sectionPos) * 16
        );
        var pos = new BlockPos.Mutable();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    pos.set(start.getX() + x, start.getY() + y, start.getZ() + z);

                    if (!box.contains(pos)) continue;

                    changeConsumer.accept(pos.toImmutable(), targetState);
                }
            }
        }
    }
}
