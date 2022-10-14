package com.gilles_m.rpg_chest.item_table;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.gilles_m.rpg_chest.randomized_entity.RangeInteger;

import java.io.IOException;

public class ItemTableDeserializer {

	private ItemTableDeserializer() { }

	public static class RangeIntegerDeserializer extends StdDeserializer<RangeInteger> {

		public RangeIntegerDeserializer(final Class<?> vc) {
			super(vc);
		}

		public RangeIntegerDeserializer() {
			this(null);
		}

		@Override
		public RangeInteger deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
			return RangeInteger.fromString(parser.getText());
		}

	}

}
