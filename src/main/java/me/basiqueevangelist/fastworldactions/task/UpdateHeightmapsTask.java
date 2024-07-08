package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class UpdateHeightmapsTask extends PostWorldActionTask {
    public UpdateHeightmapsTask(Level level, SetBlocksTask parent) {
        super(level, "fast-world-actions:update_heightmaps", parent);
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        var motionBlocking = section.chunk().getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING);
        var motionBlockingNoLeaves = section.chunk().getOrCreateHeightmapUnprimed(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES);
        var oceanFloor = section.chunk().getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR);
        var worldSurface = section.chunk().getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE);

        section.forEachChange((pos, oldState, newState) -> {
            int sx = pos.getX() & 15;
            int y = pos.getY();
            int sz = pos.getZ() & 15;

            motionBlocking.update(sx, y, sz, newState);
            motionBlockingNoLeaves.update(sx, y, sz, newState);
            oceanFloor.update(sx, y, sz, newState);
            worldSurface.update(sx, y, sz, newState);
        });
    }
}
