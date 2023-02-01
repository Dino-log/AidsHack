package cc.aidshack.module.settings;



public class KeybindSetting extends Setting {

    private int code;

    public KeybindSetting(String name, int code) {
        super(name);
        this.code = code;
    }

    public int getCode() {
        return code == -1 ? 0 : code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
