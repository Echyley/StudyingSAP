/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.populator.impl;

import de.hybris.platform.sap.productconfig.runtime.cps.constants.SapproductconfigruntimecpsConstants;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.CPSMasterDataPossibleValueSpecific;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.masterdata.cache.CPSMasterDataCharacteristicSpecificContainer;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSValue;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualConverter;
import de.hybris.platform.sap.productconfig.runtime.interf.ContextualPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.util.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;


/**
 * Responsible to populate characteristics
 */
public class CharacteristicPopulator implements ContextualPopulator<CPSCharacteristic, CsticModel, MasterDataContext>
{
	protected static final String INTERVAL_TYPE_UNCONSTRAINED = "0";
	private ContextualConverter<CPSValue, CsticValueModel, MasterDataContext> valueConverter;
	private ContextualConverter<CPSPossibleValue, CsticValueModel, MasterDataContext> possibleValueConverter;
	private boolean readDomainValuesOnDemand;


	/**
	 * @return the valueConverter
	 */
	public ContextualConverter<CPSValue, CsticValueModel, MasterDataContext> getValueConverter()
	{
		return valueConverter;
	}


	/**
	 * @param valueConverter
	 *           the valueConverter to set
	 */
	public void setValueConverter(final ContextualConverter<CPSValue, CsticValueModel, MasterDataContext> valueConverter)
	{
		this.valueConverter = valueConverter;
	}


	/**
	 * @return the possibleValueConverter
	 */
	public ContextualConverter<CPSPossibleValue, CsticValueModel, MasterDataContext> getPossibleValueConverter()
	{
		return possibleValueConverter;
	}


	/**
	 * @param possibleValueConverter
	 *           the possibleValueConverter to set
	 */
	public void setPossibleValueConverter(
			final ContextualConverter<CPSPossibleValue, CsticValueModel, MasterDataContext> possibleValueConverter)
	{
		this.possibleValueConverter = possibleValueConverter;
	}


	@Override
	public void populate(final CPSCharacteristic source, final CsticModel target, final MasterDataContext ctxt)
	{
		populateCoreAttributes(source, target);
		populateInstanceReference(source, target);

		populateValues(source, target, ctxt);
		populatePossibleValues(source, target, ctxt);

	}

	protected void populateInstanceReference(final CPSCharacteristic source, final CsticModel target)
	{
		final CPSItem parentItem = source.getParentItem();
		if (parentItem == null)
		{
			throw new IllegalStateException("Characteristic does not carry a parent: " + source.getId());
		}
		target.setInstanceName(parentItem.getKey());
		target.setInstanceId(parentItem.getId());

	}

	protected String getEmptyValueConstant()
	{
		return Config.getString("sapproductconfigruntimecps.emptyValueReplacement",
				SapproductconfigruntimecpsConstants.REPLACE_EMPTY_DOMAIN_VALUE);
	}

	protected void populateValues(final CPSCharacteristic source, final CsticModel target, final MasterDataContext ctxt)
	{
		final List<CsticValueModel> assignedValues = new ArrayList<>();
		if (source.getValues() != null)
		{
			for (final CPSValue value : source.getValues())
			{
				final CsticValueModel valueModel = getValueConverter().convertWithContext(value, ctxt);
				assignedValues.add(valueModel);
			}

			if (source.getPossibleValues() != null)
			{
				final List<String> possibleValues = source.getPossibleValues().stream().map(
						possibleValue -> "".equals(possibleValue.getValueLow()) ? getEmptyValueConstant() : possibleValue.getValueLow())
						.collect(Collectors.toList());

				Collections.sort(assignedValues,
						Comparator.comparing(csticValue -> possibleValues.contains(csticValue.getName())
								? Integer.valueOf(possibleValues.indexOf(csticValue.getName()))
								: Integer.valueOf(assignedValues.size() - 1)));
			}

		}
		target.setAssignedValuesWithoutCheckForChange(assignedValues);
	}

	protected void populatePossibleValues(final CPSCharacteristic source, final CsticModel target, final MasterDataContext ctxt)
	{
		final List<CsticValueModel> possibleValues = new ArrayList<>();
		boolean hasOneInterval = false;
		final boolean constrained = isConstrained(source, ctxt);
		target.setConstrained(constrained);
		final boolean allowsAdditionalValue = allowsAdditionalValue(source, ctxt);
		target.setAllowsAdditionalValues(allowsAdditionalValue);
		if (source.getPossibleValues() != null)
		{
			hasOneInterval = fillPossibleValues(source, possibleValues, ctxt);
		}


		final List<CsticValueModel> assignedValues = target.getAssignedValues();
		if (mergeAssignedWithPossibleValues(constrained, allowsAdditionalValue, assignedValues))
		{
			for (final CsticValueModel assignedValue : assignedValues)
			{
				if (!possibleValues.contains(assignedValue))
				{
					possibleValues.add(assignedValue);
				}
			}
		}
		target.setAssignableValues(possibleValues);
		target.setIntervalInDomain(hasOneInterval);
	}

	protected boolean mergeAssignedWithPossibleValues(final boolean constrained, final boolean allowsAdditionalValue,
			final List<CsticValueModel> assignedValues)
	{
		return (constrained || allowsAdditionalValue) && CollectionUtils.isNotEmpty(assignedValues);
	}

