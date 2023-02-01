package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import cc.aidshack.command.types.ModuleArgumentType;
import cc.aidshack.module.Module;
import cc.aidshack.utils.ChatUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class Description extends Command {
    public Description() {
        super("description", "", "desc");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.module()).executes(context -> {
            Module module = context.getArgument("module", Module.class);
            ChatUtils.tellPlayer(module.getDescription());
            return SINGLE_SUCCESS;
        }));
    }
}
