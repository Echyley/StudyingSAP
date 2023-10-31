/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.CPSConflictGroupHandler;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSDomainOnDemandStrategy;
import de.hybris.platform.sap.productconfig.runtime.cps.CharonFacade;
import de.hybris.platform.sap.productconfig.runtime.cps.masterdata.service.ConfigurationMasterDataService;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.populator.impl.MasterDataContext;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.CsticGroup;
import de.hybris.platform.sap.productconfig.runtime.interf.impl.CsticGroupImpl;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.services.UniqueKeyGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;


/**
 * Communication to cloud engine
 */
public class CPSDomainOnDemandStrategyImpl implements CPSDomainOnDemandStrategy
{
	private static final Logger LOG = Logger.getLogger(CPSDomainOnDemandStrategyImpl.class);
	private UniqueKeyGenerator keyGenerator;
	private CPSConflictGroupHandler conflictGroupHandler;
	private ConfigurationMasterDataService masterDataService;
	private CharonFacade charonFacade;
	private ContextualConverter<CPSItem, InstanceModel, MasterDataContext> itemModelConverter;

	public void setKeyGenerator(final UniqueKeyGenerator keyGenerator)
	{
		this.keyGenerator = keyGenerator;
	}

	protected UniqueKeyGenerator getKeyGenerator()
	{
		return this.keyGenerator;
	}

	public void setConflictGroupHandler(final CPSConflictGroupHandler conflictGroupHandler)
	{
		this.conflictGroupHandler = conflictGroupHandler;
	}

	protected CPSConflictGroupHandler getConflictGroupHandler()
	{
		return conflictGroupHandler;
	}

	public void setMasterDataService(final ConfigurationMasterDataService masterDataService)
	{
		this.masterDataService = masterDataService;
	}

	protected ConfigurationMasterDataService getMasterDataService()
	{
		return masterDataService;
	}

	public void setCharonFacade(final CharonFacade charonFacade)
	{
		this.charonFacade = charonFacade;
	}

	protected CharonFacade getCharonFacade()
	{
		return charonFacade;
	}

	public void setItemModelConverter(final ContextualConverter<CPSItem, InstanceModel, MasterDataContext> instanceModelConverter)
	{
		this.itemModelConverter = instanceModelConverter;
	}

	protected ContextualConverter<CPSItem, InstanceModel, MasterDataContext> getItemModelConverter()
	{
		return itemModelConverter;
	}


	@Override
	public void processRequiredGroups(final ConfigModel configModel, final String currentGroup) throws ConfigurationEngineException
	{
		final String groupToBeRequested = currentGroup != null ? currentGroup : determineFirstGroup(configModel);
		final String groupToBeRequestedName = getKeyGenerator().retrieveGroupName(groupToBeRequested);
		final String requestedInstanceId = getKeyGenerator().retrieveInstanceId(groupToBeRequested);

		final Map<String, List<String>> conflictingGroups = getConflictGroupHandler().retrieveNotCachedUniqueGroupIds(configModel);

		var currentGroupProcessed = false;
		for (final var entry : conflictingGroups.entrySet())
		{
			final String instanceId = entry.getKey();
			final List<String> groupNames = entry.getValue();
			final List<String> updatedGroupNames = new ArrayList<>(groupNames);
			if (instanceId.equals(requestedInstanceId))
			{
				//in this case we can be sure that the current group is covered
				currentGroupProcessed = true;
				if (!groupNames.contains(groupToBeRequestedName))
				{
					updatedGroupNames.add(groupToBeRequestedName);
				}
			}
			enrichModelWithGroupForInstance(configModel, instanceId, updatedGroupNames);
		}
		if (!currentGroupProcessed)
		{
			enrichModelWithGroup(configModel, currentGroup);
		}
	}

	@Override
	public String determineFirstGroup(final ConfigModel configModel)
	{
		Preconditions.checkNotNull(configModel, "Model must not be null");
		final InstanceModel rootInstance = configModel.getRootInstance();
		Preconditions.checkNotNull(rootInstance, "Model must carry a root instance");
		final Pair<CsticGroupModel, InstanceModel> firstGroupAndInstance = this.findFirstGroupWithVisibleCstic(rootInstance);
		Preconditions.checkState(firstGroupAndInstance != null,
				"Model must contain at least one group with visible characteristics.");

		final CsticGroup group = new CsticGroupImpl();
		final String groupName = firstGroupAndInstance.getLeft().getName();

		group.setName(groupName);
		return getKeyGenerator().generateGroupIdForGroup(firstGroupAndInstance.getRight(), group);
	}

