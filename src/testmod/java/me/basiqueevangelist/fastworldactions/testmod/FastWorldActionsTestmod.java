package me.basiqueevangelist.fastworldactions.testmod;

import me.basiqueevangelist.fastworldactions.FastWorldActions;
import me.basiqueevangelist.fastworldactions.action.SphereFillWorldAction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;

import static net.minecraft.commands.Commands.literal;

public class FastWorldActionsTestmod implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FWATFillCommand.register(dispatcher, registryAccess);

            dispatcher.register(literal("explosion")
                .executes(ctx -> {
                    var center = BlockPos.containing(ctx.getSource().getPosition());

                    var world = ctx.getSource().getLevel();
                    int radius = 128;

                    FastWorldActions.action(new SphereFillWorldAction(center, radius, Blocks.AIR.defaultBlockState()))
                        .syncToPlayers()
                        .run(world);

                    return 0;
                }));
        });
    }
}
