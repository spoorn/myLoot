package org.spoorn.myloot.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.block.MyLootBlocks;
import org.spoorn.myloot.block.entity.MyLootContainer;
import org.spoorn.myloot.block.entity.vehicle.MyLootChestMinecartEntity;
import org.spoorn.myloot.config.ModConfig;
import org.spoorn.spoornpacks.api.Resource;
import org.spoorn.spoornpacks.type.BlockType;
import org.spoorn.spoornpacks.type.ItemType;
import org.spoorn.spoornpacks.type.VehicleType;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;

public final class MyLootUtil {
    
    public static final String PLAYER_INSTANCE_DROP_BEHAVIOR = "PLAYER_INSTANCE";
    public static final String ALL_DROP_BEHAVIOR = "ALL";
    private static final String CHEST = "CHEST";
    private static final String BARREL = "BARREL";
    private static final String SHULKER_BOX = "SHULKER_BOX";
    private static final Random RANDOM = new Random();
    
    public static boolean supportedEntity(Object be) {
        return !(be instanceof MyLootContainer) 
                && ((be instanceof ChestBlockEntity) || (be instanceof BarrelBlockEntity) || (be instanceof ChestMinecartEntity) || (be instanceof ShulkerBoxBlockEntity));
    }
    
    public static boolean isSupportedMyLootContainer(String s) {
        return CHEST.equals(s) || BARREL.equals(s) || SHULKER_BOX.equals(s); 
    }
    
    public static String getBlockName(Block block) {
        return Registry.BLOCK.getId(block).toString();
    }
    
    public static Block getMyLootBlockFromName(String name) {
        switch (name) {
            case CHEST -> {
                return MyLootBlocks.MY_LOOT_CHEST_BLOCK;
            }
            case BARREL -> {
                return MyLootBlocks.MY_LOOT_BARREL_BLOCK;
            }
            case SHULKER_BOX -> {
                return MyLootBlocks.MY_LOOT_SHULKER_BOX_BLOCK;
            }
            default -> {
                throw new IllegalArgumentException("Block=" + name + " is not a valid myLoot block");
            }
        }
    }
    
