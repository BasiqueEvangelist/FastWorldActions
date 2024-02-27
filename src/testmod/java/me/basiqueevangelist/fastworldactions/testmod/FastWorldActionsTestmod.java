package me.basiqueevangelist.fastworldactions.testmod;

import me.basiqueevangelist.fastworldactions.FastWorldActions;
import me.basiqueevangelist.fastworldactions.action.SphereFillWorldAction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.literal;

public class FastWorldActionsTestmod implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            FWATFillCommand.register(dispatcher, registryAccess);

            dispatcher.register(literal("explosion")
                .executes(ctx -> {
                    var center = BlockPos.ofFloored(ctx.getSource().getPosition());

                    var world = ctx.getSource().getWorld();
                    int radius = 128;
//                    var pos = new BlockPos.Mutable();
//                    int radiusSq = radius * radius;
//                    Map<BlockPos, BlockState> changes = new HashMap<>();
//
//                    for (int ox = -radius; ox < radius; ox++) {
//                        for (int oy = -radius; oy < radius; oy++) {
//                            for (int oz = -radius; oz < radius; oz++) {
//                                if (ox * ox + oy * oy + oz * oz > radiusSq) continue;
//
//                                pos.set(center.getX() + ox, center.getY() + oy, center.getZ() + oz);
//                                var state = world.getBlockState(pos);
//
//                                if (state.isAir() || state.getBlock().getBlastResistance() > 10000) continue;
//
//                                changes.put(pos.toImmutable(), Blocks.AIR.getDefaultState());
//                            }
//                        }
//                    }
//
//                    FastWorldActions.run(world, new MapWorldAction(changes));

                    FastWorldActions.action(new SphereFillWorldAction(center, radius, Blocks.AIR.getDefaultState()))
                        .syncToPlayers()
                        .run(world);

                    return 0;
                }));
        });
    }
}
