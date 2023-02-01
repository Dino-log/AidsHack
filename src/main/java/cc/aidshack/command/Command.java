package cc.aidshack.command;

import cc.aidshack.utils.ChatUtils;
import cc.aidshack.utils.ColorUtils;
import cc.aidshack.utils.math.Wrapper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Command {
    protected static MinecraftClient mc;

    private final String name;
    private final String description;
    private final List<String> aliases = new ArrayList<>();
    public int SINGLE_SUCCESS = com.mojang.brigadier.Command.SINGLE_SUCCESS;

    public Command(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        Collections.addAll(this.aliases, aliases);
        mc = MinecraftClient.getInstance();
    }

    // Helper methods to painlessly infer the CommandSource generic type argument
    protected static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

    protected static LiteralArgumentBuilder<CommandSource> literal(final String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public final void registerTo(CommandDispatcher<CommandSource> dispatcher) {
        register(dispatcher, name);
        for (String alias : aliases) register(dispatcher, alias);
    }

    public void register(CommandDispatcher<CommandSource> dispatcher, String name) {
        LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(name);
        build(builder);
        dispatcher.register(builder);
    }

    public abstract void build(LiteralArgumentBuilder<CommandSource> builder);

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String toString() {
        return CommandManager.get().getPrefix() + name;
    }

    public String toString(String... args) {
        StringBuilder base = new StringBuilder(toString());
        for (String arg : args)
            base.append(' ').append(arg);

        return base.toString();
    }

    public void info(Text message) {
        ChatUtils.sendMsg(ChatUtils.AIDSPREFIX, message);
    }

    public void info(String message) {
        Wrapper.tellPlayer(message.replace("(highlight)", ColorUtils.white).replace("(default)", ColorUtils.gray));
    }

    public void warning(String message, Object... args) {
        ChatUtils.warning(ChatUtils.AIDSPREFIX, message, args);
    }

    public void error(String message, Object... args) {
        ChatUtils.error(ChatUtils.AIDSPREFIX, message, args);
    }
}
