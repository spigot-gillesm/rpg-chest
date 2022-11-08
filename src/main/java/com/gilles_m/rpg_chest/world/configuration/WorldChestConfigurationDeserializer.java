package com.gilles_m.rpg_chest.world.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.spigot_gillesm.format_lib.Formatter;

import java.io.IOException;

public class WorldChestConfigurationDeserializer {

	private WorldChestConfigurationDeserializer() { }

	public static class ActionDeserializer extends StdDeserializer<WorldChestConfiguration.Action> {

		public ActionDeserializer(final Class<?> vc) {
			super(vc);
		}

		public ActionDeserializer() {
			this(null);
		}

		@Override
		public WorldChestConfiguration.Action deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
			final var data = parser.getText();

			try {
				return WorldChestConfiguration.Action.valueOf(data.toUpperCase());
			} catch (final IllegalArgumentException exception) {
				Formatter.error(String.format("Invalid action: %s", data));
			}

			return null;
		}

	}

}
