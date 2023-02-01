package cc.aidshack.module;

import cc.aidshack.config.ConfigSetting;
import cc.aidshack.event.EventManager;
import cc.aidshack.module.settings.Setting;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Module implements Serializable {

	protected final static MinecraftClient mc = MinecraftClient.getInstance();
	protected final static EventManager eventManager = EventManager.INSTANCE;
	private final String name;
	public String displayName;
	private final String description;
	private Category category;
	private int key;
	private boolean binding;
	@Expose
	@SerializedName("settings")
	public ConfigSetting[] cfgSettings;

	@Expose
	@SerializedName("enabled")
	public boolean enabled;
	public ArrayList<Setting> settings1 = new ArrayList<>();

	public List<Setting> settings = new ArrayList<>();

	public Module(String name, String description, boolean enabled, Category category) {
		this.name = name;
		displayName =name;
		this.description = description;
		this.enabled = enabled;
		this.category = category;
		this.key = 0;
		this.binding = false;
		if (enabled)
			onEnable();
	}

	public List<Setting> getSettings() {
		return settings;
	}

	public void addSetting(Setting setting) {
		settings.add(setting);
	}

	public void addSettings(Setting... settings) {
		for (final Setting setting : settings) {
			addSetting(setting);
		}
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;

	}

	public void toggleSilent() {
		enabled = !enabled;
		if(enabled) {
			onEnableSilent();
			EventManager.INSTANCE.register(this);
		} else {
			onDisableSilent();
			EventManager.INSTANCE.unregister(this);
		}
	}
	public void onMotion() {};
	public Category getCategory() {
		return category;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {

		if (enabled){
			onEnable();
			if (EventManager.INSTANCE != null)
				EventManager.INSTANCE.register(this);
		} else {
			onDisable();
			if (EventManager.INSTANCE != null)
				EventManager.INSTANCE.unregister(this);
		}
		this.enabled = enabled;
	}

	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void toggle()
	{
		enabled = !enabled;
		mc.getSoundManager().play(PositionedSoundInstance.ambient(SoundEvents.UI_BUTTON_CLICK, enabled ? 1 : 0.8f, 1));
		if(enabled) {
			onEnable();
			EventManager.INSTANCE.register(this);
		} else {
			onDisable();
			EventManager.INSTANCE.unregister(this);
		}
	}

	public void onEnable() {
		EventManager.INSTANCE.register(this);
	}

	public void onDisable() {
		EventManager.INSTANCE.unregister(this);
	}

	public void onEnableSilent() {
		EventManager.INSTANCE.register(this);
	}

	public void onDisableSilent() {
		EventManager.INSTANCE.unregister(this);
	}
	public void onTick() {}
	public void onTickDisabled() {}

	public enum Category {

		COMBAT("Combat"/*,"a"*/),
		RENDER("Render"/*, "g"*/),
		MOVEMENT("Movement"/*,"b"*/),
		OTHER("Other"/*,"e"*/),
		HUD("Hud"/*, "c"*/);
		public List<Category> categories;

		public String name;
		public String offset;
		public String icon;


		Category(String name/*, String icon*/) {
			this.name = name;

			/*this.icon = icon*/
		}

	}



}
