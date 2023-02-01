package cc.aidshack.command;

import cc.aidshack.command.commands.*;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.command.CommandSource;

import java.util.*;

public class CommandManager {

	public static final CommandManager INSTANCE = new CommandManager();
	private static MinecraftClient mc = MinecraftClient.getInstance();
	private final CommandDispatcher<CommandSource> DISPATCHER = new CommandDispatcher<>();
    private final CommandSource COMMAND_SOURCE = new ChatCommandSource(mc);
    private final List<Command> commands = new ArrayList<>();
    private final Map<Class<? extends Command>, Command> commandInstances = new HashMap<>();

    private CommandManager() {
        add(new AutoEZMessage());
        add(new AutoGGMessage());
        add(new ChangeWindowNameCommand());
        add(new FakePlayer());
        add(new NameProtect());
        add(new Modules());
        add(new Bind());
        add(new Description());
        add(new VClip());
        add(new SearchCmd());
        add(new Toggle());
        add(new SaveConfigCMD());
        commands.sort(Comparator.comparing(Command::getName));
    }


    public void dispatch(String message) throws CommandSyntaxException {
        dispatch(message, new ChatCommandSource(mc));
    }

    public void dispatch(String message, CommandSource source) throws CommandSyntaxException {
        ParseResults<CommandSource> results = DISPATCHER.parse(message, source);
        DISPATCHER.execute(results);
    }

    public CommandDispatcher<CommandSource> getDispatcher() {
        return DISPATCHER;
    }

    public CommandSource getCommandSource() {
        return COMMAND_SOURCE;
    }

    private final static class ChatCommandSource extends ClientCommandSource {
        public ChatCommandSource(MinecraftClient client) {
            super(null, client);
        }
    }

    public void add(Command command) {
        commands.removeIf(command1 -> command1.getName().equals(command.getName()));
        commandInstances.values().removeIf(command1 -> command1.getName().equals(command.getName()));

        command.registerTo(DISPATCHER);
        commands.add(command);
        commandInstances.put(command.getClass(), command);
    }

    public int getCount() {
        return commands.size();
    }

    public List<Command> getCommands() {
        return commands;
    }

    @SuppressWarnings("unchecked")
    public <T extends Command> T getCommand(Class<T> klass) {
        return (T) commandInstances.get(klass);
    }

    public static CommandManager get() {
        return new CommandManager();
    }
	public String getPrefix() {
		return "+";
	}
	
	public Command getCommandByName(String commandName) {
		for(Command command : commands) {
			if ((command.getName().trim().equalsIgnoreCase(commandName)) || (command.toString().trim().equalsIgnoreCase(commandName.trim()))) {
				return command;
			}
		}
		return null;
	}
}
