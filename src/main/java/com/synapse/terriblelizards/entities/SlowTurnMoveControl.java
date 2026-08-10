package com.synapse.terriblelizards.entities;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;

public class SlowTurnMoveControl extends MoveControl {
    private final float turnSpeed;

    public SlowTurnMoveControl(Mob mob, float turnSpeed) {
        super(mob);
        this.turnSpeed = turnSpeed;
    }

    @Override
    public void tick() {
        if (this.operation == Operation.STRAFE) {
            // let vanilla handle strafing as-is
            super.tick();
            return;
        }

        if (this.operation != Operation.MOVE_TO) {
            this.mob.setSpeed(0.0F);
            return;
        }

        this.operation = Operation.WAIT;

        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        double dist2 = dx * dx + dy * dy + dz * dz;

        if (dist2 < 2.5000003E-7) {
            this.mob.setZza(0.0F);
            return;
        }

        // this is the key line — 90.0F in vanilla becomes turnSpeed here
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(),
                (float) (Mth.atan2(dz, dx) * (180D / Math.PI)) - 90.0F, this.turnSpeed));
        this.mob.setSpeed((float) (this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));

        this.mob.getNavigation();
        if (!this.mob.getNavigation().isInProgress()) {
            double horizDist = Math.sqrt(dx * dx + dz * dz);
            if (isWalkable(dy, horizDist)) {
                this.mob.setSpeed(0.0F);
            } else {
                this.mob.setZza(Math.max(this.mob.getSpeed(), 0.1F));
            }
        }
    }

    private boolean isWalkable(double dy, double horizDist) {
        return dy > (double) this.mob.maxUpStep() && horizDist < Math.max(1.0F, this.mob.getBbWidth());
    }
}