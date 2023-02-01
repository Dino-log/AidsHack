package cc.aidshack.gui;

import cc.aidshack.AidsHack;
import cc.aidshack.gui.frames.Frame;
import cc.aidshack.gui.frames.ModuleButton;
import cc.aidshack.module.Module;
import cc.aidshack.utils.font.FontManager;
import cc.aidshack.utils.font.NahrFont;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ClickGUI extends Screen {

	private Screen prevScreen;
	public static final ClickGUI INSTANCE = new ClickGUI();
	private MinecraftClient mc = MinecraftClient.getInstance();

	public ArrayList<Frame> frames1;
	public ArrayList<ModuleButton> buttons;

	int scrollAmount = 0;


	int offset = 20;

	public List<Frame> frames;

	private ClickGUI() {
		super(new LiteralText("Click GUI"));

		frames = new ArrayList<>();

		for (final Module.Category category : Module.Category.values()) {
			frames.add(new Frame(category, offset, 20, 120, 20));
			offset += 120;
		}
	}

	public static NahrFont font = FontManager.roboto;

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		for (final Frame frame : frames) {
			frame.render(matrices, mouseX, mouseY, delta);
			frame.updatePosition(mouseX, mouseY);
		}
		super.render(matrices, mouseX, mouseY, delta);
		mc.textRenderer.drawWithShadow(matrices, AidsHack.FULL_MOD_NAME, 10, 10, Color.red.getRGB());
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public int getZOffset() {
		return super.getZOffset();
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		for (final Frame frame : frames) {
			frame.mouseClicked(mouseX, mouseY, button);
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		for (final Frame frame : frames) {
			frame.mouseReleased(mouseX, mouseY, button);
		}
		return super.mouseReleased(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (amount > 0) setOffset((int) (getOffset() + 5));
		else if (amount < 0) setOffset((int) (getOffset() - 5));
		for (final Frame frame : frames) {
			frame.mouseScrolled(mouseX, mouseY, amount);
		}
		return super.mouseScrolled(mouseX, mouseY, amount);
	}

	@Override
	public boolean shouldPause() {
		return false;
	}
}
