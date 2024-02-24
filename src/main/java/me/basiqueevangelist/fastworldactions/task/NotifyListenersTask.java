package me.basiqueevangelist.fastworldactions.task;

import me.basiqueevangelist.fastworldactions.WorldAction;
import net.minecraft.world.World;

import java.util.Collection;

public class NotifyListenersTask extends WorldActionTask {
    public NotifyListenersTask(World world, Collection<WorldAction.SectionInfo> sections) {
        super(world, sections, "fast-world-actions:notify_listeners");
    }

    @Override
    public void runOn(WorldAction.SectionInfo section) {
        for (var change : section.changes().entrySet()) {
            var prev = section.previous().get(change.getKey());

            section.chunk().getWorld().updateListeners(change.getKey(), prev, change.getValue(), 3);
        }
    }
}
