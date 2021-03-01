package craftiest;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("craftiest")
public class CraftiestMod
{
	public CraftiestMod()
	{
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void registerCommands(final RegisterCommandsEvent event)
	{
		CraftiestCommand.register(event.getDispatcher());
	}

	@SubscribeEvent
	public void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event)
	{
		World world = event.getWorld();

		if (world.isRemote || world.getDimensionKey() != CraftiestWorld.BARRIER_FLAT)
		{
			return;
		}

		BlockPos pos = event.getPos();
		PlayerEntity player = event.getPlayer();
		ItemStack stack = event.getItemStack();

		if (world.getBlockState(pos).isIn(Blocks.CHEST))
		{
			TileEntity tileEntity = world.getTileEntity(pos);

			if (tileEntity != null)
			{
				if (tileEntity.getTileData().getBoolean("craftiest_victory"))
				{
					if (tileEntity instanceof LockableLootTileEntity)
					{
						LockableLootTileEntity chest = (LockableLootTileEntity)tileEntity;

						if (!chest.isEmpty())
						{
							ItemStack firstStack = chest.getStackInSlot(0);

							if (firstStack.hasTag() && firstStack.getTag().getBoolean("craftiest_victory"))
							{
								player.sendMessage(new TranslationTextComponent("craftiest.victory_banner.advice"), player.getGameProfile().getId());

								for (PlayerEntity other : world.getPlayers())
								{
									other.sendMessage(new TranslationTextComponent("craftiest.victory_banner.found.message", player.getDisplayName()), player.getGameProfile().getId());
								}
							}
						}
					}
				}
				else if (tileEntity.getTileData().getBoolean("craftiest_fake"))
				{
					if (world.rand.nextDouble() <= 0.5D)
					{
						BlockPos.Mutable checkPos = new BlockPos.Mutable(pos.getX(), 80, pos.getZ());

						for (; world.isAirBlock(checkPos) || world.getBlockState(checkPos).isIn(Blocks.BARRIER);)
						{
							if (checkPos.getY() < 1)
							{
								break;
							}

							checkPos.move(Direction.DOWN);
						}

						Vector3d surfaceVec = Vector3d.copyCenteredHorizontally(checkPos.up());

						player.setPositionAndUpdate(surfaceVec.x, surfaceVec.y, surfaceVec.z);
						player.fallDistance = 0.0F;

						world.playSound(null, surfaceVec.x, surfaceVec.y, surfaceVec.z, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
					}
					else
					{
						LightningBoltEntity lightning = EntityType.LIGHTNING_BOLT.create(world);

						lightning.moveForced(player.getPositionVec());

						world.addEntity(lightning);
					}

					tileEntity.getTileData().putBoolean("craftiest_fake", false);

					if (tileEntity instanceof LockableTileEntity)
					{
						((LockableTileEntity)tileEntity).setCustomName(null);
					}
				}
			}
		}
		else if (stack.hasTag() && stack.getTag().getBoolean("craftiest_victory"))
		{
			if (world.getBlockState(pos).getMaterial().blocksMovement() &&
				world.getBlockState(pos.up()).getMaterial().isReplaceable() && world.getBlockState(pos.up(2)).getMaterial().isReplaceable() &&
				world.canBlockSeeSky(pos.up()) && world.canBlockSeeSky(pos.up(2)))
			{
				if (player instanceof ServerPlayerEntity)
				{
					((ServerPlayerEntity)player).connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE,
						new TranslationTextComponent("craftiest.winner.title"), 20 * 3, 20 * 5, 20 * 3));
				}

				STitlePacket titlePacket = new STitlePacket(STitlePacket.Type.TITLE,
					new TranslationTextComponent("craftiest.loser.title"), 20 * 3, 20 * 5, 20 * 3);

				for (PlayerEntity other : world.getPlayers())
				{
					if (other instanceof ServerPlayerEntity && !other.getGameProfile().equals(player.getGameProfile()))
					{
						((ServerPlayerEntity)other).connection.sendPacket(titlePacket);
					}

					other.sendMessage(new TranslationTextComponent("craftiest.victory.message", player.getDisplayName()), player.getGameProfile().getId());
				}

				world.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, 1.0F, 1.0F);

				stack.getTag().remove("craftiest_victory");
			}
			else
			{
				event.setCanceled(true);

				player.sendMessage(new TranslationTextComponent("craftiest.victory_banner.wrong.message").mergeStyle(TextFormatting.RED), player.getGameProfile().getId());
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent
	public void onItemTooltip(final ItemTooltipEvent event)
	{
		ItemStack stack = event.getItemStack();

		if (stack.hasTag() && stack.getTag().getBoolean("craftiest_victory"))
		{
			event.getToolTip().add(new TranslationTextComponent("craftiest.victory_banner.advice").mergeStyle(TextFormatting.GRAY));
		}
	}
}