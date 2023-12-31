/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapimpeximportadapter.services;

import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.servicelayer.impex.ImportResult;

import java.io.InputStream;


/**
 * @deprecated since 1811, please use {@link de.hybris.platform.integrationservices.model.IntegrationObjectModel}
 *
 *             Service interface for impex import
 */
@Deprecated(since = "1811", forRemoval= true )
public interface ImpexImportService
{

	/**
	 * Creates an impex media model from the input stream
	 *
	 * @param impexStream
	 *           - impex stream input
	 * @return {@link ImpExMediaModel}
	 */
	ImpExMediaModel createImpexMedia(InputStream impexStream);

	/**
	 * Imports the {@link ImpExMediaModel}
	 * 
	 * @param impexMedia
	 *           - impex media object
	 * @return {@link ImportResult}
	 */
	ImportResult importMedia(ImpExMediaModel impexMedia);

}
