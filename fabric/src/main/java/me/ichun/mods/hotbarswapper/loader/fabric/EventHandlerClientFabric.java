package me.ichun.mods.hotbarswapper.loader.fabric;

import me.ichun.mods.hotbarswapper.common.core.EventHandlerClient;
import me.ichun.mods.ichunutil.loader.fabric.event.client.FabricClientEvents;

public class EventHandlerClientFabric extends EventHandlerClient
{
    public EventHandlerClientFabric()
    {
        FabricClientEvents.MOUSE_SCROLL.register(this::onMouseScroll);
    }
}
