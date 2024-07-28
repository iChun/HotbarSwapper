package me.ichun.mods.hotbarswapper.loader.neoforge;

import me.ichun.mods.hotbarswapper.common.HotbarSwapper;
import me.ichun.mods.hotbarswapper.common.core.Config;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = HotbarSwapper.MOD_ID, dist = Dist.CLIENT)
public class LoaderNeoForge extends HotbarSwapper
{
    public LoaderNeoForge(IEventBus modEventBus)
    {
        modProxy = this;

        if(FMLEnvironment.dist.isClient())
        {
            initClient(modEventBus);
        }
        else
        {
            LOGGER.error("You are loading " + MOD_NAME + " on a server. " + MOD_NAME + " is a client only mod!");
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient(IEventBus modEventBus)
    {
        config = iChunUtil.d().registerConfig(new Config(), modEventBus);

        eventHandlerClient = new EventHandlerClientNeoForge();

        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (modContainer, screen) -> new WorkspaceConfigs(screen));
    }
}
