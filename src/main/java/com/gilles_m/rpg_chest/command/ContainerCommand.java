package com.gilles_m.rpg_chest.command;

import com.gilles_m.rpg_chest.RPGChest;
import com.gilles_m.rpg_chest.container.ContainerLoader;
import com.gilles_m.rpg_chest.container.ContainerManager;
import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import com.gilles_m.rpg_chest.container.instance.InstanceManager;
import com.gilles_m.rpg_chest.item_table.TableLoader;
import com.gilles_m.rpg_chest.key.KeyLoader;
import com.gilles_m.rpg_chest.key.KeyManager;
import com.github.spigot_gillesm.command_lib.MainCommand;
import com.github.spigot_gillesm.command_lib.SimpleCommand;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@MainCommand
public class ContainerCommand extends SimpleCommand {

	private static final String UNKNOWN_CONTAINER_ERROR = "&cUnknown container id: %s";

	private static final String SPECIFY_CONTAINER_ERROR = "&cYou must specify the container id";

	private static final String UNKNOWN_KEY_ERROR = "&cUnknown key id: %s";

	private static final String SPECIFY_KEY_ERROR = "&cYou must specify the key id";

	public ContainerCommand() {
		super("container");

		setAliases(List.of("cont", "ctnr", "c"));
		setPlayerCommand(false);
		setDescription("RPGChest container related command");
		setPermission("rpgchest.container");

		new ReloadCommand(this);
		new SpawnContainerCommand(this);
		new DeleteContainerCommand(this);
		new FindInstanceCommand(this);
		new ClearCommand(this);
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

	/**
	 * Spawn a new container's instance
	 */
	private static class SpawnContainerCommand extends SimpleCommand {

		private SpawnContainerCommand(final SimpleCommand parentCommand) {
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
			setPermission("rpgchest.find");
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

	/**
	 * Key related commands
	 */
	private static class KeyCommand extends SimpleCommand {

		private KeyCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "key");

			setAliases(List.of("k"));
			setPermission("container.key");
			setPlayerCommand(false);
			setDescription("Key related command");

			new KeyGetCommand(this);
			new KeyGiveCommand(this);
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				displayHelp(commandSender);
			}
		}

		/**
		 * Get a single copy of the specified key
		 */
		private static class KeyGetCommand extends SimpleCommand {

			public KeyGetCommand(final SimpleCommand parentCommand) {
				super(parentCommand, "get");

				setPermission("container.key.get");
				setPlayerCommand(true);
				setDescription("Get the specified key in your inventory");
				addMandatoryArgument("key id");
			}

			@Override
			protected void run(final CommandSender commandSender, final String[] args) {
				if(args.length == 0) {
					Formatter.tell(commandSender, SPECIFY_KEY_ERROR);
					return;
				}
				final var keyId = args[0];
				final var player = (Player) commandSender;

				KeyManager.getInstance()
						.getKey(keyId)
						.ifPresentOrElse(key -> player.getInventory().addItem(key.getItemStack()),
								() -> Formatter.tell(player, UNKNOWN_KEY_ERROR, keyId));
			}

		}

		/**
		 * Give the specified key to the specified player
		 */
		private static class KeyGiveCommand extends SimpleCommand {

			public KeyGiveCommand(final SimpleCommand parentCommand) {
				super(parentCommand, "give");

				setPermission("container.key.get");
				setPlayerCommand(false);
				setDescription("Give the specified key in a player's inventory");
				addMandatoryArgument("player");
				addMandatoryArgument("key id");
				addOptionalArgument("amount");
			}

			@Override
			protected void run(final CommandSender commandSender, final String[] args) {
				if(args.length == 0) {
					Formatter.tell(commandSender, "&cYou must specify the player");
					return;
				}
				if(args.length == 1) {
					Formatter.tell(commandSender, SPECIFY_KEY_ERROR);
					return;
				}
				var amount = 1;

				if(args.length == 3) {
					try {
						amount = Integer.parseInt(args[2]);
					} catch (final NumberFormatException exception) {
						Formatter.tell(commandSender, "&cInvalid amount");
					}
				}
				final var player = getOnlinePlayer(args[0]);
				final var key = KeyManager.getInstance().getKey(args[1]);

				if(player.isEmpty()) {
					Formatter.tell(commandSender, String.format("&cUnknown player: %s", args[0]));
					return;
				}
				if(key.isEmpty()) {
					Formatter.tell(commandSender,String.format(UNKNOWN_KEY_ERROR, args[1]));
					return;
				}
				final var item = key.get().getItemStack().clone();
				item.setAmount(amount);
				player.get().getInventory().addItem(item);

				Formatter.tell(commandSender, String.format("&aSuccessfully added %s &7x&9 %d &ain &9%s&a's inventory",
						args[1], amount, args[0]));
				Formatter.tell(player.get(), String.format("&aYou received %s &7x &9%d &ain your inventory",
						item.getItemMeta().getDisplayName(), amount));
			}

			private Optional<Player> getOnlinePlayer(final String playerId) {
				final var player = RPGChest.getInstance().getServer().getPlayer(playerId);

				return player != null ? Optional.of(player) : Optional.empty();
			}

		}

	}

}
