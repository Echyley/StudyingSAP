/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.integration.service.impl;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.productconfig.services.intf.ExternalConfigurationAccess;


/**
 * Default implementation of {@link ExternalConfigurationAccess} that writes and reads to
 * {@link AbstractOrderEntryModel}
 */
public class DefaultExternalConfigurationAccess implements ExternalConfigurationAccess
{

	@Override
	public void setExternalConfiguration(final String externalConfiguration, final AbstractOrderEntryModel orderEntryModel)
	{
		orderEntryModel.setExternalConfiguration(externalConfiguration);
	}

	@Override
	public String getExternalConfiguration(final AbstractOrderEntryModel orderEntryModel)
	{
		return orderEntryModel.getExternalConfiguration();
	}

}
