package com.gilles_m.rpg_chest.listener;

import com.gilles_m.rpg_chest.container_event.ContainerEvent;
import com.gilles_m.rpg_chest.event.ContainerOffCooldownEvent;
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
			containerInstance.runEvents(ContainerEvent.Trigger.ON_OPEN);
		}
	}

	@EventHandler
	protected void onContainerOffCooldown(final ContainerOffCooldownEvent event) {
		final var instance = event.getContainerInstance();

		if(event.getLocation().getBlock().getType() != instance.getContainer().getMaterial()) {
			instance.spawn();
		}
	}

}
