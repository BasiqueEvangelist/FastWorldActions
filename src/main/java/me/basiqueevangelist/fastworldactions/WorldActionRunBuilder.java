package me.basiqueevangelist.fastworldactions;

import me.basiqueevangelist.fastworldactions.action.WorldAction;
import me.basiqueevangelist.fastworldactions.task.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

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

    public void run(Level level) {
        var setBlocks = new SetBlocksTask(level, action);

        var updateHeightmaps = new UpdateHeightmapsTask(level, setBlocks);
        var stateReplacedAdded = new StateReplacedAddedTask(level, setBlocks);
        var updateNeighbours = new UpdateNeighboursTask(level, setBlocks, this.notifyNeighbours, !forceState);
        var queueLightUpdates = new QueueLightUpdatesTask(level, setBlocks);

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
            var updateListeners = new UpdateListenersTask(level, setBlocks);
            setBlocks.addChild(updateListeners);
            updateListeners.start();
        }

        if (sync && level instanceof ServerLevel sl) {
            var packet = new WorldActionPacket(action, notifyNeighbours, notifyListeners, forceState);

            for (ServerPlayer player : sl.players()) {
                if (!isNear(player)) continue;

                ServerPlayNetworking.send(player, packet);
            }
        }
    }

    private boolean isNear(ServerPlayer player) {
        for (var sectionPos : action.chunkSections()) {
            int x = SectionPos.x(sectionPos) * 16 + 8;
            int y = SectionPos.y(sectionPos) * 16 + 8;
            int z = SectionPos.z(sectionPos) * 16 + 8;

            if (player.distanceToSqr(x, y, z) < 4096)
                return true;
        }

        return false;
    }
}
