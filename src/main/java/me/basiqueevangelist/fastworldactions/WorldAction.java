package me.basiqueevangelist.fastworldactions;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import me.basiqueevangelist.fastworldactions.task.NotifyListenersTask;
import me.basiqueevangelist.fastworldactions.task.StateReplacedAddedTask;
import me.basiqueevangelist.fastworldactions.task.UpdateHeightmapsTask;
import me.basiqueevangelist.fastworldactions.task.UpdateNeighboursTask;
import me.basiqueevangelist.fastworldactions.util.WorldUtil;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.light.ChunkLightProvider;

import java.util.HashMap;
import java.util.Map;

public class WorldAction {
    public static void run(World world, Map<BlockPos, BlockState> changes) {
        Long2ObjectMap<SectionInfo> sections = new Long2ObjectOpenHashMap<>();

        for (var entry : changes.entrySet()) {
            BlockPos pos = entry.getKey();
            var chunk = world.getWorldChunk(pos);
            var section = chunk.getSection(chunk.getSectionIndex(pos.getY()));

            if (world.isOutOfHeightLimit(pos)) continue;

            int sx = pos.getX() & 15;
            int sy = pos.getY() & 15;
            int sz = pos.getZ() & 15;

            boolean oldIsEmpty = section.isEmpty();
            var old = section.setBlockState(sx, sy, sz, entry.getValue());

            if (oldIsEmpty != section.isEmpty()) {
                world.getChunkManager().getLightingProvider().setSectionStatus(pos, section.isEmpty());
            }

            if (ChunkLightProvider.needsLightUpdate(chunk, pos, old, entry.getValue())) {
                chunk.getChunkSkyLight().isSkyLightAccessible(chunk, sx, pos.getY(), sz);
                world.getChunkManager().getLightingProvider().checkBlock(pos);
            }

            var info = sections.computeIfAbsent(
                ChunkSectionPos.asLong(pos.getX() >> 4, pos.getY() >> 4, pos.getZ() >> 4),
                sPos -> new SectionInfo(sPos, chunk, section, new HashMap<>(), new HashMap<>())
            );

            info.changes.put(pos, entry.getValue());
            info.previous.put(pos, old);

            chunk.setNeedsSaving(true);
        }

        new UpdateHeightmapsTask(world, sections.values()).start();
        new StateReplacedAddedTask(world, sections.values()).start();
        new NotifyListenersTask(world, sections.values()).start();
        new UpdateNeighboursTask(world, sections.values()).start();
    }

    public record SectionInfo(long pos, WorldChunk chunk, ChunkSection section,
                              Map<BlockPos, BlockState> changes, Map<BlockPos, BlockState> previous) {
    }
}
