package com.gilles_m.rpg_chest.container.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gilles_m.rpg_chest.container.Container;
import com.gilles_m.rpg_chest.container.ContainerManager;
import com.github.spigot_gillesm.format_lib.Formatter;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a specific container, meaning an instance of a container.
 */
public class ContainerInstance {

	@JsonProperty("container")
	private String containerId;

	@JsonProperty("world")
	private String world;

	@JsonProperty("x")
	private int x;

	@JsonProperty("y")
	private int y;

	@JsonProperty("z")
	private int z;

	@JsonProperty("face")
	private BlockFace blockFace;

	@Getter
	private boolean onCooldown = false;

	public ContainerInstance() {
		//Default constructor for Jackson
	}

	/**
	 * @param container the container from which this instance refers to
	 * @param location the location
	 * @param blockFace the facing
	 */
	public ContainerInstance(@NotNull final Container container, @NotNull final Location location,
							 @NotNull final BlockFace blockFace) {
		this.containerId = container.getId();
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.blockFace = blockFace;
	}

	/**
	 * Spawn a container of this instance.
	 */
	public void spawn() {
		spawn(false);
	}

	/**
	 * Spawn a container of this instance.
	 *
	 * @param fill whether to fill the chest on spawn
	 */
	public void spawn(final boolean fill) {
		final var location = getLocation();
		ContainerManager.getInstance().getContainer(containerId)
				.ifPresent(container -> {
					container.spawn(location, blockFace);
					final var bukkitContainer = getBukkitContainer();

					if(fill) {
						container.fillInventory(getBukkitContainer());
					}
					bukkitContainer.setCustomName(Formatter.colorize(container.getMetadata().getDisplayName()));
					bukkitContainer.update();
				});
	}

	public void fillInventory() {
		ContainerManager.getInstance().getContainer(containerId)
				.ifPresent(container -> container.fillInventory((org.bukkit.block.Container) getLocation().getBlock().getState()));
	}

	public void startCooldown() {
		//TODO
	}

	public void destroy() {
		InstanceManager.getInstance().remove(this);
	}

	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	public org.bukkit.block.Container getBukkitContainer() {
		final var block = getLocation().getBlock();

		if(!(block.getState() instanceof org.bukkit.block.Container)) {
			throw new IllegalStateException("The block at the container instance's location is not an instance of org.bukkit.block.Container");
		}

		return (org.bukkit.block.Container) getLocation().getBlock().getState();
	}

}
