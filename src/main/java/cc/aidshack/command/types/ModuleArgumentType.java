package cc.aidshack.command.types;

import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.text.LiteralText;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ModuleArgumentType implements ArgumentType<Module> {

    private static final Collection<String> EXAMPLES = ModuleManager.INSTANCE.modules
            .stream()
            .limit(3)
            .map(module -> module.getName())
            .collect(Collectors.toList());

    private static final DynamicCommandExceptionType NO_SUCH_MODULE = new DynamicCommandExceptionType(o ->
            new LiteralText("Module with name " + o + " doesn't exist."));

    public static ModuleArgumentType module() {
        return new ModuleArgumentType();
    }

    public static Module getModule(final CommandContext<?> context, final String name) {
        return context.getArgument(name, Module.class);
    }

    @Override
    public Module parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readString();
        Module module = ModuleManager.INSTANCE.getModuleByName(argument);

        if (module == null) throw NO_SUCH_MODULE.create(argument);

        return module;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestMatching(ModuleManager.INSTANCE.modules.stream().map(module -> module.getName()), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
