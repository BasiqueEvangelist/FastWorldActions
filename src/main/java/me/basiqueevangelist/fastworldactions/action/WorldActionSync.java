package me.basiqueevangelist.fastworldactions.action;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public final class WorldActionSync {
    private static final Map<ResourceLocation, SyncData<?>> ACTIONS_BY_ID = new HashMap<>();
    private static final Map<Class<?>, SyncData<?>> ACTIONS_BY_CLASS = new HashMap<>();

    public static <T extends WorldAction> void register(ResourceLocation id, Class<T> klass, FriendlyByteBuf.Reader<T> reader, FriendlyByteBuf.Writer<T> writer) {
        var syncData = new SyncData<>(id, reader, writer);

        ACTIONS_BY_ID.put(id, syncData);
        ACTIONS_BY_CLASS.put(klass, syncData);
    }

    @SuppressWarnings("unchecked")
    public static void write(FriendlyByteBuf buf, WorldAction action) {
        var syncData = ACTIONS_BY_CLASS.get(action.getClass());

        if (syncData == null)
            throw new IllegalStateException(action.getClass().getName() + " isn't syncable.");

        buf.writeResourceLocation(syncData.id());
        ((FriendlyByteBuf.Writer<Object>) syncData.writer).accept(buf, action);
    }

    public static WorldAction read(FriendlyByteBuf buf) {
        var id = buf.readResourceLocation();
        var syncData = ACTIONS_BY_ID.get(id);

        if (syncData == null)
            throw new IllegalStateException("Received an unknown WorldAction id: " + id);

        return syncData.reader().apply(buf);
    }

    @ApiStatus.Internal
    public static void init() {
        register(new ResourceLocation("fast-world-actions", "fixed"), FixedWorldAction.class,
            buf -> new FixedWorldAction(buf.readMap(FriendlyByteBuf::readBlockPos, buf1 -> buf1.readById(Block.BLOCK_STATE_REGISTRY))),
            (buf, action) -> buf.writeMap(action.changes(), FriendlyByteBuf::writeBlockPos, (buf1, state) -> buf1.writeId(Block.BLOCK_STATE_REGISTRY, state))
        );

        register(new ResourceLocation("fast-world-actions", "sphere_fill"), SphereFillWorldAction.class,
            buf -> new SphereFillWorldAction(buf.readBlockPos(), buf.readVarInt(), buf.readById(Block.BLOCK_STATE_REGISTRY)),
            (buf, action) -> {
                buf.writeBlockPos(action.center());
                buf.writeVarInt(action.radius());
                buf.writeId(Block.BLOCK_STATE_REGISTRY, action.targetState());
            }
        );

        register(new ResourceLocation("fast-world-actions", "box_fill"), BoxFillWorldAction.class,
            buf -> {
                var box = BoundingBox.fromCorners(buf.readBlockPos(), buf.readBlockPos());
                var targetState = buf.readById(Block.BLOCK_STATE_REGISTRY);

                return new BoxFillWorldAction(box, targetState);
            },
            (buf, action) -> {
                buf.writeBlockPos(new BlockPos(
                    action.box().minX(),
                    action.box().minY(),
                    action.box().minZ()
                ));

                buf.writeBlockPos(new BlockPos(
                    action.box().maxX(),
                    action.box().maxY(),
                    action.box().maxZ()
                ));

                buf.writeId(Block.BLOCK_STATE_REGISTRY, action.targetState());
            }
        );
    }

    private record SyncData<T extends WorldAction>(ResourceLocation id, FriendlyByteBuf.Reader<T> reader, FriendlyByteBuf.Writer<T> writer) {
    }
}
