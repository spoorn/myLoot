package org.spoorn.myloot.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.spoorn.myloot.entity.MyLootEntities;
import org.spoorn.myloot.mixin.ItemStackAccessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

public class MyLootChestBlockEntity extends ChestBlockEntity {
    
    private Map<String, MyLootInventory> inventories = new HashMap<>();
    
    public MyLootChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MyLootEntities.MY_LOOT_CHEST_BLOCK_ENTITY_BLOCK_ENTITY_TYPE, blockPos, blockState);
    }

    @Override
    protected Text getContainerName() {
        return new LiteralText("myLootChestBlock");
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        PlayerEntity player = playerInventory.player;
        String playerId = player.getGameProfile().getId().toString();
        System.out.println("### playerId: " + playerId);
        System.out.println("### opened inventories: " + this.inventories);
        MyLootInventory myLootInventory;
        if (!this.inventories.containsKey(playerId)) {
            DefaultedList<ItemStack> clonedList = DefaultedList.ofSize(27, ItemStack.EMPTY);
            DefaultedList<ItemStack> defaultList = this.getInvStackList();
            for (int i = 0; i < defaultList.size(); ++i) {
                ItemStack defaultItemStack = defaultList.get(i);
                clonedList.set(i, ItemStackAccessor.create(defaultItemStack.getItem(), defaultItemStack.getCount(), Optional.ofNullable(defaultItemStack.getNbt())));
            }
            myLootInventory = new MyLootInventory(clonedList, this);
            this.inventories.put(playerId, myLootInventory);
        } else {
            myLootInventory = this.inventories.get(playerId);
        }
        System.out.println("### opened inventory: " + myLootInventory);
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, myLootInventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.inventories.clear();
        if (!this.deserializeLootTable(nbt)) {
            NbtCompound root = nbt.getCompound("myLoot");
            for (String playerId : root.getKeys()) {
                NbtCompound sub = root.getCompound(playerId);
                MyLootInventory inventory = new MyLootInventory(this);
                NbtList nbtList = sub.getList("Items", 10);
                for (int i = 0; i < nbtList.size(); ++i) {
                    NbtCompound nbtCompound = nbtList.getCompound(i);
                    int j = nbtCompound.getByte("Slot") & 0xFF;
                    if (j < 0 || j >= inventory.size()) continue;
                    inventory.setStack(j, ItemStack.fromNbt(nbtCompound));
                }
                this.inventories.put(playerId, inventory);
            }
            System.out.println("### readNbt: " + this.inventories);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            NbtCompound root = new NbtCompound();
            for (Entry<String, MyLootInventory> entry : this.inventories.entrySet()) {
                NbtCompound sub = new NbtCompound();
                NbtList nbtList = new NbtList();
                MyLootInventory inventory = entry.getValue();
                for (int i = 0; i < inventory.size(); ++i) {
                    ItemStack stack = inventory.getStack(i);
                    if (stack.isEmpty()) continue;
                    NbtCompound nbtCompound = new NbtCompound();
                    nbtCompound.putByte("Slot", (byte)i);
                    stack.writeNbt(nbtCompound);
                    nbtList.add(nbtCompound);
                }
                sub.put("Items", nbtList);
                root.put(entry.getKey(), sub);
            }
            nbt.put("myLoot", root);
            System.out.println("### writeNbt: " + this.inventories);
        }
    }

    @Override
    public void clear() {
        this.inventories.clear();
    }
    
    public Map<String, MyLootInventory> getInventories() {
        return this.inventories;
    }
    
    public void setInventories(Map<String, MyLootInventory> inventories) {
        this.inventories = inventories;
    }
}
