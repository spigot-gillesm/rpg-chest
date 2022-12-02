package com.gilles_m.rpg_chest.item;

import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.github.spigot_gillesm.item_lib.ConfigurationItem;

import java.io.IOException;
import java.util.Arrays;

public class ItemLoader {

	private static final ItemLoader INSTANCE = new ItemLoader();

	private ItemLoader() { }

	public void load() {
		final var manager = ItemManager.getInstance();
		manager.clear();

		Formatter.info("Loading items...");
		final var folder = FileUtils.getResource("items/");

		if(folder != null && folder.isDirectory()) {
			Arrays.stream(folder.listFiles())
					.filter(f -> !f.isDirectory())
					.forEach(f -> {
						final var itemName = f.getName().split("\\.")[0];
								try {
									manager.registerItem(itemName,
											ConfigurationItem.fromFile(f).toItemStack());
								} catch (final IOException exception) {
									Formatter.error(String.format("Unable to load item %s: Invalid data.", itemName));
									exception.printStackTrace();
								}
							}
					);
		}
		Formatter.info(String.format("Loaded %d item(s)", manager.size()));
	}

	public static ItemLoader getInstance() {
		return INSTANCE;
	}

}
