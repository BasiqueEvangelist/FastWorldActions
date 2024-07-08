package me.basiqueevangelist.fastworldactions.client;

import me.basiqueevangelist.fastworldactions.FastWorldActions;
import me.basiqueevangelist.fastworldactions.WorldActionPacket;
import me.basiqueevangelist.fastworldactions.task.PostWorldActionTask;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class FastWorldActionsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PostWorldActionTask.Client.init();

        ClientPlayNetworking.registerGlobalReceiver(WorldActionPacket.TYPE, (packet, player, responseSender) -> {
            FastWorldActions.action(packet.action())
                .notifyNeighbours(packet.notifyNeighbours())
                .notifyListeners(packet.notifyListeners())
                .forceState(packet.forceState())
                .run(player.clientLevel);
        });
    }
}
