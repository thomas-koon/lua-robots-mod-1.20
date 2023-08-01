package com.example;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class NetworkingConstants {
    public static final Identifier OPEN_PROGRAM_EDIT_SCREEN_PACKET_ID = new Identifier("example", "open_program_edit_screen");
    public static final Identifier CHANGE_PROGRAM_TEXT_PACKET_ID = new Identifier("example", "change_program_text");
}
