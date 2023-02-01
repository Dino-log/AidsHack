package cc.aidshack.module.settings;

public class Setting {

	public String name;
	public boolean visible = true;

	public Setting(String name) {
		this.name = name;
	}

	public String displayName = " ";

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getName() {
		return name;
	}
}
