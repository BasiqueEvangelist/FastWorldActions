package me.basiqueevangelist.fastworldactions.action;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class BoxFillWorldAction implements WorldAction {
    private final BoundingBox box;
    private final BlockState targetState;
    private final LongList sections = new LongArrayList();

    public BoxFillWorldAction(BoundingBox box, BlockState targetState) {
        this.box = box;
        this.targetState = targetState;

        for (int sx = box.minX() >> 4; sx <= (box.maxX() >> 4); sx++) {
            for (int sy = box.minY() >> 4; sy <= (box.maxY() >> 4); sy++) {
                for (int sz = box.minZ() >> 4; sz <= (box.maxZ() >> 4); sz++) {
                    sections.add(SectionPos.asLong(sx, sy, sz));
                }
            }
        }
    }

    public BoundingBox box() {
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
            SectionPos.x(sectionPos) * 16,
            SectionPos.y(sectionPos) * 16,
            SectionPos.z(sectionPos) * 16
        );
        var pos = new BlockPos.MutableBlockPos();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    pos.set(start.getX() + x, start.getY() + y, start.getZ() + z);

                    if (!box.isInside(pos)) continue;

                    changeConsumer.accept(pos.immutable(), targetState);
                }
            }
        }
    }
}
