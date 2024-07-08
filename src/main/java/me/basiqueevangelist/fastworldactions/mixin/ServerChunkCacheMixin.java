package me.basiqueevangelist.fastworldactions.mixin;

import com.mojang.datafixers.util.Either;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;

@Mixin(ServerChunkCache.class)
public interface ServerChunkCacheMixin {
    @Invoker
    CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>> callGetChunkFutureMainThread(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);
}
