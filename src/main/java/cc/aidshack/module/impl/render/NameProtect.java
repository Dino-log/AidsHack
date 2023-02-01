package cc.aidshack.module.impl.render;

import cc.aidshack.module.Module;

import static cc.aidshack.AidsHack.MC;

public class NameProtect extends Module {
    private static String replacementUsername = "Marlowww";
    private static boolean isEnabledStatic = false;


    public NameProtect() {
        super("NameProtect", "Hides your name in chat.", false, Category.RENDER);
    }

    @Override
    public void onEnable() {
        isEnabledStatic = true;
    }

    @Override
    public void onDisable() {
        isEnabledStatic = false;
    }

    public static int setReplacementUsername(String newReplacementUsername) {
        replacementUsername = newReplacementUsername;
        return 0;
    }

    public static String replaceName(String string) {
        if (string != null && isEnabledStatic) {
            return string.replace(MC.getSession().getUsername(), replacementUsername);
        }
        return string;
    }
}

