package com.gilles_m.rpg_chest;

import com.gilles_m.rpg_chest.container.ContainerLoader;
import com.gilles_m.rpg_chest.item_table.TableLoader;
import com.github.spigot_gillesm.command_lib.CommandLib;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGChest extends JavaPlugin {

	@Getter
	private static RPGChest instance;

	private static void initialize(final RPGChest plugin) {
		Formatter.PREFIX = "&f[&eRPG&aChest&f]";
		FileUtils.PLUGIN_DATA_FOLDER_PATH = plugin.getDataFolder().getPath();

		Formatter.info("&aLoading RPGChest...");
		CommandLib.initialize(plugin);

		TableLoader.getInstance().load();
		ContainerLoader.getInstance().load();
	}

	@Override
	public void onEnable() {
		initialize(this);
		Formatter.info("&aDone!");
	}

}
