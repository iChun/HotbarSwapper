package me.ichun.mods.hotbarswapper.loader.forge;

import me.ichun.mods.hotbarswapper.common.core.EventHandlerClient;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;

public class EventHandlerClientForge extends EventHandlerClient
{
    public EventHandlerClientForge()
    {
        MinecraftForge.EVENT_BUS.addListener(this::onMouseScrollEvent);
    }

    private void onMouseScrollEvent(InputEvent.MouseScrollEvent event)
    {
        if(onMouseScroll(0.0D, event.getScrollDelta()))
        {
            event.setCanceled(true);
        }
    }
}
