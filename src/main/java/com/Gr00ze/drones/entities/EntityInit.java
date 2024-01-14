package com.Gr00ze.drones.entities;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.Gr00ze.drones.DronesMod.MOD_ID;

public class EntityInit {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES , MOD_ID);

    public static final ResourceLocation GENERIC_DRONE_RL = new ResourceLocation(MOD_ID,"generic_drone");
    public static final ResourceLocation HORSE_DRONE_RL = new ResourceLocation(MOD_ID,"horse_drone");
    public static final ResourceLocation BOAT_DRONE_RL = new ResourceLocation(MOD_ID,"boat_drone");
    public static final RegistryObject<EntityType<GenericDrone>> GENERIC_DRONE = ENTITIES.register("generic_drone",
            () -> EntityType.Builder.of(GenericDrone::new, MobCategory.CREATURE)
                    .sized(3,1)
                    .build(GENERIC_DRONE_RL.toString()));

    public static final RegistryObject<EntityType<HorseDroneTest>> HORSE_DRONE = ENTITIES.register("horse_drone",
            () -> EntityType.Builder.of(HorseDroneTest::new, MobCategory.CREATURE)
                    .build(HORSE_DRONE_RL.toString()));

    public static final RegistryObject<EntityType<BoatDrone>> BOAT_DRONE = ENTITIES.register("boat_drone",
            () -> EntityType.Builder.of(BoatDrone::new, MobCategory.CREATURE)
                    .sized(3,1)
                    .build(BOAT_DRONE_RL.toString()));

    public static void register(IEventBus iEventBus){
        ENTITIES.register(iEventBus);

    }
}