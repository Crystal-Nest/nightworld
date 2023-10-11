package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.base.Optional;

import crystalspider.nightworld.NightworldLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.PortalForcer;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

/**
 * Injects into {@link ServerPlayerEntity} to alter dimension travel.
 */
@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends Entity {
  /**
   * Mock constructor to make the compiler happy.
   */
  private ServerPlayerEntityMixin(EntityType<?> type, World world) {
    super(type, world);
  }

  /**
   * Injects before the {@link PortalForcer#createPortal(BlockPos, Axis)} call in the {@link ServerPlayerEntity#getPortalRect(ServerWorld, BlockPos, boolean, WorldBorder)}.
   * <p>
   * Sets the nightworld origin dimension flag for this player.
   * 
   * @param destWorld
   * @param destPos
   * @param destIsNether
   * @param worldBorder
   * @param cir
   */
  @Inject(method = "getPortalRect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/PortalForcer;createPortal(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)Ljava/util/Optional;", shift = At.Shift.BEFORE))
  private void onGetPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder, CallbackInfoReturnable<Optional<BlockLocating.Rectangle>> cir) {
    NightworldLoader.nightworldOriginThread.set(this.world.getRegistryKey() == NightworldLoader.NIGHTWORLD);
  }
}
