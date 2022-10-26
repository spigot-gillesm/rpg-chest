package com.gilles_m.rpg_chest.listener;

import com.gilles_m.rpg_chest.key.KeyManager;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class EntityListener implements Listener {

	@EventHandler
	protected void onEntityDeath(final EntityDeathEvent event) {
		final var entity = event.getEntity();
		KeyManager.getInstance().dropKeys(entity.getType().toString(), entity.getLocation());
	}

	@EventHandler
	protected void onMythicMobDeath(final MythicMobDeathEvent event) {
		final var mythicMob = event.getMob();
		KeyManager.getInstance().dropKeys(mythicMob.getMobType(), event.getEntity().getLocation());
	}

}
