package me.ichun.mods.hotbarswapper.common.core;

import com.mojang.math.Axis;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import me.ichun.mods.hotbarswapper.common.HotbarSwapper;
import me.ichun.mods.ichunutil.client.key.KeyListener;
import me.ichun.mods.ichunutil.common.entity.EntityHelper;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.util.EventCalendar;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class EventHandlerClient
{
    private KeyListener listenerHoldKey;
    private KeyListener listenerSlotKey;

    private boolean holdingSwapKey;
    private boolean holdingSwapSlotKey;

    private int currentIndex;

    private int popTimeout;
    private final ArrayList<ItemStack> hotbarWhenSwapping = new ArrayList<>();

    private boolean possibleLock;
    private final ItemStack stackBarrier = new ItemStack(Blocks.BARRIER);

    public EventHandlerClient()
    {
        iChunUtil.eC().registerClientTickEndListener(this::onClientTickEnd);

        iChunUtil.eC().registerOnClientConnectListener(mc -> onClientConnect());
    }

    public void onClientConnect()
    {
        if(listenerHoldKey == null)
        {
            findListenedHoldKey();
        }
        if(listenerSlotKey == null)
        {
            findListenedSlotKey();
        }
    }

    private void onClientTickEnd(Minecraft mc)
    {
        if(mc.player != null && !mc.player.isSpectator()) // we listen to the sprint key instead
        {
            if(listenerHoldKey != null && HotbarSwapper.config.swapKey.keyListener.keyBinding.isUnbound())
            {
                listenerHoldKey.tick(mc);
            }
            if(listenerSlotKey != null && HotbarSwapper.config.swapSlot.keyListener.keyBinding.isUnbound())
            {
                listenerSlotKey.tick(mc);
            }

            if(popTimeout > 0)
            {
                popTimeout--;
                if(popTimeout == 0) hotbarWhenSwapping.clear(); //time out

                if(!hotbarWhenSwapping.isEmpty())
                {
                    boolean foundDiff = false;
                    for(int i = 0; i < hotbarWhenSwapping.size(); i++)
                    {
                        ItemStack item = mc.player.getInventory().getItem(i);
                        if(!ItemStack.matches(item, hotbarWhenSwapping.get(i)))
                        {
                            item.setPopTime(5);
                            foundDiff = true;
                        }
                    }

                    if(foundDiff)
                    {
                        //presume the server told us we've swapped
                        hotbarWhenSwapping.clear();
                        mc.player.playSound(SoundEvents.ITEM_PICKUP, 0.2F, (mc.player.getRandom().nextFloat() - mc.player.getRandom().nextFloat()) * 1.4F + 2.0F);
                    }
                }
            }
        }
    }

    protected boolean onMouseScroll(double scrollX, double scrollY)
    {
        if(isHoldingKey())
        {
            possibleLock = false;

            if(holdingSwapSlotKey && HotbarSwapper.config.ignoredSlots.contains(Minecraft.getInstance().player.getInventory().selected))
            {
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("chat.hotbarswapper.ignoreSlots.ignored", Minecraft.getInstance().player.getInventory().selected + 1), false);
                return true;
            }

            int addAmount = (int)(HotbarSwapper.config.invertScrollDirection ? scrollY : -scrollY);
            addToIndex(addAmount);

            return true;
        }
        return false;
    }

    public boolean addToIndex(int addAmount)
    {
        currentIndex = EntityHelper.wrap(currentIndex + addAmount, 0, 3);

        while(currentIndex != 0 && HotbarSwapper.config.ignoreEmptySlots && !isNotEmpty(currentIndex, Minecraft.getInstance().player.getInventory().selected, !holdingSwapSlotKey))
        {
            currentIndex = EntityHelper.wrap(currentIndex + ((int)Math.signum(addAmount)), 0, 3);
        }
        return currentIndex == 0;
    }

    private boolean isHoldingKey()
    {
        return holdingSwapKey || !HotbarSwapper.config.requireSwapKeyForSlot && holdingSwapSlotKey;
    }

    public boolean isNotEmpty(int rowIndex, int slotIndex, boolean isRow)
    {
        NonNullList<ItemStack> items = Minecraft.getInstance().player.getInventory().items;
        if(isRow)
        {
            boolean isNotEmpty = false;
            for(int i = 9 * rowIndex; i < items.size() && i < 9 * (rowIndex + 1); i++)
            {
                int index = i - (9 * rowIndex);
                if(HotbarSwapper.config.ignoredSlots.contains(index))
                {
                    continue;
                }

                ItemStack itemStack = items.get(i);
                if(!itemStack.isEmpty())
                {
                    isNotEmpty = true;
                    break;
                }
            }
            return isNotEmpty;
        }
        else
        {
            if(HotbarSwapper.config.ignoredSlots.contains(slotIndex))
            {
                return false;
            }

            int index = rowIndex * 9 + slotIndex;
            if(index < items.size())
            {
                return !items.get(index).isEmpty();
            }
        }
        return false;
    }

    public void doSwap(boolean isRow)
    {
        if(currentIndex == 0) return;

        LocalPlayer player = Minecraft.getInstance().player;
        Inventory inventory = player.getInventory();

        popTimeout = 10;
        hotbarWhenSwapping.clear();
        for(int i = 0; i < 9; i++)
        {
            hotbarWhenSwapping.add(inventory.getItem(i).copy());
        }

        if(isRow)
        {
            for(int i = 0; i < 9; i++)
            {
                if(!HotbarSwapper.config.ignoredSlots.contains(i))
                {
                    doSwap(player, inventory, i);
                }
            }
        }
        else if(!HotbarSwapper.config.ignoredSlots.contains(inventory.selected))
        {
            doSwap(player, inventory, inventory.selected);
        }
        else
        {
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("chat.hotbarswapper.ignoreSlots.ignored", Minecraft.getInstance().player.getInventory().selected + 1), false);
        }
    }

    private void doSwap(LocalPlayer player, Inventory inventory, int index)
    {
        player.connection.send(new ServerboundContainerClickPacket(
            player.inventoryMenu.containerId,
            player.inventoryMenu.getStateId(),
            index + 9 * currentIndex,
            index,
            ClickType.SWAP,
            ItemStack.EMPTY,
            new Int2ObjectOpenHashMap<>()
        ));
    }

    public void toggleLockSlot()
    {
        if(!HotbarSwapper.config.ignoredSlots.removeIf(i -> i == Minecraft.getInstance().player.getInventory().selected))
        {
            HotbarSwapper.config.ignoredSlots.add(Minecraft.getInstance().player.getInventory().selected);
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("chat.hotbarswapper.ignoreSlots.add", Minecraft.getInstance().player.getInventory().selected + 1), false);
        }
        else
        {
            Minecraft.getInstance().player.displayClientMessage(Component.translatable("chat.hotbarswapper.ignoreSlots.remove", Minecraft.getInstance().player.getInventory().selected + 1), false);
        }
        HotbarSwapper.config.save();
    }

    public void holdSwapKey(boolean isKeyDown)
    {
        boolean prevState = holdingSwapKey;
        holdingSwapKey = isKeyDown;
        if(holdingSwapKey)
        {
            if(holdingSwapSlotKey)
            {
                possibleLock = true;
            }

            if(!HotbarSwapper.config.requireSwapKeyForSlot && holdingSwapSlotKey)
            {
                //we hit a new key, this takes priority now
                holdingSwapSlotKey = false;
            }
            else //we're setting up a new swap, reset the index
            {
                currentIndex = 0;
            }
        }
        else if(prevState)
        {
            if(currentIndex != 0)
            {
                //Swap the row's item
                doSwap(!holdingSwapSlotKey);

                //Disable slot key held if they happen to hold it at some point
                holdingSwapSlotKey = false;
            }
            else if(possibleLock)
            {
                toggleLockSlot();
            }
        }
    }

    public void holdSwapSlotKey(boolean isKeyDown)
    {
        boolean prevState = holdingSwapSlotKey;
        holdingSwapSlotKey = isKeyDown;
        if(holdingSwapSlotKey)
        {
            if(!HotbarSwapper.config.requireSwapKeyForSlot)
            {
                if(!holdingSwapKey)
                {
                    currentIndex = 0;
                }

                //If we're holding the swap key but we don't need it, unhold them.
                holdingSwapKey = false;
            }
        }
        else if(prevState && currentIndex != 0 && (!HotbarSwapper.config.requireSwapKeyForSlot || holdingSwapKey))
        {
            //Swap the slot's item
            doSwap(false);

            //Disable swap key held if they are also holding it
            holdingSwapKey = false;
        }
    }

    public void findListenedHoldKey()
    {
        Options options = Minecraft.getInstance().options;
        if(options == null) return; //we're running the code too early

        listenerHoldKey = null; //this allows us to unset the key if a blank string

        if(!HotbarSwapper.config.swapKeyListen.isEmpty())
        {
            KeyMapping key = findKey(HotbarSwapper.config.swapKeyListen);
            if(key != null)
            {
                listenerHoldKey = new KeyListener(key,
                    listener -> holdSwapKey(true),
                    listener -> holdSwapKey(false));
            }
            else
            {
                //only warn if we can't find it, if it is left blank then intentionally disabling the key
                HotbarSwapper.LOGGER.warn("Could not find key for swap key called \"{}\"!", HotbarSwapper.config.swapKeyListen);
            }
        }
    }

    public void findListenedSlotKey()
    {
        Options options = Minecraft.getInstance().options;
        if(options == null) return; //we're running the code too early

        listenerSlotKey = null; //this allows us to unset the key if a blank string

        if(!HotbarSwapper.config.swapSlotListen.isEmpty())
        {
            KeyMapping key = findKey(HotbarSwapper.config.swapSlotListen);
            if(key != null)
            {
                listenerSlotKey = new KeyListener(key,
                    listener -> holdSwapSlotKey(true),
                    listener -> holdSwapSlotKey(false)
                );
            }
            else
            {
                HotbarSwapper.LOGGER.warn("Could not find key for swap slot key called \"{}\"!", HotbarSwapper.config.swapSlotListen);
            }
        }
    }

    public KeyMapping findKey(@NotNull String name)
    {
        for(KeyMapping keyMapping : Minecraft.getInstance().options.keyMappings)
        {
            if(name.equals(keyMapping.getName()))
            {
                return keyMapping;
            }
        }
        return null;
    }

    public void renderItemHotbar(GuiGraphics graphics, DeltaTracker deltaTracker)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.getCameraEntity() instanceof Player player)
        {
            if(isHoldingKey() && HotbarSwapper.config.itemScale > 0D)
            {
                ArrayList<ItemStack> items = new ArrayList<>();
                for(int i = 9 * currentIndex; i < player.getInventory().items.size() && i < 9 * (currentIndex + 1); i++)
                {
                    if(HotbarSwapper.config.ignoredSlots.contains(i - 9 * currentIndex))
                    {
                        items.add(stackBarrier);
                    }
                    else
                    {
                        if(currentIndex != 0)
                        {
                            items.add((!holdingSwapSlotKey || (i - 9 * currentIndex) == player.getInventory().selected) ? player.getInventory().items.get(i) : ItemStack.EMPTY);
                        }
                        else
                        {
                            items.add(ItemStack.EMPTY);
                        }
                    }
                }


                if(!items.stream().allMatch(ItemStack::isEmpty))
                {
                    //Taken from Gui.renderItemHotbar
                    int halfWidth = graphics.guiWidth() / 2;
                    int seed = 1;

                    for(int i = 0; i < items.size(); i++)
                    {
                        double x = halfWidth - 90 + i * 20 + 2;
                        double y = graphics.guiHeight() - 16 - 3;

                        //our additions
                        float scale = (float)HotbarSwapper.config.itemScale;
                        x += (HotbarSwapper.config.itemOffsetX * (1F - scale));
                        y += (HotbarSwapper.config.itemOffsetY * (1F - scale));

                        float offset = scale * 10;

                        graphics.pose().pushPose();
                        graphics.pose().translate(x + offset, y + offset, 100F);
                        graphics.pose().scale(scale, scale, 1F);
                        if(EventCalendar.isEventDay())
                        {
                            graphics.pose().rotateAround(Axis.ZP.rotationDegrees((player.tickCount + deltaTracker.getGameTimeDeltaPartialTick(false)) * 4F), 0F, 1F, 0F);
                        }
                        //                    if(items.get(i).isEmpty())
                        //                    {
                        //                        graphics.fill(-10, -10, 6, 6, 100, 0x44000000);
                        //                    }
                        graphics.renderItem(player, items.get(i), (int)(-offset / scale), (int)(-offset / scale), seed++);
                        graphics.renderItemDecorations(mc.font, items.get(i), (int)(-offset / scale), (int)(-offset / scale));
                        graphics.pose().popPose();
                    }
                }
            }
        }
    }
}
