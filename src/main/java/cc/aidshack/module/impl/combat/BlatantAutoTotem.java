package cc.aidshack.module.impl.combat;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventTick;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.DecimalSetting;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import static cc.aidshack.AidsHack.MC;

public class BlatantAutoTotem extends Module  {

    public DecimalSetting delay = new DecimalSetting("Delay", 0, 100, 0, 1);


    private int nextTickSlot;
    private int totems;

    private boolean swapped;

    private int clock;

    public BlatantAutoTotem() {
        super("BlatantAutoTotem","",false,Category.COMBAT);
    }
    @Override
    public void onEnable(){
        super.onEnable();
        nextTickSlot = -1;
        totems = 0;
        swapped = false;
        clock = delay.getValueInt();
    }

    @Override
    public void onDisable(){
        super.onDisable();
    }

    @EventTarget
    public void onUpdate(EventTick event) {
        doFastTotem();
    }
    private void doFastTotem()
    {
        finishMovingTotem();

        PlayerInventory inventory = MC.player.getInventory();
        int nextTotemSlot = searchForTotems(inventory);

        ItemStack offhandStack = inventory.getStack(40);
        if(isTotem(offhandStack))
        {
            totems++;
            return;
        }

        if(MC.currentScreen instanceof HandledScreen
                && !(MC.currentScreen instanceof AbstractInventoryScreen))
            return;

        if(nextTotemSlot != -1)
            moveTotem(nextTotemSlot, offhandStack);
    }
    private void finishMovingTotem()
    {
        if(nextTickSlot == -1)
            return;

        MC.interactionManager.clickSlot(0, nextTickSlot, 0, SlotActionType.PICKUP, MC.player);
        nextTickSlot = -1;
    }
    private void moveTotem(int nextTotemSlot, ItemStack offhandStack)
    {
        boolean offhandEmpty = offhandStack.isEmpty();

        MC.interactionManager.clickSlot(0, nextTotemSlot, 0, SlotActionType.PICKUP, MC.player);
        MC.interactionManager.clickSlot(0, 45, 0, SlotActionType.PICKUP, MC.player);

        if(!offhandEmpty)
            nextTickSlot = nextTotemSlot;
    }
    private boolean isTotem(ItemStack stack)
    {
        return stack.getItem() == Items.TOTEM_OF_UNDYING;
    }
    private int searchForTotems(PlayerInventory inventory)
    {
        totems = 0;
        int nextTotemSlot = -1;

        for(int slot = 0; slot <= 36; slot++)
        {
            if(!isTotem(inventory.getStack(slot)))
                continue;

            totems++;

            if(nextTotemSlot == -1)
                nextTotemSlot = slot < 9 ? slot + 36 : slot;
        }

        return nextTotemSlot;
    }
}
