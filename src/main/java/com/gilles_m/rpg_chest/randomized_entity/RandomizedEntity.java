package com.gilles_m.rpg_chest.randomized_entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RandomizedEntity {

	private static final Random random = new SecureRandom();

	@Getter
	@JsonProperty("type")
	protected Type type;

	@Getter
	@JsonProperty("chance")
	protected double chance;

	public RandomizedEntity() {
		//Default constructor for Jackson
	}

	protected RandomizedEntity(final double chance) {
		this.chance = chance;

		if(chance % 1 == 0) {
			this.type = Type.RELATIVE;
		} else {
			this.type = Type.ABSOLUTE;
		}
	}

	public RandomizedEntity(final Builder<? extends Builder<?>> builder) {
		this.type = builder.type;
		this.chance = builder.chance;
	}

	/**
	 * Randomly get an entity based on its relative chance.
	 *
	 * @param randomizedEntities the entities
	 * @param <T> the type
	 * @return a randomly chosen entity
	 */
	public static <T extends RandomizedEntity> Optional<T> randomRelative(final Collection<T> randomizedEntities) {
		final List<T> selectedEntities = new ArrayList<>();
		randomizedEntities.stream()
				.filter(entity -> entity.type == Type.RELATIVE)
				.forEach(entity -> {
					for (var i = 0; i < entity.getChance(); i++) {
						selectedEntities.add(entity);
					}
				});

		if(selectedEntities.isEmpty()) {
			return Optional.empty();
		}
		if(selectedEntities.size() == 1) {
			return Optional.of(selectedEntities.get(0));
		}

		return Optional.of(selectedEntities.get(random.nextInt(selectedEntities.size())));
	}

	/**
	 * Randomly get entities based on its absolute chance (= probability).
	 *
	 * @param randomizedEntities the entities
	 * @param <T> the type
	 * @return a list of randomly chosen entities
	 */
	public static <T extends RandomizedEntity> List<T> randomAbsolute(final Collection<T> randomizedEntities) {
		final List<T> selectedEntities = new ArrayList<>();
		randomizedEntities.stream()
				.filter(entity -> entity.type == Type.ABSOLUTE)
				.forEach(entity -> {
					if(random.nextDouble() <= entity.chance) {
						selectedEntities.add(entity);
					}
				});

		return selectedEntities;
	}

	@NoArgsConstructor
	public abstract static class Builder<T extends Builder<T>> {

		private Type type = Type.RELATIVE;

		private double chance = 1;

		@SuppressWarnings("unchecked")
		protected T thisObject() {
			return (T) this;
		}

		public T type(Type type) {
			this.type = type;
			return thisObject();
		}

		/**
		 * @param chance the chance to be chosen if type is set to RELATIVE or the probability to be chosen if
		 *               the type is set to ABSOLUTE.
		 * @return this builder
		 */
		public T chance(double chance) {
			this.chance = chance;
			return thisObject();
		}

		public abstract RandomizedEntity build() throws IllegalArgumentException;

	}

	public enum Type {

		RELATIVE,
		ABSOLUTE

	}

}
