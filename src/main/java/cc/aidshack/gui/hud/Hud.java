package cc.aidshack.gui.hud;

import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.hud.ClickGUI;
import cc.aidshack.utils.font.FontManager;
import cc.aidshack.utils.font.NahrFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

public class Hud {


    private static MinecraftClient mc = MinecraftClient.getInstance();

    public static void render(MatrixStack matrixStack, float tickDelta){
        renderArrayList(matrixStack);
    }

    public static NahrFont font = FontManager.roboto;

    public static void renderArrayList(MatrixStack matrixStack){
        if (ModuleManager.INSTANCE.getModule(cc.aidshack.module.impl.hud.Hud.class).isEnabled() && ModuleManager.INSTANCE.getModule(cc.aidshack.module.impl.hud.Hud.class).arrayList.isEnabled()){
            int index = 0 ;
            int sWidth = mc.getWindow().getScaledWidth();
            int sHeight = mc.getWindow().getScaledHeight();
            List<Module> enabled = ModuleManager.INSTANCE.getEnabledModules();
            enabled.sort(Comparator.comparingInt(m -> (int)mc.textRenderer.getWidth(((Module)m).getName())).reversed());
            for (Module module : enabled){
                int r = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getRed();
                int g = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getGreen();
                int b = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getBlue();
                mc.textRenderer.drawWithShadow(matrixStack, module.getName(), (sWidth -4) - mc.textRenderer.getWidth(module.getName()), 10 + (index * mc.textRenderer.fontHeight), new Color(r, g,b, 255).getRGB());
                index++;
            }
        }

    }
}
