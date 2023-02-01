package cc.aidshack.command.commands;

import cc.aidshack.command.Command;
import cc.aidshack.command.types.ModuleArgumentType;
import cc.aidshack.module.Module;
import cc.aidshack.utils.ChatUtils;
import cc.aidshack.utils.KeyUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

public class Bind extends Command {

    public Bind() {
        super("bind", "Binds a specified module");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("module", ModuleArgumentType.module()).then(argument("key", StringArgumentType.string()).executes(context -> {
            Module module = context.getArgument("module", Module.class);
            String key = context.getArgument("key", String.class);
            module.setKey(KeyUtils.getKey(key));
            ChatUtils.tellPlayer("Bound " + "§f" + module.getName() + "§8" + " to " + "§f" + (KeyUtils.getKey(key) != -1 ? GLFW.glfwGetKeyScancode(KeyUtils.getKeyScanCode(key)) : "NONE"));
            return SINGLE_SUCCESS;
        })));
    }

}