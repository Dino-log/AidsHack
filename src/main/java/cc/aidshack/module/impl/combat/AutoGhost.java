package cc.aidshack.module.impl.combat;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.ModeSetting;

public class AutoGhost extends Module {
    public AutoGhost() {
        super("AutoGhost", "", false, Category.COMBAT);
        addSetting(mode);
    }
    public ModeSetting mode = new ModeSetting("Mode", "MainHand", "Offhand", "MainHand", "Both");


}
