package me.ichun.mods.hotbarswapper.common;

import com.mojang.logging.LogUtils;
import me.ichun.mods.hotbarswapper.common.core.Config;
import me.ichun.mods.hotbarswapper.common.core.EventHandlerClient;
import org.slf4j.Logger;

public abstract class HotbarSwapper
{
    public static final String MOD_ID = "hotbarswapper";
    public static final String MOD_NAME = "Hotbar Swapper";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static HotbarSwapper modProxy;

    public static Config config;

    public static EventHandlerClient eventHandlerClient;
}
