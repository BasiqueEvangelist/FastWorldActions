package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.level.Level;

public class UpdateListenersTask extends PostWorldActionTask {
    public UpdateListenersTask(Level level, SetBlocksTask parent) {
        super(level, "fast-world-actions:update_listeners", parent);
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        section.forEachChange((pos, oldState, newState) -> {
            section.chunk().getLevel().sendBlockUpdated(pos, oldState, newState, 3);
        });
    }
}
