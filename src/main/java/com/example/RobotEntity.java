package com.example;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class RobotEntity extends MobEntity implements VehicleInventory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
    @Nullable
    private Identifier lootTableId;
    private long lootSeed;
    private ItemStack currentProgramItem;

    private LuaScriptManager luaScriptManager;

    private float targetYaw;
    protected RobotEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        this.luaScriptManager = new LuaScriptManager(this);
    }

    @Override
    public void tick() {
        super.tick();
        setCurrentProgramToTopLeft();
    }

    public void setCurrentProgramToTopLeft() {
        for(int i = 0; i < 36; i++) {
            if(inventory.get(i).isOf(ExampleMod.CUSTOM_ITEM)) {
                this.currentProgramItem = inventory.get(i);
                return;
            }
        }
        this.currentProgramItem = null;
    }

    public void runCurrentProgram() {
        if(this.currentProgramItem != null) {
            NbtCompound nbt = this.currentProgramItem.getNbt();
            if(nbt != null) {
                if(nbt.contains("program_lines")) {
                    NbtList linesList = nbt.getList("program_lines", 8);
                    StringBuilder programText = new StringBuilder();
                    // Concatenate the lines to form a single string
                    for (int i = 0; i < linesList.size(); i++) {
                        programText.append(linesList.getString(i)).append("\n");
                    }
                    luaScriptManager.executeLuaScript(programText.toString());
                }
            }
        }
    }

    public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Nullable
    @Override
    public Identifier getLootTableId() {
        return null;
    }

    @Override
    public void setLootTableId(@Nullable Identifier lootTableId) {
        this.lootTableId = lootTableId;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootSeed = lootTableSeed;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public void resetInventory() {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }

    @Override
    public int size() {
        return 27;
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.getInventoryStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        System.out.println("hi122");
        return this.removeInventoryStack(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.removeInventoryStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.setInventoryStack(slot, stack);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        if (this.lootTableId == null || !playerEntity.isSpectator()) {
            this.generateInventoryLoot(playerInventory.player);
            return this.getScreenHandler(syncId, playerInventory);
        }
        return null;
    }

    @Override
    public void clear() {
        System.out.println("begone");
        this.clearInventory();
    }
}
