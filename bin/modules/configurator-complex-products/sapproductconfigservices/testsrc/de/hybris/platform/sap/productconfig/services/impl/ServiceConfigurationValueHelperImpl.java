/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.services.impl;

import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticGroupModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;


public class ServiceConfigurationValueHelperImpl
{
	public CsticModel getCstic(final ConfigModel configModel, final String csticName)
	{
		final InstanceModel rootInstance = configModel.getRootInstance();
		Optional<CsticModel> cstic = getCsticModel(csticName, rootInstance.getCstics());

		final Iterator<InstanceModel> instanceItr = rootInstance.getSubInstances().iterator();
		while (instanceItr.hasNext() && cstic.isEmpty())
		{
			cstic = getCsticModel(csticName, instanceItr.next().getCstics());
		}
		return cstic.orElse(null);
	}

	public CsticModel getCstic(final ConfigModel configModel, final String instanceId, final String csticName)
	{
		final InstanceModel rootInstance = configModel.getRootInstance();
		Optional<CsticModel> cstic = !instanceId.equalsIgnoreCase(rootInstance.getId()) ? Optional.empty()
				: getCsticModel(csticName, rootInstance.getCstics());

		final Iterator<InstanceModel> instanceItr = rootInstance.getSubInstances().iterator();
		while (instanceItr.hasNext() && cstic.isEmpty())
		{
			final InstanceModel subInstance = instanceItr.next();
			if (instanceId.equalsIgnoreCase(subInstance.getId()))
			{
				cstic = getCsticModel(csticName, subInstance.getCstics());
			}
		}
		return cstic.orElse(null);
	}

	protected Optional<CsticModel> getCsticModel(final String csticValueName, final List<CsticModel> cstics)
	{
		return cstics.stream().filter(cstic -> csticValueName.equalsIgnoreCase(cstic.getName())).findFirst();
	}

	public CsticValueModel getCsticValue(final ConfigModel configModel, final String csticName, final String csticValueName)
	{
		final CsticModel cstic = getCstic(configModel, csticName);
		return getAssignableValue(cstic.getAssignableValues(), csticValueName);
	}

	public boolean isValueAssigned(final CsticModel cstic, final String name)
	{
		return cstic.getAssignedValues().stream().filter(value -> name.equalsIgnoreCase(value.getName())).findAny().isPresent();
	}

	public void setSingleCsticValue(final ConfigModel configModel, final String csticName, final String csticValueName)
	{
		final CsticModel cstic = getCstic(configModel, csticName);
		cstic.setSingleValue(csticValueName);
	}

	public void addCsticValue(final ConfigModel configModel, final String csticName, final String csticValueName)
	{
		final CsticModel cstic = getCstic(configModel, csticName);
		cstic.addValue(csticValueName);
	}

	protected CsticValueModel getAssignableValue(final List<CsticValueModel> assignableValues, final String value)
	{
		if (CollectionUtils.isNotEmpty(assignableValues))
		{
			for (final CsticValueModel assignableValue : assignableValues)
			{
				if (value.equalsIgnoreCase(assignableValue.getName()))
				{
					return assignableValue;
				}
			}
		}

		return null;
	}

	public void selectUnselectCsticValue(final ConfigModel configModel, final String csticName, final String csticValueName,
			final boolean selected)
	{
		final CsticModel cstic = getCstic(configModel, csticName);
		if (selected)
		{
			cstic.addValue(csticValueName);
		}
		else
		{
			cstic.removeValue(csticValueName);
		}
	}

	public CsticGroupModel getGroup(final ConfigModel configModel, final String csticName)
	{
		final InstanceModel rootInstance = configModel.getRootInstance();
		Optional<CsticGroupModel> group = getGroupModel(rootInstance, csticName);

		final Iterator<InstanceModel> instanceItr = rootInstance.getSubInstances().iterator();
		while (instanceItr.hasNext() && group.isEmpty())
		{
			group = getGroupModel(instanceItr.next(), csticName);
		}
		return group.orElse(null);
	}

	protected Optional<CsticGroupModel> getGroupModel(final InstanceModel instance, final String csticName)
	{
		return instance.getCsticGroups().stream().filter(group -> group.getCsticNames().contains(csticName)).findFirst();
	}

}
