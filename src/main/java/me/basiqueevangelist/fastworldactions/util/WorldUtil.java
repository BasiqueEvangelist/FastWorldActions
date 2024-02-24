package me.basiqueevangelist.fastworldactions.util;

import me.basiqueevangelist.fastworldactions.mixin.ServerChunkManagerAccessor;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.WorldChunk;

import java.util.concurrent.CompletableFuture;

public final class WorldUtil {
    private WorldUtil() {

    }

    public static CompletableFuture<WorldChunk> getChunkFuture(World world, int cx, int cz) {
        if (!(world.getChunkManager() instanceof ServerChunkManager scm))
            return CompletableFuture.completedFuture(world.getChunkManager().getWorldChunk(cx, cz));

        return ((ServerChunkManagerAccessor) scm).callGetChunkFuture(cx, cz, ChunkStatus.FULL, true)
            .thenApply(x -> (WorldChunk) x.left().orElseThrow());
    }
}
