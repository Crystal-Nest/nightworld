package it.crystalnest.nightworld.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import it.crystalnest.nightworld.api.NightworldPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into {@link NetherPortalBlock} to alter Nightworld Portals mob spawn.
 */
@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin {
  /**
   * Injects into the method {@link NetherPortalBlock#randomTick(BlockState, ServerLevel, BlockPos, RandomSource)} before the call to {@link BlockState#isValidSpawn(BlockGetter, BlockPos, EntityType)}.<br />
   * Allows Zombies and Skeletons spawn when it's a Nightworld Portal.
   *
   * @param state block state.
   * @param level dimension.
   * @param pos position.
   * @param random random source.
   * @param ci {@link CallbackInfo}.
   */
  @Inject(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z", shift = Shift.BEFORE))
  private void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
    if (NightworldPortalChecker.isNightworldPortal(level, pos.above())) {
      if (random.nextInt(0, 100) < 50) {
        this.handleSpawnEntity(EntityType.ZOMBIE, level, pos);
      } else {
        this.handleSpawnEntity(EntityType.SKELETON, level, pos);
      }
    }
  }

  /**
   * Modifies the condition returned by {@link BlockState#isValidSpawn(BlockGetter, BlockPos, EntityType)} inside the method {@link NetherPortalBlock#randomTick(BlockState, ServerLevel, BlockPos, RandomSource)}.<br />
   * Prevents Zombified Piglins spawn when it's a Nightworld Portal.
   *
   * @param original original condition value.
   * @param state block state.
   * @param level dimension.
   * @param pos position.
   * @param rand random source.
   * @return whether spawning is allowed.
   */
  @ModifyExpressionValue(method = "randomTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isValidSpawn(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/EntityType;)Z"))
  private boolean modifyIsValidSpawn(boolean original, BlockState state, ServerLevel level, BlockPos pos, RandomSource rand) {
    return original && !NightworldPortalChecker.isNightworldPortal(level, pos.above());
  }

  /**
   * Handles spawning an entity of the given type if allowed.
   *
   * @param <T> entity type.
   * @param entityType entity type.
   * @param level dimension.
   * @param pos position.
   */
  @Unique
  private <T extends EntityType<?>> void handleSpawnEntity(T entityType, ServerLevel level, BlockPos pos) {
    Entity entity;
    if (level.getBlockState(pos).isValidSpawn(level, pos, entityType) && (entity = entityType.spawn(level, pos.above(), MobSpawnType.STRUCTURE)) != null) {
      entity.setPortalCooldown();
    }
  }
}
