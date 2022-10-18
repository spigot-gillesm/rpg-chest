package com.gilles_m.rpg_chest.key;

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

	public Optional<ContainerKey> getKey(@NotNull final String id) {
		return registeredKeys.stream()
				.filter(key -> key.getId().equals(id))
				.findFirst();
	}

	public Optional<ContainerKey> getKey(@NotNull final ItemStack itemStack) {
		return registeredKeys.stream()
				.filter(key -> key.getItemStack().isSimilar(itemStack))
				.findFirst();
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
