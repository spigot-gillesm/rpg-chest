package com.gilles_m.rpg_chest.item_table;

import com.gilles_m.rpg_chest.randomized_entity.RandomizedEntity;
import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.google.common.base.MoreObjects;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

public class ItemSection extends RandomizedEntity {

	@Getter(AccessLevel.PACKAGE)
	private final RangeInteger amount;

	private final ItemStack item;

	ItemSection(final ItemStack itemStack, final RangeInteger amount, final double chance) {
		super(chance);

		this.item = itemStack;
		this.amount = amount;
	}

	ItemStack getItem() {
		final var copy = item.clone();
		copy.setAmount(amount.getInt());

		return copy;
	}

	public final String toString() {
		return MoreObjects.toStringHelper(this)
				.add("amount", amount)
				.add("chance", chance)
				.add("item", item)
				.toString();
	}

}
