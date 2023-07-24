package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;

public class ExampleModClient implements ClientModInitializer {
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
	}
}