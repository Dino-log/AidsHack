package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import cc.aidshack.command.types.BlockStateArgumentType;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.module.impl.render.Search;
import cc.aidshack.utils.ColorUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;

public class SearchCmd extends Command {

	Search search = ModuleManager.INSTANCE.getModule(Search.class);
	public SearchCmd() {
		super("search", "Add blocks to search", "search");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(
                literal("add").then(argument("block", BlockStateArgumentType.blockState()).executes(context -> {
                    Block block = BlockStateArgumentType.getBlockState(context, "block").getBlockState().getBlock();

                    if(block == null) error("Block not Found!");
                    else {
                        if(!search.blocks.contains(block)) {
                        	search.blocks.add(block);
                            info("Added " + ColorUtils.purple + block.getName().getString() + ColorUtils.white + " to search list!");
                        }
                            else error("Block is already in search list!");
                    }

                    return 1;
                }))).then(
                literal("del").then(argument("block", BlockStateArgumentType.blockState()).executes(c -> {
                    Block block = BlockStateArgumentType.getBlockState(c, "block").getBlockState().getBlock();

                    if(block == null) error("Block not Found!");
                    else {
                        if(search.blocks.contains(block)) {
                        	search.blocks.remove(block);
                            info("Deleted " + ColorUtils.purple + block.getName().getString() + ColorUtils.white + " from search list!");
                        } else info("Block isn't in search list!");
                    }

                    return 1;
                }))).then(
                literal("list").executes(c -> {
                    search.blocks.forEach(block -> {
                    	info(block.asItem().getName());
                    });
                    return 1;
                })
        );
		
	}

}
