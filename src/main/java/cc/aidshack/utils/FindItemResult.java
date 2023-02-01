package cc.aidshack.utils;

import net.minecraft.util.Hand;

import static cc.aidshack.utils.WorldUtils.mc;

public class FindItemResult {
    public static final int OFFHAND = 45;

    public static final int HOTBAR_START = 0;
    public static final int HOTBAR_END = 8;

    public static final int MAIN_START = 9;
    public static final int MAIN_END = 35;

    public static final int ARMOR_START = 36;
    public static final int ARMOR_END = 39;


    private final int slot, count;

    public FindItemResult(int slot, int count) {
        this.slot = slot;
        this.count = count;
    }

    public int getSlot() {
        return slot;
    }

    public int getCount() {
        return count;
    }

    public boolean found() {
        return slot != -1;
    }

    public Hand getHand() {
        if (slot == OFFHAND) return Hand.OFF_HAND;
        else if (slot == mc.player.getInventory().selectedSlot) return Hand.MAIN_HAND;
        return null;
    }

    public boolean isMainHand() {
        return getHand() == Hand.MAIN_HAND;
    }

    public boolean isOffhand() {
        return getHand() == Hand.OFF_HAND;
    }

    public boolean isHotbar() {
        return slot >= HOTBAR_START && slot <= HOTBAR_END;
    }

    public boolean isMain() {
        return slot >= MAIN_START && slot <= MAIN_END;
    }

    public boolean isArmor() {
        return slot >= ARMOR_START && slot <= ARMOR_END;
    }
}