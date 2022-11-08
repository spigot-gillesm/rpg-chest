package com.gilles_m.rpg_chest.world.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gilles_m.rpg_chest.item_table.ItemTableDeserializer;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorldChestConfiguration {

	@Setter(AccessLevel.PACKAGE)
	private Set<WorldConfiguration> worldConfigurations = new HashSet<>();

	WorldChestConfiguration() { }

	public final String toString() {
		return MoreObjects.toStringHelper(this)
				.add("worldConfigurations", worldConfigurations.toString())
				.toString();
	}

	Set<WorldConfiguration> getWorldConfigurations() {
		return ImmutableSet.copyOf(worldConfigurations);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	static class WorldConfiguration {

		@Getter(AccessLevel.PACKAGE)
		@Setter(AccessLevel.PACKAGE)
		private String world;

		@Getter(AccessLevel.PACKAGE)
		@JsonProperty("item-table")
		@JsonDeserialize(using = ItemTableDeserializer.class)
		private String itemTable;

		@Getter(AccessLevel.PACKAGE)
		@JsonProperty("action")
		@JsonDeserialize(using = WorldChestConfigurationDeserializer.ActionDeserializer.class)
		private Action action = Action.ADD;

		@Setter(AccessLevel.PACKAGE)
		private Set<LocationConfiguration> locationConfigurations = new HashSet<>();

		public final String toString() {
			return MoreObjects.toStringHelper(this)
					.add("world", world)
					.add("itemTable", itemTable)
					.add("action", action.toString())
					.add("locationConfigurations", locationConfigurations.toString())
					.toString();
		}

		Set<LocationConfiguration> getLocationConfigurations() {
			return ImmutableSet.copyOf(locationConfigurations);
		}

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	static class LocationConfiguration {

		@Getter(AccessLevel.PACKAGE)
		@Setter(AccessLevel.PACKAGE)
		private String location;

		@Getter(AccessLevel.PACKAGE)
		@JsonProperty("action")
		@JsonDeserialize(using = WorldChestConfigurationDeserializer.ActionDeserializer.class)
		private Action action = Action.ADD;

		@Getter(AccessLevel.PACKAGE)
		@JsonProperty("item-table")
		@JsonDeserialize(using = ItemTableDeserializer.class)
		private String itemTable;

		public final String toString() {
			return MoreObjects.toStringHelper(this)
					.add("location", location)
					.add("action", action.toString())
					.add("itemTable", itemTable)
					.toString();
		}

	}

	public enum Action {

		ADD,
		REPLACE

	}

}
