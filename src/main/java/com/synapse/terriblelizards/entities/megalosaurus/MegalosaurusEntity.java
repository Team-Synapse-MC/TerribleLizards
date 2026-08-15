package com.synapse.terriblelizards.entities.megalosaurus;

import com.synapse.terriblelizards.TerribleLizards;
import com.synapse.terriblelizards.entities.SlowTurnMoveControl;
import com.synapse.terriblelizards.entities.goals.AnimatedCooldownMeleeAttackGoal;
import com.synapse.terriblelizards.entities.megalosaurus.goals.MegalosaurusSleepGoal;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MegalosaurusEntity extends Animal implements GeoEntity {

    private static final EntityDataAccessor<Boolean> DATA_ASLEEP =
            SynchedEntityData.defineId(MegalosaurusEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // track potential target positions while sleeping (to see if they move)
    private final Map<UUID, Vec3> lastPositions = new HashMap<>();

    public MegalosaurusEntity(EntityType<? extends Animal> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new SlowTurnMoveControl(this, 10f);
    }

    public static AttributeSupplier setAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0f)
                .add(Attributes.ATTACK_DAMAGE, 10f)
                .add(Attributes.ATTACK_SPEED, .5)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5F)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.2f)
                .add(Attributes.FOLLOW_RANGE, 20.0)
                .add(Attributes.MOVEMENT_SPEED, .17f).build();
    }

    @Override
    public void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MegalosaurusSleepGoal(this));
        this.goalSelector.addGoal(3, new AnimatedCooldownMeleeAttackGoal(this, 1.0D,
                false, 40, "attack", "bite", 34, 16));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));

        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, this::isAttackable));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
        controllers.add(new AnimationController<>(this, "sleep", 5, this::sleepPredicate)
                .transitionLength(0)
                .triggerableAnim("stop", RawAnimation.begin().then("sit_end", Animation.LoopType.PLAY_ONCE)));
        controllers.add(new AnimationController<>(this, "attack", 5, (state) -> PlayState.STOP)
                .triggerableAnim("bite", RawAnimation.begin().then("bite_set", Animation.LoopType.PLAY_ONCE)));
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> animationState) {
        if (this.isAsleep()) {
            return PlayState.STOP;
        }

        if (animationState.isMoving()) {
            animationState.setAndContinue(RawAnimation.begin().then("walk", Animation.LoopType.LOOP));
        } else {
            animationState.setAndContinue(RawAnimation.begin().then("idle", Animation.LoopType.LOOP));
        }

        return PlayState.CONTINUE;
    }

    private <T extends GeoAnimatable> PlayState sleepPredicate(AnimationState<T> state) {
        if (this.isAsleep()) {
            state.getController().setAnimation(
                    RawAnimation.begin()
                            .then("sit_start", Animation.LoopType.PLAY_ONCE)
                            .then("sit_loop", Animation.LoopType.LOOP)
            );
            return PlayState.CONTINUE;
        }
        return PlayState.STOP;
    }

    private boolean isAttackable(LivingEntity target) {
        if (!this.canAttack(target)) {
            this.lastPositions.remove(target.getUUID());
            return false;
        }
        if (!this.isAsleep()) return true;
        if (this.distanceTo(target) < 10 && this.isMoving(target) && !target.isCrouching()) {
            this.setTarget(target);
            return true;
        }
        return false;
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.getTarget() != null) {
            if (this.distanceTo(this.getTarget()) > this.getAttributeValue(Attributes.FOLLOW_RANGE)) {
                this.setTarget(null);
            }
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(@NotNull ServerLevel serverLevel, @NotNull AgeableMob ageableMob) {
        return null;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ASLEEP, false);
    }

    public boolean isAsleep() {
        return this.entityData.get(DATA_ASLEEP);
    }

    public void setAsleep(boolean asleep) {
        this.entityData.set(DATA_ASLEEP, asleep);
    }

    private boolean isMoving(LivingEntity livingEntity) {
        UUID id = livingEntity.getUUID();
        Vec3 currentPos = livingEntity.position();
        Vec3 lastPos = lastPositions.get(id);

        lastPositions.put(id, currentPos);

        if (lastPos == null) {
            return false;
        }

        double dx = currentPos.x - lastPos.x;
        double dz = currentPos.z - lastPos.z;
        TerribleLizards.LOGGER.debug("Horizontal movement: {}, {}", dx, dz);
        double horizontalDistSqr = dx * dx + dz * dz;

        return horizontalDistSqr > 0.0001 && livingEntity.onGround();
    }
}