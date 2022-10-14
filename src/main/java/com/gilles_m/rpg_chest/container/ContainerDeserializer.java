package com.gilles_m.rpg_chest.container;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.Material;

import java.io.IOException;

public class ContainerDeserializer {

	private ContainerDeserializer() { }

	public static class MaterialDeserializer extends StdDeserializer<Material> {

		public MaterialDeserializer(final Class<?> vc) {
			super(vc);
		}

		public MaterialDeserializer() {
			this(null);
		}

		@Override
		public Material deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
			final var data = parser.getText();

			try {
				final var material = Material.valueOf(data.toUpperCase());

				if(!isMaterialValid(material)) {
					throw new IllegalArgumentException("The material must be a container");
				}

				return material;
			} catch (final IllegalArgumentException exception) {
				Formatter.error(String.format("Invalid material: %s", data));
			}

			return null;
		}

		private boolean isMaterialValid(final Material material) {
			return material == Material.CHEST || material == Material.BARREL || material == Material.TRAPPED_CHEST;
		}

	}

}
