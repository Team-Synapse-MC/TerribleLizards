package com.synapse.terriblelizards.entities.megalosaurus.goals;

import com.synapse.terriblelizards.entities.megalosaurus.MegalosaurusEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class MegalosaurusSleepGoal extends Goal {

    private final MegalosaurusEntity megalosaurusEntity;

    private static final int WAKE_ANIM_TIME = 20; // 10 tps
    private int wakeUpTimer = 0;

    private boolean sleeping = false;
    private boolean waking = false;

    public MegalosaurusSleepGoal(MegalosaurusEntity megalosaurusEntity) {
        this.megalosaurusEntity = megalosaurusEntity;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (shouldSleep()) {
            sleeping = true;
            waking = false;
            megalosaurusEntity.setAsleep(true);
        }
        return shouldSleep();
    }

    @Override
    public boolean canContinueToUse() {
        return sleeping || waking;
    }

    @Override
    public void tick() {
        if (sleeping && !shouldSleep()) {
            sleeping = false;
            waking = true;
            wakeUpTimer = WAKE_ANIM_TIME;
            megalosaurusEntity.triggerAnim("sleep", "stop");
        }

        if (waking) {
            wakeUpTimer--;
            if (wakeUpTimer <= 0) {
                waking = false;
                megalosaurusEntity.setAsleep(false);
            }
        }
    }

    @Override
    public void stop() {
        sleeping = false;
        waking = false;
        wakeUpTimer = 0;
        megalosaurusEntity.setAsleep(false);
    }

    private boolean shouldSleep() {
        return !this.megalosaurusEntity.level().isNight() && this.megalosaurusEntity.getTarget() == null;
    }
}