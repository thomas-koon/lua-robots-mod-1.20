package com.example;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class RobotPDAItem extends Item {

    public RobotPDAItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity playerEntity, LivingEntity entity, Hand hand) {
        if (entity instanceof RobotEntity robotEntity && entity.isAlive()) {
            robotEntity.runCurrentProgram();
        }
        return ActionResult.PASS;
    }
}
