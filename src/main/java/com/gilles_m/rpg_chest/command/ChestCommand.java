package com.gilles_m.rpg_chest.command;

import com.gilles_m.rpg_chest.container.ContainerManager;
import com.github.spigot_gillesm.command_lib.MainCommand;
import com.github.spigot_gillesm.command_lib.SimpleCommand;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@MainCommand
public class ChestCommand extends SimpleCommand {

	public ChestCommand() {
		super("chest");
		setAliases(List.of("ch", "c"));
		setPlayerCommand(false);
		setDescription("RPGChest main command");
		setPermission("rpgchest.chest");

		new SpawnChestCommand(this);
	}

	@Override
	protected void run(final CommandSender commandSender, final String[] args) {
		if(args.length == 0) {
			displayHelp(commandSender);
		}
	}

	private static class SpawnChestCommand extends SimpleCommand {

		private SpawnChestCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "spawn");
			setAliases(List.of("s"));
			setPlayerCommand(true);
			setDescription("Spawn a new instance of the specified container at your location");
			addMandatoryArgument("container id");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				Formatter.tell(commandSender, "&cYou must specify the container id");
				return;
			}
			final var containerId = args[0];
			final var player = (Player) commandSender;

			ContainerManager.getInstance()
					.getContainer(containerId)
					.ifPresentOrElse(container -> container.newInstance(player.getLocation(), player.getFacing())
									.spawn(),
							() -> Formatter.tell(commandSender, "&cUnknown container id: %s", containerId));
		}

	}

}
