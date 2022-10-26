package com.gilles_m.rpg_chest.util;

import com.gilles_m.rpg_chest.RPGChest;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@UtilityClass
public class ServerUtil {

	public Optional<Player> getOnlinePlayer(@NotNull final String playerId) {
		final var player = RPGChest.getInstance().getServer().getPlayer(playerId);

		return player != null ? Optional.of(player) : Optional.empty();
	}

	public Optional<Location> getLocation(@NotNull final String world, @NotNull final String x, @NotNull final String y,
										  @NotNull final String z) {
		final var bukkitWorld = RPGChest.getInstance().getServer().getWorld(world);

		if(bukkitWorld == null) {
			return Optional.empty();
		}

		try {
			return Optional.of(new Location(bukkitWorld, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)));
		} catch (final NumberFormatException exception) {
			return Optional.empty();
		}
	}

}
