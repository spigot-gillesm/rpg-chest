package com.gilles_m.rpg_chest.listener;

import com.gilles_m.rpg_chest.world.configuration.WorldChestManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;

public class WorldListener implements Listener {

	@EventHandler
	private void onLootGenerated(final LootGenerateEvent event) {
		final var inventoryHolder = event.getInventoryHolder();

		if(inventoryHolder == null) {
			return;
		}
		if(WorldChestManager.getInstance().fillContainer(event.getInventoryHolder())) {
			event.setCancelled(true);
		}
	}

}
