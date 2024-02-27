package me.basiqueevangelist.fastworldactions;

import me.basiqueevangelist.fastworldactions.action.WorldAction;
import me.basiqueevangelist.fastworldactions.task.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.World;

public final class WorldActionRunBuilder {
    private final WorldAction action;
    private boolean notifyNeighbours = true;
    private boolean notifyListeners = true;
    private boolean forceState = false;
    private boolean sync = false;

    WorldActionRunBuilder(WorldAction action) {
        this.action = action;
    }

    public WorldActionRunBuilder notifyNeighbours(boolean notifyNeighbours) {
        this.notifyNeighbours = notifyNeighbours;
        return this;
    }

    public WorldActionRunBuilder notifyListeners(boolean notifyListeners) {
        this.notifyListeners = notifyListeners;
        return this;
    }

    public WorldActionRunBuilder forceState(boolean forceState) {
        this.forceState = forceState;
        return this;
    }

    public WorldActionRunBuilder syncToPlayers() {
        this.sync = true;
        return this;
    }

    public void run(World world) {
        var setBlocks = new SetBlocksTask(world, action);

        var updateHeightmaps = new UpdateHeightmapsTask(world, setBlocks);
        var stateReplacedAdded = new StateReplacedAddedTask(world, setBlocks);
        var updateNeighbours = new UpdateNeighboursTask(world, setBlocks, this.notifyNeighbours, !forceState);
        var queueLightUpdates = new QueueLightUpdatesTask(world, setBlocks);

        setBlocks.addChild(updateHeightmaps);
        setBlocks.addChild(stateReplacedAdded);
        setBlocks.addChild(updateNeighbours);
        setBlocks.addChild(queueLightUpdates);

        setBlocks.start();
        updateHeightmaps.start();
        stateReplacedAdded.start();
        updateNeighbours.start();
        queueLightUpdates.start();

        if (this.notifyListeners) {
            var updateListeners = new UpdateListenersTask(world, setBlocks);
            setBlocks.addChild(updateListeners);
            updateListeners.start();
        }

        if (sync && world instanceof ServerWorld sw) {
            var packet = new WorldActionPacket(action, notifyNeighbours, notifyListeners, forceState);

            for (ServerPlayerEntity player : sw.getPlayers()) {
                if (!isNear(player)) continue;

                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    private boolean isNear(ServerPlayerEntity player) {
        for (var sectionPos : action.chunkSections()) {
            int x = ChunkSectionPos.unpackX(sectionPos) * 16 + 8;
            int y = ChunkSectionPos.unpackY(sectionPos) * 16 + 8;
            int z = ChunkSectionPos.unpackZ(sectionPos) * 16 + 8;

            if (player.squaredDistanceTo(x, y, z) < 4096)
                return true;
        }

        return false;
    }
}
