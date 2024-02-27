package me.basiqueevangelist.fastworldactions.action;

import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

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
            long sPos = ChunkSectionPos.from(entry.getKey()).asLong();

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
