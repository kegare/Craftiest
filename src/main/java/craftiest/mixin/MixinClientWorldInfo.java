package craftiest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import craftiest.CraftiestWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld.ClientWorldInfo;

@Mixin(ClientWorldInfo.class)
public abstract class MixinClientWorldInfo
{
	private boolean isBarrierFlat()
	{
		Minecraft mc = Minecraft.getInstance();

		return mc.world != null && mc.world.getDimensionKey() == CraftiestWorld.BARRIER_FLAT;
	}

	@Inject(at = @At("HEAD"), method = "getVoidFogHeight()D", cancellable = true)
	public void getVoidFogHeight(CallbackInfoReturnable<Double> callback)
	{
		if (isBarrierFlat())
		{
			callback.setReturnValue(0.0D);
		}
	}

	@Inject(at = @At("HEAD"), method = "getFogDistance()D", cancellable = true)
	public void getFogDistance(CallbackInfoReturnable<Double> callback)
	{
		if (isBarrierFlat())
		{
			callback.setReturnValue(1.0D);
		}
	}
}