package com.gilles_m.rpg_chest.item_table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.gilles_m.rpg_chest.util.Dependency;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.github.spigot_gillesm.item_lib.ConfigurationItem;
import com.github.spigot_gillesm.item_lib.YamlItem;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
		itemTable.getItemSections().addAll(loadItemSections(file, FileUtils.getConfiguration(file), id));

		return Optional.of(itemTable);
	}

	private List<ItemSection> loadItemSections(final File file, final YamlConfiguration configuration, final String id)
			throws IOException {
		final List<ItemSection> itemSections = new ArrayList<>();

		if(!configuration.isConfigurationSection("items")) {
			Formatter.warning(String.format("Item table %s has no items listed", id));
			return itemSections;
		}

		for(final var key : configuration.getConfigurationSection("items").getKeys(false)) {
			loadItemSection(file, configuration.getConfigurationSection("items." + key), id)
					.ifPresent(itemSections::add);
		}

		return itemSections;
	}

	private Optional<ItemSection> loadItemSection(final File file, final ConfigurationSection configurationSection, final String id)
			throws IOException {
		try {
			return loadItemStackFromFile(file, configurationSection, id)
					.map(itemStack -> new ItemSection(
					itemStack,
					RangeInteger.fromString(configurationSection.getString("amount", "1")),
					configurationSection.getInt("chance", 1))
			).or(() -> {
				Formatter.error(String.format("Could not load item in item section %s", id));
				return Optional.empty();
			});
		} catch (final IllegalArgumentException exception) {
			Formatter.warning(String.format("Invalid item section data in %s item table in section %s", id,
					configurationSection.getName()));
		}

		return Optional.empty();
	}

	private Optional<ItemStack> loadItemStackFromFile(final File file, final ConfigurationSection configurationSection,
													  final String id) throws IOException {
		if(configurationSection.contains("item")) {
			return Dependency.getInstance().getItemStackFromString(configurationSection.getString("item"));
		} else {
			final Map<String, Object> content = configurationSection.getValues(true);
			Formatter.info("values: " + content);
			final Map<String, Object> recursiveLessContent = removeRecursive(content);
			Formatter.info("values recurless: " + recursiveLessContent);

			Formatter.info("To string from conf section: " + new ObjectMapper().writeValueAsString(recursiveLessContent));
			final var item = ConfigurationItem.fromFile(file, "items", id);
			return Optional.of(YamlItem.fromConfiguration(configurationSection).getItemFromFile().make().getItemStack());
		}
	}

	private Map<String, Object> removeRecursive(final Map<String, Object> map) {
		final Map<String, Object> copy = new HashMap<>();

		for(final var entrySet : map.entrySet()) {
			if(entrySet.getValue() instanceof ConfigurationSection) {
				copy.put(entrySet.getKey(), removeRecursive(((ConfigurationSection) entrySet.getValue()).getValues(true)));
			} else {
				copy.put(entrySet.getKey(), entrySet.getValue());
			}
		}

		return copy;
	}

	public static TableLoader getInstance() {
		return INSTANCE;
	}

}
