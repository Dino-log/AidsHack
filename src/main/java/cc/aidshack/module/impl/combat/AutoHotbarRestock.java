package cc.aidshack.module.impl.combat;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import static cc.aidshack.AidsHack.MC;

public class AutoHotbarRestock extends Module  {
    public BooleanSetting cry = new BooleanSetting("End Crystal", false);
    public BooleanSetting oby = new BooleanSetting("Obsidian", false);
    public BooleanSetting pearl = new BooleanSetting("Pearl", false);
    public BooleanSetting gap = new BooleanSetting("Gapple", false);
    public DecimalSetting delay = new DecimalSetting("Delay", 0, 20, 0, 0.1);
    public DecimalSetting crySlot = new DecimalSetting("Crystal Slot", 0, 8, 0, 1);
    public DecimalSetting obySlot = new DecimalSetting("Obsidian Slot", 0, 8, 0, 1);
    public DecimalSetting pearlSlot = new DecimalSetting("Pearl Slot", 0, 8, 0, 1);
    public DecimalSetting gapSlot = new DecimalSetting("Gapple Slot", 0, 8, 0, 1);

    private int invClock = -1;

    public AutoHotbarRestock() {
        super("AutoHotbarRestock", "", false,Category.COMBAT);
        addSettings(cry,oby,pearl,gap,delay,crySlot,obySlot,pearlSlot,gapSlot);
    }

    @Override
    public void onEnable() {
        invClock = -1;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onTick() {
        if (!(MC.currentScreen instanceof InventoryScreen))
        {
            invClock = -1;
            return;
        }
        if (invClock == -1)
            invClock = delay.getValueInt();
        if (invClock > 0)
        {
            invClock--;
            return;
        }
        PlayerInventory inv = MC.player.getInventory();
        ItemStack crystalSlot = inv.main.get(crySlot.getValueInt());
        ItemStack obsidianSlot = inv.main.get(obySlot.getValueInt());
        ItemStack enderpearlSlot = inv.main.get(pearlSlot.getValueInt());
        ItemStack gappleSlot = inv.main.get(gapSlot.getValueInt());
        if (cry.isEnabled() && crystalSlot.getItem() != Items.END_CRYSTAL)
        {
            int slot = findCrystalSlot();
            if (slot != -1)
            {
                MC.interactionManager.clickSlot(((InventoryScreen) MC.currentScreen).getScreenHandler().syncId, slot, crySlot.getValueInt(), SlotActionType.SWAP, MC.player);
            }
        }
        else if (oby.isEnabled() && obsidianSlot.getItem() != Items.OBSIDIAN)
        {
            int slot = findObsidianSlot();
            if (slot != -1)
            {
                MC.interactionManager.clickSlot(((InventoryScreen) MC.currentScreen).getScreenHandler().syncId, slot, obySlot.getValueInt(), SlotActionType.SWAP, MC.player);
            }
        }
        else if (pearl.isEnabled() && enderpearlSlot.getItem() != Items.ENDER_PEARL)
        {
            int slot = findPearlSlot();
            if (slot != -1)
            {
                MC.interactionManager.clickSlot(((InventoryScreen) MC.currentScreen).getScreenHandler().syncId, slot, pearlSlot.getValueInt(), SlotActionType.SWAP, MC.player);
            }
        }
        else if (gap.isEnabled() && gappleSlot.getItem() != Items.GOLDEN_APPLE)
        {
            int slot = findGappleSlot();
            if (slot != -1)
            {
                MC.interactionManager.clickSlot(((InventoryScreen) MC.currentScreen).getScreenHandler().syncId, slot, gapSlot.getValueInt(), SlotActionType.SWAP, MC.player);
            }
        }
    }



    private int findCrystalSlot()
    {
        PlayerInventory inv = MC.player.getInventory();
        for (int i = 9; i < 36; i++)
        {
            if (inv.main.get(i).getItem() == Items.END_CRYSTAL)
                return i;
        }
        return -1;
    }
    private int findObsidianSlot()
    {
        PlayerInventory inv = MC.player.getInventory();
        for (int i = 9; i < 36; i++)
        {
            if (inv.main.get(i).getItem() == Items.OBSIDIAN)
                return i;
        }
        return -1;
    }
    private int findPearlSlot()
    {
        PlayerInventory inv = MC.player.getInventory();
        for (int i = 9; i < 36; i++)
        {
            if (inv.main.get(i).getItem() == Items.ENDER_PEARL)
                return i;
        }
        return -1;
    }
    private int findGappleSlot()
    {
        PlayerInventory inv = MC.player.getInventory();
        for (int i = 9; i < 36; i++)
        {
            if (inv.main.get(i).getItem() == Items.GOLDEN_APPLE)
                return i;
        }
        return -1;
    }
}
