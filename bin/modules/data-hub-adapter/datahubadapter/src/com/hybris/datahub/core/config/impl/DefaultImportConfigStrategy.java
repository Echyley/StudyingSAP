/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.hybris.datahub.core.config.impl;

import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.impex.ImpExResource;
import de.hybris.platform.servicelayer.impex.ImportConfig;

import com.hybris.datahub.core.config.ImportConfigStrategy;
import com.hybris.datahub.core.dto.ItemImportTaskData;
import com.hybris.datahub.core.services.ImpExResourceFactory;

import org.springframework.beans.factory.annotation.Required;

public class DefaultImportConfigStrategy implements ImportConfigStrategy
{
	private Boolean isDistributedImpex;
	private Boolean isSld;
	private ImpExResourceFactory resourceFactory;

	@Override
	public ImportConfig createImportConfig(final ItemImportTaskData ctx) throws ImpExException
	{
		final ImportConfig cfg = new ImportConfig();
		final ImpExResource resource = resourceFactory.createResource(ctx);
		cfg.setScript(resource);
		cfg.setValidationMode(ImportConfig.ValidationMode.STRICT);
		cfg.setLegacyMode(false);
		cfg.setDistributedImpexEnabled(isDistributedImpex);
		cfg.setSynchronous(true);
		cfg.setSldForData(isSld);

		return cfg;
	}

	@Required
	public void setSld(final Boolean sld)
	{
		isSld = sld;
	}

	@Required
	public void setDistributedImpex(final Boolean distributedImpex)
	{
		isDistributedImpex = distributedImpex;
	}

	@Required
	public void setResourceFactory(final ImpExResourceFactory resourceFactory)
	{
		this.resourceFactory = resourceFactory;
	}
}
