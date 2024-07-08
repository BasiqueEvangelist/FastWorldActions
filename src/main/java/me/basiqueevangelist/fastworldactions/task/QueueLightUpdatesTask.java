package me.basiqueevangelist.fastworldactions.task;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LightEngine;

public class QueueLightUpdatesTask extends PostWorldActionTask {
    public QueueLightUpdatesTask(Level level, SetBlocksTask parent) {
        super(level, "fast-world-actions:queue_light_updates", parent);
    }

    @Override
    public void runOn(SectionUpdateInfo section) {
        level.getChunkSource().getLightEngine().updateSectionStatus(SectionPos.of(section.pos()), false);

        section.forEachChange((pos, oldState, newState) -> {
            int sx = pos.getX() & 15;
            int sz = pos.getZ() & 15;

            if (LightEngine.hasDifferentLightProperties(section.chunk(), pos, oldState, newState)) {
                section.chunk().getSkyLightSources().update(section.chunk(), sx, pos.getY(), sz);

//                if (world.isClient) {
//                    ((ChunkLightProviderAccessor) world.getChunkManager().getLightingProvider().get(LightType.BLOCK)).callMethod_51529(pos.asLong());
//                    ((ChunkLightProviderAccessor) world.getChunkManager().getLightingProvider().get(LightType.SKY)).callMethod_51529(pos.asLong());
//                } else {
                level.getChunkSource().getLightEngine().checkBlock(pos);
//                }
            }
        });
    }

    @Override
    public long timeQuotaMs() {
        return level.isClientSide ? 1 : 10;
    }
}
