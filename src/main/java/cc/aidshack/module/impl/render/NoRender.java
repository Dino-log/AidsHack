package cc.aidshack.module.impl.render;

import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;

public class NoRender extends Module {

    public BooleanSetting fire = new BooleanSetting("Fire", false);
    public BooleanSetting portal = new BooleanSetting("Portal", false);
    public BooleanSetting liquid = new BooleanSetting("Liquid", false);
    public BooleanSetting pumpkin = new BooleanSetting("Pumpkin", false);

    public NoRender() {
        super("NoRender", "Removes certain overlays", false,Module.Category.RENDER);
        addSettings(fire, portal, liquid, pumpkin);
    }
}
