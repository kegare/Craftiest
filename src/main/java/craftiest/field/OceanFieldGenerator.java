package craftiest.field;

import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.loot.LootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class OceanFieldGenerator extends FieldGenerator
{
	public static final ResourceLocation CHESTS_OCEAN_BONUS_CHEST = new ResourceLocation("craftiest", "chests/ocean_bonus_chest");

	public OceanFieldGenerator(ServerWorld worldIn, BlockPos posIn)
	{
		super(worldIn, posIn, 50, 10);
	}

	@Override
	protected void makeBase()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize, 0, originZ, originX + fieldSize, 64, originZ))
		{
			int y = pos.getY();

			if (y > 53 && !world.getBlockState(pos).isIn(Blocks.WATER))
			{
				world.setBlockState(pos, Blocks.WATER.getDefaultState(), 2);
			}
			else if (y > 50 && !world.getBlockState(pos).isIn(Blocks.SAND))
			{
				world.setBlockState(pos, Blocks.SAND.getDefaultState(), 2);
			}
			else if (y > 47 && !world.getBlockState(pos).isIn(Blocks.DIRT))
			{
				world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 2);
			}
			else if (!world.getBlockState(pos).isIn(Blocks.STONE))
			{
				world.setBlockState(pos, Blocks.STONE.getDefaultState(), 2);
			}
		}
	}

	@Override
	protected void makeLiquids()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize + 5, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = 7; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 10), random.nextInt(random.nextInt(random.nextInt(25) + 15) + 5), 0);

			makeLiquid(pos, Blocks.LAVA.getDefaultState());
		}

		for (int i = 12; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 10), random.nextInt(35) + 15, 0);

			makeLiquid(pos, Blocks.WATER.getDefaultState());
		}
	}

	@Override
	protected void decorateSurface()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize + 2, 54, originZ, originX + fieldSize - 2, 54, originZ))
		{
			if (random.nextDouble() <= 0.3D)
			{
				world.setBlockState(pos, Blocks.SEAGRASS.getDefaultState(), 2);
			}
			else if (random.nextDouble() <= 0.1D)
			{
				world.setBlockState(pos, Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, random.nextInt(4) + 1), 2);
			}
		}

		spawnWaterCreatures();
	}

	protected void spawnWaterCreatures()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = 15; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(5) + 58, 0);

			if (world.getBlockState(pos).isIn(Blocks.WATER))
			{
				EntityType.PUFFERFISH.spawn(world, null, null, null, pos, SpawnReason.NATURAL, false, false);
			}
		}

		for (int i = 15; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(5) + 58, 0);

			if (world.getBlockState(pos).isIn(Blocks.WATER))
			{
				EntityType.TROPICAL_FISH.spawn(world, null, null, null, pos, SpawnReason.NATURAL, false, false);
			}
		}

		for (int i = 5; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(5) + 58, 0);

			if (world.getBlockState(pos).isIn(Blocks.WATER))
			{
				EntityType.SQUID.spawn(world, null, null, null, pos, SpawnReason.NATURAL, false, false);
			}
		}
	}

	@Override
	protected void makeTraps()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = random.nextInt(10) + 3; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(40) + 5, 0);

			makeMineTrap(pos);
		}
	}

	@Override
	protected void makeChests()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = 25; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), random.nextInt(45), 0);

			if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_SIMPLE_DUNGEON);
			}
			else if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_ARMORER);
			}
			else if (random.nextDouble() <= 0.3D)
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_WEAPONSMITH);
			}
			else
			{
				makeChest(pos, LootTables.CHESTS_VILLAGE_VILLAGE_BUTCHER);
			}
		}

		for (int i = 10; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength), 54, 0);

			if (!world.getBlockState(pos.west()).isIn(Blocks.CHEST) && !world.getBlockState(pos.east()).isIn(Blocks.CHEST) &&
				world.getBlockState(pos.down()).getMaterial().blocksMovement())
			{
				makeChest(pos, CHESTS_OCEAN_BONUS_CHEST);
			}
		}
	}

	@Override
	protected void makeStructures()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize + 2, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = random.nextInt(3) + 1; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(15) + 25, 0);

			makeVillageHouse(pos, Blocks.DARK_OAK_LOG.getDefaultState(), Blocks.DARK_OAK_PLANKS.getDefaultState());
		}

		for (int i = random.nextInt(10); i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(35) + 5, 0);

			makeMonsterRoom(pos, Blocks.COBBLESTONE.getDefaultState(), 5, EntityType.DROWNED);
		}

		for (int i = random.nextInt(10); i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(35) + 5, 0);

			makeMonsterRoom(pos, Blocks.COBBLESTONE.getDefaultState(), 4, EntityType.SKELETON);
		}

		for (int i = random.nextInt(10); i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(35) + 5, 0);

			makeMonsterRoom(pos, Blocks.COBBLESTONE.getDefaultState(), 3, EntityType.CREEPER);
		}
	}
}