package craftiest;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import craftiest.field.FieldGenerator;
import craftiest.field.ForestFieldGenerator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CraftiestCommand
{
	public static void register(final CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("craftiest").then(registerTravel()).then(registerForest()));
	}

	private static ArgumentBuilder<CommandSource, ?> registerTravel()
	{
		return Commands.literal("travel").executes(ctx -> execute(ctx, CraftiestCommand::travelDimension));
	}

	private static ArgumentBuilder<CommandSource, ?> registerForest()
	{
		return Commands.literal("forest").executes(ctx -> execute(ctx, CraftiestCommand::setupForestField));
	}

	private static int execute(CommandContext<CommandSource> context, CommandConsumer<CommandContext<CommandSource>> command) throws CommandSyntaxException
	{
		return command.run(context);
	}

	private static void travelDimension(CommandContext<CommandSource> context) throws CommandSyntaxException
	{
		MinecraftServer server = context.getSource().getServer();
		ServerPlayerEntity player = context.getSource().asPlayer();
		ServerWorld world = context.getSource().getWorld();

		if (world.getDimensionKey() == World.OVERWORLD)
		{
			ServerWorld barrierFlat = server.getWorld(CraftiestWorld.BARRIER_FLAT);

			if (barrierFlat != null)
			{
				player.changeDimension(barrierFlat, new CraftiestTeleporter(null));
			}
		}
		else if (world.getDimensionKey() == CraftiestWorld.BARRIER_FLAT)
		{
			player.changeDimension(server.getWorld(World.OVERWORLD), new CraftiestTeleporter(null));
		}
	}

	private static void setupField(CommandContext<CommandSource> context, BlockPos pos, FieldGenerator generator)
	{
		ServerWorld world = context.getSource().getWorld();

		if (world.getDimensionKey() != CraftiestWorld.BARRIER_FLAT)
		{
			context.getSource().sendErrorMessage(new TranslationTextComponent("craftiest.command.error.world"));

			return;
		}

		generator.generate();

		for (ServerPlayerEntity player : world.getPlayers())
		{
			if (pos.withinDistance(new BlockPos(player.getPosX(), pos.getY(), player.getPosZ()), 100.0D))
			{
				CraftiestWorld.moveToField(player, pos);
			}
		}
	}

	private static void setupForestField(CommandContext<CommandSource> context) throws CommandSyntaxException
	{
		ServerWorld world = context.getSource().getWorld();
		ChunkPos chunkPos = world.getChunk(new BlockPos(context.getSource().getPos())).getPos();
		BlockPos pos = new BlockPos(chunkPos.getXStart() + 8, 65, chunkPos.getZStart() + 8);

		setupField(context, pos, new ForestFieldGenerator(world, pos));
	}

	interface CommandConsumer<T>
	{
		void accept(T t) throws CommandSyntaxException;

		default int run(T t) throws CommandSyntaxException
		{
			accept(t);

			return 1;
		}
	}
}