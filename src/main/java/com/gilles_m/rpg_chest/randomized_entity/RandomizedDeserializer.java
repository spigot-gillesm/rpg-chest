package com.gilles_m.rpg_chest.randomized_entity;

public class RandomizedDeserializer {

	/*private RandomizedDeserializer() { }

	public static class TypeDeserializer extends StdDeserializer<RandomizedEntity.Type> {

		public TypeDeserializer(final Class<?> vc) {
			super(vc);
		}

		public TypeDeserializer() {
			this(null);
		}

		@Override
		public RandomizedEntity.Type deserialize(final JsonParser parser, final DeserializationContext context) throws IOException {
			final var data = parser.getText();

			try {
				return RandomizedEntity.Type.valueOf(data.toUpperCase());
			} catch (final IllegalArgumentException exception) {
				Formatter.error(String.format("Invalid type: %s", data));
			}

			return null;
		}

	}*/

}
