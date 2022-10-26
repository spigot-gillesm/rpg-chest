package com.gilles_m.rpg_chest.container_event.container_event_implementation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gilles_m.rpg_chest.container_event.ContainerEvent;
import com.gilles_m.rpg_chest.item_table.RangeIntegerDeserializer;
import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.gilles_m.rpg_chest.util.Dependency;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.google.common.base.MoreObjects;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SpawnEntityEvent extends ContainerEvent {

	@JsonProperty("entities")
	@JsonDeserialize(contentUsing = RangeIntegerDeserializer.class)
	private Map<String, RangeInteger> entities = new HashMap<>();

	public SpawnEntityEvent() {
		//Default constructor for Jackson
	}

	public SpawnEntityEvent(final Trigger trigger) {
		super(trigger);
	}

	@Override
	public void run(@NotNull final Location location) {
		for(final var entrySet : entities.entrySet()) {
			final var amount = entrySet.getValue().getInt();
			final var entityName = entrySet.getKey();

			if(entityName.toLowerCase().startsWith("mm:") || entityName.toLowerCase().startsWith("mythicmobs:")) {
				Dependency.getInstance().spawnMythicMob(entityName.split(":")[1], location, amount);
			} else {
				final var entity = getEntity(entityName);

				if(entity == null) {
					Formatter.warning(String.format("Could not spawn %s at location %s", entrySet.getKey(), location));
					continue;
				}
				for (int i = 0; i < amount; i++) {
					location.getWorld().spawnEntity(location, entity);
				}
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

	public final String toString() {
		return MoreObjects.toStringHelper(this)
				.add("trigger", getTrigger())
				.add("entities", entities.toString())
				.toString();
	}

}
