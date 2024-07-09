package me.basiqueevangelist.fastworldactions.action;

import me.basiqueevangelist.fastworldactions.FastWorldActions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

    public static final StreamCodec<RegistryFriendlyByteBuf, WorldAction> ACTION_CODEC = new StreamCodec<RegistryFriendlyByteBuf, WorldAction>() {
        @Override
        public WorldAction decode(RegistryFriendlyByteBuf buffer) {
            var id = buffer.readResourceLocation();
            var syncData = ACTIONS_BY_ID.get(id);

            if (syncData == null)
                throw new IllegalStateException("Received an unknown WorldAction id: " + id);

            return syncData.codec().decode(buffer);
        }

        @SuppressWarnings("unused")
        @Override
        public void encode(RegistryFriendlyByteBuf buffer, WorldAction value) {
            var syncData = ACTIONS_BY_CLASS.get(value.getClass());

            if (syncData == null)
                throw new IllegalStateException(value.getClass().getName() + " isn't syncable.");

            buffer.writeResourceLocation(syncData.id());
            ((StreamCodec<RegistryFriendlyByteBuf, Object>) syncData.codec()).encode(buffer, value);
        }
    };

    public static <T extends WorldAction> void register(ResourceLocation id, Class<T> klass, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        var syncData = new SyncData<>(id, codec);

        ACTIONS_BY_ID.put(id, syncData);
        ACTIONS_BY_CLASS.put(klass, syncData);
    }

    @ApiStatus.Internal
    public static void init() {
        register(FastWorldActions.id("fixed"), FixedWorldAction.class, FixedWorldAction.STREAM_CODEC);
        register(FastWorldActions.id("sphere_fill"), SphereFillWorldAction.class, SphereFillWorldAction.STREAM_CODEC);
        register(FastWorldActions.id("box_fill"), BoxFillWorldAction.class, BoxFillWorldAction.STREAM_CODEC);
    }

    private record SyncData<T extends WorldAction>(ResourceLocation id, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
    }
}
