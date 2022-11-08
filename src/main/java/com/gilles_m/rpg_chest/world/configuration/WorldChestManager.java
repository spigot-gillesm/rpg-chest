package com.gilles_m.rpg_chest.world.configuration;

import com.gilles_m.rpg_chest.item_table.TableManager;
import com.gilles_m.rpg_chest.util.ServerUtil;
import com.gilles_m.rpg_chest.world.WorldLocation;
import lombok.AccessLevel;
import lombok.Setter;
import org.bukkit.World;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class WorldChestManager {

	private static final WorldChestManager INSTANCE = new WorldChestManager();

	@Setter(AccessLevel.PACKAGE)
	private WorldChestConfiguration configuration = new WorldChestConfiguration();

	private WorldChestManager() { }

	/**
	 * Try to replace the content of the given inventory should a world chest configuration match the inventory's environment.
	 *
	 * @param inventoryHolder the inventory holder
	 * @return true if the inventory should not contain its original content when getting filled
	 */
	public boolean fillContainer(@NotNull final InventoryHolder inventoryHolder) {
		final var inventory = inventoryHolder.getInventory();
		final var containerLocation = inventory.getLocation();

		if(containerLocation == null) {
			return false;
		}
		final var worldConfiguration = getWorldConfiguration(containerLocation.getWorld());

		//Check if the world is listed in the configurations
		if(worldConfiguration.isEmpty()) {
			return false;
		}
		for(final var locationConfiguration : worldConfiguration.get().getLocationConfigurations()) {
			final var configuredLocation = locationConfiguration.getLocation();
			final var shouldReplace = locationConfiguration.getAction() == WorldChestConfiguration.Action.REPLACE;

			if(shouldFillContainer(inventoryHolder, getWorldLocation(configuredLocation), configuredLocation)) {
				fillContainer(inventory, locationConfiguration.getItemTable(), shouldReplace);

				return shouldReplace;
			}
		}
		//If no location configuration were found, check for a default configuration
		final var shouldReplace = worldConfiguration.get().getAction() == WorldChestConfiguration.Action.REPLACE;
		fillContainer(inventory, worldConfiguration.get().getItemTable(), shouldReplace);

		//If there's no specific nor default conf, the default value of action is ADD -> return false
		return shouldReplace;
	}

	private void fillContainer(final Inventory inventory, final String itemTable, final boolean replace) {
		TableManager.getInstance()
				.getItemTable(itemTable)
				.ifPresent(table -> ServerUtil.fillInventory(inventory, table.generateItems(), replace));
	}

	private boolean shouldFillContainer(final InventoryHolder inventoryHolder, final WorldLocation worldLocation,
										final String configuredLocation) {
		final var inventoryLocation = inventoryHolder.getInventory().getLocation();

		if(inventoryLocation == null) {
			return false;
		}
		if(worldLocation != null && worldLocation.getLocationAnalyzer().isValid(inventoryHolder)) {
			return true;
		}
		else return ServerUtil.isBiome(configuredLocation.toUpperCase())
				&& ServerUtil.getBiome(configuredLocation.toUpperCase()) == inventoryLocation.getBlock().getBiome();
	}

	private Optional<WorldChestConfiguration.WorldConfiguration> getWorldConfiguration(final World world) {
		return configuration.getWorldConfigurations()
				.stream()
				.filter(conf -> conf.getWorld().equals(world.getName()))
				.findFirst();
	}

	private WorldLocation getWorldLocation(final String location) {
		try {
			return WorldLocation.valueOf(location.toUpperCase());
		} catch (final IllegalArgumentException exception) {
			return null;
		}
	}

	public static WorldChestManager getInstance() {
		return INSTANCE;
	}

}
