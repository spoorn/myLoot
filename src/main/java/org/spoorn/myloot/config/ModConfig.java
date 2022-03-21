package org.spoorn.myloot.config;

import draylar.omegaconfig.OmegaConfig;
import draylar.omegaconfig.api.Comment;
import draylar.omegaconfig.api.Config;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.util.MyLootUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
@ToString
public class ModConfig implements Config {
    
    private static ModConfig CONFIG;

    @Comment("Controls behavior of dropped loot when a myLoot container is broken.  [default = PLAYER_INSTANCE]\n" +
            "\t- \"PLAYER_INSTANCE\" to drop player's instanced loot of player who broke the container.\n" +
            "\t- \"ALL\" to drop all instanced loot for all players.")
    public String dropBehavior = "PLAYER_INSTANCE";
    
    @Comment("Set to false to disable breaking of myLoot containers by players not in creative mode [default = true]\n" +
            "\tTrue will allow players to break myLoot containers by holding Sneak while breaking.")
    public boolean allowNonCreativeBreak = true;
    
    @Comment("Set to true if you want each myLoot container to generate random loot for each player [default = false]\n" +
            "\tThe default behavior creates the same instanced loot across all players.")
    public boolean enableRandomSeedLootPerPlayer = false;
    
    @Comment("Controls what blocks are replaced by myLoot containers.  Blocks omitted from this list will not be replaced.\n" +
            "This also supports regex for the map block value.\n" +
            "Acceptable myLoot container types are CHEST, BARREL, SHULKER_BOX\n" +
            "Note: these only control *blocks*.  Entities such as Chest Minecarts have a different mechanism and separate config.\n" +
            "\nDefault:\n" +
            "\"blockMapping\": [\n" +
            "\t\t{\n" +
            "\t\t\t\"myLootType\": \"BARREL\",\n" +
            "\t\t\t\"replaces\": [\n" +
            "\t\t\t\t\"minecraft:barrel\"\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"myLootType\": \"CHEST\",\n" +
            "\t\t\t\"replaces\": [\n" +
            "\t\t\t\t\".*chest\"\n" +
            "\t\t\t]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"myLootType\": \"SHULKER_BOX\",\n" +
            "\t\t\t\"replaces\": [\n" +
            "\t\t\t\t\"minecraft:shulker_box\"\n" +
            "\t\t\t]\n" +
            "\t\t}\n" +
            "\t]")
    public List<BlockMapping> blockMapping = Arrays.asList(
        new BlockMapping("BARREL", Arrays.asList("minecraft:barrel")),
        new BlockMapping("CHEST", Arrays.asList(".*chest")),
        new BlockMapping("SHULKER_BOX", Arrays.asList("minecraft:shulker_box"))
    );
    
    @Comment("True to enable replacing of Chest Minecarts with the myLoot variant.  False to disable. [default = true]")
    public boolean enableChestMinecarts = true;
    
    @Comment("Dimensions disable list.  Add dimensions to this list to disable container replacement\n" +
            "Example: [ \"minecraft:the_nether\" ]")
    public List<String> disabledDimensions = new ArrayList<>();
    
    @Comment("Loot table disable list.  Add loot table IDs to this list to disable container replacement when the\n" +
            "loot table matches one of these IDs.  Example: [ \"minecraft:chests/simple_dungeon\" ]")
    public List<String> disabledLootTables = new ArrayList<>();
    
    public static void init() {
        CONFIG = OmegaConfig.register(ModConfig.class);
        
        String thisDropBehavior = ModConfig.get().dropBehavior;
        if (!MyLootUtil.PLAYER_INSTANCE_DROP_BEHAVIOR.equals(thisDropBehavior) && !MyLootUtil.ALL_DROP_BEHAVIOR.equals(thisDropBehavior)) {
            log.error("myLoot dropBehavior={} is not supported", thisDropBehavior);
            throw new UnsupportedOperationException("myLoot dropBehavior=" + thisDropBehavior + " is not supported");
        }
        
        for (BlockMapping blockMapping : ModConfig.get().blockMapping) {
            String s = blockMapping.myLootType;
            if (!MyLootUtil.isSupportedMyLootContainer(s)) {
                log.error("myLoot blockMapping type {} is not supported", s);
                throw new UnsupportedOperationException("myLoot blockMapping type " + s + " is not supported");
            }
        }
    }

    public static ModConfig get() {
        return CONFIG;
    }

    @Override
    public String getName() {
        return MyLoot.MODID;
    }

    @Override
    public String getExtension() {
        // For nicer comments parsing in text editors, and backwards compatibility since older versions used Cloth Config with Jankson
        return "json5";
    }
}
