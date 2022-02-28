package org.spoorn.myloot.block.entity.vehicle;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.entity.MyLootContainerBlockEntity;
import org.spoorn.myloot.block.entity.common.MyLootContainerBlockEntityCommon;
import org.spoorn.myloot.entity.MyLootEntities;
import org.spoorn.myloot.mixin.EntityAccessor;
import org.spoorn.myloot.mixin.StorageMinecartEntityAccessor;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class MyLootChestMinecartEntity extends ChestMinecartEntity implements MyLootContainerBlockEntity {

    private final MyLootContainerBlockEntityCommon common = new MyLootContainerBlockEntityCommon(null);
    
    public MyLootChestMinecartEntity(EntityType<? extends ChestMinecartEntity> entityType, World world) {
        super(entityType, world);
        ((EntityAccessor) this).setType(MyLootEntities.MY_LOOT_CHEST_MINECART_ENTITY_TYPE);
    }
    
    public MyLootChestMinecartEntity(World world, double x, double y, double z) {
        super(world, x, y, z);
        ((EntityAccessor) this).setType(MyLootEntities.MY_LOOT_CHEST_MINECART_ENTITY_TYPE);
    }

    @Override
    public Text getContainerName() {
        return new TranslatableText("myloot.loot_chest_minecart.container.name");
    }

    public boolean hasPlayerOpened(PlayerEntity player) {
        return this.common.hasPlayerOpened(player);
    }

    @Nullable
    public Inventory getPlayerInstancedInventory(PlayerEntity player) {
        return this.common.getOrCreateNewInstancedInventoryIfAbsent(player, this.getOriginalInventory(), this);
    }

    @Override
    public List<Inventory> getAllInstancedInventories() {
        return new ArrayList<>(this.common.getInventories().values());
    }

    @Override
    public DefaultedList<ItemStack> getOriginalInventory() {
        return ((StorageMinecartEntityAccessor) this).getOriginalStorageInventory();
    }

    @Override
    public DefaultedList<ItemStack> getDefaultLoot() {
        return this.common.getDefaultLoot();
    }

    @Override
    public void setDefaultLoot() {
        this.common.setDefaultLoot(this.getOriginalInventory());
    }

    @Override
    public BlockState getDefaultContainedBlock() {
        return (BlockState) MyLootBlocks.MY_LOOT_CHEST_BLOCK.getDefaultState().with(ChestBlock.FACING, Direction.NORTH);
    }

    @Override
    public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return this.common.createScreenHandler(syncId, playerInventory, this.getOriginalInventory(), this);
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.common.readNbt(nbt, this);
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        this.common.writeNbt(nbt);
    }

    @Override
    public ItemStack getPickBlockStack() {
        // TODO
        return new ItemStack(Items.MINECART);
    }

    @Override
    public void clear() {
        super.clear();
        this.common.clear();
    }

    /*
       The following methods just invoke the super method, but are required as a multiplayer server will complain at
       runtime that these methods which are part of the MyLootContainerBlockEntity interface aren't implemented.
       Not sure why inheriting superclass methods do not count for interface implementations, but this gets around it.
    */
    @Override
    public void setLootTable(Identifier id, long seed) {
        super.setLootTable(id, seed);
    }

    @Override
    public void markDirty() {
        super.markDirty();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return super.canPlayerUse(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        super.onClose(player);
    }

    @Override
    public void onOpen(PlayerEntity player) {
        super.onOpen(player);
    }
}
