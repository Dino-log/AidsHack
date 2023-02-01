package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import cc.aidshack.config.ConfigManager;
import cc.aidshack.config.SaveLoad;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class SaveConfigCMD extends Command {

    public SaveConfigCMD() {
        super("saveconfig", "", "saveconfig");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            SaveLoad.INSTANCE.save();
            ConfigManager.INSTANCE.saveConfig();
            ConfigManager.INSTANCE.saveAll();
            return SINGLE_SUCCESS;
        });
    }

}

