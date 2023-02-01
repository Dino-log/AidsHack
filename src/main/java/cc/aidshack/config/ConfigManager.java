package cc.aidshack.config;

import cc.aidshack.AidsHack;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.settings.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {
    public static ConfigManager INSTANCE = new ConfigManager();

    private static final List<Config> configs = new ArrayList<>();
    private final File file = new File(AidsHack.aidsDir, "/configs");
    public File config = new File(AidsHack.aidsDir, "/Config.json");
    String[] pathnames;

    public ConfigManager() {
        file.mkdirs();
    }

    public static Config getConfigByName(String name) {
        for (Config config : configs) {
            if (config.getName().equalsIgnoreCase(name)) return config;
        }
        return null;
    }

    public boolean load(String name) {
        Config config = new Config(name);
        try {
            String configString = new String(Files.readAllBytes(config.getFile().toPath()));
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            Module[] modules = gson.fromJson(configString, Module[].class);

            for (Module module : ModuleManager.INSTANCE.getAllModules()) {
                for (Module configModule : modules) {
                    if (module.getName().equals(configModule.getName())) {
                        try {
                            if (configModule.isEnabled() && !module.isEnabled())
                                module.toggle();
                            else if (!configModule.isEnabled() && module.isEnabled())
                                module.setEnabled(false);
                            
                            for (Setting setting : module.settings) {
                                for (ConfigSetting cfgSetting : configModule.cfgSettings) {
                                    if (setting.name.equals(cfgSetting.name)) {
                                        if (setting instanceof BooleanSetting) {
                                            ((BooleanSetting) setting).setEnabled((boolean) cfgSetting.value);
                                        }
                                        if (setting instanceof ModeSetting) {
                                            ((ModeSetting) setting).setMode((String) cfgSetting.value);
                                        }
                                        if (setting instanceof DecimalSetting) {
                                            ((DecimalSetting) setting).setValue((double) cfgSetting.value);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean loadConfig() {
        try {
            String configString = new String(Files.readAllBytes(config.toPath()));
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
            Module[] modules = gson.fromJson(configString, Module[].class);

            for (Module module : ModuleManager.INSTANCE.getAllModules()) {
                for (Module configModule : modules) {
                    if (module.getName().equals(configModule.getName())) {
                        try {
                            if (configModule.isEnabled() && !module.isEnabled())
                                module.setEnabled(true);
                            else if (!configModule.isEnabled() && module.isEnabled())
                                module.setEnabled(false);
                            for (Setting setting : module.settings) {
                                for (ConfigSetting cfgSetting : configModule.cfgSettings) {
                                    if (setting.name.equals(cfgSetting.name)) {
                                        if (setting instanceof BooleanSetting) {
                                            ((BooleanSetting) setting).setEnabled((boolean) cfgSetting.value);
                                        }
                                        if (setting instanceof ModeSetting) {
                                            ((ModeSetting) setting).setMode((String) cfgSetting.value);
                                        }
                                        if (setting instanceof DecimalSetting) {
                                            ((DecimalSetting) setting).setValue((double) cfgSetting.value);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean save(String name) {


        Config config = new Config(name);

        try {
            config.getFile().getParentFile().mkdirs();
            Files.write(config.getFile().toPath(), config.serialize().getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save " + config);
            return false;
        }
    }

    public void saveConfig() {
        try {
            config.getParentFile().mkdirs();
            Files.write(config.toPath(), serialize().getBytes(StandardCharsets.UTF_8));

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save " + config);
        }
    }

    public String serialize() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
        for (Module module : ModuleManager.INSTANCE.getAllModules()) {
            List<ConfigSetting> settings = new ArrayList<>();
            for (Setting setting : module.settings) {
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

    public boolean save(Config config) {
        return this.save(config);
    }

    public void saveAll() {
        configs.forEach(config -> save(config.getName()));
    }

    public void loadConfigs() {
        for (File file : file.listFiles()) {
            configs.add(new Config(file.getName().replace(".json", "")));
        }
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public void list() {
        pathnames = file.list();
        for (String pathname : pathnames) {
        	System.out.println(pathname.substring(0, pathname.length() - 5));
        }
    }

    public void delete(String configName) {
        Config config = new Config(configName);
        try {
            Files.deleteIfExists(config.getFile().toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
