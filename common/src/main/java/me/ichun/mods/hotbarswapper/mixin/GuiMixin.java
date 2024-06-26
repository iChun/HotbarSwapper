package me.ichun.mods.hotbarswapper.mixin;

import me.ichun.mods.hotbarswapper.common.HotbarSwapper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin
{
    @Inject(method = "renderHotbar", at = @At("TAIL"))
    public void hotbarswapper$renderHotbar(float partialTick, GuiGraphics graphics, CallbackInfo ci)
    {
        HotbarSwapper.eventHandlerClient.renderItemHotbar(graphics, partialTick);
    }
}
