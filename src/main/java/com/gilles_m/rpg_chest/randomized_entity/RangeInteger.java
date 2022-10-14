package com.gilles_m.rpg_chest.randomized_entity;

import com.google.common.base.MoreObjects;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Random;

public class RangeInteger {

	private static final String FROM_STRING_ERROR_MSG = "The given string does not match the expression '%d-%d' or '%d'";

	@Getter
	private final int low;

	@Getter
	private final int high;

	public RangeInteger(final int low, final int high) {
		if(low > high) {
			throw new IllegalArgumentException("The lower bound cannot be greater than the higher bound");
		}
		this.low = low;
		this.high = high;
	}

	public RangeInteger(final int constant) {
		this(constant, constant);
	}

	public int getInt() {
		if(low == high) {
			return low;
		}

		return new Random().nextInt(high - low + 1) + low;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("low", low)
				.add("high", high)
				.toString();
	}

	@Override
	public final int hashCode() {
		return Objects.hash(low, high);
	}

	@Override
	public final boolean equals(Object other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(!(other instanceof RangeInteger)) {
			return false;
		}
		final var otherInteger = (RangeInteger) other;

		return otherInteger.low == low && otherInteger.high == high;
	}

	public static RangeInteger fromString(@NotNull final String string) {
		final String[] rawValues = string.strip().split("-");

		if(rawValues.length == 1) {
			try {
				return new RangeInteger(Integer.parseInt(rawValues[0]));
			} catch (final NumberFormatException exception) {
				throw new IllegalArgumentException(FROM_STRING_ERROR_MSG);
			}
		}
		else if(rawValues.length == 2) {
			try {
				return new RangeInteger(Integer.parseInt(rawValues[0]), Integer.parseInt(rawValues[1]));
			} catch (final NumberFormatException exception) {
				throw new IllegalArgumentException(FROM_STRING_ERROR_MSG);
			}
		} else {
			throw new IllegalArgumentException(FROM_STRING_ERROR_MSG);
		}
	}

	public static RangeInteger fromString(final String string, final int defaultValue) {
		if(string == null) {
			return new RangeInteger(defaultValue, defaultValue);
		}
		try {
			return fromString(string);
		} catch (IllegalArgumentException exception) {
			return new RangeInteger(defaultValue, defaultValue);
		}
	}

}
