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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.dimension.AreaHelper;

/**
 * Injects into {@link AreaHelper} to alter dimension travel.
 */
@Mixin(AreaHelper.class)
public abstract class AreaHelperMixin implements NightworldPortalChecker {
  /**
   * Shadowed {@link AreaHelper#world}.
   */
  @Shadow
  private WorldAccess world;
  /**
   * Shadowed {@link AreaHelper#negativeDir}.
   */
  @Shadow
  private Direction negativeDir;
  /**
   * Shadowed {@link AreaHelper#foundPortalBlocks}.
   */
  @Shadow
  private int foundPortalBlocks;
  /**
   * Shadowed {@link AreaHelper#lowerCorner}.
   */
  @Shadow
  @Nullable
  private BlockPos lowerCorner;
  /**
   * Shadowed {@link AreaHelper#width}.
   */
  @Shadow
  private int width;
  /**
   * Shadowed {@link AreaHelper#height}.
   */
  @Shadow
  private int height;

  /**
   * Whether it's a Nightworld Portal.
   */
  private boolean isNightworldPortal = false;

  /**
   * Shadowed {@link AreaHelper#validStateInsidePortal(BlockState)}.
   * 
   * @return whether the state is valid.
   */
  @Shadow
  private static boolean validStateInsidePortal(BlockState state) {
    throw new UnsupportedOperationException("Tried to call a dummy body of a shadowed method: AreaHelper#validStateInsidePortal(BlockState)");
  }

  /**
   * Shadowed {@link AreaHelper#isValid()}.
   * 
   * @return whether the portal is valid.
   */
  @Shadow
  public abstract boolean isValid();

  /**
   * Accessor to allow changes to {@link AreaHelper#width}.
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
  @Inject(method = "<init>(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)V", at = @At(value = "TAIL"))
  private void onInit(WorldAccess world, BlockPos pos, Axis axis, CallbackInfo ci) {
    if (!world.isClient()) {
      ServerWorld serverWorld = (ServerWorld) world;
      if (this.isValid() && serverWorld.getRegistryKey() == NightworldLoader.NIGHTWORLD) {
        // If it's a Nether Portal and we are in the Nightworld, prevent creating the portal.
        this.lowerCorner = null;
        this.setWidth(1);
        this.height = 1;
      } else if (!this.isValid() && (serverWorld.getRegistryKey() == World.OVERWORLD || serverWorld.getRegistryKey() == NightworldLoader.NIGHTWORLD)) {
        // If it's not a Nether Portal and we are either in the Overworld or the Nightworld, check if it's a Nightworld Portal.
        this.lowerCorner = this.getLowerCornerForNightworld(pos);
        if (this.lowerCorner == null) {
          this.lowerCorner = pos;
          this.setWidth(1);
          this.height = 1;
        } else {
          this.setWidth(this.getWidthForNightworld());
          if (this.width > 0) {
            this.height = this.getHeightForNightworld();
            this.isNightworldPortal = true;
          }
        }
      }
    }
  }

  /**
   * Copy-paste of {@link AreaHelper#getLowerCorner(BlockPos)}, changed only to use Crying Obsidian.
   * 
   * @param pos
   * @return
   */
  @Nullable
  private BlockPos getLowerCornerForNightworld(BlockPos pos) {
    int i = Math.max(this.world.getBottomY(), pos.getY() - 21);
    while (pos.getY() > i && AreaHelperMixin.validStateInsidePortal(this.world.getBlockState(pos.down()))) {
      pos = pos.down();
    }
    Direction direction = this.negativeDir.getOpposite();
    int j = this.getWidthForNightworld(pos, direction) - 1;
    if (j < 0) {
      return null;
    }
    return pos.offset(direction, j);
  }

  /**
   * Copy-paste of {@link AreaHelper#getWidth()}, changed only to use Crying Obsidian.
   * 
   * @return
   */
  private int getWidthForNightworld() {
    int i = this.getWidthForNightworld(this.lowerCorner, this.negativeDir);
    if (i < 2 || i > 21) {
      return 0;
    }
    return i;
  }

  /**
   * Copy-paste of {@link AreaHelper#getWidth(BlockPos, Direction)}, changed only to use Crying Obsidian.
   * 
   * @param pos
   * @param direction
   * @return
   */
  private int getWidthForNightworld(BlockPos pos, Direction direction) {
    BlockPos.Mutable mutable = new BlockPos.Mutable();
    for (int i = 0; i <= 21; ++i) {
      mutable.set(pos).move(direction, i);
      BlockState blockState = this.world.getBlockState(mutable);
      if (!AreaHelperMixin.validStateInsidePortal(blockState)) {
        if (!blockState.isOf(Blocks.CRYING_OBSIDIAN)) break;
        return i;
      }
      BlockState blockState2 = this.world.getBlockState(mutable.move(Direction.DOWN));
      if (!blockState2.isOf(Blocks.CRYING_OBSIDIAN)) break;
    }
    return 0;
  }

  /**
   * Copy-paste of {@link AreaHelper#getHeight()}, changed only to use Crying Obsidian.
   * 
   * @return
   */
  private int getHeightForNightworld() {
    BlockPos.Mutable mutable = new BlockPos.Mutable();
    int i = this.getPotentialHeightForNightworld(mutable);
    if (i < 3 || i > 21 || !this.isHorizontalFrameValidForNightworld(mutable, i)) {
      return 0;
    }
    return i;
  }

  /**
   * Copy-paste of {@link AreaHelper#isHorizontalFrameValid(BlockPos.Mutable, int)}, changed only to use Crying Obsidian.
   * 
   * @param pos
   * @param height
   * @return
   */
  private boolean isHorizontalFrameValidForNightworld(BlockPos.Mutable pos, int height) {
    for (int i = 0; i < this.width; ++i) {
      BlockPos.Mutable mutable = pos.set(this.lowerCorner).move(Direction.UP, height).move(this.negativeDir, i);
      if (this.world.getBlockState(mutable).isOf(Blocks.CRYING_OBSIDIAN)) continue;
      return false;
    }
    return true;
  }

  /**
   * Copy-paste of {@link AreaHelper#getPotentialHeight(BlockPos.Mutable)}, changed only to use Crying Obsidian.
   * 
   * @param pos
   * @return
   */
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
        if (!AreaHelperMixin.validStateInsidePortal(blockState)) {
          return i;
        }
        if (!blockState.isOf(Blocks.NETHER_PORTAL)) continue;
        ++this.foundPortalBlocks;
      }
    }
    return 21;
  }
}
