package com.gilles_m.rpg_chest.command;

import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import com.gilles_m.rpg_chest.container.instance.InstanceManager;
import com.gilles_m.rpg_chest.util.ServerUtil;
import com.github.spigot_gillesm.command_lib.SimpleCommand;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Container instance related commands
 */
public class InstanceCommand extends SimpleCommand {

	private static final String UNKNOWN_INSTANCE_ERROR = "&cThere's no container instance at %s %s %s %s";

	private static final String INVALID_INSTANCE_ERROR = "&cThe target is not a container instance";

	InstanceCommand(final SimpleCommand parentCommand) {
		super(parentCommand, "instance");

		setAliases(List.of("inst", "i"));
		setPermission("rpgchest.instance");
		setPlayerCommand(false);
		setDescription("Container instance related command");

		new DeleteInstanceCommand(this);
	}

	@Override
	protected void run(final CommandSender commandSender, final String[] args) {
		if(args.length == 0) {
			displayHelp(commandSender);
		}
	}

	private static class DeleteInstanceCommand extends SimpleCommand {

		private DeleteInstanceCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "delete");

			setAliases(List.of("d"));
			setPermission("rpgchest.instance.delete");
			setPlayerCommand(false);
			setDescription("Remove the targeted or specified container instance from the server");
			addOptionalArgument("world");
			addOptionalArgument("x");
			addOptionalArgument("y");
			addOptionalArgument("z");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				if(!(commandSender instanceof Player)) {
					Formatter.tell(commandSender, "&cYou must be a player to run this command");
					return;
				}
				final var target = ((Player) commandSender).getTargetBlockExact(8);

				if(target == null || target.getType() == Material.AIR) {
					Formatter.tell(commandSender, "&cYou must target a valid block");
					return;
				}
				InstanceManager.getInstance()
						.getContainerInstance(target.getLocation())
						.ifPresentOrElse(ContainerInstance::destroy,
								() -> Formatter.tell(commandSender, INVALID_INSTANCE_ERROR));
			} else {
				if(args.length < 4) {
					Formatter.tell(commandSender, "&cYou must specify the world, x, y and z coordinates");
					return;
				}
				final var location = ServerUtil.getLocation(args[0], args[1], args[2], args[3]);

				if(location.isEmpty()) {
					Formatter.tell(commandSender, "&cInvalid location");
					return;
				}
				InstanceManager.getInstance()
						.getContainerInstance(location.get())
						.ifPresentOrElse(ContainerInstance::destroy,
								() -> Formatter.tell(commandSender, String.format(UNKNOWN_INSTANCE_ERROR,
										args[0], args[1], args[2], args[3])));
 			}
		}

	}

}
