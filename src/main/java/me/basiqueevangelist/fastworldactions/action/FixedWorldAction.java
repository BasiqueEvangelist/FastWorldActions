package me.basiqueevangelist.fastworldactions.action;

import it.unimi.dsi.fastutil.longs.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.block.state.BlockState;

public class FixedWorldAction implements WorldAction {
    private final Long2ObjectMap<Map<BlockPos, BlockState>> sections;
    private final Map<BlockPos, BlockState> changes;

    public FixedWorldAction(Map<BlockPos, BlockState> changes) {
        this.changes = changes;
        this.sections = new Long2ObjectOpenHashMap<>();

        for (var entry : changes.entrySet()) {
//            int sx = entry.getKey().getX() >> 4;
//            int sy = entry.getKey().getY() >> 4;
//            int sz = entry.getKey().getZ() >> 4;
//
//            long sPos = ChunkSectionPos.asLong(sx, sy, sz);
            long sPos = SectionPos.of(entry.getKey()).asLong();

            var map = sections.computeIfAbsent(sPos, unused -> new HashMap<>());
            map.put(entry.getKey(), entry.getValue());
        }
    }

    public Map<BlockPos, BlockState> changes() {
        return changes;
    }

    @Override
    public LongCollection chunkSections() {
        return sections.keySet();
    }

    @Override
    public void forSection(long sectionPos, BiConsumer<BlockPos, BlockState> changeConsumer) {
        sections.get(sectionPos).forEach(changeConsumer);
    }
}
