package crystalspider.nightworld.mixin;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import crystalspider.nightworld.NightworldLoader;
import crystalspider.nightworld.api.EntityPortal;
import crystalspider.nightworld.api.MinecraftEntity;
import crystalspider.nightworld.api.Teleportable;
import net.minecraft.BlockUtil;
import net.minecraft.BlockUtil.FoundRectangle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.PortalInfo;
import net.minecraft.world.phys.Vec3;

@Mixin({Entity.class, ServerPlayer.class})
public abstract class EntityAndPlayerMixin implements Teleportable, EntityPortal, MinecraftEntity {
  @Unique
	@Nullable
	protected PortalInfo customPortalInfo;

  @Shadow
  protected abstract Optional<FoundRectangle> getExitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder);

  @Override
  public abstract Optional<FoundRectangle> exitPortal(ServerLevel destination, BlockPos pos, boolean destIsNether, WorldBorder worldBorder); 

  @Override
  public abstract Vec3 relativePortalPosition(Axis axis, FoundRectangle rectangle);

  @Override
  public abstract BlockPos portalEntrancePos();

  @Override
  public @Nullable PortalInfo getCustomPortalInfo() {
    return customPortalInfo;
  }

  @Override
  public @Nullable PortalInfo consumeCustomPortalInfo() {
    PortalInfo portalInfo = new PortalInfo(customPortalInfo.pos, customPortalInfo.speed, customPortalInfo.yRot, customPortalInfo.xRot);
    customPortalInfo = null;
    return portalInfo;
  }

  @Override
  public void setCustomPortalInfo(@Nullable PortalInfo portalInfo) {
    customPortalInfo = portalInfo;
  }

  @Inject(method = "findDimensionEntryPoint", at = @At("HEAD"), cancellable = true, allow = 1)
	private void onFindDimensionEntryPoint(ServerLevel destination, CallbackInfoReturnable<PortalInfo> cir) {
		if (customPortalInfo != null) {
			cir.setReturnValue(consumeCustomPortalInfo());
		}
	}

  /**
   * Injects at the start of the method {@link Entity#getExitPortal(ServerLevel, BlockPos, boolean, WorldBorder)}.
   * <p>
   * Sets the nightworld origin dimension flag for this entity.
   * 
   * @param destWorld
   * @param destPos
   * @param destIsNether
   * @param worldBorder
   * @param cir
   */
  @Inject(method = "getExitPortal", at = @At(value = "HEAD"))
  private void onGetExitPortal(ServerLevel destWorld, BlockPos destPos, boolean destIsNether, WorldBorder worldBorder, CallbackInfoReturnable<Optional<BlockUtil.FoundRectangle>> cir) {
    NightworldLoader.nightworldOriginThread.set(getLevel().dimension() == NightworldLoader.NIGHTWORLD);
  }
}
