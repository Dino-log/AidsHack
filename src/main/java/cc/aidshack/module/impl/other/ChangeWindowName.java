package cc.aidshack.module.impl.other;

import cc.aidshack.AidsHack;
import cc.aidshack.module.Module;

public class ChangeWindowName extends Module {
    public static String windowName = AidsHack.FULL_MOD_NAME_DASH;

    public ChangeWindowName() {
        super("ChangeWindowName", "", false, Category.OTHER);
    }

    public static int replaceWindowName(String string){
        windowName = string;
        return 0;
    }
}
