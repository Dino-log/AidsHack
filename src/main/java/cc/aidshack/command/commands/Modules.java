package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.utils.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class Modules extends Command {
    public Modules() {
        super("modules", "Gives you a list of all of the modules");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            for (Module mod : ModuleManager.INSTANCE.modules) {
                ChatUtils.tellPlayerRaw("§c" + "Module" + "§8" + ": " + mod.getName());
                ChatUtils.tellPlayerRaw("§8" +  mod.getDescription());
            }
            return SINGLE_SUCCESS;
        });
    }
}
