package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ExampleModClient implements ClientModInitializer {

	public static final EntityModelLayer MODEL_ROBOT_LAYER = new EntityModelLayer(new Identifier("tutorial", "robot"), "main");
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientPlayNetworking.registerGlobalReceiver(NetworkingConstants.OPEN_PROGRAM_EDIT_SCREEN_PACKET_ID,
				(client, handler, buf, responseSender) -> {
			client.execute(() -> {
				Text title = Text.literal("Program Editor");
				client.setScreen(new ProgramEditScreen(client.player, client.player.getMainHandStack()));
			});
		});

		EntityRendererRegistry.register(ExampleMod.ROBOT, (context) -> {
			return new RobotEntityRenderer(context);
		});

		EntityModelLayerRegistry.registerModelLayer(MODEL_ROBOT_LAYER, RobotEntityModel::getTexturedModelData);
	}
}