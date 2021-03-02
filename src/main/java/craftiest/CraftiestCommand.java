package craftiest;

import java.util.function.Consumer;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import craftiest.field.ForestFieldGenerator;
import craftiest.field.PlainsFieldGenerator;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CraftiestCommand
{
	public static void register(final CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("craftiest").then(registerTravel()).then(registerForest()).then(registerPlains()));
	}

	private static ArgumentBuilder<CommandSource, ?> registerTravel()
	{
		return Commands.literal("travel").executes(ctx -> execute(ctx, CraftiestCommand::travelDimension));
	}

	private static ArgumentBuilder<CommandSource, ?> registerForest()
	{
		return Commands.literal("forest").executes(ctx -> execute(ctx, CraftiestCommand::setupForestField));
	}

	private static ArgumentBuilder<CommandSource, ?> registerPlains()
	{
		return Commands.literal("plains").executes(ctx -> execute(ctx, CraftiestCommand::setupPlainsField));
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
		else
		{
			context.getSource().sendErrorMessage(new TranslationTextComponent("craftiest.command.error.travel"));
		}
	}

	private static void setupField(CommandContext<CommandSource> context, BlockPos pos, Consumer<BlockPos> generator)
	{
		ServerWorld world = context.getSource().getWorld();

		if (world.getDimensionKey() != CraftiestWorld.BARRIER_FLAT)
		{
			context.getSource().sendErrorMessage(new TranslationTextComponent("craftiest.command.error.field"));

			return;
		}

		generator.accept(pos);

		for (ServerPlayerEntity player : world.getPlayers())
		{
			CraftiestWorld.moveToField(player, pos);
		}
	}

	private static void setupForestField(CommandContext<CommandSource> context) throws CommandSyntaxException
	{
		setupField(context, new BlockPos(8, 65, 8), pos -> new ForestFieldGenerator(context.getSource().getWorld(), pos).generate());
	}

	private static void setupPlainsField(CommandContext<CommandSource> context) throws CommandSyntaxException
	{
		setupField(context, new BlockPos(8, 65, 8), pos -> new PlainsFieldGenerator(context.getSource().getWorld(), pos).generate());
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