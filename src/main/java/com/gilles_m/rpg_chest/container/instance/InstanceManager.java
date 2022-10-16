package com.gilles_m.rpg_chest.container.instance;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

	public void remove(@NotNull final ContainerInstance containerInstance) {
		registeredInstances.remove(containerInstance);
	}

	public void clear() {
		registeredInstances.clear();
	}

	public static InstanceManager getInstance() {
		return INSTANCE;
	}

}
