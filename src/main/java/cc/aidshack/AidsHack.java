package cc.aidshack;

import cc.aidshack.config.ConfigManager;
import cc.aidshack.config.SaveLoad;
import cc.aidshack.core.BlockIterator;
import cc.aidshack.core.CrystalDataTracker;
import cc.aidshack.event.EventManager;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.utils.DamageUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum AidsHack {


	CLIENT;
	public static final Identifier LOCATION = new Identifier("aidshack", "aidshack");

	public static String MOD_VERSION = "1.0";
	public static String MOD_NAME = "AidsHack";
	public static Logger LOGGER = LogManager.getLogger("NoHurtCarn");

	public static String MOD_NAME_WITH_SPACE = "AidsHack ";
	public static String FULL_MOD_NAME = "AidsHack 1.0";
	public static String FULL_MOD_NAME_DASH = "AidsHack-1.0";
	public static String aidsDir = MinecraftClient.getInstance().runDirectory.getPath() + File.separator + "aidshack";


	public static MinecraftClient mCc = MinecraftClient.getInstance();

	public static MinecraftClient mc = MinecraftClient.getInstance();
	public static MinecraftClient MC = MinecraftClient.getInstance();

	public ModuleManager moduleManager;
	public EventManager eventManager;
	public ConfigManager cfgManager;
	public SaveLoad saveload;
	public CrystalDataTracker crystalDataTracker;

	public List<String> users = new ArrayList<>();
	public static boolean disabledByCurrentServer;



	public String[] devID = {
		"e407a8c0-ba44-497f-9ee0-73032cc5192f",
		"620839ff-96b5-4e65-85b3-803ca1df9c99"
	};
	private Robot robot;
	public void register(){
		moduleManager = ModuleManager.INSTANCE;
		eventManager = EventManager.INSTANCE;
		cfgManager = new ConfigManager();
		saveload = new SaveLoad();
		System.setProperty("java.awt.headless", "false");
		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		crystalDataTracker = CrystalDataTracker.INSTANCE;
		EventManager.INSTANCE.register(DamageUtils.getInstance());
		EventManager.INSTANCE.register(BlockIterator.INSTANCE);
		ModuleManager.INSTANCE.loadModules();
		MC = MinecraftClient.getInstance();
		mc = MinecraftClient.getInstance();





		ClientPlayNetworking.registerGlobalReceiver(LOCATION, (client, handler, buf, responseSender) -> {
			disabledByCurrentServer = buf.readBoolean();
			client.execute(() -> client.getToastManager().add(SystemToast.create(client, SystemToast.Type.TUTORIAL_HINT,
					new LiteralText("CrystalPingBypass"),
					new TranslatableText(disabledByCurrentServer ? "crpingbypass.server.disabled" : "crpingbypass.server.enabled"))));
		});
	}
	public void loadFiles(){
		if (cfgManager.config.exists()) {
			cfgManager.loadConfig();
		}
		Thread configDaemon = new Thread(() -> {
			while (true) {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				cfgManager.saveConfig();
				saveload.save();
			}
		});
		configDaemon.setDaemon(true);
		configDaemon.start();
		saveload.load();
	}
	public Robot getRobot() {
		return robot;
	}

	public static boolean isAidsHackUser(String name) {
		return AidsHack.CLIENT.users.contains(name);
	}

	public static void setAidsHackUser(String name, boolean using) {
		if (using) AidsHack.CLIENT.users.add(name);
		else if (!using && AidsHack.CLIENT.users.contains(name)) AidsHack.CLIENT.users.remove(name);
	}
	public void shutdown() {
		System.out.println("SHUTING DOWN, GOODBYE");
	}

}
