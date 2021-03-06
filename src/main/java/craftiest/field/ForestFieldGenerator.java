package craftiest.field;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class ForestFieldGenerator extends FieldGenerator
{
	public ForestFieldGenerator(ServerWorld worldIn, BlockPos posIn)
	{
		super(worldIn, posIn, 30, 5);
	}

	@Override
	protected void makeLiquids()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize + 5, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = 3; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 10), random.nextInt(random.nextInt(random.nextInt(30) + 20) + 10), 0);

			makeLiquid(pos, Blocks.LAVA.getDefaultState());
		}

		for (int i = 6; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 10), random.nextInt(40) + 20, 0);

			makeLiquid(pos, Blocks.WATER.getDefaultState());
		}
	}

	@Override
	protected void decorateSurface()
	{
		int originX = originPos.getX();
		int originZ = originPos.getZ();

		for (BlockPos pos : BlockPos.getAllInBoxMutable(originX - fieldSize + 2, 65, originZ, originX + fieldSize - 2, 65, originZ))
		{
			if (pos.withinDistance(originPos, 2.0D))
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

				makeTree(pos, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_LEAVES.getDefaultState());
			}
		}

		makeGrass();
	}

	@Override
	protected void makeStructures()
	{
		BlockPos startPos = new BlockPos(originPos.getX() - fieldSize + 2, 0, originPos.getZ());
		int fullLength = fieldSize * 2;

		for (int i = random.nextInt(3) + 2; i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(30) + 30, 0);

			makeVillageHouse(pos, Blocks.OAK_LOG.getDefaultState(), Blocks.OAK_PLANKS.getDefaultState());
		}

		for (int i = random.nextInt(10); i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(50) + 10, 0);

			makeMonsterRoom(pos, Blocks.COBBLESTONE.getDefaultState(), 5, EntityType.ZOMBIE);
		}

		for (int i = random.nextInt(10); i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(50) + 10, 0);

			makeMonsterRoom(pos, Blocks.COBBLESTONE.getDefaultState(), 4, EntityType.SKELETON);
		}

		for (int i = random.nextInt(10); i > 0; i--)
		{
			BlockPos pos = startPos.add(random.nextInt(fullLength - 4), random.nextInt(50) + 10, 0);

			makeMonsterRoom(pos, Blocks.COBBLESTONE.getDefaultState(), 3, EntityType.CREEPER);
		}
	}
}