package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import crystalspider.nightworld.api.NightworldPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Injects into {@link NetherPortalBlock} to to alter Nightworld Portals mob spawn.
 */
@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
  /**
   * Injects into the method {@link NetherPortalBlock#randomTick(BlockState, ServerLevel, BlockPos, RandomSource)} before the call to {@link BlockState#isValidSpawn(BlockGetter, BlockPos, EntityType)}.
   * <p>
   * Allows Zombies and Skeletons spawn when it's a Nightworld Portal.
   * 
   * @param state
   * @param world
   * @param pos
   * @param random
   * @param ci
   */
  @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z", shift = Shift.BEFORE))
  private void onRandomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random, CallbackInfo ci) {
    if (NightworldPortalChecker.isNightworldPortal(world, pos.above())) {
      if (random.nextInt(0, 100) < 50) {
        this.handleSpawnEntity(EntityType.ZOMBIE, state, world, pos);
      } else {
        this.handleSpawnEntity(EntityType.SKELETON, state, world, pos);
      }
    }
  }

  /**
   * Redirects the call to {@link BlockState#isValidSpawn(BlockGetter, BlockPos, EntityType)} inside the method {@link NetherPortalBlock#randomTick(BlockState, ServerLevel, BlockPos, RandomSource)}.
   * <p>
   * Prevents Zombified Piglins spawn when it's a Nightworld Portal.
   * 
   * @param caller
   * @param world
   * @param pos
   * @param entityType
   * @return
   */
  @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z"))
  private boolean redirectAllowsSpawning(BlockState caller, BlockGetter world, BlockPos pos, EntityType<ZombifiedPiglin> entityType) {
    return !NightworldPortalChecker.isNightworldPortal((Level) world, pos.above()) && caller.isValidSpawn(world, pos, entityType);
  }

  /**
   * Handles spawning an entity of the given type if allowed.
   * 
   * @param <T>
   * @param entityType
   * @param state
   * @param world
   * @param pos
   */
  private <T extends EntityType<?>> void handleSpawnEntity(T entityType, BlockState state, ServerLevel world, BlockPos pos) {
    Entity entity;
    if (world.getBlockState(pos).isValidSpawn(world, pos, entityType) && (entity = entityType.spawn(world, pos.above(), MobSpawnType.STRUCTURE)) != null) {
      entity.setPortalCooldown();
    }
  }
}
