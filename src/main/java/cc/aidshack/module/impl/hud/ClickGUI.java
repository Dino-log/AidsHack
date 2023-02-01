package cc.aidshack.module.impl.hud;

import cc.aidshack.event.EventTarget;
import cc.aidshack.event.events.EventKeyPress;
import cc.aidshack.module.Module;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public class ClickGUI extends Module   {

    public DecimalSetting red = new DecimalSetting("Red",0, 255, 171, 1);
    public DecimalSetting green = new DecimalSetting("Green",0, 255, 2, 1);
    public DecimalSetting blue = new DecimalSetting("Blue",0, 255, 2, 1);
    public BooleanSetting prideMode = new BooleanSetting("Epilepsy", false);
    public DecimalSetting prideSpeed = new DecimalSetting("Epilepsy Speed", 1, 20, 5, 1);

    public double h = 360;
    public double s = 1;
    public double v = 1;
    public ClickGUI() {
        super("ClickGUI", "toggles the ClickGUI", true, Category.HUD);
        addSettings(prideMode, red, green, blue, prideSpeed);
        this.setKey(GLFW.GLFW_KEY_RIGHT_CONTROL);
    }


    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.setEnabled(true);
    }

    public int getBlue() {
        if(prideMode.isEnabled()) {
            Color rgb = new Color(Color.HSBtoRGB((float) h/360.0f, (float) s, (float) v));
            return rgb.getBlue();
        }
        return blue.getValueInt();
    }

    public int getGreen() {
        if(prideMode.isEnabled()) {
            int rgb = (Color.HSBtoRGB((float) h/360.0f, (float) s, (float) v));
            return new Color(rgb).getGreen();
        }
        return green.getValueInt();
    }

    public int getRed() {
        if(prideMode.isEnabled()) {
            Color rgb = new Color(Color.HSBtoRGB((float) h/360.0f, (float) s, (float) v));
            return rgb.getRed();
        }
        return red.getValueInt();
    }

    @EventTarget
    public void onKeyPress(EventKeyPress event) {
        if (event.getKey() == this.getKey()){
            mc.setScreen(cc.aidshack.gui.ClickGUI.INSTANCE);
        }
    }

    @Override
    public void onTick() {
        if (prideSpeed.getValueInt() == 1) {
            if(h<360){
                h++;
            } else {
                h=0;
            }
        } else if (prideSpeed.getValueInt() == 2) {
            if(h<360){
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 3) {
            if(h<360){
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 4) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 5) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 6) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 7) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 8) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;

            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 9) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 10) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 11) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 12) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 13) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 14) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 15) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 16) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 17) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 18) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 19) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }else if (prideSpeed.getValueInt() == 20) {
            if(h<360){
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
                h++;
            } else {
                h=0;
            }
        }
    }
}
