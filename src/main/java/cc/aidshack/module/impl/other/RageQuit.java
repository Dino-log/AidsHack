package cc.aidshack.module.impl.other;

import cc.aidshack.module.Module;

public class RageQuit extends Module{
    public RageQuit() {
        super("RageQuit", "nigga", false, Category.OTHER);
    }


    @Override
    public void onTick() {
        noHurtCamMixins();
    }

    public void noHurtCamMixins(){
        noHurtCamMixins();
    }
}
