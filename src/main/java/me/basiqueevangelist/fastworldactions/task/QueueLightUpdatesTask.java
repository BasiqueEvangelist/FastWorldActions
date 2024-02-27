package me.basiqueevangelist.fastworldactions.task;

import me.basiqueevangelist.fastworldactions.mixin.client.ChunkLightProviderAccessor;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.light.ChunkLightProvider;

public class QueueLightUpdatesTask extends PostWorldActionTask {
    public QueueLightUpdatesTask(World world, SetBlocksTask parent) {
        super(world, "fast-world-actions:queue_light_updates", parent);
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        world.getChunkManager().getLightingProvider().setSectionStatus(ChunkSectionPos.from(section.pos()), false);

        section.forEachChange((pos, oldState, newState) -> {
            int sx = pos.getX() & 15;
            int sz = pos.getZ() & 15;

            if (ChunkLightProvider.needsLightUpdate(section.chunk(), pos, oldState, newState)) {
                section.chunk().getChunkSkyLight().isSkyLightAccessible(section.chunk(), sx, pos.getY(), sz);

                if (world.isClient) {
                    ((ChunkLightProviderAccessor) world.getChunkManager().getLightingProvider().get(LightType.BLOCK)).callMethod_51529(pos.asLong());
                    ((ChunkLightProviderAccessor) world.getChunkManager().getLightingProvider().get(LightType.SKY)).callMethod_51529(pos.asLong());
                } else {
                    world.getChunkManager().getLightingProvider().checkBlock(pos);
                }
            }
        });
    }

    @Override
    public long timeQuotaMs() {
        return world.isClient ? 2 : 10;
    }
}
