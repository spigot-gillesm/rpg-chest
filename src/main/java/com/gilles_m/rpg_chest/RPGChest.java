package com.gilles_m.rpg_chest;

import com.gilles_m.rpg_chest.container.ContainerLoader;
import com.gilles_m.rpg_chest.container.instance.InstanceLoader;
import com.gilles_m.rpg_chest.item.ItemLoader;
import com.gilles_m.rpg_chest.item_table.TableLoader;
import com.gilles_m.rpg_chest.key.KeyLoader;
import com.gilles_m.rpg_chest.listener.ContainerListener;
import com.gilles_m.rpg_chest.listener.EntityListener;
import com.gilles_m.rpg_chest.listener.PlayerListener;
import com.gilles_m.rpg_chest.listener.WorldListener;
import com.gilles_m.rpg_chest.util.Dependency;
import com.gilles_m.rpg_chest.world.configuration.WorldChestConfigurationLoader;
import com.github.spigot_gillesm.command_lib.CommandLib;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class RPGChest extends JavaPlugin {

	@Getter
	private static RPGChest instance;

	private static void initialize(final RPGChest plugin) {
		instance = plugin;
		Formatter.PREFIX = "&f[&eRPG&aChest&f]";
		FileUtils.PLUGIN_DATA_FOLDER_PATH = plugin.getDataFolder().getPath();

		Formatter.info("&aLoading RPGChest...");
		CommandLib.initialize(plugin);

		Dependency.getInstance().load();
		ItemLoader.getInstance().load();
		KeyLoader.getInstance().load();
		TableLoader.getInstance().load();
		ContainerLoader.getInstance().load();
		WorldChestConfigurationLoader.getInstance().load();
		InstanceLoader.getInstance().load();
	}

	@Override
	public void onEnable() {
		initialize(this);

		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		getServer().getPluginManager().registerEvents(new ContainerListener(), this);
		getServer().getPluginManager().registerEvents(new EntityListener(), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);

		Formatter.info("&aDone!");
	}

	@Override
	public void onDisable() {
		try {
			InstanceLoader.getInstance().saveAll();
		} catch (final IOException e) {
			Formatter.error("Error saving container instances");
			e.printStackTrace();
		}
	}

}
