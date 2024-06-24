package me.ichun.mods.hotbarswapper.loader.forge;

import me.ichun.mods.hotbarswapper.common.HotbarSwapper;
import me.ichun.mods.hotbarswapper.common.core.Config;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.common.iChunUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

@Mod(HotbarSwapper.MOD_ID)
public class LoaderForge extends HotbarSwapper
{
    public LoaderForge()
    {
        modProxy = this;

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::initClient);
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, () -> () -> LOGGER.error("You are loading " + MOD_NAME + " on a server. " + MOD_NAME + " is a client only mod!"));

        //Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, IExtensionPoint.DisplayTest.IGNORE_ALL_VERSION);
    }

    @OnlyIn(Dist.CLIENT)
    private void initClient()
    {
        config = iChunUtil.d().registerConfig(new Config());

        eventHandlerClient = new EventHandlerClientForge();

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(WorkspaceConfigs::new));
    }
}
