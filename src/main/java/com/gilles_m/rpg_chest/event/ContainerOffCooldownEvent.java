package com.gilles_m.rpg_chest.event;

import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ContainerOffCooldownEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	@Getter
	private ContainerInstance containerInstance;

	public ContainerOffCooldownEvent(@NotNull final ContainerInstance containerInstance) {
		this.containerInstance = containerInstance;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
