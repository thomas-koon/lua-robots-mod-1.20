package com.example;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

public class CustomItem extends Item {
    public CustomItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        Globals globals = JsePlatform.standardGlobals();
        try {
            // Load and execute the Lua script
            LuaValue chunk = globals.load("print 'hello, world'");
            System.out.println(chunk.call());
        } catch (Exception e) {
            e.printStackTrace();
        }

        playerEntity.playSound(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, 1.0F, 1.0F);
        return TypedActionResult.success(playerEntity.getStackInHand(hand));
    }
}
