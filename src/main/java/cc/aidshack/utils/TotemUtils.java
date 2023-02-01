package cc.aidshack.utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Items;

public class TotemUtils {
    static MinecraftClient mc = MinecraftClient.getInstance();
    static int totemSlot = 1;

    public static int putTotemInMainHand()  {
        mc.player.getInventory().selectedSlot = totemSlot-1;
        mc.player.getInventory().setStack(totemSlot-1, Items.TOTEM_OF_UNDYING.getDefaultStack());
        return 1;
    }
    public static int putTotemInOffHand()  {
        mc.player.getInventory().offHand.set(totemSlot-1, Items.TOTEM_OF_UNDYING.getDefaultStack());
        return 1;
    }
    public static int putTotemInBoth()  {
        mc.player.getInventory().offHand.set(totemSlot-1, Items.TOTEM_OF_UNDYING.getDefaultStack());
        mc.player.getInventory().selectedSlot = totemSlot-1;
        mc.player.getInventory().setStack(totemSlot-1, Items.TOTEM_OF_UNDYING.getDefaultStack());
        return 1;
    }
}
