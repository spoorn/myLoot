package org.spoorn.myloot.block.entity.common;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.block.entity.MyLootInventory;
import org.spoorn.myloot.config.ModConfig;
import org.spoorn.myloot.util.MyLootUtil;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j2
public class MyLootContainerBlockEntityCommon {
    
    public static final String NBT_KEY = "myLoot";

    @Getter
    @Setter
    private Map<String, MyLootInventory> inventories = new HashMap<>();
    private DefaultedList<ItemStack> defaultLoot = DefaultedList.ofSize(27, ItemStack.EMPTY);
    @Getter
    private final Set<String> playersOpened = new HashSet<>();
    @Getter
    @Setter
    private Identifier originalLootTableId;
    
    private final ViewerCountManager stateManager;
    
    public MyLootContainerBlockEntityCommon(@Nullable ViewerCountManager viewerCountManager) {
        this.stateManager = viewerCountManager;
    }

    public boolean hasPlayerOpened(PlayerEntity player) {
        return this.playersOpened.contains(player.getGameProfile().getId().toString());
    }

    @Environment(EnvType.CLIENT)
    public boolean hasPlayerOpenedOnClient() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            return player != null && this.hasPlayerOpened(player);
        } else {
            return false;
        }
    }

    public DefaultedList<ItemStack> getDefaultLoot() {
        return this.defaultLoot;
    }

    public void setDefaultLoot(DefaultedList<ItemStack> originalInventory) {
        this.defaultLoot = MyLootUtil.deepCloneInventory(originalInventory);
    }

    public ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory, 
                                                DefaultedList<ItemStack> defaultList, MyLootContainer myLootContainer) {
        PlayerEntity player = playerInventory.player;
        Inventory inventory = getOrCreateNewInstancedInventoryIfAbsent(player, defaultList, myLootContainer);
        return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, inventory);
    }

    public ScreenHandler createShulkerBoxScreenHandler(int syncId, PlayerInventory playerInventory, DefaultedList<ItemStack> defaultList, MyLootContainer myLootContainer) {
        PlayerEntity player = playerInventory.player;
        Inventory inventory = getOrCreateNewInstancedInventoryIfAbsent(player, defaultList, myLootContainer);
        return new ShulkerBoxScreenHandler(syncId, playerInventory, inventory);
    }
    
    public Inventory getOrCreateNewInstancedInventoryIfAbsent(PlayerEntity player, DefaultedList<ItemStack> defaultList, MyLootContainer myLootContainer) {
        String playerId = player.getGameProfile().getId().toString();
        MyLootInventory myLootInventory = null;
        if (!this.inventories.containsKey(playerId)) {
            // Below code will silently fail if we were unable to randomly generate loot, and instead default to the
            // original loot to avoid situations where a chest loads empty loot due to errors causing players to get a disadvantage
            // due to a bug.
            boolean generatedRandomLoot = false;
            if (ModConfig.get().enableRandomSeedLootPerPlayer) {
                try {
                    myLootInventory = new MyLootInventory(DefaultedList.ofSize(defaultList.size(), ItemStack.EMPTY), myLootContainer);
                    generatedRandomLoot = MyLootUtil.generateRandomLoot(myLootContainer, myLootInventory, player);
                } catch (Exception e) {
                    log.error("Could not generate random seed loot for myLoot container=" + myLootContainer + " player=" + player + ".  Defaulting to original loot");
                }
            }
            
            if (!generatedRandomLoot) {
                myLootInventory = cloneDefaultInventory(defaultList, myLootContainer);
            }
            
            this.inventories.put(playerId, myLootInventory);
        } else {
            myLootInventory = this.inventories.get(playerId);
        }
        return myLootInventory;
    }
    
    private MyLootInventory cloneDefaultInventory(DefaultedList<ItemStack> defaultList, MyLootContainer myLootContainer) {
        DefaultedList<ItemStack> inventoryList = MyLootUtil.deepCloneInventory(defaultList);
        return new MyLootInventory(inventoryList, myLootContainer);
    }
    
    public void loadPlayersOpenedToNbt(NbtCompound root) {
        NbtList playersOpenedList = new NbtList();
        for (String player : this.playersOpened) {
            playersOpenedList.add(NbtString.of(player));
        }
        root.put("players", playersOpenedList);
    }
    
    public void unloadPlayersOpenedFromNbt(NbtCompound root) {
        NbtList playersOpened = root.getList("players", NbtElement.STRING_TYPE);
        for (int i = 0; i < playersOpened.size(); ++i) {
            this.playersOpened.add(playersOpened.getString(i));
        }
    }

    public void readNbt(NbtCompound nbt, MyLootContainer myLootContainer) {
        this.inventories.clear();
        this.playersOpened.clear();
        this.defaultLoot.clear();
        NbtCompound root = nbt.getCompound(NBT_KEY);
        // Inventories
        for (String playerId : root.getKeys()) {
            NbtCompound sub = root.getCompound(playerId);
            MyLootInventory inventory = new MyLootInventory(myLootContainer);
            NbtList nbtList = sub.getList("Items", NbtElement.COMPOUND_TYPE);
            for (int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                int j = nbtCompound.getByte("Slot") & 0xFF;
                if (j < 0 || j >= inventory.size()) continue;
                inventory.setStack(j, ItemStack.fromNbt(nbtCompound));
            }
            this.inventories.put(playerId, inventory);
        }
        // Players opened
        NbtList playersOpened = root.getList("players", NbtElement.STRING_TYPE);
        for (int i = 0; i < playersOpened.size(); ++i) {
            this.playersOpened.add(playersOpened.getString(i));
        }
        // Default loot
        NbtList nbtList = root.getList("defaultLoot", NbtElement.COMPOUND_TYPE);
        for (int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            int j = nbtCompound.getByte("Slot") & 0xFF;
            if (j < 0 || j >= this.defaultLoot.size()) continue;
            this.defaultLoot.set(j, ItemStack.fromNbt(nbtCompound));
        }
        // Original loot table id
        String lootTableId = root.getString("origLootTableId");
        if (!lootTableId.isEmpty()) {
            this.originalLootTableId = new Identifier(lootTableId);
            // Corruption safety
            if (this.originalLootTableId.getNamespace().isEmpty() || this.originalLootTableId.getPath().isEmpty()) {
                log.warn("myLootContainer={} has invalid originalLootTableId={}", myLootContainer, this.originalLootTableId);
                this.originalLootTableId = null;
            }
        }
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound root = new NbtCompound();
        // Inventories
        for (Map.Entry<String, MyLootInventory> entry : this.inventories.entrySet()) {
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
        // Players opened
        NbtList playersOpenedList = new NbtList();
        for (String player : this.playersOpened) {
            playersOpenedList.add(NbtString.of(player));
        }
        root.put("players", playersOpenedList);
        // Default loot
        NbtList defaultLoot = new NbtList();
        for (int i = 0; i < this.defaultLoot.size(); ++i) {
            ItemStack stack = this.defaultLoot.get(i);
            if (stack.isEmpty()) continue;
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.putByte("Slot", (byte)i);
            stack.writeNbt(nbtCompound);
            defaultLoot.add(nbtCompound);
        }
        root.put("defaultLoot", defaultLoot);
        
        if (this.originalLootTableId != null && !this.originalLootTableId.getNamespace().isEmpty() && !this.originalLootTableId.getPath().isEmpty()) {
            root.putString("origLootTableId", this.originalLootTableId.toString());
        }
        nbt.put(NBT_KEY, root);
    }
    
    public void clear() {
        this.inventories.clear();
    }

    public void onOpen(PlayerEntity player, BlockEntity blockEntity) {
        if (!blockEntity.isRemoved()) {
            World world = blockEntity.getWorld();
            BlockPos pos = blockEntity.getPos();
            BlockState cachedState = blockEntity.getCachedState();
            if (this.stateManager != null && !player.isSpectator()) {
                this.stateManager.openContainer(player, world, pos, cachedState);
            }

            String playerId = player.getGameProfile().getId().toString();
            if (!this.playersOpened.contains(playerId)) {
                this.playersOpened.add(playerId);
                blockEntity.markDirty();
                if (world != null) {
                    // Force sync with clients
                    world.updateListeners(pos, cachedState, cachedState, Block.NOTIFY_ALL);
                }
            }
        }
    }
    
    public boolean addPlayerOpenedIfAbsent(PlayerEntity player) {
        return this.playersOpened.add(player.getGameProfile().getId().toString());
    }

    public void onClose(PlayerEntity player, BlockEntity blockEntity) {
        if (this.stateManager != null && !blockEntity.isRemoved() && !player.isSpectator()) {
            this.stateManager.closeContainer(player, blockEntity.getWorld(), blockEntity.getPos(), blockEntity.getCachedState());
        }
    }

    public void onScheduledTick(BlockEntity blockEntity) {
        if (this.stateManager != null && !blockEntity.isRemoved()) {
            this.stateManager.updateViewerCount(blockEntity.getWorld(), blockEntity.getPos(), blockEntity.getCachedState());
        }
    }
}
