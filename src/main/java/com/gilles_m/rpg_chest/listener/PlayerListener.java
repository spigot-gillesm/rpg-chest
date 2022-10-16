package com.gilles_m.rpg_chest.listener;

import com.gilles_m.rpg_chest.container.instance.InstanceManager;
import com.gilles_m.rpg_chest.event.ContainerOpenEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {

	@EventHandler
	protected void onPlayerInteract(final PlayerInteractEvent event) {
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		final Block block = event.getClickedBlock();

		if(block == null) {
			return;
		}
		final Material material = block.getType();

		if(!material.name().contains("CHEST") && !material.name().contains("BARREL")) {
			return;
		}
		InstanceManager.getInstance().getContainerInstance(block.getLocation())
				.ifPresent(containerInstance -> {
					final ContainerOpenEvent containerEvent = new ContainerOpenEvent(containerInstance, event.getPlayer());
					Bukkit.getServer().getPluginManager().callEvent(containerEvent);

					if(containerEvent.isCancelled()) {
						event.setCancelled(true);
					}
				});
	}

}
