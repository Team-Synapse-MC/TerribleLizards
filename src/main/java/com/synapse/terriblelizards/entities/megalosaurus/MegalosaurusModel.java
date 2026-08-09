package com.synapse.terriblelizards.entities.megalosaurus;

import com.synapse.terriblelizards.TerribleLizards;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MegalosaurusModel extends GeoModel<MegalosaurusEntity> {
    @Override
    public ResourceLocation getModelResource(MegalosaurusEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(TerribleLizards.MODID, "geo/megalosaurus.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MegalosaurusEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(TerribleLizards.MODID, "textures/entity/megalosaurus.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MegalosaurusEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(TerribleLizards.MODID, "animations/megalosaurus.animation.json");
    }

    @Override
    public void setCustomAnimations(MegalosaurusEntity animatable, long instanceId, AnimationState<MegalosaurusEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
