package com.gilles_m.rpg_chest.event;

import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ContainerCloseEvent extends Event {

	private static final HandlerList HANDLERS = new HandlerList();

	@Getter
	private ContainerInstance containerInstance;

	@Getter
	private Player player;

	public ContainerCloseEvent(@NotNull final ContainerInstance containerInstance, final Player player) {
		this.containerInstance = containerInstance;
		this.player = player;
	}

	@NotNull
	public Location getLocation() {
		return containerInstance.getLocation();
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
