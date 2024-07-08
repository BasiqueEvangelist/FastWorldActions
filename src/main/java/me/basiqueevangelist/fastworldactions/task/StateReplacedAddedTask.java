package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.level.Level;

public class StateReplacedAddedTask extends PostWorldActionTask {
    public StateReplacedAddedTask(Level level, SetBlocksTask parent) {
        super(level, "fast-world-actions:state_replaced_added", parent);
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        if (section.chunk().getLevel().isClientSide) {
            section.forEachChange((pos, oldState, newState) -> {
                if (!newState.is(oldState.getBlock()) && oldState.hasBlockEntity()) {
                    section.chunk().removeBlockEntity(pos);
                }
            });
        } else {
            section.forEachChange((pos, oldState, newState) -> {
                oldState.onRemove(section.chunk().getLevel(), pos, newState, false);
                newState.onPlace(section.chunk().getLevel(), pos, oldState, false);
            });
        }
    }
}
