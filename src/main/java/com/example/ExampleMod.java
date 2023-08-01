package com.example;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static final ProgramItem CUSTOM_ITEM = new ProgramItem(new FabricItemSettings());

	public static final EntityType<RobotEntity> ROBOT = Registry.register(
			Registries.ENTITY_TYPE, new Identifier("tutorial", "robot"),
			FabricEntityTypeBuilder.create(SpawnGroup.MISC, RobotEntity::new).dimensions(EntityDimensions.fixed(1.0f, 1.0f)).build()
	);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Registry.register(Registries.ITEM, new Identifier("tutorial", "custom_item"), CUSTOM_ITEM);
		FabricDefaultAttributeRegistry.register(ROBOT, RobotEntity.createMobAttributes());
		LOGGER.info("Hello Fabric world!");

		ServerPlayNetworking.registerGlobalReceiver(NetworkingConstants.CHANGE_PROGRAM_TEXT_PACKET_ID, (server, player, handler, buf, responseSender) -> {
			String bufContents = buf.readString();
			server.execute( () -> {
				NbtCompound nbtCompound = player.getMainHandStack().getOrCreateNbt();
				System.out.println();
				NbtList linesList = new NbtList();
				String[] lines = bufContents.split("\n");
				for(String line : lines) {
					linesList.add(NbtString.of(line));
				}
				nbtCompound.put("program_lines", linesList);
			});
		});

	}
}