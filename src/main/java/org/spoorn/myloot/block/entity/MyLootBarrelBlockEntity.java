package org.spoorn.myloot.block.entity;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spoorn.myloot.block.entity.common.MyLootContainerBlockEntityCommon;
import org.spoorn.myloot.entity.MyLootEntities;
import org.spoorn.myloot.mixin.BlockEntityAccessor;

import java.util.ArrayList;
import java.util.List;

public class MyLootBarrelBlockEntity extends BarrelBlockEntity implements MyLootContainer {

    public final ViewerCountManager stateManager = new ViewerCountManager(){

        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            MyLootBarrelBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_OPEN);
            MyLootBarrelBlockEntity.this.setOpen(state, true);
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            MyLootBarrelBlockEntity.this.playSound(state, SoundEvents.BLOCK_BARREL_CLOSE);
            MyLootBarrelBlockEntity.this.setOpen(state, false);
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
        }

        // This checks if player is viewing their instance of the loot chest.  Otherwise, the chest will instantly close.
        @Override
        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
                Inventory thisInventory = MyLootBarrelBlockEntity.this.common.getInventories().get(player.getGameProfile().getId().toString());
                return thisInventory != null && inventory == thisInventory || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(thisInventory);
            }
            return false;
        }
    };

    private final MyLootContainerBlockEntityCommon common = new MyLootContainerBlockEntityCommon(stateManager);
    
    public MyLootBarrelBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
        ((BlockEntityAccessor) this).setType(MyLootEntities.MY_LOOT_BARREL_BLOCK_ENTITY_TYPE);
    }

    @Override
    public Text getContainerName() {
        return Text.translatable("myloot.loot_barrel.container.name");
    }

    @Override
    public Identifier getOriginalLootTableIdentifier() {
        return this.common.getOriginalLootTableId();
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
    public List<Inventory> getAllInstancedInventories() {
        return new ArrayList<>(this.common.getInventories().values());
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
    public void setDefaultLoot() {
        this.common.setDefaultLoot(super.getInvStackList());
    }

    @Override
    public void checkLootInteraction(@Nullable PlayerEntity player) {
        if (this.common.getOriginalLootTableId() == null && this.lootTableId != null) {
            this.common.setOriginalLootTableId(this.lootTableId);
        }
        super.checkLootInteraction(player);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return this.common.createScreenHandler(syncId, playerInventory, this.getDefaultLoot(), this);
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
    public void onClose(PlayerEntity player) {
        this.common.onClose(player, this);
    }

    @Override
    public void tick() {
        this.common.onScheduledTick(this);
    }

    @Override
    public void clear() {
        super.clear();
        this.common.clear();
    }

    void setOpen(BlockState state, boolean open) {
        this.world.setBlockState(this.getPos(), (BlockState)state.with(BarrelBlock.OPEN, open), Block.NOTIFY_ALL);
    }

    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = state.get(BarrelBlock.FACING).getVector();
        double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5f, this.world.random.nextFloat() * 0.1f + 0.9f);
    }

    /*
       The following methods just invoke the super method, but are required as a multiplayer server will complain at
       runtime that these methods which are part of the MyLootContainerBlockEntity interface aren't implemented.
       Not sure why inheriting superclass methods do not count for interface implementations, but this gets around it.
    */
    @Override
    public void setMyLootLootTable(Identifier id, long seed) {
        if (this.common.getOriginalLootTableId() == null && id != null) {
            this.common.setOriginalLootTableId(id);
        }
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

    @Nullable
    @Override
    public World getMyLootWorld() {
        return super.getWorld();
    }

    @Override
    public BlockPos getBlockPos() {
        return super.getPos();
    }
}
