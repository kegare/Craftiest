package craftiest.field;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.tags.BlockTags;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class NetherFieldGenerator extends FieldGenerator
{
	public NetherFieldGenerator(ServerWorld worldIn, BlockPos posIn)
	{
		super(worldIn, posIn, 40, 6);
	}

	@Override
	protected void makeBase()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize, 0, originZ, originX + fieldSize, 64, originZ))
		{
			int y = pos.getY();

			if (y == 64 && !world.getBlockState(pos).isIn(Blocks.CRIMSON_NYLIUM))
			{
				world.setBlockState(pos, Blocks.CRIMSON_NYLIUM.getDefaultState(), 2);
			}
			else if (!world.getBlockState(pos).isIn(Blocks.NETHERRACK))
			{
				world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 2);
			}
		}
	}

	@Override
	protected void makeLiquids()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize + 10, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = 10; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 20), random.nextInt(50), 0);

			makeLiquid(pos, Blocks.LAVA.getDefaultState());
		}
	}

	@Override
	protected void makeOres()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize + 5, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = random.nextInt(30) + 50; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 10), random.nextInt(60), 0);

			makeOre(pos, 5, Blocks.NETHER_GOLD_ORE.getDefaultState());
		}
	}

	@Override
	protected void makeOre(BlockPos posIn, int count, BlockState oreBlock)
	{
		BlockPos.Mutable pos = posIn.toMutable();

		for (int i = 0; i < count; i++)
		{
			if (world.getBlockState(pos.move(getRandomFacing())).isIn(Blocks.NETHERRACK))
			{
				world.setBlockState(pos, oreBlock, 2);
			}
		}
	}

	@Override
	protected void decorateSurface()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize + 2, 65, originZ, originX + fieldSize - 2, 65, originZ))
		{
			if (pos.withinDistance(originPos, 3.0D))
			{
				continue;
			}

			if (random.nextDouble() <= 0.3D)
			{
				if (BlockTags.LOGS.contains(world.getBlockState(pos.west()).getBlock()))
				{
					continue;
				}

				if (BlockTags.LOGS.contains(world.getBlockState(pos.east()).getBlock()))
				{
					continue;
				}

				makeTree(pos, Blocks.CRIMSON_STEM.getDefaultState(), Blocks.NETHER_WART_BLOCK.getDefaultState());
			}
			else if (random.nextDouble() <= 0.5D)
			{
				world.setBlockState(pos, Blocks.RED_MUSHROOM.getDefaultState(), 2);
			}
			else if (random.nextDouble() <= 0.5D)
			{
				world.setBlockState(pos, Blocks.CRIMSON_ROOTS.getDefaultState(), 2);
			}
			else if (random.nextDouble() <= 0.5D)
			{
				world.setBlockState(pos, Blocks.CRIMSON_FUNGUS.getDefaultState(), 2);
			}
		}
	}

	@Override
	protected void makeTree(BlockPos posIn, BlockState logBlock, BlockState leavesBlock)
	{
		int height = random.nextInt(8) + 7;
		int leaves = 4;

		for (int i = 0; i < height; i++)
		{
			BlockPos pos = posIn.up(i);

			world.setBlockState(pos, logBlock, 2);

			if (i >= height - 4 && leaves > 0)
			{
				BlockPos.Mutable posCache = new BlockPos.Mutable();

				switch (leaves--)
				{
					case 1:
						world.setBlockState(pos.up(), leavesBlock, 2);

						if (world.isAirBlock(posCache.setPos(pos.west())))
						{
							world.setBlockState(posCache, leavesBlock, 2);
						}

						if (world.isAirBlock(posCache.setPos(pos.east())))
						{
							world.setBlockState(posCache, leavesBlock, 2);
						}

						break;
					default:
						for (int j = 1; j <= 2; j++)
						{
							if (world.isAirBlock(posCache.setPos(pos.west(j))))
							{
								world.setBlockState(posCache, leavesBlock, 2);
							}

							if (world.isAirBlock(posCache.setPos(pos.east(j))))
							{
								world.setBlockState(posCache, leavesBlock, 2);
							}
						}

						break;
				}
			}
		}
	}

	@Override
	protected void makeStructures()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize + 2, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = random.nextInt(15); i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(50) + 10, 0);

			makeMonsterRoom(pos, 5, EntityType.PIGLIN);
		}

		for (int i = random.nextInt(5); i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(50) + 10, 0);

			makeMonsterRoom(pos, 5, EntityType.ZOMBIFIED_PIGLIN);
		}

		for (int i = random.nextInt(4) + 3; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(50) + 1, 0);

			makeMonsterHouse(pos, 5, EntityType.WITHER_SKELETON);
		}

		for (int i = random.nextInt(4) + 3; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(50) + 1, 0);

			makeMonsterHouse(pos, 5, EntityType.BLAZE);
		}
	}

	protected void makeMonsterRoom(BlockPos posIn, int count, EntityType<?> type)
	{
		for (BlockPos pos : BlockPos.getAllInBoxMutable(posIn.west(3), posIn.east(3).up(4)))
		{
			if (world.isAirBlock(pos))
			{
				return;
			}
		}

		world.setBlockState(posIn, Blocks.NETHERRACK.getDefaultState(), 2);

		for (int i = 1; i <= 3; i++)
		{
			world.setBlockState(posIn.west(i), Blocks.NETHERRACK.getDefaultState(), 2);
			world.setBlockState(posIn.east(i), Blocks.NETHERRACK.getDefaultState(), 2);
		}

		for (int i = 1; i <= 3; i++)
		{
			BlockPos pos = posIn.up(i);

			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

			for (int j = 1; j <= 3; j++)
			{
				world.setBlockState(pos.west(j), j < 3 ? Blocks.AIR.getDefaultState() : Blocks.NETHERRACK.getDefaultState(), 2);
				world.setBlockState(pos.east(j), j < 3 ? Blocks.AIR.getDefaultState() : Blocks.NETHERRACK.getDefaultState(), 2);
			}
		}

		BlockPos pos = posIn.up(4);

		world.setBlockState(pos, Blocks.NETHERRACK.getDefaultState(), 2);

		for (int i = 1; i <= 3; i++)
		{
			world.setBlockState(pos.west(i), Blocks.NETHERRACK.getDefaultState(), 2);
			world.setBlockState(pos.east(i), Blocks.NETHERRACK.getDefaultState(), 2);
		}

		pos = posIn.up().west(2);

		for (int i = 0; i < count; i++)
		{
			Entity entity = type.spawn(world, null, null, null, i > 5 ? posIn.up() : pos.east(i), SpawnReason.STRUCTURE, false, false);

			if (entity != null && entity instanceof MobEntity)
			{
				((MobEntity)entity).enablePersistence();
			}
		}
	}

	protected void makeMonsterHouse(BlockPos posIn, int count, EntityType<? extends MobEntity> mobType)
	{
		for (BlockPos pos : BlockPos.getAllInBoxMutable(posIn.west(3), posIn.east(3).up(5)))
		{
			if (world.isAirBlock(pos))
			{
				return;
			}
		}

		world.setBlockState(posIn, Blocks.NETHER_BRICKS.getDefaultState(), 2);

		for (int i = 1; i <= 3; ++i)
		{
			world.setBlockState(posIn.west(i), Blocks.NETHER_BRICKS.getDefaultState(), 2);
			world.setBlockState(posIn.east(i), Blocks.NETHER_BRICKS.getDefaultState(), 2);
		}

		for (int i = 1; i <= 3; i++)
		{
			BlockPos pos = posIn.up(i);

			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

			for (int j = 1; j <= 3; j++)
			{
				world.setBlockState(pos.west(j), j < 3 ? Blocks.AIR.getDefaultState() : Blocks.NETHER_BRICKS.getDefaultState(), 2);
				world.setBlockState(pos.east(j), j < 3 ? Blocks.AIR.getDefaultState() : Blocks.NETHER_BRICKS.getDefaultState(), 2);
			}
		}

		BlockPos pos = posIn.up(4);
		BlockPos.Mutable posCache = new BlockPos.Mutable();

		world.setBlockState(posCache.setPos(pos), Blocks.NETHER_BRICKS.getDefaultState(), 2);
		world.setBlockState(posCache.move(Direction.UP), Blocks.NETHER_BRICK_FENCE.getDefaultState(), 2);

		for (int i = 1; i <= 3; i++)
		{
			world.setBlockState(posCache.setPos(pos.west(i)), Blocks.NETHER_BRICKS.getDefaultState(), 2);
			world.setBlockState(posCache.move(Direction.UP), Blocks.NETHER_BRICK_FENCE.getDefaultState(), 2);
			world.setBlockState(posCache.setPos(pos.east(i)), Blocks.NETHER_BRICKS.getDefaultState(), 2);
			world.setBlockState(posCache.move(Direction.UP), Blocks.NETHER_BRICK_FENCE.getDefaultState(), 2);
		}

		pos = posIn.up().west(2);

		for (int i = 0; i < count; i++)
		{
			MobEntity entity = mobType.spawn(world, null, null, null, pos.east(i), SpawnReason.STRUCTURE, false, false);

			if (entity != null)
			{
				entity.enablePersistence();
			}
		}
	}

	@Override
	protected void makeTraps() {}

	@Override
	protected void makeChests()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = 20; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(66), 0);

			if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_NETHER_BRIDGE);
			}
			else if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_ARMORER);
			}
			else if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_BUTCHER);
			}
			else
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_WEAPONSMITH);
			}

			if (random.nextDouble() <= 0.1D)
			{
				TileEntity tileEntity = world.getTileEntity(pos);

				if (tileEntity != null && tileEntity instanceof LockableLootTileEntity)
				{
					LockableLootTileEntity chest = (LockableLootTileEntity)tileEntity;

					chest.setInventorySlotContents(random.nextInt(chest.getSizeInventory()), new ItemStack(Items.DIAMOND));
				}
			}
		}
	}

	@Override
	protected void makeObsidianBox(BlockPos posIn, boolean fake)
	{
		for (BlockPos pos : BlockPos.getAllInBoxMutable(posIn.add(-1, -1, 0), posIn.add(1, 1, 0)))
		{
			world.setBlockState(pos, Blocks.CRYING_OBSIDIAN.getDefaultState());
		}

		makeChest(posIn, fake ? LootTables.CHESTS_SHIPWRECK_TREASURE : null);

		TileEntity tileEntity = world.getTileEntity(posIn);

		if (tileEntity != null && tileEntity instanceof LockableLootTileEntity)
		{
			LockableLootTileEntity chest = (LockableLootTileEntity)tileEntity;

			if (fake)
			{
				chest.setCustomName(new TranslationTextComponent("craftiest.trap_box.name"));
				chest.getTileData().putBoolean("craftiest_fake", true);
			}
			else
			{
				chest.setCustomName(new TranslationTextComponent("craftiest.treasure_box.name"));
				chest.getTileData().putBoolean("craftiest_victory", true);

				ItemStack stack = new ItemStack(Items.RED_BANNER);

				stack.setDisplayName(new TranslationTextComponent("craftiest.victory_banner.name"));
				stack.getOrCreateTag().putBoolean("craftiest_victory", true);

				chest.setInventorySlotContents(0, stack);
			}
		}
	}
}