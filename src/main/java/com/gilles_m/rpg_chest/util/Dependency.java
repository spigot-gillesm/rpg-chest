package com.gilles_m.rpg_chest.util;

import com.github.spigot_gillesm.format_lib.Formatter;
import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.utils.serialize.Position;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class Dependency {

	private static final Dependency INSTANCE = new Dependency();

	private MMOItems mmoItems;

	private MythicPlugin mythicMobs;

	private Dependency() { }

	public void load() {
		Formatter.info("Loading dependencies...");
		loadMmoItems();
		loadMythicMobs();
		Formatter.info("&aDone!");
	}

	//Method used to avoid null checks everywhere in the class
	private <T> Optional<T> retrieveFromAPI(final Supplier<Optional<T>> supplier) {
		try {
			return supplier.get();
		} catch (final NullPointerException exception) {
			return Optional.empty();
		}
	}

	public Optional<ItemStack> getMmoItem(@NotNull final String id) {
		return retrieveFromAPI(() -> {
			for(final var type : mmoItems.getTypes().getAll()) {
				final MMOItem mmoItem = mmoItems.getMMOItem(type, id);

				if(mmoItem != null) {
					final ItemStack itemStack = mmoItem.newBuilder().build();

					if(itemStack != null) {
						return Optional.of(itemStack);
					}
				}
			}
			return Optional.empty();
		});
	}

	public Optional<ItemStack> getMythicMobsItem(@NotNull final String id) {
		return retrieveFromAPI(() ->
			mythicMobs.getItemManager()
					.getItem(id)
					.map(mythicItem -> BukkitAdapter.adapt(mythicItem.generateItemStack(1)))
		);
	}

	public Optional<MythicMob> getMythicMob(@NotNull final String id) {
		return retrieveFromAPI(() ->
				mythicMobs.getMobManager()
						.getMythicMob(id)
		);
	}

	public void spawnMythicMob(@NotNull final String id, @NotNull final Location location, final int level, final int amount) {
		getMythicMob(id).ifPresentOrElse(mob -> {
			for(int i = 0; i < amount; i++) {
				mob.spawn(new AbstractLocation(Position.of(location)), level);
			}
		}, () -> Formatter.warning(String.format("Unknown MythicMob id: %s", id)));
	}

	public void spawnMythicMob(@NotNull final String id, @NotNull final Location location, final int amount) {
		spawnMythicMob(id, location, 1, amount);
	}

	public void spawnMythicMob(@NotNull final String id, @NotNull final Location location) {
		spawnMythicMob(id, location, 1, 1);
	}

	public Optional<ItemStack> getItemStackFromString(@NotNull final String data) {
		if(data.toLowerCase().startsWith("mm:") || data.toLowerCase().startsWith("mythicmobs:")) {
			return getMythicMobsItem(data.split(":")[1]);
		} else if(data.toLowerCase().startsWith("mi:") || data.toLowerCase().startsWith("mmoitems:")) {
			return getMmoItem(data.split(":")[1]);
		}

		return Optional.empty();
	}

	private void loadMmoItems() {
		if(Bukkit.getServer().getPluginManager().getPlugin("MMOItems") != null) {
			mmoItems = MMOItems.plugin;
			Formatter.info("&aHooked onto MMOItems.");
		} else {
			Formatter.info("MMOItems not found.");
		}
	}

	private void loadMythicMobs() {
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
			this.mythicMobs = MythicProvider.get();
			Formatter.info("&aHooked onto MythicMobs.");
		} else {
			Formatter.info("MythicMobs not found.");
		}
	}

	public static Dependency getInstance() {
		return INSTANCE;
	}

}
