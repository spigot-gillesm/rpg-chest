package com.gilles_m.rpg_chest.container.container_implementation;

import com.gilles_m.rpg_chest.container.Container;
import com.gilles_m.rpg_chest.container.ContainerManager;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.jetbrains.annotations.NotNull;

public class SimpleContainer extends Container {

	public SimpleContainer() { }

	@Override
	public void spawn(@NotNull final Location location, @NotNull final BlockFace blockFace) {
		ContainerManager.getInstance().getContainer(getId())
				.ifPresent(container -> {
					final var block = location.getWorld().getBlockAt(location);
					block.setType(container.getMaterial());

					if(block.getBlockData() instanceof Directional) {
						final var data = ((Directional) block.getBlockData());
						data.setFacing(blockFace);
						block.setBlockData(data);
					}
				});
	}

}
