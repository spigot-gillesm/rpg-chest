package com.gilles_m.rpg_chest.container;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import com.gilles_m.rpg_chest.container.instance.InstanceManager;
import com.google.common.base.MoreObjects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Container {

	@Setter(AccessLevel.PACKAGE)
	@Getter
	private String id;

	@Getter
	@JsonProperty("material")
	private Material material;

	@Setter(AccessLevel.PACKAGE)
	@Getter
	@JsonDeserialize(using = ContainerDeserializer.MaterialDeserializer.class)
	private Metadata metadata;

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("material", material)
				.add("metadata", metadata)
				.toString();
	}

	public ContainerInstance newInstance(@NotNull final Location location, final BlockFace blockFace) {
		final var instance = new ContainerInstance(this, location, blockFace);
		InstanceManager.getInstance().register(instance);

		return instance;
	}

	public abstract void spawn(@NotNull Location location, @NotNull BlockFace blockFace);

	@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Metadata {

		@Getter
		@JsonProperty("display-name")
		private String displayName;

		/*@Getter
		private ItemTable itemTable;*/

		@Getter
		@JsonProperty("is-despawning")
		private boolean despawning;

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
					.add("cooldown", cooldown)
					.add("cooldownPerPlayer", cooldownPerPlayer)
					.toString();
		}

	}

}
