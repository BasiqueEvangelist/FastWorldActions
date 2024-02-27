package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.World;

public class UpdateListenersTask extends PostWorldActionTask {
    public UpdateListenersTask(World world, SetBlocksTask parent) {
        super(world, "fast-world-actions:update_listeners", parent);
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        section.forEachChange((pos, oldState, newState) -> {
            section.chunk().getWorld().updateListeners(pos, oldState, newState, 3);
        });
    }
}
