package net.runelite.client.plugins.astar;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;


@ConfigGroup("astarDisplay")
public interface AstarAnswerConfig extends Config {

    @ConfigItem(
        keyName = "AstarOn",
        name = "Is Astar On",
        description = "Shows if it is running",
        position = 1
    )
    default boolean AstarOn()
    {
        return true;
    }


}
