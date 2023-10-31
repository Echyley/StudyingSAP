/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.impl;

import de.hybris.platform.core.Registry;
import de.hybris.platform.sap.productconfig.runtime.cps.CPSConfigurationChangeAdapter;
import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConflictingAssumptionModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.SolvableConflictModel;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;


/**
 * Default implementation of the {@link CPSConfigurationChangeAdapter}
 */
public class CPSConfigurationChangeAdapterImpl implements CPSConfigurationChangeAdapter
{

	@Override
	public CPSConfiguration prepareChangedConfiguration(final ConfigModel model)
	{
		final CPSConfiguration changedConfiguration = new CPSConfiguration();
		changedConfiguration.setId(model.getId());
		changedConfiguration.setETag(model.getVersion());
		final CPSItem changedRootItem = processInstance(model.getRootInstance(), model.getSolvableConflicts());
		changedConfiguration.setRootItem(changedRootItem);
		return changedConfiguration;
	}

	protected CPSItem processInstance(final InstanceModel instanceModel, final List<SolvableConflictModel> conflicts)
	{
		final CPSItem changedItem = new CPSItem();
		changedItem.setSubItems(new ArrayList<>());
		changedItem.setId(instanceModel.getId());
		changedItem.setCharacteristics(collectInstanceCsticChanges(instanceModel, conflicts));
		final List<InstanceModel> subInstances = instanceModel.getSubInstances();
		for (final InstanceModel subInstanceModel : subInstances)
		{
			final CPSItem changedSubItem = processInstance(subInstanceModel, conflicts);
			changedItem.getSubItems().add(changedSubItem);
		}
		return changedItem;
	}

	protected String getEmptyValueConstant()
	{
		if (Registry.hasCurrentTenant())
		{
			// productive mode
			return Config.getString("sapproductconfigruntimecps.emptyValueReplacement",
					SapproductconfigruntimecpsConstants.REPLACE_EMPTY_DOMAIN_VALUE);
		}
		else
		{
			// unit test mode
			return SapproductconfigruntimecpsConstants.REPLACE_EMPTY_DOMAIN_VALUE;
		}
	}

	protected List<CPSCharacteristic> collectInstanceCsticChanges(final InstanceModel instanceModel,
			final List<SolvableConflictModel> conflicts)
	{
		final List<CPSCharacteristic> changedCstics = new ArrayList<>();

		final List<CsticModel> csticModels = instanceModel.getCstics();
		for (final CsticModel csticModel : csticModels)
		{
			processCstic(changedCstics, csticModel, conflicts);
		}
		return changedCstics;
	}

	protected void processCstic(final List<CPSCharacteristic> changedCstics, final CsticModel csticModel,
			final List<SolvableConflictModel> conflicts)
	{
		if (csticModel.isChangedByFrontend())
		{
			final CPSCharacteristic changedCstic = new CPSCharacteristic();
			changedCstic.setId(csticModel.getName());
			if (csticModel.isRetractTriggered())
			{
				changedCstic.setValues(collectRetractValue(csticModel));
			}
			else
			{
				changedCstic.setValues(collectChangedValues(csticModel));
			}
			changedCstics.add(changedCstic);
		}
	}

	protected List<CPSValue> collectRetractValue(final CsticModel csticModel)
	{
		final List<CPSValue> changedValues = new ArrayList<>();
		collectRetractValueTriggeredInConfiguration(csticModel, changedValues);
		return changedValues;
	}

	protected void collectRetractValueTriggeredInConfiguration(final CsticModel csticModel, final List<CPSValue> changedValues)
	{
		if (csticModel.isMultivalued())
		{
			final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();
			final List<CsticValueModel> assignableValues = csticModel.getAssignableValues();
			for (final CsticValueModel valueModel : assignableValues)
			{
				if (assignedValues.contains(valueModel))
				{
					addCloudEngineValue(valueModel.getName(), false, changedValues);
				}
			}
		}
		else
		{
			final String value = csticModel.getSingleValue();
			if (StringUtils.isNotBlank(value))
			{
				addCloudEngineValue(value, false, changedValues);
			}
		}
	}

	protected String getRetractValueName(final CsticModel csticModel, final List<SolvableConflictModel> conflicts)
	{
		for (final SolvableConflictModel conflict : conflicts)
		{
			for (final ConflictingAssumptionModel assumption : conflict.getConflictingAssumptions())
			{
				if (csticModel.getInstanceId().equals(assumption.getInstanceId())
						&& csticModel.getName().equals(assumption.getCsticName()))
				{
					return assumption.getValueName();
				}
			}
		}
		throw new IllegalStateException("Retract triggered for cstic that is not part of a conflict: " + csticModel.getName());
	}

	protected List<CPSValue> collectChangedValues(final CsticModel csticModel)
	{
		final List<CPSValue> changedValues = new ArrayList<>();
		final List<CsticValueModel> assignedValues = csticModel.getAssignedValues();
		final List<CsticValueModel> assignableValues = csticModel.getAssignableValues();
		if (csticModel.isMultivalued())
		{
			for (final CsticValueModel valueModel : assignableValues)
			{
				addCloudEngineValue(valueModel.getName(), assignedValues.contains(valueModel), changedValues);
			}
			assignedValues.stream().filter(value -> !assignableValues.contains(value))
					.forEach(value -> addChangedValue(value, changedValues));
		}
		else
		{
			final String value = csticModel.getSingleValue();
			if (value != null && !value.isEmpty())
			{
				addCloudEngineValue(csticModel.getSingleValue(), true, changedValues);
			}
			else
			{
				handleDeselection(changedValues, assignableValues);
			}
		}
		return changedValues;

	}

	protected void addChangedValue(final CsticValueModel value, final List<CPSValue> changedValues)
	{
		final var changedValue = new CPSValue();
		changedValue.setValue(value.getName());
		changedValue.setSelected(true);
		changedValues.add(changedValue);
	}

	protected void handleDeselection(final List<CPSValue> changedValues, final List<CsticValueModel> assignableValues)
	{
		for (final CsticValueModel valueModel : assignableValues)
		{
			if (CsticValueModel.AUTHOR_USER.equals(valueModel.getAuthor()))
			{
				addCloudEngineValue(valueModel.getName(), false, changedValues);
			}
		}
	}

	protected void addCloudEngineValue(final String value, final boolean selected, final List<CPSValue> valuesList)
	{
		if (null != value)
		{
			final CPSValue changedValue = new CPSValue();
			if (value.equals(getEmptyValueConstant()))
			{
				changedValue.setValue("");
			}
			else
			{
				changedValue.setValue(value);
			}
			//do not set characteristicId here as cloud engine is not aware of this attribute
			//its purpose only is to add it as key to be able to fetch master data for it
			changedValue.setSelected(selected);
			valuesList.add(changedValue);
		}
	}

}
