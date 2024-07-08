package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.level.Level;

import java.util.*;

public abstract class PostWorldActionTask extends WorldActionTask {
    private final Queue<SectionUpdateInfo> sections;
    private final SetBlocksTask parent;

    protected PostWorldActionTask(Level level, String name, SetBlocksTask parent) {
        super(level, name);
        this.parent = parent;
        this.sections = new ArrayDeque<>();
    }

    @Override
    public void tick() {
        if (sections.isEmpty()) return;

        long startNanos = System.nanoTime();
        int totalRun = 0;

        // 5 ms
        while (!sections.isEmpty() && System.nanoTime() - startNanos < timeQuotaMs() * 1000000) {
            runOn(sections.poll());
            totalRun++;
        }

//        FastWorldActions.LOGGER.info("{}, ran on {} sections in {} ms", name, totalRun, (double)(System.nanoTime() - startNanos) / 1000000);
    }

    public abstract void runOn(SectionUpdateInfo section);

    public Queue<SectionUpdateInfo> sections() {
        return sections;
    }

    public long timeQuotaMs() {
        return 10;
    }

    @Override
    public boolean isDone() {
        return parent.isDone() && sections.isEmpty();
    }

    @Override
    public int sectionsRemaining() {
        return sections.size();
    }
}
