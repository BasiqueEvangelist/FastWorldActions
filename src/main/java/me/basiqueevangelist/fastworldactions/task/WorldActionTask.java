package me.basiqueevangelist.fastworldactions.task;

import me.basiqueevangelist.fastworldactions.WorldAction;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public abstract class WorldActionTask {
    private static final Logger LOGGER = LoggerFactory.getLogger("FWA/WorldActionTask");
    private static final List<WorldActionTask> TASKS = new ArrayList<>();

    private final World world;
    private final Queue<WorldAction.SectionInfo> sections;
    private final String name;

    protected WorldActionTask(World world, Collection<WorldAction.SectionInfo> sections, String name) {
        this.world = world;
        this.sections = new ArrayDeque<>(sections);
        this.name = name;
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            TASKS.removeIf(x -> {
                x.tick();
                return x.isDone();
            });
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            TASKS.clear();
        });
    }

    public void tick() {
        long startNanos = System.nanoTime();
        int totalRun = 0;

        // 5 ms
        while (!isDone() && System.nanoTime() - startNanos < 20 * 1000000) {
            runOn(sections.poll());
            totalRun++;
        }

//        LOGGER.info("{}: ran on {} sections in {} ms", name, totalRun, (double)(System.nanoTime() - startNanos) / 1000000);
    }

    public abstract void runOn(WorldAction.SectionInfo section);

    public boolean isDone() {
        return sections.isEmpty();
    }

    public void start() {
        TASKS.add(this);
    }
}
