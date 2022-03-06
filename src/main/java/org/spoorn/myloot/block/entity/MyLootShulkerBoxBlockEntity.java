package org.spoorn.myloot.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spoorn.myloot.block.entity.common.MyLootContainerBlockEntityCommon;
import org.spoorn.myloot.entity.MyLootEntities;
import org.spoorn.myloot.mixin.BlockEntityAccessor;

import java.util.ArrayList;
import java.util.List;

public class MyLootShulkerBoxBlockEntity extends ShulkerBoxBlockEntity implements MyLootContainerBlockEntity {

    private final MyLootContainerBlockEntityCommon common = new MyLootContainerBlockEntityCommon(null);
    
    public MyLootShulkerBoxBlockEntity(@Nullable DyeColor color, BlockPos pos, BlockState state) {
        super(color, pos, state);
        ((BlockEntityAccessor) this).setType(MyLootEntities.MY_LOOT_SHULKER_BOX_BLOCK_ENTITY_TYPE);
    }

    @Override
    public Text getContainerName() {
        return new TranslatableText("myloot.loot_shulker_box.container.name");
    }

    @Override
    public boolean hasBeenOpened() {
        return !this.common.getPlayersOpened().isEmpty();
    }

    public boolean hasPlayerOpened(PlayerEntity player) {
        return this.common.hasPlayerOpened(player);
    }

    @Nullable
    public Inventory getPlayerInstancedInventory(PlayerEntity player) {
        return this.common.getOrCreateNewInstancedInventoryIfAbsent(player, this.getDefaultLoot(), this);
    }

    @Override
    public DefaultedList<ItemStack> getOriginalInventory() {
        return super.getInvStackList();
    }

    @Override
    public DefaultedList<ItemStack> getDefaultLoot() {
        return this.common.getDefaultLoot();
    }

    @Override
    public List<Inventory> getAllInstancedInventories() {
        return new ArrayList<>(this.common.getInventories().values());
    }

    @Override
    public void setDefaultLoot() {
        this.common.setDefaultLoot(super.getInvStackList());
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return this.common.createShulkerBoxScreenHandler(syncId, playerInventory, this.getDefaultLoot(), this);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (!this.deserializeLootTable(nbt)) {
            this.common.readNbt(nbt, this);
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (!this.serializeLootTable(nbt)) {
            this.common.writeNbt(nbt);
        }
    }

    @Override
    public void onOpen(PlayerEntity player) {
        this.common.onOpen(player, this);
        super.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        this.common.onClose(player, this);
        super.onClose(player);
    }

    // Allow syncing to client
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
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
    public void setMyLootLootTable(Identifier id, long seed) {
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
}
