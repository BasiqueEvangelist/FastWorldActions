package me.basiqueevangelist.fastworldactions.task;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import me.basiqueevangelist.fastworldactions.action.WorldAction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.ArrayList;
import java.util.List;

public class SetBlocksTask extends WorldActionTask {
    private final LongList sections;
    private final WorldAction action;
    private int processed = 0;
    private final List<PostWorldActionTask> children = new ArrayList<>();

    public SetBlocksTask(Level level, WorldAction action) {
        super(level, "fast-world-actions:set_blocks");

        sections = new LongArrayList(action.chunkSections());
        this.action = action;
    }

    public void addChild(PostWorldActionTask child) {
        children.add(child);
    }

    @Override
    public void tick() {
        long startNanos = System.nanoTime();
        int totalRun = 0;

        // 5 ms
        while (!isDone() && System.nanoTime() - startNanos < 25 * 1000000) {
            runOn(sections.getLong(processed));

            totalRun++;
            processed++;
        }

//        FastWorldActions.LOGGER.info("{}, ran on {} sections in {} ms", name, totalRun, (double)(System.nanoTime() - startNanos) / 1000000);
    }

    private void runOn(long sectionPos) {
        var chunk = level.getChunk(SectionPos.x(sectionPos), SectionPos.z(sectionPos));
        var sectionCoord = SectionPos.y(sectionPos);

        if (chunk.isEmpty()) return;
        if (chunk.getMaxSection() <= sectionCoord) return;
        if (chunk.getMinSection() > sectionCoord) return;

        var section = chunk.getSection(chunk.getSectionIndexFromSectionY(sectionCoord));
        var info = SectionUpdateInfo.create(sectionPos, chunk, section);
        var changed = new MutableBoolean(false);

        action.forSection(sectionPos, (pos, newState) -> {
            if (level.isOutsideBuildHeight(pos)) return;

            int sx = pos.getX() & 15;
            int sy = pos.getY() & 15;
            int sz = pos.getZ() & 15;

            var old = section.setBlockState(sx, sy, sz, newState);

            if (old == newState) return;

            info.setChange(sx, sy, sz, old, newState);
            chunk.setUnsaved(true);
            changed.setValue(true);
        });

        if (!changed.booleanValue()) return;

        for (var child : children) {
            child.sections().add(info);
        }
    }

    @Override
    public boolean isDone() {
        return sections.size() <= processed;
    }

    @Override
    public int sectionsRemaining() {
        return sections.size() - processed;
    }
}
