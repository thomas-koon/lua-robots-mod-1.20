package com.example;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class RobotInvisibleAttackProjectileEntity extends ThrownEntity {

    public RobotInvisibleAttackProjectileEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
        System.out.println("spawned");
    }

    public RobotInvisibleAttackProjectileEntity(World world, LivingEntity owner) {
        super(LuabotsMod.ROBOT_INVISIBLE_ATTACK_PROJECTILE, world);
        System.out.println("spawned");
    }

    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        System.out.println("entity hit");
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)
        entity.damage(entity.getDamageSources().genericKill(), 4); // deals damage
    }

    protected void onCollision(HitResult hitResult) { // called on collision with a block
        System.out.println("collide with " + hitResult.getPos());
        super.onCollision(hitResult);
        if (!this.getWorld().isClient) { // checks if the world is client
            System.out.println("kill projectile");
            this.kill(); // kills the projectile
        }
    }
    @Override
    protected void initDataTracker() {
    }
}
