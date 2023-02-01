package cc.aidshack.command.types;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockArgumentParser;
import net.minecraft.command.argument.BlockStateArgument;
import net.minecraft.util.registry.Registry;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public class BlockStateArgumentType implements ArgumentType<BlockStateArgument> {
    private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

    public static BlockStateArgumentType blockState() {
        return new BlockStateArgumentType();
    }

    public BlockStateArgument parse(StringReader stringReader) throws CommandSyntaxException {
        BlockArgumentParser blockArgumentParser = (new BlockArgumentParser(stringReader, false)).parse(true);
        return new BlockStateArgument(blockArgumentParser.getBlockState(), blockArgumentParser.getBlockProperties().keySet(), blockArgumentParser.getNbtData());
    }

    public static BlockStateArgument getBlockState(CommandContext<CommandSource> context, String name) {
        return (BlockStateArgument)context.getArgument(name, BlockStateArgument.class);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        StringReader stringReader = new StringReader(builder.getInput());
        stringReader.setCursor(builder.getStart());
        BlockArgumentParser blockArgumentParser = new BlockArgumentParser(stringReader, false);

        try {
            blockArgumentParser.parse(true);
        } catch (CommandSyntaxException var6) {
        }

        return blockArgumentParser.getSuggestions(builder, Registry.BLOCK);
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}