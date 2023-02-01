package cc.aidshack.event.events;

import cc.aidshack.event.Event;
import net.minecraft.client.util.math.MatrixStack;

public class EventRenderHUD extends Event {

    private final MatrixStack matrices;
    private final double tickDelta;


    public EventRenderHUD(MatrixStack matrices,double tickDelta) {
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    public MatrixStack getMatrices() {
        return matrices;
    }

    public double getTickDelta() {
        return tickDelta;
    }

    public static class Tick extends EventRenderGUI {
    }
}
