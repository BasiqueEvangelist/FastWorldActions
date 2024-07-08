package me.basiqueevangelist.fastworldactions.mixin.client;

import me.basiqueevangelist.fastworldactions.task.WorldActionTask;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {
    @Inject(method = "getGameInformation", at = @At("RETURN"))
    private void addDebugText(CallbackInfoReturnable<List<String>> cir) {
        cir.getReturnValue().add("[FWA] " +
            WorldActionTask.SERVER_SECTIONS +
            " server tasks, " +
            WorldActionTask.CLIENT_SECTIONS +
            " client tasks");
    }
}
