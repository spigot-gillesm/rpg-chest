package com.gilles_m.rpg_chest.container_event;

import lombok.Getter;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public abstract class ContainerEvent {

	@Getter
	private final Trigger trigger;

	protected ContainerEvent(final Trigger trigger) {
		this.trigger = trigger;
	}

	public abstract void run(@NotNull Location location);

	public enum Trigger {

		ON_OPEN,
		ON_CLOSE

	}

}
