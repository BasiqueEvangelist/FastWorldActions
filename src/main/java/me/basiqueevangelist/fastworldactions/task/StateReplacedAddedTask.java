package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.World;

import java.util.Collection;

public class StateReplacedAddedTask extends PostWorldActionTask {
    public StateReplacedAddedTask(World world, SetBlocksTask parent) {
        super(world, "fast-world-actions:state_replaced_added", parent);
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        if (section.chunk().getWorld().isClient) {
            section.forEachChange((pos, oldState, newState) -> {
                if (!newState.isOf(oldState.getBlock()) && oldState.hasBlockEntity()) {
                    section.chunk().removeBlockEntity(pos);
                }
            });
        } else {
            section.forEachChange((pos, oldState, newState) -> {
                oldState.onStateReplaced(section.chunk().getWorld(), pos, newState, false);
                newState.onBlockAdded(section.chunk().getWorld(), pos, oldState, false);
            });
        }
    }
}
