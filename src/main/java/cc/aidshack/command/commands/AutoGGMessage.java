package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import cc.aidshack.module.impl.other.AutoGG;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class AutoGGMessage extends Command {
    public AutoGGMessage() {
        super("autogg", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("new message", StringArgumentType.greedyString()).executes(context -> {
            AutoGG.setReplacementMessage(StringArgumentType.getString(context, "new message"));
            return SINGLE_SUCCESS;
        }));
    }
}
