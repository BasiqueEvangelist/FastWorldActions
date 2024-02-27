package me.basiqueevangelist.fastworldactions.mixin.client;

import net.minecraft.world.chunk.light.ChunkLightProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkLightProvider.class)
public interface ChunkLightProviderAccessor {
    @Invoker
    void callMethod_51529(long blockPos);
}
