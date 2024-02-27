package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.World;

import java.util.Collection;

public class UpdateNeighboursTask extends PostWorldActionTask {
    private final boolean notifyNeighbours;
    private final boolean prepareState;

    public UpdateNeighboursTask(World world, SetBlocksTask parent, boolean notifyNeighbours, boolean prepareState) {
        super(world, "fast-world-actions:update_neighbours", parent);
        this.notifyNeighbours = notifyNeighbours;
        this.prepareState = prepareState;
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        if (section.chunk().getWorld().isClient) return;

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

        section.forEachChange((pos, oldState, newState) -> {
            if (notifyNeighbours) {
                section.chunk().getWorld().updateNeighbors(pos, oldState.getBlock());
                if (!section.chunk().getWorld().isClient && newState.hasComparatorOutput()) {
                    section.chunk().getWorld().updateComparators(pos, newState.getBlock());
                }
            }

            if (prepareState) {
                oldState.prepare(section.chunk().getWorld(), pos, 2);
                newState.updateNeighbors(section.chunk().getWorld(), pos, 2);
                newState.prepare(section.chunk().getWorld(), pos, 2);
            }

            section.chunk().getWorld().onBlockChanged(pos, oldState, newState);
        });
    }
}
