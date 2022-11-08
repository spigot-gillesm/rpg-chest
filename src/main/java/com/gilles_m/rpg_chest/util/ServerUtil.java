package com.gilles_m.rpg_chest.util;

import com.gilles_m.rpg_chest.RPGChest;
import com.github.spigot_gillesm.format_lib.Formatter;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.Optional;
import java.util.Random;

@UtilityClass
public class ServerUtil {

	private static final Random RANDOM = new SecureRandom();

	/**
	 * Fill the given container with the specified items.
	 *
	 * @param inventory the inventory
	 * @param items the items
	 * @param clearFirst whether to clear the inventory before adding the items
	 * @return true if the inventory was successfully filled
	 */
	public boolean fillInventory(@NotNull final Inventory inventory, @NotNull final Collection<ItemStack> items,
								 final boolean clearFirst) {
		var currentAmount = 0;

		if(clearFirst) {
			inventory.clear();

			//Count the already existing items
			for(final var item : inventory.getContents()) {
				if(item != null && item.getType() != Material.AIR) {
					currentAmount++;
				}
			}
		}
		final var inventorySize = inventory.getSize();

		//Check there's enough room in the inventory
		if(inventorySize - currentAmount < items.size()) {
			Formatter.error("RPGChest tried to put more items than the inventory can contain. Verify the item table configurations");
			return false;
		}
		//Pick random positions within the container to place items
		for(final var item : items) {
			var slot = RANDOM.nextInt(inventorySize);

			while(inventory.getItem(slot) != null && (new ItemStack(Material.AIR)).isSimilar(inventory.getItem(slot))) {
				slot = RANDOM.nextInt(inventorySize);
			}
			inventory.setItem(slot, item);
		}
		inventory.getViewers().forEach(humanEntity -> ((Player) humanEntity).updateInventory());

		return true;
	}

	public Optional<Player> getOnlinePlayer(@NotNull final String playerId) {
		final var player = RPGChest.getInstance().getServer().getPlayer(playerId);

		return player != null ? Optional.of(player) : Optional.empty();
	}

	public Optional<Location> getLocation(@NotNull final String world, @NotNull final String x, @NotNull final String y,
										  @NotNull final String z) {
		final var bukkitWorld = RPGChest.getInstance().getServer().getWorld(world);

		if(bukkitWorld == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(new Location(bukkitWorld, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)));
		} catch (final NumberFormatException exception) {
			return Optional.empty();
		}
	}

	public boolean isBukkitWorld(@NotNull final String world) {
		return RPGChest.getInstance().getServer().getWorld(world) != null;
	}

	public Biome getBiome(@NotNull final String biome) {
		try {
			return Biome.valueOf(biome);
		} catch(final IllegalArgumentException exception){
			return null;
		}
	}

	public boolean isBiome(@NotNull final String biome) {
		try {
			Biome.valueOf(biome);
			return true;
		} catch (final IllegalArgumentException exception) {
			return false;
		}
	}

}
