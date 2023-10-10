package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PortalForcer;

@Mixin(PortalForcer.class)
public abstract class PortalForcerMixin {
  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"))
  private boolean redirectSetBlockState0$createPortal(ServerWorld caller, BlockPos pos, BlockState state) {
    return caller.setBlockState(pos, state);
  }

  @Redirect(method = "createPortal", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z", ordinal = 0))
  private boolean redirectSetBlockState1$createPortal(ServerWorld caller, BlockPos pos, BlockState state, int flags) {
    return caller.setBlockState(pos, state, flags);
  }
}
