package com.gilles_m.rpg_chest.world.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.gilles_m.rpg_chest.util.ServerUtil;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorldChestConfigurationLoader {

	private static final WorldChestConfigurationLoader INSTANCE = new WorldChestConfigurationLoader();

	private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

	private WorldChestConfigurationLoader() { }

	public void load() {
		final var manager = WorldChestManager.getInstance();
		Formatter.info("Loading world chests configuration...");

		final var file = FileUtils.getResource("world_chests.yml");

		if(file != null && !file.isDirectory()) {
			try {
				manager.setConfiguration(loadConfiguration(file));
			} catch (final IOException e) {
				Formatter.error("Unable to load world chests configuration: invalid data");
			}
		}
		Formatter.info("Configuration loaded!");
	}

	private WorldChestConfiguration loadConfiguration(final File file) throws IOException {
		final var configuration = new WorldChestConfiguration();

		final var worlds = objectMapper.readTree(file).elements();
		final Set<WorldChestConfiguration.WorldConfiguration> worldConfigurations = new HashSet<>();
		final List<String> worldNames = new ArrayList<>();

		objectMapper.readTree(file)
				.fieldNames()
				.forEachRemaining(worldNames::add);

		for(final var worldName : worldNames) {
			if(worlds.hasNext()) {
				if(ServerUtil.isBukkitWorld(worldName)) {
					worldConfigurations.add(loadWorldConfiguration(worldName, worlds.next()));
				} else {
					Formatter.error(String.format("Unknown world: %s", worldName));
				}
			}
		}
		configuration.setWorldConfigurations(worldConfigurations);

		return configuration;
	}

	private WorldChestConfiguration.WorldConfiguration loadWorldConfiguration(final String world, final JsonNode jsonNode)
			throws JsonProcessingException {
		final var worldConfiguration = objectMapper.readValue(jsonNode.toString(),
				WorldChestConfiguration.WorldConfiguration.class);
		worldConfiguration.setWorld(world);

		final var locations = jsonNode.path("locations").elements();
		final Set<WorldChestConfiguration.LocationConfiguration> locationConfigurations = new HashSet<>();
		final List<String> locationNames = new ArrayList<>();

		jsonNode.path("locations")
				.fieldNames()
				.forEachRemaining(locationNames::add);

		for(final var locationName : locationNames) {
			if(locations.hasNext()) {
				locationConfigurations.add(loadLocationConfiguration(locationName, locations.next()));
			}
		}
		worldConfiguration.setLocationConfigurations(locationConfigurations);

		return worldConfiguration;
	}

	private WorldChestConfiguration.LocationConfiguration loadLocationConfiguration(final String location, final JsonNode jsonNode)
			throws JsonProcessingException {
		final var locationConfiguration = objectMapper.readValue(jsonNode.toString(),
				WorldChestConfiguration.LocationConfiguration.class);
		locationConfiguration.setLocation(location);

		return locationConfiguration;
	}

	public static WorldChestConfigurationLoader getInstance() {
		return INSTANCE;
	}

}
