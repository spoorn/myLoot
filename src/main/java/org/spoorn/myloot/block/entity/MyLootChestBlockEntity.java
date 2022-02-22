package org.spoorn.myloot.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.block.enums.ChestType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spoorn.myloot.block.entity.common.MyLootContainerBlockEntityCommon;
import org.spoorn.myloot.entity.MyLootEntities;

import javax.annotation.Nullable;
import java.util.Map;

public class MyLootChestBlockEntity extends ChestBlockEntity implements MyLootContainerBlockEntity {
    
    public final ViewerCountManager stateManager = new ViewerCountManager(){

        @Override
        protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
            MyLootChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_OPEN);
        }

        @Override
        protected void onContainerClose(World world, BlockPos pos, BlockState state) {
            MyLootChestBlockEntity.playSound(world, pos, state, SoundEvents.BLOCK_CHEST_CLOSE);
        }

        @Override
        protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {
            MyLootChestBlockEntity.this.onInvOpenOrClose(world, pos, state, oldViewerCount, newViewerCount);
        }

        // This checks if player is viewing their instance of the loot chest.  Otherwise, the chest will instantly close.
        @Override
        protected boolean isPlayerViewing(PlayerEntity player) {
            if (player.currentScreenHandler instanceof GenericContainerScreenHandler) {
                Inventory inventory = ((GenericContainerScreenHandler)player.currentScreenHandler).getInventory();
                Inventory thisInventory = MyLootChestBlockEntity.this.common.getInventories().get(player.getGameProfile().getId().toString());
                return thisInventory != null && inventory == thisInventory || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(thisInventory);
            }
            return false;
        }
    };
    
    private final MyLootContainerBlockEntityCommon common = new MyLootContainerBlockEntityCommon(stateManager);

    public MyLootChestBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(MyLootEntities.MY_LOOT_CHEST_BLOCK_ENTITY_TYPE, blockPos, blockState);
    }
    
    @Override
    public Text getContainerName() {
        return new TranslatableText("myloot.loot_chest.container.name");
    }

    public boolean hasPlayerOpened(PlayerEntity player) {
        return this.common.hasPlayerOpened(player);
    }

    @Nullable
    public Inventory getPlayerInstancedInventory(PlayerEntity player) {
        return this.common.getPlayerInstancedInventory(player);
    }

    @Override
    public void setLootTable(Identifier id, long seed) {
        super.setLootTable(id, seed);
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return this.common.createScreenHandler(syncId, playerInventory, this.getInvStackList(), this);
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
    public void onScheduledTick() {
        this.common.onScheduledTick(this);
    }

    @Override
    public void clear() {
        this.common.clear();
    }

    public Map<String, MyLootInventory> getInventories() {
        return this.common.getInventories();
    }

    public void setInventories(Map<String, MyLootInventory> inventories) {
        this.common.setInventories(inventories);
    }

    static void playSound(World world, BlockPos pos, BlockState state, SoundEvent soundEvent) {
        ChestType chestType = state.get(ChestBlock.CHEST_TYPE);
        if (chestType == ChestType.LEFT) {
            return;
        }
        double d = (double)pos.getX() + 0.5;
        double e = (double)pos.getY() + 0.5;
        double f = (double)pos.getZ() + 0.5;
        if (chestType == ChestType.RIGHT) {
            Direction direction = ChestBlock.getFacing(state);
            d += (double)direction.getOffsetX() * 0.5;
            f += (double)direction.getOffsetZ() * 0.5;
        }
        world.playSound(null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5f, world.random.nextFloat() * 0.1f + 0.9f);
    }
}
