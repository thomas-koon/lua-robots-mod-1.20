package com.example;



import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
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
        if(playerEntity instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer,
                    NetworkingConstants.OPEN_PROGRAM_EDIT_SCREEN_PACKET_ID, PacketByteBufs.empty());

        }

        return TypedActionResult.success(playerEntity.getStackInHand(hand));
    }
}
