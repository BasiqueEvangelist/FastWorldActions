package me.basiqueevangelist.fastworldactions.testmod;

import me.basiqueevangelist.fastworldactions.WorldAction;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class FastWorldActionsTestmod implements ModInitializer {
    @Override
    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("explosion")
                .executes(ctx -> {
                    var center = BlockPos.ofFloored(ctx.getSource().getPosition());

                    var world = ctx.getSource().getWorld();
                    var pos = new BlockPos.Mutable();
                    int radius = 128;
                    int radiusSq = radius * radius;
                    Map<BlockPos, BlockState> changes = new HashMap<>();

                    for (int ox = -radius; ox < radius; ox++) {
                        for (int oy = -radius; oy < radius; oy++) {
                            for (int oz = -radius; oz < radius; oz++) {
                                if (ox * ox + oy * oy + oz * oz > radiusSq) continue;

                                pos.set(center.getX() + ox, center.getY() + oy, center.getZ() + oz);
                                var state = world.getBlockState(pos);

                                if (state.isAir() || state.getBlock().getBlastResistance() > 10000) continue;

                                changes.put(pos.toImmutable(), Blocks.AIR.getDefaultState());
                            }
                        }
                    }

                    WorldAction.run(world, changes);

                    return 0;
                }));
        });
    }
}