    @Nullable
    @Environment(EnvType.CLIENT)
    public static PlayerEntity getClientPlayerEntity() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            return MinecraftClient.getInstance().player;
        } else {
            return null;
        }
    }
    
    public static Block getBlockFromResource(Resource resource, BlockType type, String name) {
        Optional<Block> block = resource.getBlock(type, name);
        if (block.isEmpty()) {
            throw new RuntimeException("Could not generate block " + MyLoot.MODID + "." + name + type.getSuffix());
        }
        return block.get();
    }

    public static Item getItemFromResource(Resource resource, ItemType type, String name) {
        Optional<Item> item = resource.getItem(type, name);
        if (item.isEmpty()) {
            throw new RuntimeException("Could not generate item " + MyLoot.MODID + "." + name + type.getSuffix());
        }
        return item.get();
    }

    public static Item getVehicleItemFromResource(Resource resource, VehicleType type, String name) {
        Optional<Item> item = resource.getVehicleItem(type, name);
        if (item.isEmpty()) {
            throw new RuntimeException("Could not generate vehicleItem " + MyLoot.MODID + "." + name + type.getSuffix());
        }
        return item.get();
    }
    
    public static boolean generateRandomLoot(MyLootContainer myLootContainer, Inventory inventory, PlayerEntity player) {
        Identifier originalLootTableId = myLootContainer.getOriginalLootTableIdentifier();
        World world = myLootContainer.getMyLootWorld();
        if (originalLootTableId != null && world != null && world.getServer() != null) {
            LootTable lootTable = world.getServer().getLootManager().getTable(originalLootTableId);
            if (player instanceof ServerPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, originalLootTableId);
            }

            LootContext.Builder builder;
            if (myLootContainer instanceof MyLootChestMinecartEntity && myLootContainer.getEntityPos() != null) {
                builder = new LootContext.Builder((ServerWorld)world).parameter(LootContextParameters.ORIGIN, myLootContainer.getEntityPos()).random(RANDOM.nextLong());
            } else if (myLootContainer.getBlockPos() != null) {
                builder = new LootContext.Builder((ServerWorld)world).parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(myLootContainer.getBlockPos())).random(RANDOM.nextLong());
            } else {
                throw new RuntimeException("Could not generate random loot for myLootContainer=" + myLootContainer + " for player=" + player);
            }
           
            if (player != null) {
                builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
            }
            lootTable.supplyInventory(inventory, builder.build(LootContextTypes.CHEST));
            return true;
        }
        return false;
    }
    
    public static void dropMyLoot(World world, BlockPos pos, Inventory inventory) {
        // If container hasn't been opened, drop the default loot
        if (inventory instanceof MyLootContainer myLootContainer && myLootContainer.hasBeenOpened()) {
            String dropBehavior = ModConfig.get().dropBehavior;
            if (ALL_DROP_BEHAVIOR.equals(dropBehavior)) {
                for (Inventory inv : myLootContainer.getAllInstancedInventories()) {
                    ItemScatterer.spawn(world, pos, inv);
                }
            } else if (PLAYER_INSTANCE_DROP_BEHAVIOR.equals(dropBehavior)) {
                scatterDifferences(world, pos, myLootContainer.getDefaultLoot(), myLootContainer.getOriginalInventory());
            } else {
                throw new RuntimeException("dropBehavior=" + dropBehavior + " is not supported for " + myLootContainer);
            }
        } else {
            ItemScatterer.spawn(world, pos, inventory);
        }
    }
    
    public static DefaultedList<ItemStack> deepCloneInventory(DefaultedList<ItemStack> original) {
        DefaultedList<ItemStack> res = DefaultedList.ofSize(original.size(), ItemStack.EMPTY);
        for (int i = 0; i < original.size(); i++) {
            res.set(i, original.get(i).copy());
        }
        return res;
    }
    
    public static void scatterDifferences(World world, BlockPos pos, DefaultedList<ItemStack> originalLoot, DefaultedList<ItemStack> originalInventory) {
        List<ItemStack> differences = getAdditionsToOriginalLoot(originalLoot, originalInventory);
        for (int i = 0; i < differences.size(); i++) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), differences.get(i));
        }
    }
    
    private static List<ItemStack> getAdditionsToOriginalLoot(DefaultedList<ItemStack> originalLoot, DefaultedList<ItemStack> originalInventory) {
        Map<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> originalLootItemsList = constructWrapperMap(originalLoot);
        Map<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> originalInventoryItemsList = constructWrapperMap(originalInventory);
        
        if (originalInventoryItemsList.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<ItemStack> res = new ArrayList<>();
        
        for (Entry<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> entry : originalInventoryItemsList.entrySet()) {
            List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount> originalLootItems = originalLootItemsList.get(entry.getKey());
            List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount> stacks = entry.getValue();
            
            if (originalLootItems == null || originalLootItems.isEmpty()) {
                for (ItemStackWrapperWithEqualsAndHashCodeExcludingCount m : stacks) {
                    res.add(m.stack);
                }
            } else {
                // Assumption at this point is every list in the maps are already compressed
                for (int i = 0; i < stacks.size(); i++) {
                    ItemStackWrapperWithEqualsAndHashCodeExcludingCount curr = stacks.get(i);
                    int indexInOriginalLoot = originalLootItems.indexOf(curr);
                    if (indexInOriginalLoot > -1) {
                        ItemStack origStack = originalLootItems.get(indexInOriginalLoot).stack;
                        int countDiff = curr.stack.getCount() - origStack.getCount();
                        if (countDiff > 0) {
                            ItemStack newStack = origStack.copy();
                            newStack.setCount(countDiff);
                            res.add(newStack);
                        }
                    } else {
                        res.add(curr.stack);
                    }
                }
            }
        }
        
        return res;
    }
    
    // Compressed map of items in an inventory
    private static Map<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> constructWrapperMap(DefaultedList<ItemStack> inventory) {
        Map<Item, List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount>> res = new HashMap<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                List<ItemStackWrapperWithEqualsAndHashCodeExcludingCount> lst;
                if (res.containsKey(stack.getItem())) {
                    lst = res.get(stack.getItem());
                    ItemStackWrapperWithEqualsAndHashCodeExcludingCount stackWrapper = new ItemStackWrapperWithEqualsAndHashCodeExcludingCount(stack);
                    int indexOf = lst.indexOf(stackWrapper);
                    if (indexOf > -1) {
                        ItemStack s = lst.get(indexOf).stack;
                        s.setCount(s.getCount() + stack.getCount());
                    } else {
                        stackWrapper.cloneAndSetStack();
                        lst.add(stackWrapper);
                    }
                } else {
                    lst = new ArrayList<>();
                    lst.add(new ItemStackWrapperWithEqualsAndHashCodeExcludingCount(stack.copy()));
                    res.put(stack.getItem(), lst);
                }
            }
        }
        return res;
    }
    
    private static class ItemStackWrapperWithEqualsAndHashCodeExcludingCount {
        ItemStack stack;

        ItemStackWrapperWithEqualsAndHashCodeExcludingCount(ItemStack stack) {
            this.stack = stack;
        }
        
        void cloneAndSetStack() {
            this.stack = this.stack.copy();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemStackWrapperWithEqualsAndHashCodeExcludingCount other = (ItemStackWrapperWithEqualsAndHashCodeExcludingCount) o;
            ItemStack otherStack = other.stack;
            boolean sameItem = stack.getItem().equals(otherStack.getItem());  // Should always be true
            boolean sameNbtExcludeCount = false;
            if (stack.hasNbt() && otherStack.hasNbt()) {
                NbtCompound nbt1 = stack.getNbt().copy();
                NbtCompound nbt2 = otherStack.getNbt().copy();
                nbt1.remove("Count");
                nbt2.remove("Count");
                sameNbtExcludeCount = nbt1.equals(nbt2);
            }
            boolean sameNbt = (!stack.hasNbt() && !otherStack.hasNbt()) || (sameNbtExcludeCount);
            return sameItem && sameNbt;
        }

        @Override
        public int hashCode() {
            return Objects.hash(stack.getItem(), stack.getNbt());
        }
    }
}
