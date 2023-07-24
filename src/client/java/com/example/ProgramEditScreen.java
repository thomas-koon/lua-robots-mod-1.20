package com.example;

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
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
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
        NbtCompound nbt = itemStack.getNbt();
        if(nbt != null) {
            if(nbt.contains("program_lines")) {
                NbtList linesList = nbt.getList("program_lines", 8);
                StringBuilder programText = new StringBuilder();
                // Concatenate the lines to form a single string
                for (int i = 0; i < linesList.size(); i++) {
                    programText.append(linesList.getString(i)).append("\n");
                }
                programTextBox.setText(programText.toString());
            }
        }
    }

    private void saveDataToProgram() {
        NbtCompound nbtCompound = itemStack.getOrCreateNbt();
        String[] lines = programTextBox.getText().split("\\r?\\n");
        NbtList linesList = new NbtList();
        for(String line : lines) {
            linesList.add(NbtString.of(line));
        }
        nbtCompound.put("program_lines", linesList);
        System.out.println("meow" + itemStack.getNbt().get("program_lines"));
    }
}
