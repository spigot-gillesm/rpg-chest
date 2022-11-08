package com.gilles_m.rpg_chest.key;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class KeyManager {

	private static final KeyManager INSTANCE = new KeyManager();

	private final Set<ContainerKey> registeredKeys = new HashSet<>();

	private KeyManager() { }

	public void register(@NotNull final ContainerKey containerKey) {
		registeredKeys.add(containerKey);
	}

	public void register(@NotNull final Collection<ContainerKey> containerKeys) {
		registeredKeys.addAll(containerKeys);
	}

	public Optional<ContainerKey> getKey(final String id) {
		if(id == null) {
			return Optional.empty();
		}
		return registeredKeys.stream()
				.filter(key -> key.getId().equals(id))
				.findFirst();
	}

	public Optional<ContainerKey> getKey(final ItemStack itemStack) {
		if(itemStack == null) {
			return Optional.empty();
		}
		return registeredKeys.stream()
				.filter(key -> key.getItemStack().isSimilar(itemStack))
				.findFirst();
	}

	/**
	 * Look for keys to drop from the given source at the specified location.
	 *
	 * @param sourceId the source id
	 * @param location the location
	 */
	public void dropKeys(@NotNull final String sourceId, @NotNull final Location location) {
		registeredKeys.forEach(key -> key.drop(sourceId, location));
	}

	public int size() {
		return registeredKeys.size();
	}

	public void clear() {
		registeredKeys.clear();
	}

	public static KeyManager getInstance() {
		return INSTANCE;
	}

}
