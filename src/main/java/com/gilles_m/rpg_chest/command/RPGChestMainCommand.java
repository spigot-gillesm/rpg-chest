package com.gilles_m.rpg_chest.command;

import com.gilles_m.rpg_chest.container.ContainerLoader;
import com.gilles_m.rpg_chest.item_table.TableLoader;
import com.gilles_m.rpg_chest.key.KeyLoader;
import com.gilles_m.rpg_chest.util.Dependency;
import com.gilles_m.rpg_chest.world.configuration.WorldChestConfigurationLoader;
import com.github.spigot_gillesm.command_lib.MainCommand;
import com.github.spigot_gillesm.command_lib.SimpleCommand;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.command.CommandSender;

import java.util.List;

@MainCommand
public class RPGChestMainCommand extends SimpleCommand {

	public RPGChestMainCommand() {
		super("rpgchest");

		setAliases(List.of("rpgc", "chest", "c"));
		setPlayerCommand(false);
		setDescription("RPGChest container related command");
		setPermission("rpgchest.container");

		new ReloadCommand(this);
		new ContainerCommand(this);
		new KeyCommand(this);
		new InstanceCommand(this);
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
			Dependency.getInstance().load();
			KeyLoader.getInstance().load();
			TableLoader.getInstance().load();
			WorldChestConfigurationLoader.getInstance().load();
			ContainerLoader.getInstance().load();
			Formatter.tell(commandSender, "&aDone!");
		}

	}

}
