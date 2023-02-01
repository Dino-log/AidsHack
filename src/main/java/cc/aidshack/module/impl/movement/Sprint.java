package cc.aidshack.module.impl.movement;

import cc.aidshack.module.Module;

public class Sprint extends Module   {
    public Sprint(){
        super("Sprint", "auto sprints",false, Category.MOVEMENT);

    }

    @Override
    public void onEnable() {
        super.onEnable();

    }

    @Override
    public void onDisable() {
        super.onDisable();
    }



    @Override
    public void onTick() {
        mc.player.setSprinting(true);
    }

}
