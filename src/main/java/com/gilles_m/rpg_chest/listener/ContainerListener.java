package com.gilles_m.rpg_chest.listener;

import com.gilles_m.rpg_chest.event.ContainerOpenEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ContainerListener implements Listener {

	@EventHandler
	protected void onContainerOpen(final ContainerOpenEvent event) {
		final var containerInstance = event.getContainerInstance();

		if(!containerInstance.isOnCooldown()) {
			containerInstance.fillInventory();
			containerInstance.startCooldown();
		}
	}

}
