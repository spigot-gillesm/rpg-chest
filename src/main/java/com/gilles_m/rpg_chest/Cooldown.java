package com.gilles_m.rpg_chest;

import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import com.gilles_m.rpg_chest.event.ContainerOffCooldownEvent;
import com.google.common.base.MoreObjects;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class Cooldown {

	private double duration = 0;

	private long startedAt = 0;

	public void start(@NotNull final ContainerInstance containerInstance, final double duration) {
		this.duration = duration;
		this.startedAt = System.currentTimeMillis();

		Bukkit.getServer().getScheduler()
				.runTaskLater(RPGChest.getInstance(),
						() -> Bukkit.getServer()
								.getPluginManager()
								.callEvent(new ContainerOffCooldownEvent(containerInstance)),
						(long) (duration * 20));
	}

	/**
	 * @return the remaining time in seconds
	 */
	public double remainingTime() {
		if(startedAt <= 0) {
			return 0;
		}
		final var remainingTime = BigDecimal.valueOf(duration - (System.currentTimeMillis()  - startedAt) / 1000.0)
				.doubleValue();

		if(remainingTime < 0) {
			return 0;
		}

		return remainingTime;
	}

	public final String toString() {
		return MoreObjects.toStringHelper(this)
				.add("duration", duration)
				.add("startedAt", startedAt)
				.add("remainingTime", remainingTime())
				.toString();
	}

}
