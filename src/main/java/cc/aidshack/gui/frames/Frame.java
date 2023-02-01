package cc.aidshack.gui.frames;

import cc.aidshack.config.SaveLoad;
import cc.aidshack.gui.component.Component;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.hud.ClickGUI;
import cc.aidshack.module.impl.other.CustomFont;
import cc.aidshack.utils.font.FontManager;
import cc.aidshack.utils.font.NahrFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Frame {
	private final ArrayList<Frame> frames = new ArrayList<>();
	public ArrayList<ModuleButton> moduleButtons;

	public int x, y, width, height, dragX, dragY ;
	public Module.Category category;
	public List<Module.Category> categories;

	public double scrollAmount;

	public boolean dragging, extended;

	protected ArrayList<Component> components = new ArrayList<>();


	public List<ModuleButton> buttons;
	public List<DrawableHelper> drawOffsets;

	protected MinecraftClient mc = MinecraftClient.getInstance();

	public Frame(Module.Category category, int x, int y, int width, int height) {
		this.category = category;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.dragging = false;
		this.extended = false;

		buttons = new ArrayList<>();

		int offset = height;
		for (final Module module : ModuleManager.INSTANCE.getModulesInCategory(category)) {
			buttons.add(new ModuleButton(module, this, offset));
			offset += height;
		}
	}
	public static NahrFont font = FontManager.roboto;


	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
		int r = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getRed();
		int g = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getGreen();
		int b = ClickGUI.class.cast(ModuleManager.INSTANCE.getModule(ClickGUI.class)).getBlue();
		final int offset = height / 2 - mc.textRenderer.fontHeight / 2;
		DrawableHelper.fill(matrixStack, x, y, x + width, y +height, new Color(r, g, b).getRGB());


		if (ModuleManager.INSTANCE.getModule(CustomFont.class).isEnabled()){
			font.drawWithShadow(matrixStack, category.name, x + offset, y + offset, Color.white.getRGB());
			font.drawWithShadow(matrixStack, extended ? "-" : "+", x + width - offset - 2 - font.getWidth("+"), y + offset, Color.white.getRGB());
		}else {
			mc.textRenderer.drawWithShadow(matrixStack, category.name, x + offset, y + offset, Color.white.getRGB());
			mc.textRenderer.drawWithShadow(matrixStack, extended ? "-" : "+", x + width - offset - 2 - mc.textRenderer.getWidth("+"), y + offset, Color.white.getRGB());

		}

		if (extended) {

			for (final ModuleButton button : buttons) {
				button.render(matrixStack, mouseX, mouseY, delta);
			}
		}
	}

	public boolean isExtended() {
		return extended;
	}

	public Module.Category getCategory() {
		return category;
	}
	public String getCategoryName(){
		return category.name;
	}


	public void mouseClicked(double mouseX, double mouseY, int button) {
		if (isHovered(mouseX, mouseY)) {
			if (button == 0) {
				dragging = true;
				dragX = (int) (mouseX - x);
				dragY = (int) (mouseY - y);
			} else if (button == 1) {
				extended = !extended;
			}
		}
		if (extended)
			for (final ModuleButton mb : buttons) {
				mb.mouseClicked(mouseX, mouseY, button);
			}
	}


	public void setExtended(boolean extended) {
		this.extended = extended;
	}

	public boolean isHovered(double mouseX, double mouseY) {
		return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y+ height;
	}

	public void mouseReleased(double mouseX, double mouseY, int button) {
		if (button == 0 && dragging == true)
			dragging = false;
			SaveLoad.INSTANCE.save();
		for (final ModuleButton mb : buttons) {
			mb.mouseReleased(mouseX, mouseY, button);
		}
	}
	public void mouseScrolled(double mouseX, double mouseY, double amount){
			if (amount > 0) setY((int) (getY() + 15));
			else if (amount < 0) setY((int) (getY() - 15));
			/*
			for (ModuleButton button : buttons) {
				if (amount > 0) button.setOffset((int) (button.getOffset() + 5));
				else if (amount < 0) button.setOffset((int) (button.getOffset() - 5));
				button.mouseScrolled(mouseX, mouseY, amount);
			}

			 */

		}


	public void setDragging(boolean dragging) {
		if (dragging)
			SaveLoad.INSTANCE.save();
		this.dragging = dragging;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public int getX() {
		return x;
	}

	public void updatePosition(double mouseX, double mouseY) {
		if (dragging) {
			x = (int) mouseX - dragX;
			y = (int) mouseY - dragY;
		}
	}

	public void updateButtons() {
		int offset = height;

		for (final ModuleButton button : buttons) {
			button.offset = offset;
			offset += height;
			if (button.extended) {
				for (final Component component : button.components) {
					if (component.setting.isVisible())
						offset += height;
				}
			}
		}
	}
}
