package com.gilles_m.rpg_chest.container.instance;

import com.gilles_m.rpg_chest.container.Container;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class InstanceManager {

	private static final InstanceManager INSTANCE = new InstanceManager();

	private final Set<ContainerInstance> registeredInstances = new HashSet<>();

	private InstanceManager() { }

	public void register(@NotNull final ContainerInstance containerInstance) {
		registeredInstances.add(containerInstance);
	}

	public Optional<ContainerInstance> getContainerInstance(@NotNull final Location location) {
		return registeredInstances.stream()
				.filter(instance -> instance.getLocation().equals(location))
				.findFirst();
	}

	public Set<ContainerInstance> getContainerInstances(@NotNull final String containerId) {
		return registeredInstances.stream()
				.filter(instance -> instance.getContainerId().equals(containerId))
				.collect(Collectors.toSet());
	}

	public Set<ContainerInstance> getContainerInstances(@NotNull final Container container) {
		return registeredInstances.stream()
				.filter(instance -> instance.getContainer().equals(container))
				.collect(Collectors.toSet());
	}

	public void remove(@NotNull final ContainerInstance containerInstance) {
		registeredInstances.remove(containerInstance);
	}

	public void clear() {
		registeredInstances.clear();
	}

	public int size() {
		return registeredInstances.size();
	}

	/**
	 * @return an immutable set containing all the registered instances
	 */
	public Set<ContainerInstance> getRegisteredInstances() {
		return Collections.unmodifiableSet(registeredInstances);
	}

	public static InstanceManager getInstance() {
		return INSTANCE;
	}

}
