package com.synapse.terriblelizards.entities.megalosaurus.goals;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class MegalosaurusHuntGoal extends MeleeAttackGoal {
    public MegalosaurusHuntGoal(PathfinderMob pMob, double pSpeedModifier, boolean pFollowingTargetEvenIfNotSeen) {
        super(pMob, pSpeedModifier, pFollowingTargetEvenIfNotSeen);
    }


}
