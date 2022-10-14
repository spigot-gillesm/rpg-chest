package com.gilles_m.rpg_chest.container.instance;

public class InstanceLoader {

	private static final InstanceLoader INSTANCE = new InstanceLoader();

	private InstanceLoader() { }

	public void load() {

	}

	public static InstanceLoader getInstance() {
		return INSTANCE;
	}

}
