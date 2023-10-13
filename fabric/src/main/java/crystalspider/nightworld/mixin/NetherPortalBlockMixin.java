package crystalspider.nightworld.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import crystalspider.nightworld.api.NightworldPortalChecker;
import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * Injects into {@link NetherPortalBlock} to alter Nightworld Portals mob spawn.
 */
@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
  /**
   * Injects into the method {@link NetherPortalBlock#randomTick(BlockState, ServerWorld, BlockPos, Random)} before the call to {@link BlockState#allowsSpawning(BlockView, BlockPos, EntityType)}.
   * <p>
   * Allows Zombies and Skeletons spawn when it's a Nightworld Portal.
   * 
   * @param state
   * @param world
   * @param pos
   * @param random
   * @param ci
   */
  @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;allowsSpawning(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z", shift = Shift.BEFORE))
  private void onRandomTick(BlockState state, ServerWorld world, BlockPos pos, Random random, CallbackInfo ci) {
    if (NightworldPortalChecker.isNightworldPortal(world, pos.up())) {
      if (random.nextBetweenExclusive(0, 100) < 50) {
        this.handleSpawnEntity(EntityType.ZOMBIE, state, world, pos);
      } else {
        this.handleSpawnEntity(EntityType.SKELETON, state, world, pos);
      }
    }
  }

  /**
   * Redirects the call to {@link BlockState#allowsSpawning(BlockView, BlockPos, EntityType)} inside the method {@link NetherPortalBlock#randomTick(BlockState, ServerWorld, BlockPos, Random)}.
   * <p>
   * Prevents Zombified Piglins spawn when it's a Nightworld Portal.
   * 
   * @param caller
   * @param world
   * @param pos
   * @param entityType
   * @return
   */
  @Redirect(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;allowsSpawning(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/EntityType;)Z"))
  private boolean redirectAllowsSpawning(BlockState caller, BlockView world, BlockPos pos, EntityType<ZombifiedPiglinEntity> entityType) {
    return !NightworldPortalChecker.isNightworldPortal((World) world, pos.up()) && caller.allowsSpawning(world, pos, entityType);
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
  private <T extends EntityType<?>> void handleSpawnEntity(T entityType, BlockState state, ServerWorld world, BlockPos pos) {
    Entity entity;
    if (world.getBlockState(pos).allowsSpawning(world, pos, entityType) && (entity = entityType.spawn(world, pos.up(), SpawnReason.STRUCTURE)) != null) {
      entity.resetPortalCooldown();
    }
  }
}
