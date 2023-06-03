package crystalspider.nightworld.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.NetherPortal;

@Mixin(NetherPortal.class)
public abstract class NetherPortalMixin {
  /**
   * Shadowed {@link NetherPortal#world}.
   */
  @Shadow
  private WorldAccess world;
  /**
   * Shadowed {@link NetherPortal#negativeDir}.
   */
  @Shadow
  @Nullable
  private Direction negativeDir;
  /**
   * Shadowed {@link NetherPortal#foundPortalBlocks}.
   */
  @Shadow
  private int foundPortalBlocks;
  /**
   * Shadowed {@link NetherPortal#lowerCorner}.
   */
  @Shadow
  private BlockPos lowerCorner;
  /**
   * Shadowed {@link NetherPortal#width}.
   */
  @Shadow
  private int width;
  /**
   * Shadowed {@link NetherPortal#height}.
   */
  @Shadow
  private int height;

  /**
   * Shadowed {@link NetherPortal#validStateInsidePortal(BlockState)}.
   * 
   * @return whether the state is valid.
   */
  @Shadow
  private static boolean validStateInsidePortal(BlockState state) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: NetherPortal#validStateInsidePortal(BlockState)");
  }

  /**
   * Shadowed {@link NetherPortal#isValid()}.
   * 
   * @return whether the portal is valid.
   */
  @Shadow
  public abstract boolean isValid();

  @Mutable
  @Accessor("width")
  public abstract void setWidth(int width);

  @Inject(method = "<init>(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)V", at = @At(value = "TAIL"))
  private void onInit(WorldAccess world, BlockPos pos, Axis axis, CallbackInfo ci) {
    if (!this.isValid()) {
      this.lowerCorner = this.getLowerCornerForNightworld(pos);
      if (this.lowerCorner == null) {
        this.lowerCorner = pos;
        this.setWidth(1);
        this.height = 1;
      } else {
        this.setWidth(this.getWidthForNightworld());
        if (this.width > 0) {
          this.height = this.getHeightForNightworld();
        }
      }
    }
  }

  @Nullable
  private BlockPos getLowerCornerForNightworld(BlockPos pos) {
    int i = Math.max(this.world.getBottomY(), pos.getY() - 21);
    while (pos.getY() > i && NetherPortalMixin.validStateInsidePortal(this.world.getBlockState(pos.down()))) {
      pos = pos.down();
    }
    Direction direction = this.negativeDir.getOpposite();
    int j = this.getWidthForNightworld(pos, direction) - 1;
    if (j < 0) {
      return null;
    }
    return pos.offset(direction, j);
  }

  private int getWidthForNightworld() {
    int i = this.getWidthForNightworld(this.lowerCorner, this.negativeDir);
    if (i < 2 || i > 21) {
      return 0;
    }
    return i;
  }

  private int getWidthForNightworld(BlockPos pos, Direction direction) {
    BlockPos.Mutable mutable = new BlockPos.Mutable();
    for (int i = 0; i <= 21; ++i) {
      mutable.set(pos).move(direction, i);
      BlockState blockState = this.world.getBlockState(mutable);
      if (!NetherPortalMixin.validStateInsidePortal(blockState)) {
        if (!blockState.isOf(Blocks.CRYING_OBSIDIAN)) break;
        return i;
      }
      BlockState blockState2 = this.world.getBlockState(mutable.move(Direction.DOWN));
      if (!blockState2.isOf(Blocks.CRYING_OBSIDIAN)) break;
    }
    return 0;
  }

  private int getHeightForNightworld() {
    BlockPos.Mutable mutable = new BlockPos.Mutable();
    int i = this.getPotentialHeightForNightworld(mutable);
    if (i < 3 || i > 21 || !this.isHorizontalFrameValidForNightworld(mutable, i)) {
      return 0;
    }
    return i;
  }

  private boolean isHorizontalFrameValidForNightworld(BlockPos.Mutable pos, int height) {
    for (int i = 0; i < this.width; ++i) {
      BlockPos.Mutable mutable = pos.set(this.lowerCorner).move(Direction.UP, height).move(this.negativeDir, i);
      if (this.world.getBlockState(mutable).isOf(Blocks.CRYING_OBSIDIAN)) continue;
      return false;
    }
    return true;
  }

  private int getPotentialHeightForNightworld(BlockPos.Mutable pos) {
    for (int i = 0; i < 21; ++i) {
      pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, -1);
      if (!this.world.getBlockState(pos).isOf(Blocks.CRYING_OBSIDIAN)) {
        return i;
      }
      pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, this.width);
      if (!this.world.getBlockState(pos).isOf(Blocks.CRYING_OBSIDIAN)) {
        return i;
      }
      for (int j = 0; j < this.width; ++j) {
        pos.set(this.lowerCorner).move(Direction.UP, i).move(this.negativeDir, j);
        BlockState blockState = this.world.getBlockState(pos);
        if (!NetherPortalMixin.validStateInsidePortal(blockState)) {
          return i;
        }
        if (!blockState.isOf(Blocks.NETHER_PORTAL)) continue;
        ++this.foundPortalBlocks;
      }
    }
    return 21;
  }
}
