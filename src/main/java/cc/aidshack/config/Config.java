package cc.aidshack.config;

import cc.aidshack.AidsHack;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.settings.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private final String name;
    private final File file;

    public ArrayList<Setting> settings = new ArrayList<>();

    public Config(String name) {
        this.name = name;
        this.file = new File(AidsHack.aidsDir, "/configs/" + name + ".json");
    }

    public String getName() {
        return name;
    }

    public File getFile() {
        return file;
    }

    public String serialize() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        for (Module module : ModuleManager.INSTANCE.getAllModules()) {
            List<ConfigSetting> settings = new ArrayList<>();
            for (Setting setting : module.getSettings()) {
                if (setting instanceof KeybindSetting)
                    continue;

                ConfigSetting cfgSetting = new ConfigSetting(null, null);
                cfgSetting.name = setting.name;
                if (setting instanceof BooleanSetting) {
                    cfgSetting.value = ((BooleanSetting) setting).isEnabled();
                }
                if (setting instanceof ModeSetting) {
                    cfgSetting.value = ((ModeSetting) setting).getMode();
                }
                if (setting instanceof DecimalSetting) {
                    cfgSetting.value = ((DecimalSetting) setting).getValue();
                }

                settings.add(cfgSetting);
            }
            module.cfgSettings = settings.toArray(new ConfigSetting[0]);
        }
        return gson.toJson(ModuleManager.INSTANCE.getAllModules());
    }

}
