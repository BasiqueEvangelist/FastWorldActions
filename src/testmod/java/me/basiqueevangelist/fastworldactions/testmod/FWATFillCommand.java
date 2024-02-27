package me.basiqueevangelist.fastworldactions.testmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.basiqueevangelist.fastworldactions.FastWorldActions;
import me.basiqueevangelist.fastworldactions.action.BoxFillWorldAction;
import net.minecraft.block.BlockState;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.BlockStateArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class FWATFillCommand {
    private FWATFillCommand() {

    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access) {
        dispatcher.register(literal("fwat")
            .then(literal("fill")
                .then(argument("from", BlockPosArgumentType.blockPos())
                    .then(argument("to", BlockPosArgumentType.blockPos())
                        .then(argument("with", BlockStateArgumentType.blockState(access))
                            .executes(FWATFillCommand::fill))))));
    }

    private static int fill(CommandContext<ServerCommandSource> ctx) {
        BlockPos from = BlockPosArgumentType.getBlockPos(ctx, "from");
        BlockPos to = BlockPosArgumentType.getBlockPos(ctx, "to");
        BlockState with = BlockStateArgumentType.getBlockState(ctx, "with").getBlockState();

        var box = BlockBox.create(from, to);

        FastWorldActions.action(new BoxFillWorldAction(box, with))
            .syncToPlayers()
            .run(ctx.getSource().getWorld());

        return 0;
    }
}
