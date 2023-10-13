package crystalspider.nightworld.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import crystalspider.nightworld.NightworldLoader;
import crystalspider.nightworld.api.NightworldPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;

/**
 * Injects into {@link PortalShape} to alter dimension travel.
 */
@Mixin(PortalShape.class)
public abstract class PortalShapeMixin implements NightworldPortalChecker {
  /**
   * Shadowed {@link PortalShape#level}.
   */
  @Shadow
  private LevelAccessor level;
  /**
   * Shadowed {@link PortalShape#rightDir}.
   */
  @Shadow
  private Direction rightDir;
  /**
   * Shadowed {@link PortalShape#numPortalBlocks}.
   */
  @Shadow
  private int numPortalBlocks;
  /**
   * Shadowed {@link PortalShape#bottomLeft}.
   */
  @Shadow
  @Nullable
  private BlockPos bottomLeft;
  /**
   * Shadowed {@link PortalShape#width}.
   */
  @Shadow
  private int width;
  /**
   * Shadowed {@link PortalShape#height}.
   */
  @Shadow
  private int height;

  /**
   * Whether it's a Nightworld Portal.
   */
  private boolean isNightworldPortal = false;

  /**
   * Shadowed {@link PortalShape#isEmpty(BlockState)}.
   * 
   * @return whether the state is valid.
   */
  @Shadow
  private static boolean isEmpty(BlockState state) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: PortalShape#isEmpty(BlockState)");
  }

  /**
   * Shadowed {@link PortalShape#isValid()}.
   * 
   * @return whether the portal is valid.
   */
  @Shadow
  public abstract boolean isValid();

  /**
   * Accessor to allow changes to {@link PortalShape#width}.
   * 
   * @param width
   */
  @Mutable
  @Accessor("width")
  protected abstract void setWidth(int width);

  @Override
  public boolean isNightworldPortal() {
    return isNightworldPortal;
  }
  
  /**
   * Injects at the end of the constructor.
   * <p>
   * Checks if a Nightworld Portal can be created.
   * 
   * @param world
   * @param pos
   * @param axis
   * @param ci
   */
  @Inject(method = "<init>(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction$Axis;)V", at = @At(value = "TAIL"))
  private void onInit(LevelAccessor world, BlockPos pos, Axis axis, CallbackInfo ci) {
    if (!world.isClientSide()) {
      ServerLevel serverWorld = (ServerLevel) world;
      if (this.isValid() && serverWorld.dimension() == NightworldLoader.NIGHTWORLD) {
        // If it's a Nether Portal and we are in the Nightworld, prevent creating the portal.
        this.bottomLeft = null;
        this.setWidth(1);
        this.height = 1;
      } else if (!this.isValid() && (serverWorld.dimension() == Level.OVERWORLD || serverWorld.dimension() == NightworldLoader.NIGHTWORLD)) {
        // If it's not a Nether Portal and we are either in the Overworld or the Nightworld, check if it's a Nightworld Portal.
        this.bottomLeft = this.calculateBottomLeftForNightworld(pos);
        if (this.bottomLeft == null) {
          this.bottomLeft = pos;
          this.setWidth(1);
          this.height = 1;
        } else {
          this.setWidth(this.calculateWidthForNightworld());
          if (this.width > 0) {
            this.height = this.calculateHeightForNightworld();
            this.isNightworldPortal = true;
          }
        }
      }
    }
  }

  /**
   * Copy-paste of {@link PortalShape#calculateBottomLeft(BlockPos)}, changed only to use Crying Obsidian.
   * 
   * @param pos
   * @return
   */
  @Nullable
  private BlockPos calculateBottomLeftForNightworld(BlockPos pos) {
    for(int i = Math.max(this.level.getMinBuildHeight(), pos.getY() - 21); pos.getY() > i && isEmpty(this.level.getBlockState(pos.below())); pos = pos.below());
    Direction direction = this.rightDir.getOpposite();
    int j = this.getDistanceUntilEdgeAboveFrameForNightworld(pos, direction) - 1;
    return j < 0 ? null : pos.relative(direction, j);
  }

  /**
   * Copy-paste of {@link PortalShape#calculateWidth()}, changed only to use Crying Obsidian.
   * 
   * @return
   */
  private int calculateWidthForNightworld() {
    int i = this.getDistanceUntilEdgeAboveFrameForNightworld(this.bottomLeft, this.rightDir);
    return i >= 2 && i <= 21 ? i : 0;
  }

  /**
   * Copy-paste of {@link PortalShape#getDistanceUntilEdgeAboveFrame(BlockPos, Direction)}, changed only to use Crying Obsidian.
   * 
   * @param pos
   * @param direction
   * @return
   */
  private int getDistanceUntilEdgeAboveFrameForNightworld(BlockPos pos, Direction direction) {
    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
    for(int i = 0; i <= 21; ++i) {
      blockpos$mutableblockpos.set(pos).move(direction, i);
      BlockState blockstate = this.level.getBlockState(blockpos$mutableblockpos);
      if (!isEmpty(blockstate)) {
        if (blockstate.is(Blocks.CRYING_OBSIDIAN)) {
          return i;
        }
        break;
      }
      BlockState blockstate1 = this.level.getBlockState(blockpos$mutableblockpos.move(Direction.DOWN));
      if (!blockstate1.is(Blocks.CRYING_OBSIDIAN)) {
        break;
      }
    }
    return 0;
  }

  /**
   * Copy-paste of {@link PortalShape#calculateHeight()}, changed only to use Crying Obsidian.
   * 
   * @return
   */
  private int calculateHeightForNightworld() {
    BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
    int i = this.getDistanceUntilTopForNightworld(blockpos$mutableblockpos);
    return i >= 3 && i <= 21 && this.hasTopFrameForNightworld(blockpos$mutableblockpos, i) ? i : 0;
  }

  /**
   * Copy-paste of {@link PortalShape#hasTopFrame(BlockPos.MutableBlockPos, int)}, changed only to use Crying Obsidian.
   * 
   * @param pos
   * @param height
   * @return
   */
  private boolean hasTopFrameForNightworld(BlockPos.MutableBlockPos pos, int height) {
    for(int i = 0; i < this.width; ++i) {
      BlockPos.MutableBlockPos blockpos$mutableblockpos = pos.set(this.bottomLeft).move(Direction.UP, height).move(this.rightDir, i);
      if (!this.level.getBlockState(blockpos$mutableblockpos).is(Blocks.CRYING_OBSIDIAN)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Copy-paste of {@link PortalShape#getDistanceUntilTop(BlockPos.MutableBlockPos)}, changed only to use Crying Obsidian.
   * 
   * @param pos
   * @return
   */
  private int getDistanceUntilTopForNightworld(BlockPos.MutableBlockPos pos) {
    for(int i = 0; i < 21; ++i) {
      pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, -1);
      if (!this.level.getBlockState(pos).is(Blocks.CRYING_OBSIDIAN)) {
        return i;
      }
      pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, this.width);
      if (!this.level.getBlockState(pos).is(Blocks.CRYING_OBSIDIAN)) {
        return i;
      }
      for(int j = 0; j < this.width; ++j) {
        pos.set(this.bottomLeft).move(Direction.UP, i).move(this.rightDir, j);
        BlockState blockstate = this.level.getBlockState(pos);
        if (!isEmpty(blockstate)) {
          return i;
        }
        if (blockstate.is(Blocks.NETHER_PORTAL)) {
          ++this.numPortalBlocks;
        }
      }
   }
   return 21;
  }
}
