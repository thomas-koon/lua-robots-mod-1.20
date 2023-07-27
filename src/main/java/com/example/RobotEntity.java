package com.example;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class RobotEntity extends MobEntity {
    protected RobotEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
}
