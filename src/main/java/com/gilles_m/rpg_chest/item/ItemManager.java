package com.gilles_m.rpg_chest.item;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemManager {

	private static final ItemManager INSTANCE = new ItemManager();

	private final Map<String, ItemStack> registeredItems = new HashMap<>();

	private ItemManager() { }

	public void registerItem(@NotNull final String id, @NotNull final ItemStack itemStack) {
		registeredItems.put(id, itemStack);
	}

	public Optional<ItemStack> getItem(@NotNull final String id) {
		if(registeredItems.containsKey(id)) {
			return Optional.of(registeredItems.get(id));
		} else {
			return Optional.empty();
		}
	}

	public void clear() {
		registeredItems.clear();
	}

	public int size() {
		return registeredItems.size();
	}

	public static ItemManager getInstance() {
		return INSTANCE;
	}

}
