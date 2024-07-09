package me.basiqueevangelist.fastworldactions.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class CodecUtils {
    public static final StreamCodec<ByteBuf, BoundingBox> BOUNDING_BOX = new StreamCodec<>() {
        @Override
        public BoundingBox decode(ByteBuf buffer) {
            return BoundingBox.fromCorners(FriendlyByteBuf.readBlockPos(buffer), FriendlyByteBuf.readBlockPos(buffer));
        }

        @Override
        public void encode(ByteBuf buffer, BoundingBox value) {
            FriendlyByteBuf.writeBlockPos(buffer, new BlockPos(
                value.minX(),
                value.minY(),
                value.minZ()
            ));

            FriendlyByteBuf.writeBlockPos(buffer, new BlockPos(
                value.maxX(),
                value.maxY(),
                value.maxZ()
            ));
        }
    };
}
