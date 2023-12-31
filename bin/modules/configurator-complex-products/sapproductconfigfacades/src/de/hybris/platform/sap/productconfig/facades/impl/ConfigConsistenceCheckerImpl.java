/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.impl;

import de.hybris.platform.sap.productconfig.facades.ConfigConsistenceChecker;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.CsticValueData;
import de.hybris.platform.sap.productconfig.facades.UiGroupData;
import de.hybris.platform.sap.productconfig.facades.UiType;

import java.util.List;

import org.apache.log4j.Logger;


/**
 * Default implementation of the {@link ConfigConsistenceChecker}.<br>
 */
public class ConfigConsistenceCheckerImpl implements ConfigConsistenceChecker
{
	private static final Logger LOG = Logger.getLogger(ConfigConsistenceCheckerImpl.class);

	@Override
	public void checkConfiguration(final ConfigurationData configData)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug("do consistence check for product '" + configData.getKbKey().getProductCode() + "' with configID '"
					+ configData.getConfigId() + "'");
		}
		final List<UiGroupData> csticGroups = configData.getGroups();

		for (final UiGroupData csticGroup : csticGroups)
		{
			checkGroup(csticGroup);
		}
	}

	protected void checkGroup(final UiGroupData group)
	{
		final List<CsticData> cstics = group.getCstics();

		for (final CsticData cstic : cstics)
		{
			checkCstic(cstic);
		}

		final List<UiGroupData> subGroups = group.getSubGroups();
		if (subGroups == null || subGroups.isEmpty())
		{
			return;
		}
		for (final UiGroupData subGroup : subGroups)
		{
			checkGroup(subGroup);
		}

	}

	protected void checkCstic(final CsticData cstic)
	{
		if (UiType.RADIO_BUTTON.equals(cstic.getType()))
		{
			checkRadioButtonConsistence(cstic);
		}
	}

	protected void checkRadioButtonConsistence(final CsticData cstic)
	{
		final List<CsticValueData> domainValues = cstic.getDomainvalues();
		final int valueCount = domainValues.size();
		if (valueCount != 1)
		{
			return;
		}

		final CsticValueData singleValue = domainValues.get(0);
		if (!singleValue.isSelected())
		{
			return;
		}

		if (LOG.isDebugEnabled())
		{
			LOG.debug("changed radio button type to readOnly for cstic '" + cstic.getName() + "'");
		}
		cstic.setType(UiType.READ_ONLY);
	}
}
