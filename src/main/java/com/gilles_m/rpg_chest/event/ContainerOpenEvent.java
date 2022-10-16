package com.gilles_m.rpg_chest.event;

import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ContainerOpenEvent extends Event implements Cancellable {

	private static final HandlerList HANDLERS = new HandlerList();

	@Getter
	private final ContainerInstance containerInstance;

	@Getter
	private final Player player;

	private boolean cancelled;

	public ContainerOpenEvent(@NotNull final ContainerInstance containerInstance, @NotNull final Player player) {
		this.containerInstance = containerInstance;
		this.player = player;
	}

	public Location getLocation() {
		return containerInstance.getLocation();
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(final boolean cancel) {
		cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

}
