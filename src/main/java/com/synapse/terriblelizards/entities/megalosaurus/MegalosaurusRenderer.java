package com.synapse.terriblelizards.entities.megalosaurus;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.synapse.terriblelizards.TerribleLizards;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MegalosaurusRenderer extends GeoEntityRenderer<MegalosaurusEntity> {
    public MegalosaurusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MegalosaurusModel());
        this.withScale(1.5f);
        this.shadowRadius = 1.25f;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MegalosaurusEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(TerribleLizards.MODID, "textures/entity/megalosaurus.png");
    }

    @Override
    public void postRender(PoseStack poseStack, MegalosaurusEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        if (!isReRender && isWalkingForward(animatable)) {
            // spawn particles where megalosaurus drags tail on ground
            model.getBone("bone").ifPresent(bone -> {
                Vector3d pos = bone.getWorldPosition();
                spawnTailParticles(animatable, new Vec3(pos.x, pos.y, pos.z));
            });
        }
    }

    private boolean isWalkingForward(MegalosaurusEntity animatable) {
        double dx = animatable.getX() - animatable.xo;
        double dz = animatable.getZ() - animatable.zo;
        double horizontalSpeedSqr = dx * dx + dz * dz;

        return horizontalSpeedSqr > 0.0001 && animatable.onGround();
    }

    private void spawnTailParticles(MegalosaurusEntity animatable, Vec3 pos) {
        Level level = animatable.level();
        if (!level.isClientSide) return;

        BlockPos groundPos = BlockPos.containing(pos.x, pos.y - 0.5, pos.z);
        BlockState state = level.getBlockState(groundPos);

        if (state.isAir()) return;

        double groundY = groundPos.getY() + 1.0;

        level.addParticle(
                new BlockParticleOption(ParticleTypes.BLOCK, state),
                pos.x, groundY, pos.z,
                0.0, 0.02, 0.0
        );
    }
}
