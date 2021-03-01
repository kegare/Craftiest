package craftiest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.serialization.Lifecycle;

import net.minecraft.world.storage.ServerWorldInfo;

@Mixin(ServerWorldInfo.class)
public class MixinServerWorldInfo
{
	@Inject(at = @At("HEAD"), method = "getLifecycle", cancellable = true)
	public void getLifecycle(CallbackInfoReturnable<Lifecycle> callback)
	{
		callback.setReturnValue(Lifecycle.stable());
	}
}