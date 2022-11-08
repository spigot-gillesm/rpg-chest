package com.gilles_m.rpg_chest.world;

import org.bukkit.Material;
import org.bukkit.entity.minecart.StorageMinecart;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public interface LocationAnalyzer {

	DungeonAnalyzer DUNGEON_ANALYZER = new DungeonAnalyzer();

	MineshaftAnalyzer MINESHAFT_ANALYZER = new MineshaftAnalyzer();

	StrongholdAnalyzer STRONGHOLD_ANALYZER = new StrongholdAnalyzer();

	boolean isValid(@NotNull InventoryHolder inventoryHolder);

	class DungeonAnalyzer implements LocationAnalyzer {

		@Override
		public boolean isValid(@NotNull final InventoryHolder inventoryHolder) {
			final var location = inventoryHolder.getInventory().getLocation();

			if(location == null) {
				return false;
			}
			final var height = location.getBlockY();
			final var belowMaterial = location.subtract(0, 1, 0).getBlock().getType();

			return height <= 60 && (belowMaterial == Material.COBBLESTONE || belowMaterial == Material.MOSSY_COBBLESTONE);
		}

	}

	class MineshaftAnalyzer implements LocationAnalyzer {

		@Override
		public boolean isValid(@NotNull final InventoryHolder inventoryHolder) {
			return inventoryHolder instanceof StorageMinecart;
		}

	}

	class StrongholdAnalyzer implements LocationAnalyzer {

		@Override
		public boolean isValid(@NotNull final InventoryHolder inventoryHolder) {
			final var location = inventoryHolder.getInventory().getLocation();

			if(location == null) {
				return false;
			}
			final var belowMaterial = location.subtract(0, 1, 0).getBlock().getType();

			return belowMaterial.name().contains("STONE_BRICK") || belowMaterial == Material.BOOKSHELF
					|| belowMaterial == Material.OAK_PLANKS;
		}

	}

}
