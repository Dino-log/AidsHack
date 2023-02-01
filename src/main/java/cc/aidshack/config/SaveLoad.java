package cc.aidshack.config;

import cc.aidshack.AidsHack;
import cc.aidshack.gui.ClickGUI;
import cc.aidshack.gui.frames.Frame;
import cc.aidshack.gui.frames.ModuleButton;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;

import java.io.*;
import java.util.ArrayList;

public class SaveLoad {
    public File dir;
    public File configs;
    public File dataFile;

    public static SaveLoad INSTANCE = new SaveLoad();

    public SaveLoad() {
        dir = new File(AidsHack.aidsDir);
        if (!dir.exists()) {
            dir.mkdir();
        }
        configs = new File(AidsHack.aidsDir);
        if (!configs.exists()) {
            configs.mkdir();
        }
        dataFile = new File(configs, "data.txt");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {e.printStackTrace();}
        }
    }


    public void save() {

        AidsHack.LOGGER.info("Saving...");
        ArrayList<String> toSave = new ArrayList<String>();

        for (Module mod : ModuleManager.INSTANCE.modules) {
            toSave.add("MOD:" + mod.getName() + ":" + mod.isEnabled() + ":" + mod.getKey());
        }


        for (Frame frame : ClickGUI.INSTANCE.frames) {
            toSave.add("FRAME:" + frame.getCategoryName() + ":" + frame.getX() + ":" + frame.getY() + ":" + frame.isExtended());
        }
        toSave.add("CLICKGUI:X:" + ClickGUI.INSTANCE.getZOffset() + ":Y:" + ClickGUI.INSTANCE.getOffset());

        try {
            PrintWriter pw = new PrintWriter(this.dataFile);
            for (String str : toSave) {
                pw.println(str);
            }
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void load() {


        ArrayList<String> lines = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.dataFile));
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String s : lines) {
            String[] args = s.split(":");
            if (s.toLowerCase().startsWith("mod:")) {
                Module m = ModuleManager.INSTANCE.getModuleByName(args[1]);
                if (m != null) {
                    m.setKey(Integer.parseInt(args[3]));
                }
            } else if (s.toLowerCase().startsWith("frame:")) {
            	for (Frame frame : ClickGUI.INSTANCE.frames) {
            		if (frame.getCategoryName().equalsIgnoreCase(args[1])) {
            			frame.setX(Integer.parseInt(args[2]));
            			frame.setY(Integer.parseInt(args[3]));
            			frame.setExtended(Boolean.parseBoolean(args[4]));
            		}
            	}
            } else if (s.toLowerCase().startsWith("clickgui:")) {
                ClickGUI.INSTANCE.setOffset(Integer.parseInt(args[4]));
                ClickGUI.INSTANCE.setZOffset(Integer.parseInt(args[2]));
            } else if (s.toLowerCase().startsWith("settingpos:")) {
            	for (ModuleButton mb : ClickGUI.INSTANCE.buttons) {
                	if (mb.name.equalsIgnoreCase(args[1])) {

                	}
                }
            }
        }
    }
}
