package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import cc.aidshack.module.impl.other.AutoEZ;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class AutoEZMessage extends Command {
    public AutoEZMessage() {
        super("autoez", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("new message", StringArgumentType.greedyString()).executes(context -> {
            AutoEZ.setKillMessage(StringArgumentType.getString(context, "new message"));
            return SINGLE_SUCCESS;
        }));
    }
}
