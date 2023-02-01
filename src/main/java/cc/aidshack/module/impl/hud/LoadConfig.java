package cc.aidshack.module.impl.hud;

import cc.aidshack.config.SaveLoad;
import cc.aidshack.module.Module;

public class LoadConfig extends Module {
    public LoadConfig() {
        super("LoadConfig", "", false, Category.HUD);
    }

    @Override
    public void onEnable() {
        SaveLoad.INSTANCE.load();
        this.setEnabled(false);
        super.onEnable();
    }
}
