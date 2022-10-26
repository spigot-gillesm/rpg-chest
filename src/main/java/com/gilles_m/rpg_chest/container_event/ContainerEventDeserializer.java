package com.gilles_m.rpg_chest.container_event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.github.spigot_gillesm.format_lib.Formatter;

import java.io.IOException;

public class ContainerEventDeserializer {

	private ContainerEventDeserializer() { }

	public static class TriggerDeserializer extends StdDeserializer<ContainerEvent.Trigger> {

		public TriggerDeserializer(final Class<?> vc) {
			super(vc);
		}

		public TriggerDeserializer() {
			this(null);
		}

		@Override
		public ContainerEvent.Trigger deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
			try {
				return ContainerEvent.Trigger.valueOf(parser.getText().toUpperCase());
			} catch (final IllegalArgumentException exception) {
				Formatter.error(String.format("Invalid event trigger: %s", parser.getText()));
			}

			return null;
		}

	}

}
