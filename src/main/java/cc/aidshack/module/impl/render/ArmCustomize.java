package cc.aidshack.module.impl.render;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventRenderHand;
import cc.aidshack.event.events.EventRenderItem;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.DecimalSetting;
import net.minecraft.item.ItemGroup;

public class ArmCustomize extends Module {

    public DecimalSetting scale = new DecimalSetting("Hand Scale", 0, 2, 1, 0.01);
    public DecimalSetting mainX = new DecimalSetting("Mainhand X", -2, 2, 0, 0.01);
    public DecimalSetting mainY = new DecimalSetting("Mainhand Y", -2, 2, 0, 0.01);
    public DecimalSetting mainZ = new DecimalSetting("Mainhand Z", -2, 2, 0, 0.01);
    public DecimalSetting offX = new DecimalSetting("Offhand X", -2, 2, 0, 0.01);
    public DecimalSetting offY = new DecimalSetting("Offhand Y", -2, 2, 0, 0.01);
    public DecimalSetting offZ = new DecimalSetting("Offhand Z", -2, 2, 0, 0.01);

    public ArmCustomize() {
        super("ArmCustomize", "Change how your arm looks", false, Module.Category.RENDER);
        addSettings(scale, mainX, mainY, mainZ, offX, offY, offZ);
    }

    @EventTarget
    public void renderHeldItem(EventRenderItem event) {
        float main = (float) scale.getValue();
        float off = (float) scale.getValue();
        switch (event.getRenderTime()) {
            case PRE:
                event.getMatrixStack().push();
                switch (event.getType()) {
                    case FIRST_PERSON_RIGHT_HAND:
                        event.getMatrixStack().translate(mainX.getValue(), mainY.getValue(), mc.player.getMainHandStack().getItem().getGroup() == ItemGroup.FOOD && mc.player.isUsingItem() ? 0 : mainZ.getValue());
                        event.getMatrixStack().scale(main, main, main);
                        break;
                    case FIRST_PERSON_LEFT_HAND:
                        event.getMatrixStack().translate(offX.getValue(), offY.getValue(), mc.player.getMainHandStack().getItem().getGroup() == ItemGroup.FOOD && mc.player.isUsingItem() ? 0 : -offZ.getValue());
                        event.getMatrixStack().scale(off, off, off);
                        break;
                    case FIXED:
                        break;
                    case GROUND:
                        break;
                    case GUI:
                        break;
                    case HEAD:
                        break;
                    case NONE:
                        break;
                    case THIRD_PERSON_LEFT_HAND:

                        break;
                    case THIRD_PERSON_RIGHT_HAND:
                        break;
                    default:
                        break;
                }
                break;
            case POST:
                event.getMatrixStack().pop();
                break;
        }
    }

    @EventTarget
    public void renderHand(EventRenderHand event) {
    }
}
