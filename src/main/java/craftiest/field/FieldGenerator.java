package craftiest.field;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.server.ServerWorld;

public class FieldGenerator
{
	public static final ResourceLocation CHESTS_MISC_CHEST = new ResourceLocation("craftiest", "chests/misc_chest");

	protected final ServerWorld world;
	protected final BlockPos originPos;
	protected final Random random;

	protected final int fieldSize;
	protected final int boxCount;

	public FieldGenerator(ServerWorld worldIn, BlockPos posIn, int size, int boxCount)
	{
		this.world = worldIn;
		this.originPos = posIn;
		this.random = new Random();
		this.fieldSize = size;
		this.boxCount = boxCount;
	}

	public void generate()
	{
		makeSpace();
		makeBase();
		makeLiquids();
		makeOres();
		makeStructures();
		makeTraps();
		makeChests();
		makeObsidianBoxes();
		decorateSurface();
		makeBarrier();
	}

	protected void makeSpace()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();
		int maxY = world.getHeight() - 1;

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize - 20, 0, originZ - 20, originX + fieldSize + 20, maxY, originZ + 20))
		{
			if (pos.getY() == 0)
			{
				if (!world.getBlockState(pos).isIn(Blocks.BARRIER))
				{
					world.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2);
				}
			}
			else if (!world.getBlockState(pos).isIn(Blocks.AIR))
			{
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
		}

		for (Entity entity : world.getEntities(null, e -> e.getType() != EntityType.PLAYER))
		{
			entity.remove();
		}
	}

	protected void makeBase()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize, 0, originZ, originX + fieldSize, 64, originZ))
		{
			int y = pos.getY();

			if (y == 64 && !world.getBlockState(pos).isIn(Blocks.GRASS_BLOCK))
			{
				world.setBlockState(pos, Blocks.GRASS_BLOCK.getDefaultState(), 2);
			}
			else if (y > 60 && !world.getBlockState(pos).isIn(Blocks.DIRT))
			{
				world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
			}
			else if (!world.getBlockState(pos).isIn(Blocks.STONE))
			{
				world.setBlockState(pos, Blocks.STONE.getDefaultState(), 2);
			}
		}
	}

	protected void makeLiquids() {}

	protected void makeTrapezoid(BlockPos posIn, int up, int down, int width, int min, BlockState state)
	{
		for (int y = posIn.getY() + up; y > posIn.getY() - down; y--)
		{
			BlockPos pos1 = new BlockPos(posIn.getX(), y, posIn.getZ());

			for (BlockPos pos2 : BlockPos.getAllInBoxMutable(pos1.west(width), pos1.east(width)))
			{
				if (world.getBlockState(pos2).getMaterial().blocksMovement())
				{
					world.setBlockState(pos2, state, 2);
				}
			}

			if (--width < min)
			{
				break;
			}
		}
	}

	protected void makeLiquid(BlockPos posIn, BlockState liquidBlock)
	{
		makeTrapezoid(posIn, random.nextInt(3) + 1, random.nextInt(5) + 2, random.nextInt(5) + 5, 5, liquidBlock);
	}

	protected void makeOres()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = random.nextInt(15) + 30; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(20) + 40, 0);

			makeOre(pos, 10, Blocks.COAL_ORE.getDefaultState());
		}

		for (int i = random.nextInt(15) + 30; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(35) + 10, 0);

			makeOre(pos, 5, Blocks.IRON_ORE.getDefaultState());
		}

		for (int i = random.nextInt(10) + 10; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(40) + 10, 0);

			makeOre(pos, 3, Blocks.REDSTONE_ORE.getDefaultState());
		}

		for (int i = random.nextInt(10) + 10; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(30) + 30, 0);

			makeOre(pos, 10, Blocks.EMERALD_ORE.getDefaultState());
		}

		for (int i = random.nextInt(10) + 10; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(15), 0);

			makeOre(pos, 3, Blocks.DIAMOND_ORE.getDefaultState());
		}

		for (int i = 5; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(10) + 50, 0);

			makeTrapezoid(pos, random.nextInt(2) + 1, random.nextInt(2) + 1, random.nextInt(5) + 2, 2, Blocks.SAND.getDefaultState());
		}

		for (int i = 30; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(50), 0);

			makeOre(pos, 10, Blocks.GRAVEL.getDefaultState());
		}

		for (int i = 20; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(50), 0);

			makeOre(pos, 10, Blocks.GRANITE.getDefaultState());
		}

		for (int i = 20; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(50), 0);

			makeOre(pos, 10, Blocks.DIORITE.getDefaultState());
		}

		for (int i = 20; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(50), 0);

			makeOre(pos, 10, Blocks.ANDESITE.getDefaultState());
		}
	}

	protected Direction getRandomFacing()
	{
		Direction facing = Direction.getRandomDirection(random);

		if (facing.getAxis() == Direction.Axis.Z)
		{
			return facing.rotateY();
		}

		return facing;
	}

	protected void makeOre(BlockPos posIn, int count, BlockState oreBlock)
	{
		BlockPos.Mutable pos = posIn.toMutable();

		for (int i = 0; i < count; i++)
		{
			if (world.getBlockState(pos.move(getRandomFacing())).isIn(Blocks.STONE))
			{
				world.setBlockState(pos, oreBlock, 2);
			}
		}
	}

	protected void decorateSurface() {}

	protected void makeTree(BlockPos posIn, BlockState logBlock, BlockState leavesBlock)
	{
		int height = random.nextInt(3) + 4;
		int leaves = 3;

		for (int i = 0; i < height; i++)
		{
			BlockPos pos = posIn.up(i);

			world.setBlockState(pos, logBlock, 2);

			if (i >= height - 3 && leaves > 0)
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
					case 2:
					case 3:
						for (int j = 1; j <= leaves; ++j)
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
					default:
						break;
				}
			}
		}
	}

	protected void makeGrass()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize, 64, originZ, originX + fieldSize, 64, originZ))
		{
			if (random.nextDouble() <= 0.5D && world.isAirBlock(pos.up()))
			{
				BlockState state = world.getBlockState(pos);

				if (state.getBlock() instanceof IGrowable)
				{
					IGrowable growable = (IGrowable)state.getBlock();

					if (growable.canGrow(world, pos, state, false))
					{
						growable.grow(world, random, pos, state);
					}
				}
			}
		}
	}

	protected void makeStructures() {}

	protected void makeVillageHouse(BlockPos posIn, BlockState logBlock, BlockState planksBlock)
	{
		for (BlockPos pos : BlockPos.getAllInBoxMutable(posIn.west(3), posIn.east(3).up(5)))
		{
			if (world.isAirBlock(pos))
			{
				return;
			}
		}

		world.setBlockState(posIn, planksBlock, 2);

		for (int i = 1; i <= 3; ++i)
		{
			world.setBlockState(posIn.west(i), planksBlock, 2);
			world.setBlockState(posIn.east(i), planksBlock, 2);
		}

		for (int i = 1; i <= 3; i++)
		{
			BlockPos pos = posIn.up(i);

			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

			for (int j = 1; j <= 3; j++)
			{
				world.setBlockState(pos.west(j), j < 3 ? Blocks.AIR.getDefaultState() : planksBlock, 2);
				world.setBlockState(pos.east(j), j < 3 ? Blocks.AIR.getDefaultState() : planksBlock, 2);
			}
		}

		BlockPos pos = posIn.up(4);
		BlockPos.Mutable posCache = new BlockPos.Mutable();

		world.setBlockState(posCache.setPos(pos), planksBlock, 2);
		world.setBlockState(posCache.move(Direction.UP), logBlock, 2);

		for (int i = 1; i <= 3; i++)
		{
			world.setBlockState(posCache.setPos(pos.west(i)), planksBlock, 2);
			world.setBlockState(posCache.move(Direction.UP), logBlock, 2);
			world.setBlockState(posCache.setPos(pos.east(i)), planksBlock, 2);
			world.setBlockState(posCache.move(Direction.UP), logBlock, 2);
		}

		pos = posIn.up().west(2);

		for (int i = 0; i < 5; i++)
		{
			Entity entity = EntityType.VILLAGER.spawn(world, null, null, null, pos.east(i), SpawnReason.STRUCTURE, false, false);

			if (entity != null && entity instanceof VillagerEntity)
			{
				VillagerEntity villager = ((VillagerEntity)entity);
				VillagerProfession profession = VillagerProfession.ARMORER;

				if (random.nextDouble() <= 0.3D)
				{
					profession = VillagerProfession.BUTCHER;
				}
				else if (random.nextDouble() <= 0.3D)
				{
					profession = VillagerProfession.FARMER;
				}

				villager.setVillagerData(villager.getVillagerData().withProfession(profession).withLevel(random.nextInt(3) + 1));
			}
		}
	}

	protected void makeMonsterRoom(BlockPos posIn, BlockState stoneBlock, int count, EntityType<? extends MobEntity> type)
	{
		for (BlockPos pos : BlockPos.getAllInBoxMutable(posIn.west(3), posIn.east(3).up(4)))
		{
			if (world.isAirBlock(pos))
			{
				return;
			}
		}

		world.setBlockState(posIn, stoneBlock, 2);

		for (int i = 1; i <= 3; i++)
		{
			world.setBlockState(posIn.west(i), stoneBlock, 2);
			world.setBlockState(posIn.east(i), stoneBlock, 2);
		}

		for (int i = 1; i <= 3; i++)
		{
			BlockPos pos = posIn.up(i);

			world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);

			for (int j = 1; j <= 3; j++)
			{
				world.setBlockState(pos.west(j), j < 3 ? Blocks.AIR.getDefaultState() : stoneBlock, 2);
				world.setBlockState(pos.east(j), j < 3 ? Blocks.AIR.getDefaultState() : stoneBlock, 2);
			}
		}

		BlockPos pos = posIn.up(4);

		world.setBlockState(pos, stoneBlock, 2);

		for (int i = 1; i <= 3; i++)
		{
			world.setBlockState(pos.west(i), stoneBlock, 2);
			world.setBlockState(pos.east(i), stoneBlock, 2);
		}

		pos = posIn.up().west(2);

		for (int i = 0; i < count; i++)
		{
			MobEntity entity = type.spawn(world, null, null, null, i > 5 ? posIn.up() : pos.east(i), SpawnReason.STRUCTURE, false, false);

			if (entity != null)
			{
				entity.enablePersistence();
			}
		}
	}

	protected void makeTraps()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = random.nextInt(10) + 3; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(55) + 5, 0);

			makeMineTrap(pos);
		}
	}

	protected void makeMineTrap(BlockPos posIn)
	{
		world.setBlockState(posIn.down(), Blocks.STONE.getDefaultState(), 2);
		world.setBlockState(posIn.down(2), Blocks.TNT.getDefaultState(), 2);
		world.setBlockState(posIn, Blocks.STONE_PRESSURE_PLATE.getDefaultState(), 2);
	}

	protected void makeChests()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = 15; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(66), 0);

			if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_ARMORER);
			}
			else if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_WEAPONSMITH);
			}
			else if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_BUTCHER);
			}
			else
			{
				makeChest(pos, CHESTS_MISC_CHEST);
			}
		}
	}

	protected void makeChest(BlockPos posIn, @Nullable ResourceLocation lootTable)
	{
		if (world.getBlockState(posIn.down()).getMaterial().blocksMovement() && !world.getBlockState(posIn).isIn(Blocks.CHEST))
		{
			world.setBlockState(posIn, StructurePiece.correctFacing(world, posIn, Blocks.CHEST.getDefaultState()), 2);

			if (lootTable != null)
			{
				LockableLootTileEntity.setLootTable(world, random, posIn, lootTable);
			}
		}
	}

	protected void makeObsidianBoxes()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize + 5, 1, originPos.getZ());
		int fullLength = fieldSize * 2;
		boolean victoryBox = false;

		for (int i = boxCount; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 10), random.nextInt(50), 0);

			if (victoryBox)
			{
				makeObsidianBox(pos, true);
			}
			else if (random.nextDouble() <= 0.5D)
			{
				makeObsidianBox(pos, false);

				victoryBox = true;
			}
			else
			{
				makeObsidianBox(pos, i > 1);
			}
		}
	}

	protected void makeObsidianBox(BlockPos posIn, boolean fake)
	{
		for (BlockPos pos : BlockPos.getAllInBoxMutable(posIn.add(-1, -1, 0), posIn.add(1, 1, 0)))
		{
			world.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
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

				ItemStack stack = new ItemStack(Items.YELLOW_BANNER);

				stack.setDisplayName(new TranslationTextComponent("craftiest.victory_banner.name"));
				stack.getOrCreateTag().putBoolean("craftiest_victory", true);

				chest.setInventorySlotContents(0, stack);
			}
		}
	}

	protected void makeBarrier()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();
		BlockPos.Mutable posCache = new BlockPos.Mutable();

		for (int y = 0; y <= 80; y++)
		{
			if (!world.getBlockState(posCache.setPos(originX - fieldSize - 1, y, originZ)).isIn(Blocks.BARRIER))
			{
				world.setBlockState(posCache, Blocks.BARRIER.getDefaultState(), 2);
			}

			if (!world.getBlockState(posCache.setPos(originX + fieldSize + 1, y, originZ)).isIn(Blocks.BARRIER))
			{
				world.setBlockState(posCache, Blocks.BARRIER.getDefaultState(), 2);
			}
		}

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize - 1, 0, originZ - 1, originX + fieldSize + 1, 80, originZ - 1))
		{
			if (!world.getBlockState(pos).isIn(Blocks.BARRIER))
			{
				world.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2);
			}
		}

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize - 1, 0, originZ + 1, originX + fieldSize + 1, 80, originZ + 1))
		{
			if (!world.getBlockState(pos).isIn(Blocks.BARRIER))
			{
				world.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2);
			}
		}

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize - 1, 80, originZ, originX + fieldSize + 1, 80, originZ))
		{
			if (!world.getBlockState(pos).isIn(Blocks.BARRIER))
			{
				world.setBlockState(pos, Blocks.BARRIER.getDefaultState(), 2);
			}
		}
	}
}