package cc.aidshack.module.impl.combat;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventTick;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

import static cc.aidshack.AidsHack.MC;

public class AutoInventoryTotem extends Module
{

	public BooleanSetting autoSwitch = new BooleanSetting("Auto Switch", false);
	public DecimalSetting delay = new DecimalSetting("Delay", 0, 20, 0, 0.1);
    public DecimalSetting totemSlot = new DecimalSetting("Totem Slot", 0, 8, 0 ,1);

	public BooleanSetting forceTotem = new BooleanSetting("Force Totem", false);

	public AutoInventoryTotem()
	{
		super("AutoInventoryTotem", "Automatically puts on totems for you when you are in inventory", false, Category.COMBAT);
		addSettings(autoSwitch, delay, totemSlot, forceTotem);
	}

	private int invClock = -1;

	@Override
	public void onEnable()
	{
		super.onEnable();
		invClock = -1;
	}

	@Override
	public void onDisable()
	{
		super.onDisable();
	}

	@EventTarget
	public void onPlayerTick(EventTick event)
	{
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
		if (autoSwitch.isEnabled())
			inv.selectedSlot = totemSlot.getValueInt();
		if (inv.offHand.get(0).getItem() != Items.TOTEM_OF_UNDYING)
		{
			int slot = findTotemSlot();
			if (slot != -1)
			{
				MC.interactionManager.clickSlot(((InventoryScreen) MC.currentScreen).getScreenHandler().syncId, slot, 40, SlotActionType.SWAP, MC.player);
				return;
			}
		}
		ItemStack mainHand = inv.main.get(inv.selectedSlot);
		if (mainHand.isEmpty() ||
				forceTotem.isEnabled() && mainHand.getItem() != Items.TOTEM_OF_UNDYING)
		{
			int slot = findTotemSlot();
			if (slot != -1)
			{
				MC.interactionManager.clickSlot(((InventoryScreen) MC.currentScreen).getScreenHandler().syncId, slot, inv.selectedSlot, SlotActionType.SWAP, MC.player);
			}
		}
	}

	private int findTotemSlot()
	{
		PlayerInventory inv = MC.player.getInventory();
		for (int i = 9; i < 36; i++)
		{
			if (inv.main.get(i).getItem() == Items.TOTEM_OF_UNDYING)
				return i;
		}
		return -1;
	}
}
