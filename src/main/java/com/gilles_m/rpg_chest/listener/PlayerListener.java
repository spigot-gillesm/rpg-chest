package com.gilles_m.rpg_chest.listener;

import com.gilles_m.rpg_chest.container.instance.InstanceManager;
import com.gilles_m.rpg_chest.event.ContainerCloseEvent;
import com.gilles_m.rpg_chest.event.ContainerOpenEvent;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
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
				.ifPresent(instance -> {
					final var player = event.getPlayer();

					if(instance.isLocked()) {
						if(instance.canOpen(player)) {
							instance.unlock(player);
						} else {
							Formatter.tell(player, "&cYou do not have the required keys to open this container:");
							instance.displayRequiredKeys(player);
						}
						event.setCancelled(true);
					} else {
						final ContainerOpenEvent containerEvent = new ContainerOpenEvent(instance, player);
						Bukkit.getServer().getPluginManager().callEvent(containerEvent);

						if(containerEvent.isCancelled()) {
							event.setCancelled(true);
						}
					}
				});
	}

	@EventHandler
	protected void onPlayerBreakBlock(final BlockBreakEvent event) {
		//Check if a player has broken a rpg chest
		InstanceManager.getInstance().getContainerInstance(event.getBlock().getLocation())
				.ifPresent(instance -> {
					if(instance.getContainer().getMetadata().isUnbreakable()) {
						event.setCancelled(true);
						return;
					}
					//Do not fill the container's inventory if it is still on cooldown or locked
					if(!instance.isOnCooldown() && !instance.isLocked()) {
						instance.fillInventory();
					}
					instance.startCooldown();
				});
	}

	@EventHandler
	protected void onPlayerCloseInventory(final InventoryCloseEvent event) {
		InstanceManager.getInstance().getContainerInstance(
				event.getPlayer().getOpenInventory().getTopInventory().getHolder().getInventory().getLocation()
		).ifPresent(instance ->
				Bukkit.getServer().getPluginManager()
						.callEvent(new ContainerCloseEvent(instance, (Player) event.getPlayer()))
		);
	}

}
