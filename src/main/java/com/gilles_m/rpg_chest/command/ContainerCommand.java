package com.gilles_m.rpg_chest.command;

import com.gilles_m.rpg_chest.container.ContainerManager;
import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import com.gilles_m.rpg_chest.container.instance.InstanceManager;
import com.github.spigot_gillesm.command_lib.SimpleCommand;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Container related commands
 */
public class ContainerCommand extends SimpleCommand {

	private static final String UNKNOWN_CONTAINER_ERROR = "&cUnknown container id: %s";

	private static final String SPECIFY_CONTAINER_ERROR = "&cYou must specify the container id";

	ContainerCommand(final SimpleCommand parentCommand) {
		super(parentCommand, "container");

		setAliases(List.of("cont", "ctnr", "c"));
		setPlayerCommand(false);
		setDescription("Container related command");
		setPermission("rpgchest.container");

		new SpawnContainerCommand(this);
		new DeleteContainerCommand(this);
		new FindInstanceCommand(this);
		new ClearCommand(this);
	}

	@Override
	protected void run(final CommandSender commandSender, final String[] args) {
		if(args.length == 0) {
			displayHelp(commandSender);
		}
	}

	/**
	 * Spawn a new container's instance
	 */
	private static class SpawnContainerCommand extends SimpleCommand {

		private SpawnContainerCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "spawn");

			setAliases(List.of("s"));
			setPermission("rpgchest.container.spawn");
			setPlayerCommand(true);
			setDescription("Spawn a new instance of the specified container on the targeted block");
			addMandatoryArgument("container id");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				Formatter.tell(commandSender, SPECIFY_CONTAINER_ERROR);
				return;
			}
			final var containerId = args[0];
			final var player = (Player) commandSender;

			ContainerManager.getInstance()
					.getContainer(containerId)
					.ifPresentOrElse(container -> {
								container.newInstance(player.getTargetBlockExact(10).getLocation().add(0, 1, 0),
										player.getFacing().getOppositeFace()).spawn();
								Formatter.tell(player, String.format("&aSuccessfully spawned a new instance of %s", containerId));
							},
							() -> Formatter.tell(commandSender, String.format(UNKNOWN_CONTAINER_ERROR, containerId)));
		}

	}

	private static class DeleteContainerCommand extends SimpleCommand {

		private DeleteContainerCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "delete");

			setAliases(List.of("rmv", "r"));
			setPermission("rpgchest.container.delete");
			setPlayerCommand(false);
			setDescription("Delete the specified container and clear all its instances");
			addMandatoryArgument("container id");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				Formatter.tell(commandSender, SPECIFY_CONTAINER_ERROR);
				return;
			}
			final var containerId = args[0];

			if(ContainerManager.getInstance().deleteContainer(containerId)) {
				Formatter.tell(commandSender, String.format("&aSuccessfully deleted %s", containerId));
			} else {
				Formatter.tell(commandSender, String.format(UNKNOWN_CONTAINER_ERROR, containerId));
			}
		}

	}

	/**
	 * Display all the coordinates of the specified container
	 */
	private static class FindInstanceCommand extends SimpleCommand {

		private FindInstanceCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "find");

			setAliases(List.of("f"));
			setPermission("rpgchest.container.find");
			setPlayerCommand(false);
			setDescription("Find the coordinates of all the container instances of the specified container");
			addMandatoryArgument("container id");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				Formatter.tell(commandSender, SPECIFY_CONTAINER_ERROR);
				return;
			}
			final var containerId = args[0];

			if(!ContainerManager.getInstance().isContainer(containerId)) {
				Formatter.tell(commandSender, String.format(UNKNOWN_CONTAINER_ERROR, containerId));
				return;
			}
			Formatter.tell(commandSender, "&6Found:");
			InstanceManager.getInstance()
					.getContainerInstances(containerId)
					.forEach(instance -> Formatter.tell(commandSender,
							String.format("&7* %s", formatLocation(instance.getLocation()))
					));
			Formatter.tell(commandSender, "&7------------");
		}

		private String formatLocation(final Location location) {
			return location.getWorld().getName() + " " + location.getBlockX() + " " + location.getBlockY() +
					" " + location.getBlockZ();
		}

	}

	/**
	 * Remove all the given container's instances
	 */
	private static class ClearCommand extends SimpleCommand {

		private ClearCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "clear");

			setAliases(List.of("cl", "c"));
			setPermission("rpgchest.container.clear");
			setPlayerCommand(false);
			setDescription("Remove all the container's instances from the server");
			addMandatoryArgument("container id");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				Formatter.tell(commandSender, SPECIFY_CONTAINER_ERROR);
				return;
			}
			final var containerId = args[0];

			if(!ContainerManager.getInstance().isContainer(containerId)) {
				Formatter.tell(commandSender, String.format(UNKNOWN_CONTAINER_ERROR, containerId));
				return;
			}
			InstanceManager.getInstance()
					.getContainerInstances(containerId)
					.forEach(ContainerInstance::destroy);

			Formatter.tell(commandSender, String.format("&aSuccessfully cleared all instances of %s", containerId));
		}

	}

}
