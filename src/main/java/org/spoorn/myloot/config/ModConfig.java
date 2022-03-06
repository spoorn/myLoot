package org.spoorn.myloot.config;

import lombok.extern.log4j.Log4j2;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import org.spoorn.myloot.MyLoot;
import org.spoorn.myloot.util.MyLootUtil;

@Log4j2
@Config(name = MyLoot.MODID)
public class ModConfig implements ConfigData {

    @Comment("Controls behavior of dropped loot when a myLoot container is broken.  [default = PLAYER_INSTANCE]\n" +
            "\t- \"PLAYER_INSTANCE\" to drop player's instanced loot of player who broke the container.\n" +
            "\t- \"ALL\" to drop all instanced loot for all players.")
    public String dropBehavior = "PLAYER_INSTANCE";
    
    @Comment("Set to false to disable breaking of myLoot containers by players not in creative mode [default = true]\n" +
            "\tTrue will allow players to break myLoot containers by holding Sneak while breaking.")
    public boolean allowNonCreativeBreak = true;
    
    public static void init() {
        AutoConfig.register(ModConfig.class, JanksonConfigSerializer::new);
        
        String thisDropBehavior = ModConfig.get().dropBehavior;
        if (!MyLootUtil.PLAYER_INSTANCE_DROP_BEHAVIOR.equals(thisDropBehavior) && !MyLootUtil.ALL_DROP_BEHAVIOR.equals(thisDropBehavior)) {
            log.error("myLoot dropBehavior={} is not supported", thisDropBehavior);
            throw new UnsupportedOperationException("myLoot dropBehavior=" + thisDropBehavior + " is not supported");
        }
    }

    public static ModConfig get() {
        return AutoConfig.getConfigHolder(ModConfig.class).getConfig();
    }
}
