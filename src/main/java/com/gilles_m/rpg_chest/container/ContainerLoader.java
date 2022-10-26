package com.gilles_m.rpg_chest.container;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.gilles_m.rpg_chest.container.container_implementation.SimpleContainer;
import com.gilles_m.rpg_chest.container_event.ContainerEvent;
import com.gilles_m.rpg_chest.container_event.container_event_implementation.MessageEvent;
import com.gilles_m.rpg_chest.container_event.container_event_implementation.SpawnEntityEvent;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
									loadContainerFromFile(f).ifPresent(manager::register);
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
		container.setId(file.getName().split("\\.")[0]);
		container.setMetadata(metadata);
		container.registerEvents(loadContainerEventsFromFile(file));

		return Optional.of(container);
	}

	private Set<ContainerEvent> loadContainerEventsFromFile(@NotNull final File file) throws IOException {
		final var events = objectMapper.readTree(file).path("events").elements();
		final Set<ContainerEvent> containerEvents = new HashSet<>();
		final List<String> eventNames = new ArrayList<>();

		objectMapper.readTree(file).path("events")
				.fieldNames()
				.forEachRemaining(eventNames::add);

		for(final var eventName : eventNames) {
			if(events.hasNext()) {
				//TODO: Check that each mob is valid or send an error
				loadEventFromFile(eventName, events.next()).ifPresent(containerEvents::add);
			}
		}

		return containerEvents;
	}

	private Optional<ContainerEvent> loadEventFromFile(@NotNull final String eventType, @NotNull final JsonNode jsonNode)
			throws JsonProcessingException {

		if("spawn-entity".equalsIgnoreCase(eventType)) {
			return Optional.of(objectMapper.readValue(jsonNode.toString(), SpawnEntityEvent.class));
		} else if("message".equalsIgnoreCase(eventType)) {
			return Optional.of(objectMapper.readValue(jsonNode.toString(), MessageEvent.class));
		} else {
			Formatter.error(String.format("Invalid event type: %s", eventType));
			return Optional.empty();
		}
	}

	public static ContainerLoader getInstance() {
		return INSTANCE;
	}

}
