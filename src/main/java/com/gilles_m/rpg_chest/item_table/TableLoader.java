package com.gilles_m.rpg_chest.item_table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.gilles_m.rpg_chest.util.Dependency;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.github.spigot_gillesm.item_lib.ConfigurationItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

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

	private Optional<ItemTable> loadItemTableFromFile(final File file) throws IOException {
		final var itemTable = objectMapper.readValue(file, ItemTable.class);

		if(itemTable == null) {
			return Optional.empty();
		}
		final var id = file.getName().split("\\.")[0];
		itemTable.setId(id);
		itemTable.getItemSections().addAll(loadItemSections(FileUtils.getConfiguration(file), id));

		return Optional.of(itemTable);
	}

	private List<ItemSection> loadItemSections(final YamlConfiguration configuration, final String id) {
		final List<ItemSection> itemSections = new ArrayList<>();

		if(!configuration.isConfigurationSection("items")) {
			Formatter.warning(String.format("Item table %s has no items listed", id));
			return itemSections;
		}

		for(final var setionName : configuration.getConfigurationSection("items").getKeys(false)) {
			loadItemSection(configuration.getConfigurationSection("items." + setionName), id)
					.ifPresent(itemSections::add);
		}

		return itemSections;
	}

	private Optional<ItemSection> loadItemSection(final ConfigurationSection configurationSection, final String tableId) {
		try {
			return loadItemStackFromConfiguration(configurationSection)
					.map(itemStack -> new ItemSection(
					itemStack,
					RangeInteger.fromString(configurationSection.getString("amount", "1")),
					configurationSection.getInt("chance", 1))
			).or(() -> {
				Formatter.error(String.format("Could not load item section %s in %s", configurationSection.getName(), tableId));
				return Optional.empty();
			});
		} catch (final IllegalArgumentException exception) {
			Formatter.error(String.format("Invalid item section data %s in %s", tableId,
					configurationSection.getName()));
		}

		return Optional.empty();
	}

	private Optional<ItemStack> loadItemStackFromConfiguration(final ConfigurationSection configurationSection) {
		if(configurationSection.contains("item")) {
			return Dependency.getInstance().getItemStackFromString(configurationSection.getString("item"));
		} else if(configurationSection.contains("material")) {
			return Optional.of(ConfigurationItem.fromConfiguration(configurationSection).toItemStack());
		} else {
			Formatter.error("The item section must specify 'item' or 'material'");
			return Optional.empty();
		}
	}

	public static TableLoader getInstance() {
		return INSTANCE;
	}

}
