package com.gilles_m.rpg_chest.container;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import com.gilles_m.rpg_chest.container.instance.InstanceManager;
import com.gilles_m.rpg_chest.container_event.ContainerEvent;
import com.gilles_m.rpg_chest.item_table.TableManager;
import com.gilles_m.rpg_chest.key.ContainerKey;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Container {

	@Setter
	@Getter
	private String id;

	@Getter
	@JsonProperty("material")
	private Material material;

	@JsonProperty("item-table")
	private String itemTable;

	@JsonProperty("keys")
	@JsonDeserialize(keyUsing = ContainerDeserializer.ContainerKeyDeserializer.class)
	private Map<ContainerKey, Integer> containerKeys = new HashMap<>();

	@JsonIgnore
	private final Set<ContainerEvent> containerEvents = new HashSet<>();

	@Setter
	@Getter
	@JsonDeserialize(using = ContainerDeserializer.MaterialDeserializer.class)
	private Metadata metadata;

	@JsonIgnore
	private final Random random = new SecureRandom();

	public abstract void spawn(@NotNull Location location, @NotNull BlockFace blockFace);

	/**
	 * Returns a new instance of this container.
	 *
	 * @param location the location of the new instance
	 * @param blockFace the facing of the new instance
	 *
	 * @return a new container instance
	 */
	public ContainerInstance newInstance(@NotNull final Location location, final BlockFace blockFace) {
		final var instance = new ContainerInstance(this, location, blockFace);
		InstanceManager.getInstance().register(instance);

		return instance;
	}

	public void fillInventory(@NotNull final org.bukkit.block.Container bukkitContainer) {
		TableManager.getInstance().getItemTable(itemTable)
				.ifPresentOrElse(table -> {
					final var inventory = bukkitContainer.getInventory();
					inventory.clear();
					final var items = table.generateItems();

					//Pick random positions within the container to place items
					for(final var item : items) {
						var slot = random.nextInt(27);

						while(inventory.getItem(slot) != null && (new ItemStack(Material.AIR)).isSimilar(inventory.getItem(slot))) {
							slot = random.nextInt(27);
						}
						inventory.setItem(slot, item);
					}
					inventory.getViewers().forEach(humanEntity -> ((Player) humanEntity).updateInventory());
				}, () -> Formatter.warning(String.format("The container %s is referring to a removed item" +
						" table: %s", id, itemTable)));
	}

	public void registerEvent(@NotNull final ContainerEvent containerEvent) {
		containerEvents.add(containerEvent);
	}

	public void registerEvents(@NotNull final Collection<ContainerEvent> containerEvents) {
		this.containerEvents.addAll(containerEvents);
	}

	public void addKey(@NotNull final ContainerKey containerKey, final int amount) {
		containerKeys.put(containerKey, amount);
	}

	public void runEvents(final ContainerEvent.Trigger trigger, @NotNull final Location location) {
		containerEvents.stream()
				.filter(event -> event.getTrigger() == trigger)
				.forEach(event -> event.run(location));
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("material", material)
				.add("itemTable", itemTable)
				.add("metadata", metadata)
				.add("containerKeys", containerKeys.toString())
				.add("containerEvents", containerEvents.toString())
				.toString();
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Metadata {

		@Getter
		@JsonProperty("display-name")
		private String displayName;

		@Getter
		@JsonProperty("is-despawning")
		private boolean despawning;

		@Getter
		@JsonProperty("unbreakable")
		private boolean unbreakable;

		@Getter
		@JsonProperty("cooldown")
		private long cooldown;

		@Getter
		@JsonProperty("is-cooldown-per-player")
		private boolean cooldownPerPlayer;

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("displayName", displayName)
					.add("despawning", despawning)
					.add("unbreakable", unbreakable)
					.add("cooldown", cooldown)
					.add("cooldownPerPlayer", cooldownPerPlayer)
					.toString();
		}

	}

}
