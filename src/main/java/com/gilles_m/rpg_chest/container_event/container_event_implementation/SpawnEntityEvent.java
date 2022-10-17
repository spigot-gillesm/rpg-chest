package com.gilles_m.rpg_chest.container_event.container_event_implementation;

import com.gilles_m.rpg_chest.container_event.ContainerEvent;
import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SpawnEntityEvent extends ContainerEvent {

	private Map<String, RangeInteger> entities = new HashMap<>();

	public SpawnEntityEvent(final Trigger trigger) {
		super(trigger);
	}

	@Override
	public void run(@NotNull final Location location) {
		for(final var entrySet : entities.entrySet()) {
			final var amount = entrySet.getValue().getInt();
			final var entity = getEntity(entrySet.getKey());

			if(entity == null) {
				Formatter.warning(String.format("Could not spawn %s at location %s", entrySet.getKey(), location));
				continue;
			}
			for(int i = 0; i < amount; i++) {
				location.getWorld().spawnEntity(location, entity);
			}
		}
	}

	private EntityType getEntity(@NotNull final String type) {
		try {
			return EntityType.valueOf(type.toUpperCase());
		} catch (final IllegalArgumentException exception) {
			Formatter.error(String.format("Invalid mob type: %s", type));
		}

		return null;
	}

}
