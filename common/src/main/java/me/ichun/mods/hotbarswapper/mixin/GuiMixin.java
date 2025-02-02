package me.ichun.mods.hotbarswapper.mixin;

import me.ichun.mods.hotbarswapper.common.HotbarSwapper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    public void hotbarswapper$renderItemHotbar(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci)
    {
        HotbarSwapper.eventHandlerClient.renderItemHotbar(graphics, deltaTracker);
    }
}
