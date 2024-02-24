package me.basiqueevangelist.fastworldactions.task;

import me.basiqueevangelist.fastworldactions.WorldAction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Collection;

public class UpdateHeightmapsTask extends WorldActionTask {
    public UpdateHeightmapsTask(World world, Collection<WorldAction.SectionInfo> sections) {
        super(world, sections, "fast-world-actions:update_heightmaps");
    }

    @Override
    public void runOn(WorldAction.SectionInfo section) {
        var motionBlocking = section.chunk().getHeightmap(Heightmap.Type.MOTION_BLOCKING);
        var motionBlockingNoLeaves = section.chunk().getHeightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES);
        var oceanFloor = section.chunk().getHeightmap(Heightmap.Type.OCEAN_FLOOR);
        var worldSurface = section.chunk().getHeightmap(Heightmap.Type.WORLD_SURFACE);

        for (var change : section.changes().entrySet()) {
            int sx = change.getKey().getX() & 15;
            int y = change.getKey().getY();
            int sz = change.getKey().getZ() & 15;

            motionBlocking.trackUpdate(sx, y, sz, change.getValue());
            motionBlockingNoLeaves.trackUpdate(sx, y, sz, change.getValue());
            oceanFloor.trackUpdate(sx, y, sz, change.getValue());
            worldSurface.trackUpdate(sx, y, sz, change.getValue());
        }
    }
}
