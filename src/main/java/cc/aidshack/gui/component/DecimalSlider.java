package cc.aidshack.gui.component;

import cc.aidshack.gui.frames.ModuleButton;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.hud.ClickGUI;
import cc.aidshack.module.impl.other.CustomFont;
import cc.aidshack.module.settings.DecimalSetting;
import cc.aidshack.module.settings.Setting;
import cc.aidshack.utils.font.FontManager;
import cc.aidshack.utils.font.NahrFont;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class DecimalSlider extends Component {

	private DecimalSetting numSet = (DecimalSetting) setting;
	private boolean sliding = false;

	public DecimalSlider(Setting setting, ModuleButton parent, int offset) {
		super(setting, parent, offset);
		this.numSet = (DecimalSetting) setting;
		numSet.displayName = numSet.name + ": " + numSet.getValue();
	}

	public static NahrFont font = FontManager.roboto;
	public static NahrFont fontSmall = FontManager.robotoSmall;

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		numSet.displayName = numSet.name + ": " + numSet.getValue();
		int r = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getRed();
		int g = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getGreen();
		int b = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getBlue();

		double min = numSet.getMin();
		double max = numSet.getMax();

		final double diff = Math.min(parent.parent.width, Math.max(0, mouseX - parent.parent.x));
		double renderWidth = (parent.parent.width * (numSet.getValue() - min) / (max - min));


		Screen.fill(matrices, parent.parent.x, parent.parent.y + parent.offset + offset, parent.parent.x + parent.parent.width, parent.parent.y + parent.offset + offset + parent.parent.height, new Color(r,g ,b, 100).getRGB());
		Screen.fill(matrices, parent.parent.x, parent.parent.y + parent.offset + offset, (int) (parent.parent.x + renderWidth), parent.parent.y + parent.offset + offset + parent.parent.height, new Color(r, g, b).getRGB());

		if (sliding) {
			if (diff == 0) {
				numSet.setValue(numSet.getMin());
			} else {
				double newValue = roundToPlace(((diff / parent.parent.width) * (max - min) + min), 2);
				numSet.setValue(newValue);
				//numSet.setValue(roundToPlace(diff / parent.parent.width * (numSet.getMax() - numSet.getMin() + numSet.getMin()), 2));
			}
		}

		final int textOffset = parent.parent.height / 2 - mc.textRenderer.fontHeight / 2;
		if (ModuleManager.INSTANCE.getModule(CustomFont.class).isEnabled()){
			fontSmall.drawWithShadow(matrices, numSet.getName() + ": " + numSet.getValue(), parent.parent.x + textOffset, parent.parent.y + parent.offset + offset + textOffset, -1);

		}else {
			mc.textRenderer.drawWithShadow(matrices, numSet.getName() + ": " + numSet.getValue(), parent.parent.x + textOffset, parent.parent.y + parent.offset + offset + textOffset, -1);

		}

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isHovered(mouseX, mouseY)) {
			sliding = true;
		}
		super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void mouseReleased(double mouseX, double mouseY, int button) {
		sliding = false;
		super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean isHovered(double mouseX, double mouseY) {
		return mouseX > parent.parent.x && mouseX < parent.parent.x + parent.parent.width && mouseY > parent.parent.y + parent.offset + offset && mouseY < parent.parent.y + parent.offset + offset + parent.parent.height;
	}

	private double roundToPlace(double value, int place) {
		if (place < 0) {
			throw new IllegalArgumentException();
		}

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(place, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
}
