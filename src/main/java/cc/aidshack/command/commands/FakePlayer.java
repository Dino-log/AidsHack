package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class FakePlayer extends Command {
    public FakePlayer() {
        super("fakeplayer", "");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("fake player's new name", StringArgumentType.greedyString()).executes(context -> {
            cc.aidshack.module.impl.render.FakePlayer.setFakePlayerName(StringArgumentType.getString(context, "fake player's new name"));
            return SINGLE_SUCCESS;
        }));
    }
}
