package cc.aidshack.utils;


import cc.aidshack.command.CommandManager;
import cc.aidshack.mixins.ChatHudAccessor;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.render.NameProtect;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.BaseText;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import static cc.aidshack.AidsHack.MC;
import static cc.aidshack.AidsHack.mc;


public enum ChatUtils {
	;

	private static final String SYNTAX_ERROR_PREFIX =
			"\u00a74Syntax error:\u00a7r ";
	private static boolean enabled = true;

	public static void setEnabled(boolean enabled)
	{
		ChatUtils.enabled = enabled;
	}
	private static final String prefix = "§f[§9Aids§fHack§f] ";
	public static final String AIDSPREFIX = "§f[§9Aids§fHack§f] ";
	public static void tellPlayerRaw(Text message) {
		mc.inGameHud.getChatHud().addMessage(message);
	}


	public static void tellPlayer(Text message) {
		tellPlayerRaw(new LiteralText((AIDSPREFIX)).append(new LiteralText("\2477" + message.getString())));
	}

	public static void tellPlayer(String message) {
		tellPlayer(new LiteralText(message));
	}

	public static void tellPlayerRaw(String message) {
		tellPlayerRaw(new LiteralText(message));
	}

	private static String formatMsg(String format, Formatting defaultColor, Object... args) {
		String msg = String.format(format, args);
		msg = msg.replaceAll("\\(default\\)", defaultColor.toString());
		msg = msg.replaceAll("\\(highlight\\)", Formatting.WHITE.toString());
		msg = msg.replaceAll("\\(underline\\)", Formatting.UNDERLINE.toString());

		return msg;
	}
	public static void log(String message) {
		LogManager.getLogger().info("[AidsHack] {}", message.replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
	}
	public static void sendMsg(Text message) {
		sendMsg(null, message);
	}

	public static void sendMsg(String prefix, Text message) {
		sendMsg(0, prefix, Formatting.LIGHT_PURPLE, message);
	}

	public static void sendMsg(Formatting color, String message, Object... args) {
		sendMsg(0, null, null, color, message, args);
	}

	public static void sendMsg(int id, Formatting color, String message, Object... args) {
		sendMsg(id, null, null, color, message, args);
	}

	public static void warning(String message, Object... args) {
		sendMsg(Formatting.YELLOW, message, args);
	}

	public static void warning(String prefix, String message, Object... args) {
		sendMsg(0, prefix, Formatting.LIGHT_PURPLE, Formatting.YELLOW, message, args);
	}
	public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Formatting messageColor, String messageContent, Object... args) {
		sendMsg(id, prefixTitle, prefixColor, formatMsg(messageContent, messageColor, args), messageColor);
	}
	public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, String messageContent, Formatting messageColor) {
		BaseText message = new LiteralText(messageContent);
		message.setStyle(message.getStyle().withFormatting(messageColor));
		sendMsg(id, prefixTitle, prefixColor, message);
	}

	public static void sendMsg(int id, @Nullable String prefixTitle, @Nullable Formatting prefixColor, Text msg) {
		if (mc.world == null) return;

		BaseText message = new LiteralText("");
		message.append(CommandManager.get().getPrefix());
		if (prefixTitle != null) message.append(CommandManager.get().getPrefix());
		message.append(msg);

		id = 0;

		((ChatHudAccessor) mc.inGameHud.getChatHud()).add(message, id);
	}

	public static void info(String message) {
		String string = prefix + "Info: " + message;
		sendPlainMessage(string);
	}

	public static void error(String message) {
		String string = prefix + "§4Error: §f" + message;
		sendPlainMessage(string);
	}

	public static void error(String prefix, String message, Object... args) {
		sendMsg(0, prefix, Formatting.LIGHT_PURPLE, Formatting.RED, message, args);
	}

	public static void sendPlainMessage(String message) {
		InGameHud hud = MC.inGameHud;
		if (hud != null)
			hud.getChatHud().addMessage(new LiteralText(message));
	}

	public static void plainMessageWithPrefix(String message) {
		String string = prefix + message;
		sendPlainMessage(string);
	}

	public static String replaceName(String string) {
		if (string != null && ModuleManager.INSTANCE.getModule(NameProtect.class).isEnabled()) {
			return string.replace(MC.getSession().getUsername(), "Marlowww");
		}
		return string;
	}
	public static void component(Text component)
	{
		if(!enabled)
			return;

		ChatHud chatHud = MC.inGameHud.getChatHud();
		LiteralText prefix = new LiteralText(AIDSPREFIX);
		chatHud.addMessage(prefix.append(component));
	}

    public static void message(String message) {
		component(new LiteralText(message));
    }

	public static void syntaxError(String message)
	{
		message(SYNTAX_ERROR_PREFIX + message);
	}


}
