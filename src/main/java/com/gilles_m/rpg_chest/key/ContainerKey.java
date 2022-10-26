package com.gilles_m.rpg_chest.key;

import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.google.common.base.MoreObjects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ContainerKey {

	@Setter(AccessLevel.PACKAGE)
	@Getter
	private String id;

	@Setter(AccessLevel.PACKAGE)
	@Getter
	private ItemStack itemStack;

	private final Set<Drop> entityDrops = new HashSet<>();

	public ContainerKey(@NotNull final String id, @NotNull final ItemStack itemStack) {
		this.id = id;
		this.itemStack = itemStack;
	}

	public void drop(@NotNull final LivingEntity entity) {
		final var name = entity.getType().toString();

		entityDrops.stream()
				.filter(entityDrop -> entityDrop.source.equals(name))
				.forEach(entityDrop -> entityDrop.drop(entity.getLocation(), itemStack));

		//TODO: MM integrations
	}

	public void addDrop(@NotNull final Drop drop) {
		entityDrops.add(drop);
	}

	public final String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("itemStack", itemStack.toString())
				.toString();
	}

	public static class Drop {

		private final String source;

		private final RangeInteger amount;

		private final double chance;

		private final Random random = new SecureRandom();

		public Drop(@NotNull final String source, @NotNull final RangeInteger amount, final double chance) {
			this.source = source;
			this.amount = amount;
			this.chance = chance;
		}

		private void drop(@NotNull final Location location, @NotNull final ItemStack itemStack) {
			if(random.nextDouble() <= chance) {
				final var clone = itemStack.clone();
				clone.setAmount(amount.getInt());

				location.getWorld().dropItemNaturally(location, clone);
			}
		}

		public final String toString() {
			return MoreObjects.toStringHelper(this)
					.add("source", source)
					.add("amount", amount.toString())
					.add("chance", chance)
					.toString();
		}

	}

}
