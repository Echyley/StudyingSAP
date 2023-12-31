/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.pci.populator.impl;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsCharacteristic;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsItem;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsPossibleValue;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsValue;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.CsticValueModel;
import de.hybris.platform.sap.productconfig.runtime.interf.model.InstanceModel;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Responsible for specifying the attributes of an {@link AnalyticsItem} according to the {@link InstanceModel}. It
 * ignores data like consistency, completeness, quantity and units and only transports the assigned and possible values
 * (which are relevant for the calculation of popularity of values)
 */
public class AnalyticsItemPopulator implements Populator<InstanceModel, AnalyticsItem>
{
	private static final String MSG_MAX_CSTIC = "Maximum amount of charcteristics (%d) has been reached,"
			+ " no more characteristics will be send to the backend to retrieve popularity data."
			+ " It's recommended to adjust the filter logic, to reduce amount of characteristics.";

	private static final Logger LOG = Logger.getLogger(AnalyticsItemPopulator.class);

	private int maxCsticsThreshold;

	/**
	 * @param maxCsticsThreshold
	 */
	public AnalyticsItemPopulator(final int maxCsticsThreshold)
	{
		super();
		this.maxCsticsThreshold = maxCsticsThreshold;
	}

	@Override
	public void populate(final InstanceModel source, final AnalyticsItem target)
	{
		target.setProductId(source.getName());
		populateCharacteristics(source, target);

	}

	protected void populateCharacteristics(final InstanceModel source, final AnalyticsItem target)
	{
		target.setCharacteristics(new ArrayList<>());

		for (final CsticModel cstic : source.getCstics())
		{
			if (considerCsticForAnalytics(cstic))
			{
				if (target.getCharacteristics().size() >= maxCsticsThreshold)
				{
					LOG.warn(String.format(MSG_MAX_CSTIC, maxCsticsThreshold));
					break;
				}
				populateCharacteristic(target, cstic);
			}
		}

	}

	protected void populateCharacteristic(final AnalyticsItem target, final CsticModel cstic)
	{
		final AnalyticsCharacteristic analyticsCharacteristic = new AnalyticsCharacteristic();
		analyticsCharacteristic.setId(cstic.getName());
		populatePossibleValues(cstic, analyticsCharacteristic);
		populateValues(cstic, analyticsCharacteristic);
		target.getCharacteristics().add(analyticsCharacteristic);
		return;
	}

	protected boolean considerCsticForAnalytics(final CsticModel cstic)
	{
		boolean result = false;
		final boolean multivalued = cstic.isMultivalued();
		final boolean allowsAdditionalValues = cstic.isAllowsAdditionalValues();
		final boolean intervalsInDomain = cstic.isIntervalInDomain();
		final boolean constrained = cstic.isConstrained();
		if (constrained && (!(multivalued || allowsAdditionalValues || intervalsInDomain)))
		{
			final List<CsticValueModel> assignableValues = cstic.getAssignableValues();
			result = (assignableValues != null && (!assignableValues.isEmpty()));
		}
		if (LOG.isDebugEnabled())
		{
			final StringBuilder output = new StringBuilder("Consider ").append(cstic.getName()).append(" for analytics:");
			output.append("\nMultiValued: ").append(multivalued);
			output.append("\nAdditionalValues: ").append(allowsAdditionalValues);
			output.append("\nIntervalsInDomain: ").append(intervalsInDomain);
			output.append("\nConstrained: ").append(constrained);
			output.append("\nResult: ").append(result);
			LOG.debug(output);
		}

		return result;

	}

	protected void populatePossibleValues(final CsticModel source, final AnalyticsCharacteristic target)
	{
		target.setPossibleValues(new ArrayList<>());
		for (final CsticValueModel possibleValue : source.getAssignableValues())
		{
			if (possibleValue.isSelectable())
			{
				final AnalyticsPossibleValue analyticsPossibleValue = new AnalyticsPossibleValue();
				analyticsPossibleValue.setValue(possibleValue.getName());
				target.getPossibleValues().add(analyticsPossibleValue);
			}
		}

	}

	protected void populateValues(final CsticModel source, final AnalyticsCharacteristic target)
	{
		target.setValues(new ArrayList<>());
		for (final CsticValueModel value : source.getAssignedValues())
		{
			final AnalyticsValue analyticsValue = new AnalyticsValue();
			analyticsValue.setValue(value.getName());
			target.getValues().add(analyticsValue);
		}

	}

	public void setMaxCsticsThreshold(final int maxCsticsThreshold)
	{
		this.maxCsticsThreshold = maxCsticsThreshold;
	}

}
