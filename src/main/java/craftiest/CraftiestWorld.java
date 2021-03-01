package craftiest;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class CraftiestWorld
{
	public static final ResourceLocation BARRIER_FLAT_LOCATION = new ResourceLocation("craftiest", "barrier_flat");

	public static final RegistryKey<World> BARRIER_FLAT = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, BARRIER_FLAT_LOCATION);

	public static void moveToField(PlayerEntity player, BlockPos pos)
	{
		if (player.world.getDimensionKey() != BARRIER_FLAT)
		{
			MinecraftServer server = player.getServer();

			if (server != null)
			{
				player.changeDimension(server.getWorld(BARRIER_FLAT), new CraftiestTeleporter(pos));
			}
		}

		player.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
		player.fallDistance = 0.0F;

		player.clearActivePotions();

		player.inventory.clear();

		player.setGameType(GameType.SURVIVAL);

		player.sendMessage(new TranslationTextComponent("craftiest.challenge.message"), player.getGameProfile().getId());

		if (player instanceof ServerPlayerEntity)
		{
			((ServerPlayerEntity)player).func_242111_a(player.world.getDimensionKey(), pos, 0.0F, true, false);
		}
	}

	public static ItemStack createInvitationTicket(BlockPos pos)
	{
		ItemStack stack = new ItemStack(Items.PAPER);

		stack.setDisplayName(new TranslationTextComponent("craftiest.invitation_ticket.name"));
		stack.getOrCreateTag().putBoolean("craftiest_invitation", true);
		stack.getTag().put("craftiest_pos", NBTUtil.writeBlockPos(pos));

		return stack;
	}
}