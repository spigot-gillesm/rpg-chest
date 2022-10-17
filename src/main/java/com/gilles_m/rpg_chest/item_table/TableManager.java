package com.gilles_m.rpg_chest.item_table;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TableManager {

	private static final TableManager INSTANCE = new TableManager();

	private final Set<ItemTable> registeredTables = new HashSet<>();

	private TableManager() { }

	/**
	 * Register a new item table. If the table already exists, it will be replaced.
	 *
	 * @param itemTable the item table
	 */
	public void register(@NotNull final ItemTable itemTable) {
		registeredTables.add(itemTable);
	}

	/**
	 * Get the item table matching the given id.
	 *
	 * @param id the id
	 * @return an optional containing the item table
	 */
	public Optional<ItemTable> getItemTable(final String id) {
		return registeredTables.stream().filter(table -> table.getId().equals(id)).findFirst();
	}

	public void clear() {
		registeredTables.clear();
	}

	public int size() {
		return registeredTables.size();
	}

	public static TableManager getInstance() {
		return INSTANCE;
	}

}
