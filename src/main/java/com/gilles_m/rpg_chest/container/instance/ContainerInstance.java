package com.gilles_m.rpg_chest.container.instance;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gilles_m.rpg_chest.container.Container;
import com.gilles_m.rpg_chest.container.ContainerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

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

	public ContainerInstance() { }

	public ContainerInstance(@NotNull final Container container, @NotNull final Location location,
							 @NotNull final BlockFace blockFace) {
		this.containerId = container.getId();
		this.world = location.getWorld().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.blockFace = blockFace;
	}

	public void spawn() {
		ContainerManager.getInstance().getContainer(containerId)
				.ifPresent(container -> container.spawn(new Location(Bukkit.getWorld(world),x, y, z), blockFace));
	}

}
