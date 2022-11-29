package com.gilles_m.rpg_chest.command;

import com.gilles_m.rpg_chest.item.ItemManager;
import com.github.spigot_gillesm.command_lib.SimpleCommand;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ItemCommand extends SimpleCommand {

	private static final String UNKNOWN_ITEM_ERROR = "&cUnknown item id: %s";

	private static final String SPECIFY_ITEM_ERROR = "&cYou must specify the item id";

	ItemCommand(final SimpleCommand parentCommand) {
		super(parentCommand, "item");

		setAliases(List.of("itm"));
		setPermission("rpgchest.item");
		setPlayerCommand(false);
		setDescription("Item related command");

		new ItemGetCommand(this);
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
	private static class ItemGetCommand extends SimpleCommand {

		private ItemGetCommand(final SimpleCommand parentCommand) {
			super(parentCommand, "get");

			setPermission("rpgchest.item.get");
			setPlayerCommand(true);
			setDescription("Get the specified item in your inventory");
			addMandatoryArgument("item id");
		}

		@Override
		protected void run(final CommandSender commandSender, final String[] args) {
			if(args.length == 0) {
				Formatter.tell(commandSender, SPECIFY_ITEM_ERROR);
				return;
			}
			final var itemId = args[0];
			final var player = (Player) commandSender;

			ItemManager.getInstance()
					.getItem(itemId)
					.ifPresentOrElse(item -> player.getInventory().addItem(item),
							() -> Formatter.tell(player, String.format(UNKNOWN_ITEM_ERROR, itemId)));
		}

	}

}
