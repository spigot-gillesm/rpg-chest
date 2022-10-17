package com.gilles_m.rpg_chest.item_table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.github.spigot_gillesm.item_lib.YamlItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TableLoader {

	private static final TableLoader INSTANCE = new TableLoader();

	private final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

	private TableLoader() { }

	public void load() {
		final var manager = TableManager.getInstance();
		manager.clear();

		Formatter.info("Loading item tables...");
		final var folder = FileUtils.getResource("tables/");

		if(folder != null && folder.isDirectory()) {
			Arrays.stream(folder.listFiles())
					.filter(f -> !f.isDirectory())
					.forEach(f -> {
								try {
									loadItemTableFromFile(f)
											.ifPresent(manager::register);
								} catch (final IOException e) {
									Formatter.error(String.format("Unable to load item table %s: Invalid data", f.getName()));
									e.printStackTrace();
								}
							}
					);
		}
		Formatter.info(String.format("Loaded %d item table(s)", manager.size()));
	}

	private Optional<ItemTable> loadItemTableFromFile(@NotNull final File file) throws IOException {
		final var itemTable = objectMapper.readValue(file, ItemTable.class);

		if(itemTable == null) {
			return Optional.empty();
		}
		final var id = file.getName().split("\\.")[0];
		itemTable.setId(id);
		itemTable.getItemSections().addAll(loadItemSections(FileUtils.getConfiguration(file), id));

		return Optional.of(itemTable);
	}

	private List<ItemSection> loadItemSections(@NotNull final YamlConfiguration configuration, final String id) {
		final List<ItemSection> itemSections = new ArrayList<>();

		if(!configuration.isConfigurationSection("items")) {
			Formatter.warning(String.format("Item table %s has no items listed", id));
			return itemSections;
		}

		for(final var key : configuration.getConfigurationSection("items").getKeys(false)) {
			loadItemSection(configuration.getConfigurationSection("items." + key), id)
					.ifPresent(itemSections::add);
		}

		return itemSections;
	}

	private Optional<ItemSection> loadItemSection(@NotNull final ConfigurationSection configurationSection, final String id) {
		try {
			return Optional.of(new ItemSection(
					YamlItem.fromConfiguration(configurationSection).getItemFromFile().make().getItemStack(),
					RangeInteger.fromString(configurationSection.getString("amount", "1")),
					configurationSection.getInt("chance", 1)
			));
		} catch (final IllegalArgumentException exception) {
			Formatter.warning(String.format("Invalid item section data in %s item table in section %s", id,
					configurationSection.getName()));
		}

		return Optional.empty();
	}

	public static TableLoader getInstance() {
		return INSTANCE;
	}

}
