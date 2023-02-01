package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class ChangeWindowNameCommand extends Command {
    public ChangeWindowNameCommand() {
        super("changewindowname", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("new window name", StringArgumentType.greedyString()).executes(context -> {
            cc.aidshack.module.impl.other.ChangeWindowName.replaceWindowName(StringArgumentType.getString(context, "new window name"));
            return SINGLE_SUCCESS;
        }));
    }
}
