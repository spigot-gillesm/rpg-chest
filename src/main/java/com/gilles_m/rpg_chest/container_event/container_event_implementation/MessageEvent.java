package com.gilles_m.rpg_chest.container_event.container_event_implementation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gilles_m.rpg_chest.container_event.ContainerEvent;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.google.common.base.MoreObjects;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageEvent extends ContainerEvent {

	@JsonProperty("text")
	private List<String> text;

	@JsonProperty("radius")
	private int radius;

	public MessageEvent() {
		//Default constructor for Jackson
	}

	public MessageEvent(final Trigger trigger, final List<String> text, final int radius) {
		super(trigger);

		this.text = text;
		this.radius = radius;
	}

	@Override
	public void run(@NotNull final Location location) {
		location.getWorld().getNearbyEntities(location, radius, radius, radius)
				.stream()
				.filter(Player.class::isInstance)
				.forEach(entity -> text.forEach(line -> Formatter.tell(entity, line)));
	}

	public final String toString() {
		return MoreObjects.toStringHelper(this)
				.add("trigger", getTrigger())
				.add("radius", radius)
				.add("text", text.toString())
				.toString();
	}

}
