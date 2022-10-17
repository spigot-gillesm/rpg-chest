package com.gilles_m.rpg_chest.container.instance;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.bukkit.block.BlockFace;

import java.io.IOException;

public class InstanceSerialization {

	private InstanceSerialization() { }

	public static class BlockFaceSerializer extends JsonSerializer<BlockFace> {

		@Override
		public void serialize(final BlockFace blockFace, final JsonGenerator jsonGenerator,
							  final SerializerProvider serializers) throws IOException {
			jsonGenerator.writeObject(blockFace.name());
		}
	}

	public static class BlockFaceDeserializer extends StdDeserializer<BlockFace> {

		public BlockFaceDeserializer(final Class<?> vc) {
			super(vc);
		}

		public BlockFaceDeserializer() {
			this(null);
		}

		@Override
		public BlockFace deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
			final var data = parser.getText();

			try {
				return BlockFace.valueOf(data);
			} catch (final IllegalArgumentException exception) {
				Formatter.error(String.format("Invalid block face: %s", data));
			}

			return null;
		}

	}

}
