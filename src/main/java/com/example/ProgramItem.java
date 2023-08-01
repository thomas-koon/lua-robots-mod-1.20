package com.example;



import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import net.minecraft.world.event.GameEvent;
import org.luaj.vm2.*;
import org.luaj.vm2.lib.jse.*;

public class ProgramItem extends Item {

    public ProgramItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        Globals globals = JsePlatform.standardGlobals();
        if(playerEntity instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer,
                    NetworkingConstants.OPEN_PROGRAM_EDIT_SCREEN_PACKET_ID, PacketByteBufs.empty());
        }
        return TypedActionResult.success(playerEntity.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity playerEntity, LivingEntity entity, Hand hand) {
        if (entity instanceof RobotEntity robotEntity && entity.isAlive()) {
                robotEntity.open(playerEntity);
                return ActionResult.success(playerEntity.getWorld().isClient);
        }
        return ActionResult.PASS;
    }

}
