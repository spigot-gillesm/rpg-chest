package com.gilles_m.rpg_chest.item_table;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.spigot_gillesm.format_lib.Formatter;

import java.io.IOException;

public class ItemTableDeserializer extends StdDeserializer<String> {

	public ItemTableDeserializer(final Class<?> vc) {
		super(vc);
	}

	public ItemTableDeserializer() {
		this(null);
	}

	@Override
	public String deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
		final var data = parser.getText();

		if(TableManager.getInstance().getItemTable(data).isEmpty()) {
			Formatter.error(String.format("Unknown item table: %s", data));
		}

		return data;
	}

}
