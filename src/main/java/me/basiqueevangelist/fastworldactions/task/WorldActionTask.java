package me.basiqueevangelist.fastworldactions.task;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

public abstract class WorldActionTask {
    private static final List<WorldActionTask> SERVER_TASKS = new ArrayList<>();
    private static final List<WorldActionTask> CLIENT_TASKS = new ArrayList<>();

    @ApiStatus.Internal
    public volatile static int SERVER_SECTIONS = 0;
    @ApiStatus.Internal
    public volatile static int CLIENT_SECTIONS = 0;


    protected final Level level;
    protected final String name;

    public WorldActionTask(Level level, String name) {
        this.level = level;
        this.name = name;
    }

    public static void init() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            SERVER_TASKS.removeIf(x -> {
                x.tick();
                return x.isDone();
            });

            int sections = 0;

            for (WorldActionTask x : SERVER_TASKS) {
                sections += x.sectionsRemaining();
            }

            SERVER_SECTIONS = sections;
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            SERVER_TASKS.clear();
        });
    }

    public abstract void tick();

    public abstract boolean isDone();

    public abstract int sectionsRemaining();

    public void start() {
        if (level.isClientSide)
            CLIENT_TASKS.add(this);
        else
            SERVER_TASKS.add(this);
    }

    @Environment(EnvType.CLIENT)
    public static class Client {
        public static void init() {
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                CLIENT_TASKS.removeIf(x -> {
                    if (x.level != client.level) return true;

                    x.tick();
                    return x.isDone();
                });

                int sections = 0;

                for (WorldActionTask x : CLIENT_TASKS) {
                    sections += x.sectionsRemaining();
                }

                CLIENT_SECTIONS = sections;
            });
        }
    }
}
