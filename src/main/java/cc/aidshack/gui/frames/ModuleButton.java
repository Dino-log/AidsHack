package cc.aidshack.gui.frames;

import cc.aidshack.gui.component.CheckBox;
import cc.aidshack.gui.component.Component;
import cc.aidshack.gui.component.DecimalSlider;
import cc.aidshack.gui.component.ModeBox;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.hud.ClickGUI;
import cc.aidshack.module.impl.other.CustomFont;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.module.settings.ModeSetting;
import cc.aidshack.module.settings.Setting;
import cc.aidshack.utils.font.FontManager;
import cc.aidshack.utils.font.NahrFont;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static cc.aidshack.AidsHack.mc;

public class ModuleButton {

    public Module module;
    public Frame parent;
    public int offset;
    public List<Component> components;
    public String name = "";
    public boolean extended;

    public ModuleButton(Module module, Frame parent, int offset){
        this.module = module;
        this.parent = parent;
        this.offset = offset;
        this.extended = false;
        this.components = new ArrayList<>();
        this.name = module.getName();

        int setOffset = parent.height;
        for (Setting setting : module.getSettings()){
            if (setting instanceof BooleanSetting){
                components.add(new CheckBox(setting, this, setOffset));
            }else if (setting instanceof ModeSetting){
                components.add(new ModeBox(setting, this, setOffset));
            }else if (setting instanceof DecimalSetting) {
                components.add(new DecimalSlider(setting, this, setOffset));
            }
            setOffset+= parent.height;
        }
    }
    public static NahrFont font = FontManager.roboto;
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta){
        DrawableHelper.fill(matrices, parent.x, parent.y + offset, parent.x +parent.width, parent.y + offset + parent.height, new Color(0, 0, 0, 160).getRGB());
        if (isHovered(mouseX, mouseY)) DrawableHelper.fill(matrices, parent.x, parent.y + offset, parent.x +parent.width, parent.y + offset + parent.height, new Color(0, 0, 0, 160).getRGB());
        int r = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getRed();
        int g = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getGreen();
        int b = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getBlue();
        int textOffset = ((parent.height /2) - parent.mc.textRenderer.fontHeight / 2);
        if (ModuleManager.INSTANCE.getModule(CustomFont.class).isEnabled()){
            font.drawWithShadow(matrices, module.getName(), parent.x+ textOffset, parent.y + offset + textOffset, module.isEnabled() ? new Color(r, g, b, 255).getRGB() : -1);

        }else {
            mc.textRenderer.drawWithShadow(matrices, module.getName(), parent.x+ textOffset, parent.y + offset + textOffset, module.isEnabled() ? new Color(r, g, b, 255).getRGB() : -1);

        }

        if (extended) {
            for (Component component : components) {
                component.render(matrices, mouseX, mouseY, delta);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }

    public void mouseClicked(double mouseX, double mouseY, int button){
        if (isHovered(mouseX, mouseY)){
            if (button == 0){
                module.toggle();
            }else if (button == 1){
                extended = !extended;
                parent.updateButtons();
            }
        }
        for (Component component : components){
            component.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        for (Component component : components){
            component.mouseReleased(mouseX, mouseY, button);
        }
    }
    /*
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        for (Component component : components) {
            if (amount > 0) component.setOffset((int) (component.getOffset() + 5));
            else if (amount < 0) component.setOffset((int) (component.getOffset() - 5));
            component.mouseScrolled(mouseX, mouseY, amount);
        }
        return true;
    }

     */




        public boolean isHovered(double mouseX, double mouseY){
        return mouseX > parent.x && mouseX < parent.x + parent.width && mouseY > parent.y + offset && mouseY < parent.y + offset + parent.height;
    }
}
