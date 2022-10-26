package com.gilles_m.rpg_chest.command;

import com.gilles_m.rpg_chest.key.KeyManager;
import com.gilles_m.rpg_chest.util.ServerUtil;
import com.github.spigot_gillesm.command_lib.SimpleCommand;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Key related commands
 */
public class KeyCommand extends SimpleCommand {

	private static final String UNKNOWN_KEY_ERROR = "&cUnknown key id: %s";

	private static final String SPECIFY_KEY_ERROR = "&cYou must specify the key id";

	KeyCommand(final SimpleCommand parentCommand) {
		super(parentCommand, "key");

		setAliases(List.of("k"));
		setPermission("rpgchest.key");
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

		private KeyGetCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "get");

			setPermission("rpgchest.key.get");
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

		private KeyGiveCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "give");

			setPermission("rpgchest.key.give");
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
			final var player = ServerUtil.getOnlinePlayer(args[0]);
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

	}

}
