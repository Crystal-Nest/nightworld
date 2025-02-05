package it.crystalnest.nightworld.mixin;

import it.crystalnest.nightworld.Constants;
import it.crystalnest.server_sided_portals.api.CustomPortalChecker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Injects into {@link NetherPortalBlock} to alter Nightworld Portals mob spawn.
 */
@Mixin(NetherPortalBlock.class)
public abstract class NetherPortalBlockMixin extends Block {
  private NetherPortalBlockMixin() {
    super(BlockBehaviour.Properties.of());
  }

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
  @Inject(method = "randomTick", at = @At(value = "HEAD"), cancellable = true)
  private void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
    if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && random.nextInt(500) < level.getDifficulty().getId() && CustomPortalChecker.isPortalForDimension(level, pos.above(), Constants.NIGHTWORLD)) {
      BlockPos bottom;
      for (bottom = pos; level.getBlockState(bottom).is(this); bottom = bottom.below()) ;
      if (random.nextInt(0, 10) < 5) {
        this.handleSpawnEntity(EntityType.ZOMBIE, level, bottom);
      } else {
        this.handleSpawnEntity(EntityType.SKELETON, level, bottom);
      }
      ci.cancel();
    }
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
