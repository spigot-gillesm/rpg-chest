package com.gilles_m.rpg_chest.key;

import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import com.github.spigot_gillesm.item_lib.YamlItem;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class KeyLoader {

	private static final KeyLoader INSTANCE = new KeyLoader();

	private KeyLoader() { }

	public void load() {
		final var manager = KeyManager.getInstance();
		manager.clear();
		Formatter.info("Loading keys...");

		final var file = FileUtils.getResource("keys.yml");

		if(file != null && !file.isDirectory()) {
			manager.register(loadKeysFromFile(file));
		}
		Formatter.info(String.format("Loaded %d key(s)", manager.size()));
	}

	private Set<ContainerKey> loadKeysFromFile(@NotNull final File file) {
		final Set<ContainerKey> keys = new HashSet<>();
		final var configuration = FileUtils.getConfiguration(file);

		for(final var keyId : configuration.getKeys(false)) {
			if(configuration.isConfigurationSection(keyId)) {
				loadKeyFromFile(configuration.getConfigurationSection(keyId), keyId).ifPresent(keys::add);
			}
		}

		return keys;
	}

	private Optional<ContainerKey> loadKeyFromFile(@NotNull final ConfigurationSection section, final String keyid) {
		final var itemStack = YamlItem.fromConfiguration(section).getItemFromFile()
				.make()
				.getItemStack();
		final var key = new ContainerKey(keyid, itemStack);

		if(section.isConfigurationSection("drops")) {
			final var dropsSection = section.getConfigurationSection("drops");

			for(final var dropKey : dropsSection.getKeys(false)) {

				final var dropSection = section.getConfigurationSection("drops." + dropKey);
				key.addDrop(
						new ContainerKey.Drop(dropKey,
								RangeInteger.fromString(dropSection.getString("amount", "1")),
								dropSection.getDouble("chance"))
				);
			}
		}

		return Optional.of(key);
	}

	public static KeyLoader getInstance() {
		return INSTANCE;
	}

}
