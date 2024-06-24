package me.ichun.mods.hotbarswapper.common.core;

import com.mojang.blaze3d.platform.InputConstants;
import me.ichun.mods.hotbarswapper.common.HotbarSwapper;
import me.ichun.mods.ichunutil.client.gui.config.WorkspaceConfigs;
import me.ichun.mods.ichunutil.client.key.KeyBind;
import me.ichun.mods.ichunutil.common.config.ConfigBase;
import me.ichun.mods.ichunutil.common.config.annotations.Prop;
import net.minecraft.client.KeyMapping;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Config extends ConfigBase
{
    public String swapKeyListen = "key.sprint";

    @Prop(skip = true)
    public KeyBind swapKey = new KeyBind(new KeyMapping("key.hotbarswapper.swap", InputConstants.UNKNOWN.getValue(), "key.categories.inventory"), bind -> HotbarSwapper.eventHandlerClient.holdSwapKey(true), bind -> HotbarSwapper.eventHandlerClient.holdSwapKey(false));

    public String swapSlotListen = "key.sneak";

    @Prop(skip = true)
    public KeyBind swapSlot = new KeyBind(new KeyMapping("key.hotbarswapper.swapSlot", InputConstants.UNKNOWN.getValue(), "key.categories.inventory"), bind -> HotbarSwapper.eventHandlerClient.holdSwapSlotKey(true), bind -> HotbarSwapper.eventHandlerClient.holdSwapSlotKey(false));

    public boolean requireSwapKeyForSlot = true;

    public boolean ignoreEmptySlots = true;

    public boolean invertScrollDirection = false;

    @Prop(validator = "ignoredSlotsValidator")
    public List<Integer> ignoredSlots = new ArrayList<>();

    @Prop(min = 0D, max = 2D)
    public double itemScale = 0.65D;

    @Prop(min = -40, max = 40)
    public int itemOffsetX = 20;

    @Prop(min = -40, max = 40)
    public int itemOffsetY = 20;

    @Prop(skip = true)
    public KeyBind swapRowNext = new KeyBind(new KeyMapping("key.hotbarswapper.swapRowNext", InputConstants.UNKNOWN.getValue(), "key.categories.inventory"), bind -> {
        while(HotbarSwapper.eventHandlerClient.addToIndex(1)) {}
        HotbarSwapper.eventHandlerClient.doSwap(true);
    }, null);

    @Prop(skip = true)
    public KeyBind swapRowPrev = new KeyBind(new KeyMapping("key.hotbarswapper.swapRowPrev", InputConstants.UNKNOWN.getValue(), "key.categories.inventory"), bind -> {
        while(HotbarSwapper.eventHandlerClient.addToIndex(-1)) {}
        HotbarSwapper.eventHandlerClient.doSwap(true);
    }, null);

    @Prop(skip = true)
    public KeyBind swapSlotNext = new KeyBind(new KeyMapping("key.hotbarswapper.swapSlotNext", InputConstants.UNKNOWN.getValue(), "key.categories.inventory"), bind -> {
        while(HotbarSwapper.eventHandlerClient.addToIndex(1)) {}
        HotbarSwapper.eventHandlerClient.doSwap(false);
    }, null);

    @Prop(skip = true)
    public KeyBind swapSlotPrev = new KeyBind(new KeyMapping("key.hotbarswapper.swapSlotPrev", InputConstants.UNKNOWN.getValue(), "key.categories.inventory"), bind -> {
        while(HotbarSwapper.eventHandlerClient.addToIndex(-1)) {}
        HotbarSwapper.eventHandlerClient.doSwap(false);
    }, null);

    @Prop(skip = true)
    public KeyBind ignoreSlot = new KeyBind(new KeyMapping("key.hotbarswapper.ignoreSlot", InputConstants.UNKNOWN.getValue(), "key.categories.inventory"), bind -> {
        HotbarSwapper.eventHandlerClient.toggleLockSlot();
    }, null);


    public Config()
    {
        super(HotbarSwapper.MOD_ID + ".toml");
    }

    @Override
    public void registerGuiElementOverrides()
    {
        guiElementOverrides.put("HotkeySwapper:keybind", WorkspaceConfigs::createButtonToKeyBinds);
    }

    @Override
    public @NotNull String getModId()
    {
        return HotbarSwapper.MOD_ID;
    }

    @Override
    public @NotNull String getConfigName()
    {
        return HotbarSwapper.MOD_NAME;
    }

    @Override
    public Type getConfigType()
    {
        return Type.CLIENT;
    }

    @Override
    public void onPropertyChanged(boolean file, String name, Field field, Object oldObj, Object newObj)
    {
        if(field.getName().equals("swapKeyListen"))
        {
            HotbarSwapper.eventHandlerClient.findListenedHoldKey();
        }
        if(field.getName().equals("swapSlotListen"))
        {
            HotbarSwapper.eventHandlerClient.findListenedSlotKey();
        }
    }

    public boolean ignoredSlotsValidator(Object o)
    {
        return o instanceof Integer i && i >= 0 && i < 9;
    }
}
