package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.level.Level;

public class UpdateNeighboursTask extends PostWorldActionTask {
    private final boolean notifyNeighbours;
    private final boolean prepareState;

    public UpdateNeighboursTask(Level level, SetBlocksTask parent, boolean notifyNeighbours, boolean prepareState) {
        super(level, "fast-world-actions:update_neighbours", parent);
        this.notifyNeighbours = notifyNeighbours;
        this.prepareState = prepareState;
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        if (section.chunk().getLevel().isClientSide) return;

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
                section.chunk().getLevel().blockUpdated(pos, oldState.getBlock());
                if (!section.chunk().getLevel().isClientSide && newState.hasAnalogOutputSignal()) {
                    section.chunk().getLevel().updateNeighbourForOutputSignal(pos, newState.getBlock());
                }
            }

            if (prepareState) {
                oldState.updateIndirectNeighbourShapes(section.chunk().getLevel(), pos, 2);
                newState.updateNeighbourShapes(section.chunk().getLevel(), pos, 2);
                newState.updateIndirectNeighbourShapes(section.chunk().getLevel(), pos, 2);
            }

            section.chunk().getLevel().onBlockStateChange(pos, oldState, newState);
        });
    }
}
