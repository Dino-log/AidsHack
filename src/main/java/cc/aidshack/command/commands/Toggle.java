package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import cc.aidshack.command.types.ModuleArgumentType;
import cc.aidshack.module.Module;
import cc.aidshack.module.ModuleManager;
import cc.aidshack.utils.ColorUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;

public class Toggle extends Command {

	public Toggle() {
		super("toggle", "Toggles a specified module", "t", "toggle");
	}

	@Override
	public void build(LiteralArgumentBuilder<CommandSource> builder) {
		builder.then(argument("message", ModuleArgumentType.module()).executes(context -> {
			Module mod = context.getArgument("message", Module.class);
			for (Module m : ModuleManager.INSTANCE.modules) {
				if (m.equals(mod)) {
					m.toggle();
					info("Toggled " + ColorUtils.gray + m.getName() + (m.isEnabled() ? ColorUtils.green + " on" : ColorUtils.red + " off"));
				}
			}
			return SINGLE_SUCCESS;
		})); 
	}
	
}
