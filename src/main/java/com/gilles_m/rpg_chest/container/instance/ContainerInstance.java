package com.gilles_m.rpg_chest.container.instance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gilles_m.rpg_chest.util.Cooldown;
import com.gilles_m.rpg_chest.container.Container;
import com.gilles_m.rpg_chest.container.ContainerManager;
import com.gilles_m.rpg_chest.container_event.ContainerEvent;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.google.common.base.MoreObjects;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a specific container, meaning an instance of a container.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContainerInstance {

	@Getter
	@JsonProperty("container-id")
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
	@JsonSerialize(using = InstanceSerialization.BlockFaceSerializer.class)
	@JsonDeserialize(using = InstanceSerialization.BlockFaceDeserializer.class)
	private BlockFace blockFace;

	@Getter
	@JsonProperty("is-locked")
	private boolean locked = false;

	@JsonIgnore
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

	public void open() {
		fillInventory();
		startCooldown();
		runEvents(ContainerEvent.Trigger.ON_OPEN);
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

		lock();
	}

	/**
	 * Remove the container from the world (but not from the system).
	 */
	public void despawn() {
		getLocation().getBlock().setType(Material.AIR);
	}

	public void fillInventory() {
		ContainerManager.getInstance().getContainer(containerId)
				.ifPresentOrElse(container -> container.fillInventory((org.bukkit.block.Container) getLocation().getBlock().getState()),
						() -> Formatter.warning(
								String.format("A container instance is referring to a removed container: %s", containerId)
						)
				);
	}

	/**
	 * Starts the cooldown with the specified duration.
	 *
	 * @param duration the cooldown duration in seconds
	 */
	public void startCooldown(final double duration) {
		cooldown.start(this, duration);
	}

	/**
	 * Starts the cooldown with the container cooldown's duration.
	 */
	public void startCooldown() {
		startCooldown(getContainer().getMetadata().getCooldown());
	}

	public void runEvents(final ContainerEvent.Trigger trigger) {
		getContainer().runEvents(trigger, getLocation());
	}

	public void lock() {
		if(!getContainer().getContainerKeys().isEmpty()) {
			this.locked = true;
		}
	}

	/**
	 * Unlock this container instance by consuming all the required key from the player's inventory.
	 *
	 * @param player the player unlocking the container instance
	 */
	public void unlock(@NotNull final Player player) {
		final var keys = getContainer().getContainerKeys();

		if(keys.isEmpty()) {
			return;
		}
		getContainer().getContainerKeys()
				.forEach((key, amount) -> {
					final var copy = key.getItemStack().clone();
					copy.setAmount(amount);
					player.getInventory().removeItem(copy);
				});
		this.locked = false;

		player.getWorld().playSound(player, Sound.BLOCK_WOODEN_TRAPDOOR_OPEN,1, 0.85F);
	}

	public void displayRequiredKeys(@NotNull final Player player) {
		getContainer().getContainerKeys()
				.forEach((key, amount) -> Formatter.tell(player, String.format("&7* %s &7x &9%d",
						key.getItemStack().getItemMeta().getDisplayName(),
						amount)));
	}

	public void destroy() {
		InstanceManager.getInstance().remove(this);
	}

	@JsonIgnore
	@NotNull
	public Location getLocation() {
		return new Location(Bukkit.getWorld(world), x, y, z);
	}

	@JsonIgnore
	@NotNull
	public Container getContainer() {
		return ContainerManager.getInstance().getContainer(containerId)
				.orElseThrow(() -> new IllegalStateException("A container instance is referring to a removed container"));
	}

	@JsonIgnore
	@NotNull
	public org.bukkit.block.Container getBukkitContainer() {
		final var block = getLocation().getBlock();

		if(!(block.getState() instanceof org.bukkit.block.Container)) {
			throw new IllegalStateException("The block at the container instance's location is not an instance of org.bukkit.block.Container");
		}

		return (org.bukkit.block.Container) getLocation().getBlock().getState();
	}

	@JsonProperty("remaining-cooldown")
	public double getRemainingCooldown() {
		return cooldown.remainingTime();
	}

	@JsonIgnore
	public boolean isOnCooldown() {
		return getRemainingCooldown() > 0;
	}

	@JsonIgnore
	public boolean isEmpty() {
		return getBukkitContainer().getInventory().isEmpty();
	}

	/**
	 * Check if a player meets the conditions to open this container instance.
	 *
	 * @param player the player trying to open the container instance
	 * @return true if the player can open this container instance
	 */
	@JsonIgnore
	public boolean canOpen(@NotNull final Player player) {
		return getContainer().canOpen(player);
	}

	public final String toString() {
		return MoreObjects.toStringHelper(this)
				.add("containerId", containerId)
				.add("world", world)
				.add("x", x)
				.add("y", y)
				.add("z", z)
				.add("cooldown", cooldown.toString())
				.add("locked", locked)
				.toString();
	}

}
