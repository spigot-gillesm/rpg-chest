package com.gilles_m.rpg_chest.container;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.gilles_m.rpg_chest.container.container_implementation.SimpleContainer;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

public class ContainerLoader {

	private static final ContainerLoader INSTANCE = new ContainerLoader();

	private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

	private ContainerLoader() { }

	/**
	 * Clear all the registered containers and load them from the config file.
	 */
	public void load() {
		final var manager = ContainerManager.getInstance();
		manager.clear();

		Formatter.info("Loading containers...");
		final var folder = FileUtils.getResource("containers/");

		if(folder != null && folder.isDirectory()) {
			Arrays.stream(folder.listFiles())
					.filter(f -> !f.isDirectory())
					.forEach(f -> {
								try {
									loadContainerFromFile(f)
											.ifPresent(manager::register);
								} catch (final IOException e) {
									Formatter.error(String.format("Unable to load container %s: Invalid data.", f.getName()));
								}
							}
					);
		}
		Formatter.info(String.format("Loaded %d container(s)", manager.size()));
	}

	private Optional<Container> loadContainerFromFile(@NotNull final File file) throws IOException {
		final var container = objectMapper.readValue(file, SimpleContainer.class);
		final var metadata = objectMapper.readValue(file, Container.Metadata.class);

		if(container == null || metadata == null) {
			return Optional.empty();
		}
		/*container.setId(file.getName().split("\\.")[0]);
		container.setMetadata(metadata);*/

		return Optional.of(container);
	}

	public static ContainerLoader getInstance() {
		return INSTANCE;
	}

}
