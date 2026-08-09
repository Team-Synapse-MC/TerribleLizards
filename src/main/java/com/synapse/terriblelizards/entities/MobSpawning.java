package com.synapse.terriblelizards.entities;

import com.synapse.terriblelizards.TerribleLizards;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TerribleLizards.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobSpawning {
    @SubscribeEvent
    public static void entitySpawnRestriction(SpawnPlacementRegisterEvent event) {
        event.register(
                ModEntities.MEGALOSAURUS.get(),
                SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (entityType, level, spawnType, pos, random) -> true,
                SpawnPlacementRegisterEvent.Operation.REPLACE
        );
    }
}
