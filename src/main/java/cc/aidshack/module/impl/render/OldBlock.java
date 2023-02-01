package cc.aidshack.module.impl.render;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventRenderHeldItem;
import cc.aidshack.event.events.EventRenderItem;
import cc.aidshack.event.events.EventSwingHand;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.combat.Killaura;
import cc.aidshack.module.settings.ModeSetting;
import cc.aidshack.utils.math.Timer;
import net.minecraft.client.option.Perspective;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.util.Hand;

public class OldBlock extends Module {

    public ModeSetting animation = new ModeSetting("Block Animation", "1.7(ish)", "1.7(ish)", "Slide", "Sigma", "Swing");
    public OldBlock() {
        super("OldBlock", "", false, Category.RENDER);
        addSettings(animation);
    }

    public float swingTicks = 0;
    public boolean swingHasElapsed = true;


    public static Timer animationTimer = new Timer();
    @EventTarget
    private void runMethod(EventRenderItem event) {
        boolean shouldmove = animationTimer.hasTimeElapsed(1000 / 75, true);
        if (ModuleManager.INSTANCE.getModule(Killaura.class).autoBlockMode.isMode("Visual") && Killaura.target != null ? Killaura.target == null : !mc.player.isUsingItem()) return;
        if ((event.getItemStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem) && shouldmove) {
            if (swingTicks < 60 && !swingHasElapsed) {
                swingTicks+=7;
            }
            if (swingTicks >= 60) swingHasElapsed = true;
            if (swingHasElapsed) {
                swingTicks-=7;
                if (swingTicks <= 0) {
                    swingHasElapsed = false;
                }
            }
        }
    }

    @EventTarget
    private void renderHeldItem(EventRenderHeldItem eventRenderHeldItem) {
        if (eventRenderHeldItem.getHand() == Hand.OFF_HAND && eventRenderHeldItem.getItemStack().getItem() instanceof ShieldItem && (mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem) && mc.options.getPerspective() == Perspective.FIRST_PERSON)
            eventRenderHeldItem.setCancelled(true);
    }

    @EventTarget
    public void onSwingHand(EventSwingHand event) {
        if (mc.options.useKey.isPressed() && (mc.player.getMainHandStack().getItem() instanceof SwordItem || mc.player.getMainHandStack().getItem() instanceof AxeItem ) && !animation.isMode("Swing"))
            event.setCancelled(true);
    }
}
