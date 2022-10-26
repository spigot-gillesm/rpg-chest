package com.gilles_m.rpg_chest.container.instance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class InstanceLoader {

	private static final InstanceLoader INSTANCE = new InstanceLoader();

	//Maps into JSON
	private final ObjectMapper objectMapper = new ObjectMapper();

	private InstanceLoader() { }

	public void load() {
		final var manager = InstanceManager.getInstance();
		manager.clear();

		Formatter.info("Loading container instances...");
		final var folder = FileUtils.getResource("instances/");

		if(folder != null && folder.isDirectory()) {
			Arrays.stream(folder.listFiles())
					.filter(f -> !f.isDirectory())
					.forEach(f -> {
								try {
									loadInstanceFromFile(f)
											.ifPresent(manager::register);
								} catch (final IOException e) {
									Formatter.error(String.format("Unable to load container instance %s: Invalid data", f.getName()));
									e.printStackTrace();
								}
							}
					);
		}
		Formatter.info(String.format("Loaded %d container instance(s)", manager.size()));
	}

	private Optional<ContainerInstance> loadInstanceFromFile(@NotNull final File file) throws IOException {
		final var instance = objectMapper.readValue(file, ContainerInstance.class);

		if(instance == null) {
			return Optional.empty();
		}
		final var cooldown = objectMapper.readTree(file).path("remaining-cooldown").asDouble();

		if(cooldown > 0) {
			instance.startCooldown(cooldown);
		}

		return Optional.of(instance);
	}

	public void saveAll() throws IOException {
		Formatter.info("Saving container instances...");
		final var folder = FileUtils.getResource("instances/");

		//Clear the folder first
		if(folder != null && folder.isDirectory()) {
			Arrays.stream(folder.listFiles())
					.forEach(File::delete);
		}
		for(final var instance : InstanceManager.getInstance().getRegisteredInstances()) {
			save(instance);
		}
		Formatter.info("Done!");
	}

	public void save(@NotNull final ContainerInstance containerInstance) throws IOException {
		objectMapper.writeValue(FileUtils.getResource("instances/" + getInstanceUniqueId(containerInstance)),
				containerInstance);
	}

	private String getInstanceUniqueId(@NotNull final ContainerInstance containerInstance) {
		final var location = containerInstance.getLocation();

		return containerInstance.getContainerId() + "_" + location.getWorld().getName() + "_" + location.getBlockX()
				+ "_" + location.getBlockY() + "_" + location.getBlockZ();
	}

	public static InstanceLoader getInstance() {
		return INSTANCE;
	}

}
