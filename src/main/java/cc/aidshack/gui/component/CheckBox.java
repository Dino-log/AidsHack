package cc.aidshack.gui.component;

import cc.aidshack.gui.frames.ModuleButton;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.hud.ClickGUI;
import cc.aidshack.module.impl.other.CustomFont;
import cc.aidshack.module.settings.BooleanSetting;
import cc.aidshack.module.settings.Setting;
import cc.aidshack.utils.font.FontManager;
import cc.aidshack.utils.font.NahrFont;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class CheckBox extends Component {

	private BooleanSetting boolSet = (BooleanSetting) setting;

	public CheckBox(Setting setting, ModuleButton parent, int offset) {
		super(setting, parent, offset);
		this.boolSet = (BooleanSetting) setting;
	}
	public static NahrFont font = FontManager.roboto;

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		int r = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getRed();
		int g = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getGreen();
		int b = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getBlue();
		DrawableHelper.fill(matrices, parent.parent.x, parent.parent.y + parent.offset + offset, parent.parent.x + parent.parent.width, parent.parent.y + parent.offset + offset + parent.parent.height, new Color(r, g, b, 100).getRGB());
		final int textOffset = parent.parent.height / 2 - mc.textRenderer.fontHeight / 2;
		if (ModuleManager.INSTANCE.getModule(CustomFont.class).isEnabled()){
			font.drawWithShadow(matrices, boolSet.getName() + ": " + checkBoxThing(), parent.parent.x + textOffset, parent.parent.y + parent.offset + offset + textOffset, -1);
		}else {
			mc.textRenderer.drawWithShadow(matrices, boolSet.getName() + ": " + checkBoxThing(), parent.parent.x + textOffset, parent.parent.y + parent.offset + offset + textOffset, -1);
		}
		super.render(matrices, mouseX, mouseY, delta);
	}

	public String checkBoxThing(){
		String string = "Client Not Properly Loaded!";
		if (boolSet.isEnabled()) {
			string = "On";
		}else if(!boolSet.isEnabled()){
			string = "Off";
		}
		return string;
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isHovered(mouseX, mouseY) && button == 0) {
			boolSet.toggle();
		}
		super.mouseClicked(mouseX, mouseY, button);
	}
}
