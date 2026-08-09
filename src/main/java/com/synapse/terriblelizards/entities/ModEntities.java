package com.synapse.terriblelizards.entities;

import com.synapse.terriblelizards.TerribleLizards;
import com.synapse.terriblelizards.entities.megalosaurus.MegalosaurusEntity;
import com.synapse.terriblelizards.entities.megalosaurus.MegalosaurusRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntities {
    public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TerribleLizards.MODID);

    public static final RegistryObject<EntityType<MegalosaurusEntity>> MEGALOSAURUS =
            REGISTRY.register("megalosaurus",
                    () -> EntityType.Builder.of(MegalosaurusEntity::new, MobCategory.CREATURE)
                            .sized(2.5f, 3f)
                            .build(ResourceLocation.fromNamespaceAndPath(TerribleLizards.MODID, "megalosaurus").toString()));

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MEGALOSAURUS.get(), MegalosaurusRenderer::new);
    }

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MEGALOSAURUS.get(), MegalosaurusEntity.setAttributes());
    }
}
