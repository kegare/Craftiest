package craftiest;

import java.util.function.Function;

import javax.annotation.Nullable;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

public class CraftiestTeleporter implements ITeleporter
{
	private final BlockPos destPos;

	public CraftiestTeleporter(@Nullable BlockPos pos)
	{
		this.destPos = pos;
	}

	@Override
	public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function<Boolean, Entity> repositionEntity)
	{
		if (currentWorld.getDimensionKey() == World.OVERWORLD)
		{
			entity.getPersistentData().put("craftiest_travel", NBTUtil.writeBlockPos(entity.getPosition()));
		}

		Entity transported = repositionEntity.apply(false);

		if (destPos != null)
		{
			transported.setPositionAndUpdate(destPos.getX() + 0.5D, destPos.getY(), destPos.getZ() + 0.5D);
			transported.fallDistance = 0.0F;

			return transported;
		}

		if (destWorld.getDimensionKey() == CraftiestWorld.BARRIER_FLAT)
		{
			transported.setPositionAndUpdate(0.5D, 1.5D, 0.5D);
			transported.fallDistance = 0.0F;

			if (!destWorld.getBlockState(BlockPos.ZERO).isIn(Blocks.BARRIER))
			{
				destWorld.setBlockState(BlockPos.ZERO, Blocks.BARRIER.getDefaultState(), 2);
			}

			if (!destWorld.getBlockState(BlockPos.ZERO.up()).isIn(Blocks.BARRIER))
			{
				destWorld.setBlockState(BlockPos.ZERO.up(), Blocks.AIR.getDefaultState(), 2);
			}

			if (!destWorld.getBlockState(BlockPos.ZERO.up(2)).isIn(Blocks.BARRIER))
			{
				destWorld.setBlockState(BlockPos.ZERO.up(2), Blocks.AIR.getDefaultState(), 2);
			}

			return transported;
		}

		if (destWorld.getDimensionKey() == World.OVERWORLD)
		{
			CompoundNBT nbt = transported.getPersistentData();

			if (nbt.contains("craftiest_travel"))
			{
				BlockPos pos = NBTUtil.readBlockPos(nbt.getCompound("craftiest_travel"));

				if (pos != BlockPos.ZERO)
				{
					transported.setPositionAndUpdate(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D);
					transported.fallDistance = 0.0F;

					nbt.remove("craftiest_travel");

					if (destWorld.hasNoCollisions(transported))
					{
						return transported;
					}
				}
			}
		}

		BlockPos spawnPos = destWorld.getSpawnPoint();

		if (transported instanceof ServerPlayerEntity)
		{
			BlockPos pos = ((ServerPlayerEntity)transported).func_241140_K_();

			if (pos != null)
			{
				spawnPos = pos;
			}
		}

		transported.setPositionAndUpdate(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
		transported.fallDistance = 0.0F;

		return transported;
	}
}