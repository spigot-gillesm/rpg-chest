package com.gilles_m.rpg_chest.container.instance;

import com.gilles_m.rpg_chest.container.Container;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class InstanceManager {

	private static final InstanceManager INSTANCE = new InstanceManager();

	private static final Set<ContainerInstance> IMMUTABLE_EMPTY_SET = Collections.emptySet();

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

	public Set<ContainerInstance> getContainerInstances(final String containerId) {
		if(containerId == null) {
			return IMMUTABLE_EMPTY_SET;
		}
		return registeredInstances.stream()
				.filter(instance -> instance.getContainerId().equals(containerId))
				.collect(Collectors.toUnmodifiableSet());
	}

	public Set<ContainerInstance> getContainerInstances(final Container container) {
		if(container == null) {
			return IMMUTABLE_EMPTY_SET;
		}
		return registeredInstances.stream()
				.filter(instance -> instance.getContainer().equals(container))
				.collect(Collectors.toUnmodifiableSet());
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
