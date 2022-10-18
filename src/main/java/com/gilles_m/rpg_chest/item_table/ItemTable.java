package com.gilles_m.rpg_chest.item_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gilles_m.rpg_chest.randomized_entity.RandomizedEntity;
import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;
import com.google.common.base.MoreObjects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemTable {

	@Setter(AccessLevel.PACKAGE)
	@Getter
	private String id;

	@JsonProperty("amount")
	@JsonDeserialize(using = RangeIntegerDeserializer.class)
	private RangeInteger amount;

	@Getter(AccessLevel.PACKAGE)
	private final List<ItemSection> itemSections = new ArrayList<>();

	public List<ItemStack> generateItems() {
		final var randAmount = amount.getInt();
		var currentAmount = 0;
		var security = 500;
		final List<ItemSection> pickedItems = new ArrayList<>();

		//Pick the items
		while(currentAmount < randAmount && security > 0) {
			final var section = RandomizedEntity.randomRelative(itemSections);

			if(section.isPresent() && !pickedItems.contains(section.get())) {
				pickedItems.add(section.get());
				currentAmount++;
			}
			security--;
		}
		final List<ItemStack> items = new ArrayList<>();
		pickedItems.forEach(item -> items.add(item.getItem()));

		//Add the extra (absolute chance) items
		RandomizedEntity.randomAbsolute(itemSections).forEach(section -> items.add(section.getItem()));

		return items;
	}

	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(this)
				.add("id", id)
				.add("amount", amount)
				.add("itemSections", itemSections)
				.toString();
	}

}
