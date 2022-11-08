package com.gilles_m.rpg_chest.container;

import com.gilles_m.rpg_chest.container.instance.ContainerInstance;
import com.gilles_m.rpg_chest.container.instance.InstanceManager;
import com.github.spigot_gillesm.file_utils.FileUtils;
import com.github.spigot_gillesm.format_lib.Formatter;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ContainerManager {

	private static final ContainerManager INSTANCE = new ContainerManager();

	private final Set<Container> registeredContainers = new HashSet<>();

	private ContainerManager() { }

	/**
	 * Register a new container. If the container already exists, it will be replaced.
	 *
	 * @param container the container
	 */
	public void register(@NotNull final Container container) {
		registeredContainers.add(container);
	}

	public Optional<Container> getContainer(final String id) {
		if(id == null) {
			return Optional.empty();
		}
		return registeredContainers.stream().filter(container -> container.getId().equals(id)).findFirst();
	}

	/**
	 * Delete the specified container from the server and the files.
	 *
	 * @param id the container id
	 * @param clearInstances whether to clear its existing instances or not
	 * @return true if the container exists
	 */
	public boolean deleteContainer(@NotNull final String id, final boolean clearInstances) {
		final var container = getContainer(id);

		if(container.isEmpty()) {
			return false;
		} else {
			if(clearInstances) {
				InstanceManager.getInstance().getContainerInstances(container.get())
						.forEach(ContainerInstance::destroy);
			}
			registeredContainers.remove(container.get());

			if(!deleteContainerFile(id)) {
				Formatter.error(String.format("Could not delete %s configuration file", id));
			}

			return true;
		}
	}

	private boolean deleteContainerFile(final String id) {
		return FileUtils.getResource("containers/" + id + ".yml").delete();
	}

	/**
	 * Delete the specified container from the server and the files and clear its existing instances.
	 *
	 * @param id the container id
	 * @return true if the container exists
	 */
	public boolean deleteContainer(@NotNull final String id) {
		return deleteContainer(id, true);
	}

	public int size() {
		return registeredContainers.size();
	}

	public void clear() {
		registeredContainers.clear();
	}

	public boolean isContainer(@NotNull final String id) {
		return getContainer(id).isPresent();
	}

	public static ContainerManager getInstance() {
		return INSTANCE;
	}

}
