package com.gilles_m.rpg_chest.world;

import lombok.Getter;

public enum WorldLocation {

	MINESHAFT(LocationAnalyzer.MINESHAFT_ANALYZER),
	DUNGEON(LocationAnalyzer.DUNGEON_ANALYZER),
	STRONGHOLD(LocationAnalyzer.STRONGHOLD_ANALYZER);

	@Getter
	private final LocationAnalyzer locationAnalyzer;

	WorldLocation(final LocationAnalyzer locationAnalyzer) {
		this.locationAnalyzer = locationAnalyzer;
	}

}
