/*
 *  Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.util.impex;

import de.hybris.platform.core.Registry;
import de.hybris.platform.core.model.security.UserRightModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.security.permissions.PermissionManagementService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.junit.rules.ExternalResource;
import org.slf4j.Logger;

/**
 * All essential data present in a module
 */
public abstract class ModuleEssentialData extends ExternalResource
{
	private static final Logger LOG = Log.getLogger(ModuleEssentialData.class);
	private static PermissionManagementService permissionManager;

	private final List<EssentialDataFile> dataFiles;
	private final List<ModuleEssentialData> dependencies;
	private final Set<UserRightModel> preExistingUserRights;
	private boolean loaded;
	private final Set<String> integrationObjectCodes = new HashSet<>();

	protected ModuleEssentialData(final List<EssentialDataFile> files)
	{
		this(files, Collections.emptySet());
	}

	protected ModuleEssentialData(final List<EssentialDataFile> files, final Set<String> ioCodes)
	{
		dataFiles = files;
		dependencies = new LinkedList<>();
		preExistingUserRights = new HashSet<>();
		loaded = false;
		integrationObjectCodes.addAll(ioCodes);
	}

	/**
	 * Adds other module essential data, this module depends on.
	 */
	protected void enableDependencies()
	{
		if (dependencies.isEmpty())
		{
			dependencies.addAll(getDependencies());
			dependencies.forEach(ModuleEssentialData::enableDependencies);
		}
	}

	/**
	 * Retrieves other modules this essential data module depends on.
	 *
	 * @return a list of other modules this essential data depends on or an empty list, if this module
	 * does not depend on other modules.
	 */
	@Nonnull
	protected abstract List<ModuleEssentialData> getDependencies();

	/**
	 * Retrieves integration objects loaded by this module essential data.
	 * If dependencies are loaded, integration objects from them also.
	 *
	 * @return codes of all integration objects
	 */
	public Set<String> getAllIntegrationObjectCodes()
	{
		return Stream.of(integrationObjectCodes, getIntegrationObjectCodesFromDependencies())
		             .flatMap(Collection::stream)
		             .collect(Collectors.toSet());
	}

	private Set<String> getIntegrationObjectCodesFromDependencies()
	{
		return dependencies.stream()
		                   .map(ModuleEssentialData::getAllIntegrationObjectCodes)
		                   .flatMap(Collection::stream)
		                   .collect(Collectors.toSet());
	}

	/**
	 * Retrieves integration object items loaded by this module essential data.
	 * If dependencies are loaded, integration object items from them also.
	 *
	 * @return codes of all integration object items
	 */
	public Set<String> getAllIntegrationObjectItemCodes()
	{
		return getAllIntegrationObjects().stream()
		                                 .map(IntegrationObjectModel::getItems)
		                                 .flatMap(Collection::stream)
		                                 .map(IntegrationObjectItemModel::getCode)
		                                 .collect(Collectors.toSet());
	}

	private Collection<IntegrationObjectModel> getAllIntegrationObjects()
	{
		return getAllIntegrationObjectCodes().stream()
		                                     .map(io -> IntegrationTestUtil.findAll(IntegrationObjectModel.class,
				                                     m -> io.equals(m.getCode())))
		                                     .flatMap(Collection::stream)
		                                     .collect(Collectors.toSet());
	}

	@Override
	protected void before()
	{
		load();
	}

	@Override
	protected void after()
	{
		cleanup();
	}

	/**
	 * Loads all essential data in this module.
	 */
	public void load()
	{
		if (!loaded)
		{
			LOG.debug("Loading {}", toString());
			preExistingUserRights.addAll(IntegrationTestUtil.findAll(UserRightModel.class));
			dependencies.forEach(ModuleEssentialData::load);
			dataFiles.forEach(this::loadFileData);
			loaded = true;
		}
	}

	private void loadFileData(final EssentialDataFile file)
	{
		LOG.info("Loading essential data in {}", file);
		file.loadData();
	}

	/**
	 * Registers Integration Object codes with the given registry
	 *
	 * @param registry Registry to add the integration object codes
	 * @param ioCodes  Integration Object codes
	 * @return Set of Integration Object codes
	 */
	protected static Set<String> registerIntegrationObjects(final Collection<String> registry, final String... ioCodes)
	{
		final Set<String> ioCodeSet = Set.of(ioCodes);
		registry.addAll(ioCodeSet);
		return ioCodeSet;
	}

	/**
	 * Deletes all essential data present in this module from the system.
	 */
	public void cleanup()
	{
		if (loaded)
		{
			LOG.debug("Cleaning {} data", toString());
			dataFiles.forEach(this::cleanFileData);
			dependencies.forEach(ModuleEssentialData::cleanup);
			dependencies.clear();
			cleanUserRights();
			loaded = false;
		}
	}

	private void cleanFileData(final EssentialDataFile file)
	{
		LOG.info("Deleting data created in {}", file);
		file.cleanData();
	}

	private void cleanUserRights()
	{
		final var createdUserRights = IntegrationTestUtil.findAll(UserRightModel.class, m -> !preExistingUserRights.contains(m));
		IntegrationTestUtil.removeAll(createdUserRights);
		LOG.debug("Deleted user right permissions: {}", createdUserRights);
		preExistingUserRights.clear();
	}

	/**
	 * Deletes all inbound channel configurations associated with the specified integration objects.
	 *
	 * @param ioCodes codes of integration objects, for which inbound channel configuration should be deleted.
	 */
	protected static void deleteInboundChannelConfigurations(final Collection<String> ioCodes)
	{
		LOG.debug("Deleting inbound channel configurations: {}", ioCodes);
		final String headerLine = "REMOVE InboundChannelConfiguration; integrationObject(code)[unique = true]";
		final String impex = ioCodes.stream()
		                            .map(code -> "                                  ; " + code)
		                            .reduce(headerLine, (p, n) -> p + "\n" + n);
		try
		{
			IntegrationTestUtil.importCsv(impex);
		}
		catch (final ImpExException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deletes the specified integration objects.
	 *
	 * @param ioCodes code of the integration objects to be deleted.
	 */
	protected static void deleteIntegrationObjects(final Collection<String> ioCodes)
	{
		LOG.debug("Deleting integration objects: {}", ioCodes);
		IntegrationTestUtil.remove(IntegrationObjectModel.class, item -> ioCodes.contains(item.getCode()));
	}

	/**
	 * Removes all access right permisions granted to the specified types.
	 *
	 * @param typeCodes a collection of {code ComposedType} codes, for which permissions need to be cleared.
	 */
	protected static void clearAccessRightsForTypes(final Collection<String> typeCodes)
	{
		typeCodes.stream()
		         .map(ModuleEssentialData::toComposedType)
		         .filter(Objects::nonNull)
		         .forEach(ModuleEssentialData::cleanTypePermissions);
	}

	private static ComposedTypeModel toComposedType(final String code)
	{
		return IntegrationTestUtil.findAny(ComposedTypeModel.class, m -> m.getCode().equals(code))
		                          .orElse(null);
	}

	private static void cleanTypePermissions(final ComposedTypeModel type)
	{
		getPermissionManager().clearTypePermissions(type);
	}

	private static PermissionManagementService getPermissionManager()
	{
		if (permissionManager == null)
		{
			permissionManager = Registry.getApplicationContext()
			                            .getBean("permissionManagementService", PermissionManagementService.class);
		}
		return permissionManager;
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName();
	}
}
