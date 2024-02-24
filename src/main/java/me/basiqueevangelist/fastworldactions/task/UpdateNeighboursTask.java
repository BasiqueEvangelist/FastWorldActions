package me.basiqueevangelist.fastworldactions.task;

import me.basiqueevangelist.fastworldactions.WorldAction;
import net.minecraft.world.World;

import java.util.Collection;

public class UpdateNeighboursTask extends WorldActionTask {
    public UpdateNeighboursTask(World world, Collection<WorldAction.SectionInfo> sections) {
        super(world, sections, "fast-world-actions:update_neighbours");
    }

    @Override
    public void runOn(WorldAction.SectionInfo section) {
//        if ((flags & Block.NOTIFY_NEIGHBORS) != 0) {
//            this.updateNeighbors(pos, blockState.getBlock());
//            if (!this.isClient && state.hasComparatorOutput()) {
//                this.updateComparators(pos, block);
//            }
//        }
//
//        if ((flags & Block.FORCE_STATE) == 0 && maxUpdateDepth > 0) {
//            int i = flags & ~(Block.NOTIFY_NEIGHBORS | Block.SKIP_DROPS);
//            blockState.prepare(this, pos, i, maxUpdateDepth - 1);
//            state.updateNeighbors(this, pos, i, maxUpdateDepth - 1);
//            state.prepare(this, pos, i, maxUpdateDepth - 1);
//        }

        for (var change : section.changes().entrySet()) {
            var prev = section.previous().get(change.getKey());

            section.chunk().getWorld().updateNeighbors(change.getKey(), prev.getBlock());

            prev.prepare(section.chunk().getWorld(), change.getKey(), 2);
            change.getValue().updateNeighbors(section.chunk().getWorld(), change.getKey(), 2);
            change.getValue().prepare(section.chunk().getWorld(), change.getKey(), 2);

            section.chunk().getWorld().onBlockChanged(change.getKey(), prev, change.getValue());
        }
    }
}
