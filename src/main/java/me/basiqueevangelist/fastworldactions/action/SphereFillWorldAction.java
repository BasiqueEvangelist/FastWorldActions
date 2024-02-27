package me.basiqueevangelist.fastworldactions.action;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.function.BiConsumer;

public class SphereFillWorldAction implements WorldAction {
    private final BlockPos center;
    private final int radius;
    private final BlockState targetState;

    private final LongList sections = new LongArrayList();

    public SphereFillWorldAction(BlockPos center, int radius, BlockState targetState) {
        this.center = center;
        this.radius = radius;
        this.targetState = targetState;

        for (int cix = -radius; cix <= radius; cix += 16) {
            for (int ciy = -radius; ciy <= radius; ciy += 16) {
                for (int ciz = -radius; ciz <= radius; ciz += 16) {
                    sections.add(ChunkSectionPos.asLong(
                        (center.getX() + cix) >> 4,
                        (center.getY() + ciy) >> 4,
                        (center.getZ() + ciz) >> 4
                    ));
                }
            }
        }
    }

    public BlockPos center() {
        return center;
    }

    public int radius() {
        return radius;
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
        int radiusSq = radius * radius;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    pos.set(start.getX() + x, start.getY() + y, start.getZ() + z);

                    if (pos.getSquaredDistance(center) > radiusSq) continue;

                    changeConsumer.accept(pos.toImmutable(), targetState);
                }
            }
        }
    }
}
