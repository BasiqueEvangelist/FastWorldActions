package me.basiqueevangelist.fastworldactions.testmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.basiqueevangelist.fastworldactions.FastWorldActions;
import me.basiqueevangelist.fastworldactions.action.BoxFillWorldAction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public final class FWATFillCommand {
    private FWATFillCommand() {

    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext access) {
        dispatcher.register(literal("fwat")
            .then(literal("fill")
                .then(argument("from", BlockPosArgument.blockPos())
                    .then(argument("to", BlockPosArgument.blockPos())
                        .then(argument("with", BlockStateArgument.block(access))
                            .executes(FWATFillCommand::fill))))));
    }

    private static int fill(CommandContext<CommandSourceStack> ctx) {
        BlockPos from = BlockPosArgument.getBlockPos(ctx, "from");
        BlockPos to = BlockPosArgument.getBlockPos(ctx, "to");
        BlockState with = BlockStateArgument.getBlock(ctx, "with").getState();

        var box = BoundingBox.fromCorners(from, to);

        FastWorldActions.action(new BoxFillWorldAction(box, with))
            .syncToPlayers()
            .notifyNeighbours(false)
            .forceState(true)
            .run(ctx.getSource().getLevel());

        return 0;
    }
}
