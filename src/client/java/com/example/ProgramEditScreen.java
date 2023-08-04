package com.example;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.EditBoxWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.lwjgl.glfw.GLFW;

public class ProgramEditScreen extends Screen {

    protected EditBoxWidget programTextBox;
    private SelectionManager lineSelectionManager;
    private int currentLine;
    private final PlayerEntity player;
    private final ItemStack itemStack;

    public ProgramEditScreen(PlayerEntity player, ItemStack itemStack) {
        super(NarratorManager.EMPTY);
        this.player = player;
        this.itemStack = itemStack;
    }

    @Override
    protected void init() {
        this.programTextBox = this.addDrawableChild(new EditBoxWidget(this.client.textRenderer, this.width / 2 - 150,
                50, 300, 100,
                Text.literal("meow"), Text.literal("meow")));
        loadDataFromProgram();
    }
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        this.programTextBox.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void close() {
        saveDataToProgram();
        super.close();
    }

    private void loadDataFromProgram() {
        System.out.println("load");
        NbtCompound nbt = itemStack.getNbt();
        if(nbt != null) {
            if(nbt.contains("program_lines")) {
                NbtList linesList = nbt.getList("program_lines", 8);
                StringBuilder programText = new StringBuilder();
                // Concatenate the lines to form a single string
                for (int i = 0; i < linesList.size(); i++) {
                    programText.append(linesList.getString(i)).append("\n");
                }
                System.out.println("programText: " + programText);
                programTextBox.setText(programText.toString());
            }
        }
    }

    private void saveDataToProgram() {
        System.out.println("save");
        String[] lines = programTextBox.getText().split("\\r?\\n");
        StringBuilder programText = new StringBuilder();
        for(String line : lines) {
            programText.append(line + "\n");
        }
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(programText.toString());
        ClientPlayNetworking.send(NetworkingConstants.CHANGE_PROGRAM_TEXT_PACKET_ID, buf);
    }
}
