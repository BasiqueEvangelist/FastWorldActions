package me.basiqueevangelist.fastworldactions.mixin.client;

import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LightEngine.class)
public interface LightEngineAccessor {
    @Invoker
    void callCheckNode(long packedPos);
}
