package cc.aidshack.module.impl.hud;

import cc.aidshack.config.SaveLoad;
import cc.aidshack.module.Module;

public class SaveConfig extends Module {
    public SaveConfig() {
        super("SaveConfig", "", false, Category.HUD);
    }

    @Override
    public void onEnable() {
        SaveLoad.INSTANCE.save();
        this.setEnabled(false);
        super.onEnable();
    }
}
