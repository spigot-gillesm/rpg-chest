package com.gilles_m.rpg_chest.container;

import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import com.gilles_m.rpg_chest.container.instance.instance_implementation.SimpleInstance;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public enum ContainerType {

	NORMAL(SimpleInstance.class);

	private final Class<? extends ContainerInstance> instanceClass;

	ContainerType(final Class<? extends ContainerInstance> instanceClass) {
		this.instanceClass = instanceClass;
	}

	public ContainerInstance newInstance(@NotNull final Container container, @NotNull final Location location)
			throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
		return instanceClass.getDeclaredConstructor(Container.class, Location.class).newInstance();
	}

}
