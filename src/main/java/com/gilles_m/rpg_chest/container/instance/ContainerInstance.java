package com.gilles_m.rpg_chest.container.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gilles_m.rpg_chest.Cooldown;
import com.gilles_m.rpg_chest.container.Container;
import com.gilles_m.rpg_chest.container.ContainerManager;
import com.github.spigot_gillesm.format_lib.Formatter;
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

	private final Cooldown cooldown = new Cooldown();

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
		final var container = getContainer();
		container.spawn(location, blockFace);

		final var bukkitContainer = getBukkitContainer();

		if(fill) {
			container.fillInventory(getBukkitContainer());
		}
		bukkitContainer.setCustomName(Formatter.colorize(container.getMetadata().getDisplayName()));
		bukkitContainer.update();
	}

	public void fillInventory() {
		ContainerManager.getInstance().getContainer(containerId)
				.ifPresent(container -> container.fillInventory((org.bukkit.block.Container) getLocation().getBlock().getState()));
	}

	/**
	 * Starts the cooldown with the specified duration.
	 *
	 * @param duration the cooldown duration in seconds
	 */
	public void startCooldown(final long duration) {
		cooldown.start(this, duration);
	}

	/**
	 * Starts the cooldown with the container cooldown's duration.
	 */
	public void startCooldown() {
		startCooldown(getContainer().getMetadata().getCooldown());
	}

	public void destroy() {
		InstanceManager.getInstance().remove(this);
	}

	@NotNull
	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	@NotNull
	public Container getContainer() {
		return ContainerManager.getInstance().getContainer(containerId)
				.orElseThrow(() -> new IllegalStateException("A container instance is referring to a removed container"));
	}

	@NotNull
	public org.bukkit.block.Container getBukkitContainer() {
		final var block = getLocation().getBlock();

		if(!(block.getState() instanceof org.bukkit.block.Container)) {
			throw new IllegalStateException("The block at the container instance's location is not an instance of org.bukkit.block.Container");
		}

		return (org.bukkit.block.Container) getLocation().getBlock().getState();
	}

	public boolean isOnCooldown() {
		return cooldown.remainingTime() > 0;
	}

}