	/**
	 * Check whether a characteristics is domain constrained. We don't consider attribute
	 * {@link CPSPossibleValue#isSelectable()} because we are interested in the _static_ domain only. Even if a
	 * restrictable characteristic has no runtime domain, this check will state correctly that it is constrained. If
	 * domain values are read on demand the 'constrained' information is determined via master data. For characteristics
	 * where the domain is maintained via variant tables this is not possible. It would return false even if the
	 * characteristic is constrained. Also for characteristics where the static domain is overwritten at the class the
	 * information cannot be retrieved via master data. In this case the 'constrained' information is determined via the
	 * possible values of the source characteristic.
	 *
	 * @param source
	 *           Characteristic in CPS representation
	 * @return domain constrained
	 */
	protected boolean isConstrained(final CPSCharacteristic source, final MasterDataContext ctxt)
	{
		if (isReadDomainValuesOnDemand())
		{
			return hasStaticDomainDeterminedByMasterData(source, ctxt) && !allowsAdditionalValueDeterminedByMasterData(source, ctxt);
		}
		else
		{
			return CollectionUtils.isNotEmpty(source.getPossibleValues()) && !allowsAdditionalValue(source, ctxt);
		}

	}

	protected boolean hasStaticDomainDeterminedByMasterData(final CPSCharacteristic source, final MasterDataContext ctxt)
	{
		final String itemType = source.getParentItem().getType();
		if (SapproductconfigruntimecpsConstants.ITEM_TYPE_MARA.equals(itemType))
		{
			return determineHasStaticDomainForItemTypeMARA(source, ctxt);
		}
		else if (SapproductconfigruntimecpsConstants.ITEM_TYPE_KLAH.equals(itemType))
		{
			return determineHasStaticDomainForItemTypeKLAH(source);
		}
		else
		{
			throw new IllegalArgumentException("Unknown item type " + itemType);
		}
	}

	protected boolean determineHasStaticDomainForItemTypeMARA(final CPSCharacteristic source, final MasterDataContext ctxt)
	{
		final var cpsMasterDataProductContainer = ctxt.getKbCacheContainer().getProducts().get(source.getParentItem().getKey());
		final CPSMasterDataCharacteristicSpecificContainer csticSpecific = cpsMasterDataProductContainer.getCstics()
				.get(source.getId());
		if (csticSpecific == null)
		{
			throw new IllegalStateException("Could not find master data for characteristic: " + source.getId());
		}
		final Map<String, CPSMasterDataPossibleValueSpecific> possibleValueSpecifics = csticSpecific.getPossibleValueSpecifics();
		return !isPossibleValueSpecificsEmpty(possibleValueSpecifics);

	}

	protected boolean isPossibleValueSpecificsEmpty(final Map<String, CPSMasterDataPossibleValueSpecific> possibleValueSpecifics)
	{
		if (possibleValueSpecifics == null || possibleValueSpecifics.isEmpty())
		{
			return true;
		}
		else
		{
			// Check if it contains only a 'null' entry. Null entries are returned by CPS for numeric characteristics.
			return (possibleValueSpecifics.get(null) != null && possibleValueSpecifics.size() == 1);
		}
	}

	protected boolean determineHasStaticDomainForItemTypeKLAH(final CPSCharacteristic source)
	{
		// For item type KLAH we cannot retrieve the domain from the KB data as it is not provided by CPS.
		// Therefore we retrieve the domain from possible values of the source.
		return CollectionUtils.isNotEmpty(source.getPossibleValues());
	}

	protected boolean allowsAdditionalValue(final CPSCharacteristic source, final MasterDataContext ctxt)
	{
		if (isReadDomainValuesOnDemand())
		{
			return allowsAdditionalValueDeterminedByMasterData(source, ctxt);
		}
		else
		{
			final List<CPSPossibleValue> possibleValues = source.getPossibleValues();
			return null != possibleValues
					&& possibleValues.stream().filter(a -> INTERVAL_TYPE_UNCONSTRAINED.equals(a.getIntervalType())).count() == 1;
		}
	}

	protected boolean allowsAdditionalValueDeterminedByMasterData(final CPSCharacteristic source, final MasterDataContext ctxt)
	{
		final CPSMasterDataCharacteristicContainer csticContainer = ctxt.getKbCacheContainer().getCharacteristics()
				.get(source.getId());
		if (csticContainer == null)
		{
			throw new IllegalStateException("Could not find master data for characteristic: " + source.getId());
		}
		return csticContainer.isAdditionalValues();
	}

	protected boolean fillPossibleValues(final CPSCharacteristic source, final List<CsticValueModel> possibleValues,
			final MasterDataContext ctxt)
	{
		boolean hasOneInterval = false;
		for (final CPSPossibleValue possibleValue : source.getPossibleValues())
		{
			if (possibleValue.isSelectable() && !INTERVAL_TYPE_UNCONSTRAINED.equals(possibleValue.getIntervalType()))
			{
				if (CPSIntervalType.isInterval(possibleValue.getIntervalType()))
				{
					hasOneInterval = true;
				}
				final CsticValueModel csticValueModel = getPossibleValueConverter().convertWithContext(possibleValue, ctxt);
				possibleValues.add(csticValueModel);
			}
		}
		return hasOneInterval;
	}

	protected void populateCoreAttributes(final CPSCharacteristic source, final CsticModel target)
	{
		final List<CPSPossibleValue> possibleValues = source.getPossibleValues();
		target.setName(source.getId());
		target.setStaticDomainLength(possibleValues != null ? possibleValues.size() : 0);
		target.setVisible(source.isVisible());
		target.setComplete(source.isComplete());
		target.setConsistent(source.isConsistent());
		target.setReadonly(source.isReadOnly());
		target.setRequired(source.isRequired());
	}

	protected boolean isReadDomainValuesOnDemand()
	{
		return readDomainValuesOnDemand;
	}

	public void setReadDomainValuesOnDemand(final boolean readDomainValuesOnDemand)
	{
		this.readDomainValuesOnDemand = readDomainValuesOnDemand;
	}

}
