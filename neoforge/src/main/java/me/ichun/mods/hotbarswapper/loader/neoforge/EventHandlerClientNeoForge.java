package me.ichun.mods.hotbarswapper.loader.neoforge;

import me.ichun.mods.hotbarswapper.common.core.EventHandlerClient;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;

public class EventHandlerClientNeoForge extends EventHandlerClient
{
    public EventHandlerClientNeoForge()
    {
        NeoForge.EVENT_BUS.addListener(this::onMouseScrollEvent);
    }

    private void onMouseScrollEvent(InputEvent.MouseScrollingEvent event)
    {
        if(onMouseScroll(event.getScrollDeltaX(), event.getScrollDeltaY()))
        {
            event.setCanceled(true);
        }
    }
}
