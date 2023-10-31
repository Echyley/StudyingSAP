/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CPSConflictGroupHandler;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticGroupImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.UniqueKeyGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Default implementation of {@link CPSConflictGroupHandler}
 */
public class CPSConflictGroupHandlerImpl implements CPSConflictGroupHandler
{

	private UniqueKeyGenerator keyGenerator;

	@Override
	public Map<String, List<String>> retrieveNotCachedUniqueGroupIds(final ConfigModel configModel)
	{
		final Map<String, List<String>> conflictGroups = new HashMap<>();
		configModel.getSolvableConflicts().forEach(conflict -> conflict.getConflictingAssumptions()
				.forEach(assumption -> processConflictingAssumption(assumption, configModel, conflictGroups)));
		return conflictGroups;
	}

	protected void processConflictingAssumption(final ConflictingAssumptionModel conflictingAssumption,
			final ConfigModel configModel, final Map<String, List<String>> conflictGroups)
	{
		final var instanceModel = findInstance(conflictingAssumption.getInstanceId(), configModel.getRootInstance());
		final List<String> involvedGroups = determineGroupsInvolvedInAssumption(instanceModel,
				conflictingAssumption.getCsticName());
		involvedGroups.stream().forEach(groupName -> addGroupToResultMap(groupName, instanceModel, configModel, conflictGroups));
	}

	protected InstanceModel findInstance(final String instanceId, final InstanceModel instanceModel)
	{
		if (instanceModel.getId().equals(instanceId))
		{
			return instanceModel;
		}
		for (final InstanceModel subinstance : instanceModel.getSubInstances())
		{
			final InstanceModel foundInstance = findInstance(instanceId, subinstance);
			if (foundInstance != null)
			{
				return foundInstance;
			}
		}
		return null;
	}

	protected List<String> determineGroupsInvolvedInAssumption(final InstanceModel instanceModel, final String csticName)
	{
		return instanceModel.getCsticGroups().stream().filter(group -> group.getCsticNames().contains(csticName))
				.map(CsticGroupModel::getName).collect(Collectors.toList());
	}

	protected void addGroupToResultMap(final String groupName, final InstanceModel instanceModel, final ConfigModel configModel,
			final Map<String, List<String>> conflictGroups)
	{
		// Get group Unique Id
		final CsticGroup group = new CsticGroupImpl();
		group.setName(groupName);
		final String uniqueGroupId = getKeyGenerator().generateGroupIdForGroup(instanceModel, group);
		// Verify if Group already loaded into the cache
		if (!configModel.getGroupsReadCompletely().contains(uniqueGroupId))
		{
			// If not, add to the list
			List<String> groupListForInstance = conflictGroups.get(instanceModel.getId());
			if (groupListForInstance == null)
			{
				groupListForInstance = new ArrayList<>();
				conflictGroups.put(instanceModel.getId(), groupListForInstance);
			}
			groupListForInstance.add(groupName);
		}
	}

	@Override
	public String generateUniqueGroupName(final String instanceId, final String groupName, final ConfigModel configModel)
	{
		final var instanceModel = findInstance(instanceId, configModel.getRootInstance());
		final CsticGroup group = new CsticGroupImpl();
		group.setName(groupName);
		return getKeyGenerator().generateGroupIdForGroup(instanceModel, group);
	}

	protected UniqueKeyGenerator getKeyGenerator()
	{
		return this.keyGenerator;
	}

	public void setKeyGenerator(final UniqueKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

}
