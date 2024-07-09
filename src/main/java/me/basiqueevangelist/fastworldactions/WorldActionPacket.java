package me.basiqueevangelist.fastworldactions;

import me.basiqueevangelist.fastworldactions.action.WorldAction;
import me.basiqueevangelist.fastworldactions.action.WorldActionSync;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record WorldActionPacket(WorldAction action, boolean notifyNeighbours, boolean notifyListeners,
                                boolean forceState) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<WorldActionPacket> TYPE = new Type<>(FastWorldActions.id("world_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, WorldActionPacket> STREAM_CODEC = StreamCodec.composite(
        WorldActionSync.ACTION_CODEC, WorldActionPacket::action,
        ByteBufCodecs.BOOL, WorldActionPacket::notifyNeighbours,
        ByteBufCodecs.BOOL, WorldActionPacket::notifyListeners,
        ByteBufCodecs.BOOL, WorldActionPacket::forceState,
        WorldActionPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
