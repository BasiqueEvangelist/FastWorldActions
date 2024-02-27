package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Collection;

public class UpdateHeightmapsTask extends PostWorldActionTask {
    public UpdateHeightmapsTask(World world, SetBlocksTask parent) {
        super(world, "fast-world-actions:update_heightmaps", parent);
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        var motionBlocking = section.chunk().getHeightmap(Heightmap.Type.MOTION_BLOCKING);
        var motionBlockingNoLeaves = section.chunk().getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        var oceanFloor = section.chunk().getHeightmap(Heightmap.Type.OCEAN_FLOOR);
        var worldSurface = section.chunk().getHeightmap(Heightmap.Type.WORLD_SURFACE);

        section.forEachChange((pos, oldState, newState) -> {
            int sx = pos.getX() & 15;
            int y = pos.getY();
            int sz = pos.getZ() & 15;

            motionBlocking.trackUpdate(sx, y, sz, newState);
            motionBlockingNoLeaves.trackUpdate(sx, y, sz, newState);
            oceanFloor.trackUpdate(sx, y, sz, newState);
            worldSurface.trackUpdate(sx, y, sz, newState);
        });
    }
}
