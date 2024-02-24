package me.basiqueevangelist.fastworldactions.task;

import me.basiqueevangelist.fastworldactions.WorldAction;
import net.minecraft.world.World;

import java.util.Collection;

public class StateReplacedAddedTask extends WorldActionTask {
    public StateReplacedAddedTask(World world, Collection<WorldAction.SectionInfo> sections) {
        super(world, sections, "fast-world-actions:state_replaced_added");
    }

    @Override
    public void runOn(WorldAction.SectionInfo section) {
        for (var change : section.changes().entrySet()) {
            var prev = section.previous().get(change.getKey());

            prev.onStateReplaced(section.chunk().getWorld(), change.getKey(), change.getValue(), false);

            if (!section.chunk().getWorld().isClient) {
                change.getValue().onBlockAdded(section.chunk().getWorld(), change.getKey(), prev, false);
            }
        }
    }
}
