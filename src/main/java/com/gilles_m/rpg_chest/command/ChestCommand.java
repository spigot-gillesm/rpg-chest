package com.gilles_m.rpg_chest.command;

import com.gilles_m.rpg_chest.container.ContainerLoader;
import com.gilles_m.rpg_chest.container.ContainerManager;
import com.gilles_m.rpg_chest.item_table.TableLoader;
import com.gilles_m.rpg_chest.key.KeyLoader;
import com.gilles_m.rpg_chest.key.KeyManager;
import com.github.spigot_gillesm.command_lib.MainCommand;
import com.github.spigot_gillesm.command_lib.SimpleCommand;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@MainCommand
public class ChestCommand extends SimpleCommand {

	public ChestCommand() {
		super("container");

		setAliases(List.of("cont", "ctnr", "c"));
		setPlayerCommand(false);
		setDescription("RPGChest main command");
		setPermission("rpgchest.container");

		new ReloadCommand(this);
		new SpawnChestCommand(this);
		new KeyCommand(this);
	}

	@Override
	protected void run(final CommandSender commandSender, final String[] args) {
		if(args.length == 0) {
			displayHelp(commandSender);
		}
	}

	private static class ReloadCommand extends SimpleCommand {

		private ReloadCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "reload");

			setAliases(List.of("r"));
			setPermission("rpgchest.reload");
			setPlayerCommand(false);
			setDescription("Reload the plugin (but does not reload the container instances)");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length > 0) {
				Formatter.tell(commandSender, "&cThis command takes no arguments");
				return;
			}
			Formatter.tell(commandSender, "Reloading plugin...");
			KeyLoader.getInstance().load();
			TableLoader.getInstance().load();
			ContainerLoader.getInstance().load();
			Formatter.tell(commandSender, "&aDone!");
		}

	}

	private static class SpawnChestCommand extends SimpleCommand {

		private SpawnChestCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "spawn");

			setAliases(List.of("s"));
			setPermission("rpgchest.container.spawn");
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

	private static class KeyCommand extends SimpleCommand {

		private KeyCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "key");

			setAliases(List.of("k"));
			setPermission("container.key");
			setPlayerCommand(false);
			setDescription("Key related command");

			new KeyGetCommand(this);
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				displayHelp(commandSender);
			}
		}

	}

	private static class KeyGetCommand extends SimpleCommand {

		public KeyGetCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "get");

			setAliases(List.of("g"));
			setPermission("container.key.get");
			setPlayerCommand(true);
			setDescription("Get the specified key in your inventory");
			addMandatoryArgument("key id");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				Formatter.tell(commandSender, "&cYou must specify the key id");
				return;
			}
			final var keyId = args[0];
			final var player = (Player) commandSender;

			KeyManager.getInstance()
					.getKey(keyId)
					.ifPresentOrElse(key -> player.getInventory().addItem(key.getItemStack()),
							() -> Formatter.tell(player, "&cUnknown key id: %s", keyId));
		}

	}

}
