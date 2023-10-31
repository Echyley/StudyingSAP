/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation;
import de.hybris.platform.integrationservices.util.ApplicationBeans;

/**
 * This class is introduced to acquire fresh bean of {@link IntegrationObjectPresentation}
 */
public class IntegrationObjectPresentationPopulator
{
	private static final String IO_PRESENTATION_BEAN_ID = "defaultIntegrationEditorPresentation";

	/**
	 * Retrieves a bean of {@link IntegrationObjectPresentation}
	 */
	public IntegrationObjectPresentation getIOEditorPresentation()
	{
		return ApplicationBeans.getFreshBean(IO_PRESENTATION_BEAN_ID, IntegrationObjectPresentation.class);
	}
}
