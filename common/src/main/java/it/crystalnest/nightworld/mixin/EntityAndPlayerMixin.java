package it.crystalnest.nightworld.mixin;

import it.crystalnest.nightworld.Constants;
import it.crystalnest.nightworld.api.Teleportable;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalInfo;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Injects into {@link Entity} and {@link ServerPlayer} to alter dimension travel.
 */
@Mixin({Entity.class, ServerPlayer.class})
public abstract class EntityAndPlayerMixin implements Teleportable {
  /**
   * Custom portal info.
   */
  @Unique
  @Nullable
  protected PortalInfo customPortalInfo;

  @Nullable
  @Override
  public PortalInfo getCustomPortalInfo() {
    return customPortalInfo;
  }

  @Override
  public void setCustomPortalInfo(@Nullable PortalInfo info) {
    customPortalInfo = info;
  }

  @Override
  @Nullable
  public PortalInfo consumeCustomPortalInfo() {
    if (customPortalInfo != null) {
      PortalInfo portalInfo = new PortalInfo(customPortalInfo.pos, customPortalInfo.speed, customPortalInfo.yRot, customPortalInfo.xRot);
      customPortalInfo = null;
      return portalInfo;
    }
    return null;
  }

  /**
   * Injects at the start of the method {@link Entity#findDimensionEntryPoint(ServerLevel)}.<br />
   * If present, sets the custom portal info.
   *
   * @param destination destination.
   * @param cir {@link CallbackInfoReturnable}.
   */
  @Inject(method = "findDimensionEntryPoint", at = @At("HEAD"), cancellable = true)
  private void onFindDimensionEntryPoint(ServerLevel destination, CallbackInfoReturnable<PortalInfo> cir) {
    if (customPortalInfo != null) {
      cir.setReturnValue(consumeCustomPortalInfo());
    }
  }

  /**
   * Injects at the start of the method {@link Entity#getExitPortal(ServerLevel, BlockPos, boolean, WorldBorder)}.<br />
   * Sets the nightworld origin dimension flag for this entity.
   *
   * @param destination destination.
   * @param pos destination position.
   * @param destIsNether whether the destination is the Nether.
   * @param worldBorder world border.
   * @param cir {@link CallbackInfoReturnable}.
   */
  @Inject(method = "getExitPortal", at = @At(value = "HEAD"))
  private void onGetExitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir) {
    Constants.NIGHTWORLD_ORIGIN_THREAD.set(((Entity) (Object) this).level.dimension() == Constants.NIGHTWORLD);
  }
}
