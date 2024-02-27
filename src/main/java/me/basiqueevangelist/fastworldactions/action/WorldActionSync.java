package me.basiqueevangelist.fastworldactions.action;

import net.minecraft.block.Block;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public final class WorldActionSync {
    private static final Map<Identifier, SyncData<?>> ACTIONS_BY_ID = new HashMap<>();
    private static final Map<Class<?>, SyncData<?>> ACTIONS_BY_CLASS = new HashMap<>();

    public static <T extends WorldAction> void register(Identifier id, Class<T> klass, PacketByteBuf.PacketReader<T> reader, PacketByteBuf.PacketWriter<T> writer) {
        var syncData = new SyncData<>(id, reader, writer);

        ACTIONS_BY_ID.put(id, syncData);
        ACTIONS_BY_CLASS.put(klass, syncData);
    }

    @SuppressWarnings("unchecked")
    public static void write(PacketByteBuf buf, WorldAction action) {
        var syncData = ACTIONS_BY_CLASS.get(action.getClass());

        if (syncData == null)
            throw new IllegalStateException(action.getClass().getName() + " isn't syncable.");

        buf.writeIdentifier(syncData.id());
        ((PacketByteBuf.PacketWriter<Object>) syncData.writer).accept(buf, action);
    }

    public static WorldAction read(PacketByteBuf buf) {
        var id = buf.readIdentifier();
        var syncData = ACTIONS_BY_ID.get(id);

        if (syncData == null)
            throw new IllegalStateException("Received an unknown WorldAction id: " + id);

        return syncData.reader().apply(buf);
    }

    @ApiStatus.Internal
    public static void init() {
        register(new Identifier("fast-world-actions", "fixed"), FixedWorldAction.class,
            buf -> new FixedWorldAction(buf.readMap(PacketByteBuf::readBlockPos, buf1 -> buf1.readRegistryValue(Block.STATE_IDS))),
            (buf, action) -> buf.writeMap(action.changes(), PacketByteBuf::writeBlockPos, (buf1, state) -> buf1.writeRegistryValue(Block.STATE_IDS, state))
        );

        register(new Identifier("fast-world-actions", "sphere_fill"), SphereFillWorldAction.class,
            buf -> new SphereFillWorldAction(buf.readBlockPos(), buf.readVarInt(), buf.readRegistryValue(Block.STATE_IDS)),
            (buf, action) -> {
                buf.writeBlockPos(action.center());
                buf.writeVarInt(action.radius());
                buf.writeRegistryValue(Block.STATE_IDS, action.targetState());
            }
        );

        register(new Identifier("fast-world-actions", "box_fill"), BoxFillWorldAction.class,
            buf -> {
                var box = BlockBox.create(buf.readBlockPos(), buf.readBlockPos());
                var targetState = buf.readRegistryValue(Block.STATE_IDS);

                return new BoxFillWorldAction(box, targetState);
            },
            (buf, action) -> {
                buf.writeBlockPos(new BlockPos(
                    action.box().getMinX(),
                    action.box().getMinY(),
                    action.box().getMinZ()
                ));

                buf.writeBlockPos(new BlockPos(
                    action.box().getMaxX(),
                    action.box().getMaxY(),
                    action.box().getMaxZ()
                ));

                buf.writeRegistryValue(Block.STATE_IDS, action.targetState());
            }
        );
    }

    private record SyncData<T extends WorldAction>(Identifier id, PacketByteBuf.PacketReader<T> reader, PacketByteBuf.PacketWriter<T> writer) {
    }
}
