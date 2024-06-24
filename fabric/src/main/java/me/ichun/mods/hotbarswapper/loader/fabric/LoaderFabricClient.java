package me.ichun.mods.hotbarswapper.loader.fabric;

import me.ichun.mods.hotbarswapper.common.HotbarSwapper;
import me.ichun.mods.hotbarswapper.common.core.Config;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.fabricmc.api.ClientModInitializer;

public class LoaderFabricClient extends HotbarSwapper
    implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        modProxy = this;

        //register config
        config = iChunUtil.d().registerConfig(new Config());

        //init event handler
        eventHandlerClient = new EventHandlerClientFabric();
    }
}
