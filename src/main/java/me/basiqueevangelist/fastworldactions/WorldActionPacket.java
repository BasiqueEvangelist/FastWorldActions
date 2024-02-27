package me.basiqueevangelist.fastworldactions;

import me.basiqueevangelist.fastworldactions.action.WorldAction;
import me.basiqueevangelist.fastworldactions.action.WorldActionSync;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record WorldActionPacket(WorldAction action, boolean notifyNeighbours, boolean notifyListeners,
                                boolean forceState) implements FabricPacket {
    public static final PacketType<WorldActionPacket> TYPE = PacketType.create(new Identifier("fast-world-actions:world_action"), WorldActionPacket::read);

    public static WorldActionPacket read(PacketByteBuf buf) {
        WorldAction action = WorldActionSync.read(buf);
        boolean notifyNeighbours = buf.readBoolean();
        boolean notifyListeners = buf.readBoolean();
        boolean forceState = buf.readBoolean();

        return new WorldActionPacket(action, notifyNeighbours, notifyListeners, forceState);
    }

    @Override
    public void write(PacketByteBuf buf) {
        WorldActionSync.write(buf, action);
        buf.writeBoolean(notifyNeighbours);
        buf.writeBoolean(notifyListeners);
        buf.writeBoolean(forceState);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
