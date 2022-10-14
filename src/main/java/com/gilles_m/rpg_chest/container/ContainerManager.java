package com.gilles_m.rpg_chest.container;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ContainerManager {

	private static final ContainerManager INSTANCE = new ContainerManager();

	private final Set<Container> registeredContainers = new HashSet<>();

	private ContainerManager() { }

	/**
	 * Register a new container. If the container already exists, it will be replaced.
	 *
	 * @param container the container
	 */
	public void register(@NotNull final Container container) {
		registeredContainers.add(container);
	}

	public Optional<Container> getContainer(@NotNull final String id) {
		return registeredContainers.stream().filter(container -> container.getId().equals(id)).findFirst();
	}

	public int size() {
		return registeredContainers.size();
	}

	public void clear() {
		registeredContainers.clear();
	}

	public static ContainerManager getInstance() {
		return INSTANCE;
	}

}
