package com.example;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RobotEntity extends MobEntity implements VehicleInventory {

    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(36, ItemStack.EMPTY);
    @Nullable
    private Identifier lootTableId;
    private long lootSeed;
    private ItemStack currentProgramItem;

    private LuaScriptManager luaScriptManager;

    private int delayTicks;
    private float targetYaw;
    private static final double ATTACKING_RANGE = 1.0;
    protected RobotEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
        this.luaScriptManager = new LuaScriptManager(this);
    }

    public static DefaultAttributeContainer.Builder createRobotAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 20);
    }

    @Override
    public void tick() {
        super.tick();
        setCurrentProgramToTopLeft();
        tickHandSwing();
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

    @Override
    public int getMaxLookPitchChange() {
        return 180;
    }

    public void turnRight() {
        setHeadYaw(getHeadYaw() + 90);
        setBodyYaw(getBodyYaw() + 90);
        setYaw(getYaw() + 90);
    }

    public void turnLeft() {
        setHeadYaw(getHeadYaw() - 90);
        setBodyYaw(getBodyYaw() - 90);
        setYaw(getYaw() - 90);
    }

    public void turnAround() {
        setHeadYaw(getHeadYaw() + 180);
        setBodyYaw(getBodyYaw() + 180);
        setYaw(getYaw() + 180);
    }

    public void moveForward(int blocks) {
        setVelocity(Vec3d.fromPolar(0, getHeadYaw()).normalize().multiply(0.38 * blocks));
    }

    public void moveBackward(int blocks) {
        setVelocity(Vec3d.fromPolar(0, getHeadYaw()).normalize().multiply(-0.38 * blocks));
    }

    public void attack() {
        swingHand(Hand.MAIN_HAND);
        if (!getWorld().isClient) {
            Vec3d vec3d = this.getRotationVec(1.0F);
            System.out.println("Launch");
            RobotInvisibleAttackProjectileEntity attackProjectileEntity = new RobotInvisibleAttackProjectileEntity(getWorld(), this);
            attackProjectileEntity.setVelocity(this, 0, getHeadYaw(), 0.0F, 1.5F, 0F);
            attackProjectileEntity.setPosition(this.getX(), this.getY(), this.getZ());
            getWorld().spawnEntity(attackProjectileEntity);
        }
    }

    public void mine(boolean eyeLevel) {
        Vec3d eyePos = getEyePos();
        Vec3d rotVec = getRotationVec(1);
        Vec3d frontVec = eyePos.add(rotVec.x * 1, rotVec.y * 1, rotVec.z * 1);
        BlockHitResult hitResult = getWorld().raycast(new RaycastContext(eyePos, frontVec, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, this));
        BlockPos resultBlockPos = hitResult.getBlockPos();
        // if the robot is facing its head horizontally, eyeLevel determines if it mines the block
        // directly in front of its face
        // This is useless if the robot is looking up or down
        if(eyeLevel && getPitch() == 0) {
            resultBlockPos = hitResult.getBlockPos().add(0, 1, 0);
        }
        Block resultBlock = getWorld().getBlockState(resultBlockPos).getBlock();
        getWorld().breakBlock(resultBlockPos, true);
    }

    public void mineVertical(boolean down) {
        setPitch(getPitch() - 90);
        swingHand(Hand.MAIN_HAND);
        BlockPos targetBlockPos;
        if(down) {
            targetBlockPos = getBlockPos().add(0, -1, 0);
        } else {
            targetBlockPos = getBlockPos().add(0, 2, 0);
        }
        Block resultBlock = getWorld().getBlockState(targetBlockPos).getBlock();
        System.out.println(targetBlockPos);
        System.out.println(resultBlock.getName());
        getWorld().breakBlock(targetBlockPos, true);
    }

    public void placeBlock(String type, boolean eyeLevel) {
        equipIfAvailable(type);
        if(getEquippedStack(EquipmentSlot.MAINHAND).getUseAction().equals(UseAction.BLOCK)) {
            System.out.println("yass this is a block!!");
        }
    }

    public int quantity(String itemName) {
        int storedAmt = 0;
        for(ItemStack itemStack : inventory) {
            System.out.println(itemStack.getItem().toString());
            if(itemStack.getItem().toString().equalsIgnoreCase(itemName)) {
                storedAmt = storedAmt + itemStack.getCount();
            }
        }
        return storedAmt;
    }

    public void equipSlot(int slot) {
        equipStack(EquipmentSlot.MAINHAND, getInventoryStack(slot));
    }

    public String getItemTypeAtSlot(int slot) {
        return getStack(slot).getItem().toString();
    }

    public int getQuantityAtSlot(int slot) {
        return getStack(slot).getCount();
    }

    public boolean equipIfAvailable(String itemName) {
        for(ItemStack itemStack : inventory) {
            System.out.println(itemStack.getItem().toString());
            if(itemStack.getItem().toString().equalsIgnoreCase(itemName)) {
                System.out.println("yes!");
                equipStack(EquipmentSlot.MAINHAND, itemStack);
                return true;
            }
        }
        return false;
    }

    public void dropHolding() {
        dropStack(getEquippedStack(EquipmentSlot.MAINHAND));
    }

    public void dropAll(String itemName) {
        for(int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.get(i);
            if(itemStack.getItem().toString().equalsIgnoreCase(itemName)) {
                dropStack(itemStack);
                removeStack(i);
            }
        }
    }

    public void dropAmount(int amount, String itemName) {
        int amountRemaining = amount;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = getStack(i);
            if (!itemStack.isEmpty() && itemStack.getItem().toString().equalsIgnoreCase(itemName)) {
                if (itemStack.getCount() <= amount) {
                    // Drop the entire stack if the count is less than or equal to the specified amount
                    dropStack(itemStack);
                    setStack(i, ItemStack.EMPTY);
                    amount -= itemStack.getCount();
                } else {
                    // Drop a portion of the stack and update the count
                    dropStack(itemStack.split(amount));
                    amount = 0;
                }
                if (amount <= 0) {
                    break; // All items dropped, exit the loop
                }
            }
        }
    }

    public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt,inventory);
        return nbt;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt,inventory);
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
        this.clearInventory();
    }
}
