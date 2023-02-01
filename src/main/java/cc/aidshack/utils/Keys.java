package cc.aidshack.utils;

import cc.aidshack.mixins.KeyBindingAccessor;
import net.minecraft.client.option.KeyBinding;

public class Keys {
    private static final String CATEGORY = "Hypnotic Client";


    public static int getKey(KeyBinding bind) {
        return ((KeyBindingAccessor) bind).getKey().getCode();
    }
}
