package me.basiqueevangelist.fastworldactions.mixin;

import net.minecraft.server.level.ChunkResult;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.ChunkAccess;

@Mixin(ServerChunkCache.class)
public interface ServerChunkCacheMixin {
    @Invoker
    CompletableFuture<ChunkResult<ChunkAccess>> callGetChunkFutureMainThread(int x, int z, ChunkStatus chunkStatus, boolean requireChunk);
}
