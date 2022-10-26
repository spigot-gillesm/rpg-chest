package com.gilles_m.rpg_chest.container_event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ContainerEvent {

	@Getter
	@JsonProperty("trigger")
	@JsonDeserialize(using = ContainerEventDeserializer.TriggerDeserializer.class)
	private Trigger trigger;

	protected ContainerEvent() {
		//Default constructor for Jackson
	}

	protected ContainerEvent(final Trigger trigger) {
		this.trigger = trigger;
	}

	public abstract void run(@NotNull Location location);

	public enum Trigger {

		ON_OPEN,
		ON_CLOSE

	}

}
