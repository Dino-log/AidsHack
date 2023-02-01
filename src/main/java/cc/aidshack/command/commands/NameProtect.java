package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class NameProtect extends Command {
    public NameProtect() {
        super("nameprotect", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("new name", StringArgumentType.greedyString()).executes(context -> {
            cc.aidshack.module.impl.render.NameProtect.setReplacementUsername(StringArgumentType.getString(context, "new name"));
            return SINGLE_SUCCESS;
        }));
    }
}