	@Override
	public void enrichModelWithGroupForInstance(final ConfigModel configModel, final String instanceId,
			final List<String> groupNames) throws ConfigurationEngineException
	{
		final var ctxt = new MasterDataContext();
		ctxt.setKbCacheContainer(getMasterDataService().getMasterData(configModel.getKbId()));

		final CPSItem item = getCharonFacade().getItemWithGroupDetails(configModel.getId(), instanceId, groupNames);
		mergeGroupDetails(configModel, getItemModelConverter().convertWithContext(item, ctxt), instanceId, groupNames);

		groupNames.stream().forEach(groupId -> configModel.getGroupsReadCompletely()
				.add(getConflictGroupHandler().generateUniqueGroupName(instanceId, groupId, configModel)));

		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("We have read details for instance %s groups %s", instanceId, groupNames.toString()));
		}
	}


	protected void enrichModelWithGroup(final ConfigModel configModel, final String currentGroup)
			throws ConfigurationEngineException
	{
		final String groupToBeRequested = currentGroup != null ? currentGroup : determineFirstGroup(configModel);
		final String instanceId = getKeyGenerator().retrieveInstanceId(groupToBeRequested);
		enrichModelWithGroupForInstance(configModel, instanceId,
				Arrays.asList(getKeyGenerator().retrieveGroupName(groupToBeRequested)));
	}

	protected Pair<CsticGroupModel, InstanceModel> findFirstGroupWithVisibleCstic(final InstanceModel instance)
	{
		final Optional<CsticGroupModel> result = instance.getCsticGroups().stream()
				.filter(group -> hasGroupVisibleCstic(instance, group)).findFirst();
		if (result.isPresent())
		{
			return Pair.of(result.get(), instance);
		}
		for (final InstanceModel currentInstance : instance.getSubInstances())
		{
			final Pair<CsticGroupModel, InstanceModel> groupAndInstance = findFirstGroupWithVisibleCstic(currentInstance);
			if (groupAndInstance != null)
			{
				return groupAndInstance;
			}
		}
		return null;
	}

	protected boolean hasGroupVisibleCstic(final InstanceModel instance, final CsticGroupModel group)
	{
		return group.getCsticNames().stream().map(name -> instance.getCstic(name)).anyMatch(CsticModel::isVisible);
	}


	protected void mergeGroupDetails(final ConfigModel configModel, final InstanceModel enrichedInstance, final String instId,
			final List<String> groupNames)
	{
		groupNames.forEach(groupName -> mergeGroupDetails(configModel, enrichedInstance, instId, groupName));
	}

	protected void mergeGroupDetails(final ConfigModel configModel, final InstanceModel enrichedInstance, final String instId,
			final String groupName)
	{
		final Optional<CsticGroupModel> groupInSource = findGroup(configModel.getRootInstance(), instId, groupName);
		if (groupInSource.isPresent())
		{
			final List<String> csticNames = groupInSource.get().getCsticNames();
			mergeDetailsForCstics(configModel, enrichedInstance, instId, csticNames);
		}
		else
		{
			throw new IllegalStateException(
					String.format("Group was not found for instance %s and group name %s", instId, groupName));
		}
	}

	protected void mergeDetailsForCstics(final ConfigModel configModel, final InstanceModel enrichedInstance, final String instId,
			final List<String> csticNames)
	{
		final Optional<InstanceModel> instanceModel = findInstance(configModel.getRootInstance(), instId);
		final Optional<InstanceModel> instanceModelEnriched = findInstance(enrichedInstance, instId);
		if (instanceModel.isPresent() && instanceModelEnriched.isPresent())
		{
			mergeDomainValues(csticNames, instanceModel.get(), instanceModelEnriched.get());
		}
		else
		{
			throw new IllegalStateException(String.format("Instance was not found for instance %s", instId));
		}
	}

	protected void mergeDomainValues(final List<String> csticNames, final InstanceModel instanceModel,
			final InstanceModel instanceModelEnriched)
	{
		Preconditions.checkNotNull(csticNames, "We expect a list of characteristic names");
		instanceModelEnriched.getCstics().stream().filter(cstic -> csticNames.contains(cstic.getName()))
				.forEach(cstic -> mergeDomainValues(instanceModel, cstic));
	}

	protected void mergeDomainValues(final InstanceModel instanceModel, final CsticModel cstic)
	{
		final CsticModel targetCstic = instanceModel.getCstic(cstic.getName());
		targetCstic.setAssignableValues(cstic.getAssignableValues());
		targetCstic.setAllowsAdditionalValues(cstic.isAllowsAdditionalValues());
		targetCstic.setConstrained(cstic.isConstrained());
		targetCstic.setIntervalInDomain(cstic.isIntervalInDomain());
	}

	protected Optional<InstanceModel> findInstance(final InstanceModel instance, final String instId)
	{
		Preconditions.checkNotNull(instance, "Instance must be provided");
		if (instance.getId().equals(instId))
		{
			return Optional.of(instance);
		}
		else if (!CollectionUtils.isEmpty(instance.getSubInstances()))
		{
			return instance.getSubInstances().stream().map(subInstance -> findInstance(subInstance, instId))
					.filter(Optional::isPresent).findFirst().map(Optional::get);
		}
		return Optional.empty();
	}

	protected Optional<CsticGroupModel> findGroup(final InstanceModel instance, final String instId, final String groupName)
	{
		Preconditions.checkNotNull(instance, "Instance must be provided");
		if (instance.getId().equals(instId))
		{
			//never null, that is ensured in instance
			final List<CsticGroupModel> csticGroups = instance.getCsticGroups();
			return csticGroups.stream().filter(group -> group.getName().equals(groupName)).findFirst();
		}
		else
		{
			return instance.getSubInstances().stream().map(subInstance -> findGroup(subInstance, instId, groupName))
					.filter(Optional::isPresent).findFirst().map(Optional::get);
		}
	}


}
