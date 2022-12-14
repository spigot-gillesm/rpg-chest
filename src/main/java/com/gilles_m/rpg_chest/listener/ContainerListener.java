package com.gilles_m.rpg_chest.listener;

import com.gilles_m.rpg_chest.event.ContainerCloseEvent;
import com.gilles_m.rpg_chest.event.ContainerOffCooldownEvent;
import com.gilles_m.rpg_chest.event.ContainerOpenEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ContainerListener implements Listener {

	@EventHandler
	protected void onContainerOpen(final ContainerOpenEvent event) {
		final var instance = event.getContainerInstance();

		if(!instance.isOnCooldown()) {
			instance.open();
		}
	}

	@EventHandler
	protected void onContainerClose(final ContainerCloseEvent event) {
		final var instance = event.getContainerInstance();

		if(instance.getContainer().getMetadata().isDespawning() && instance.isEmpty()) {
			instance.despawn();
		}
	}

	@EventHandler
	protected void onContainerOffCooldown(final ContainerOffCooldownEvent event) {
		final var instance = event.getContainerInstance();

		if(event.getLocation().getBlock().getType() != instance.getContainer().getMaterial()) {
			instance.spawn();
		}
		instance.lock();
	}

}
