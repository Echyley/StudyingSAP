/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.services;

import de.hybris.platform.integrationbackoffice.widgets.configuration.data.ExportConfigurationEditorPresentation;
import de.hybris.platform.integrationservices.util.ApplicationBeans;

/**
 * This class is introduced to acquire fresh bean of {@link ExportConfigurationEditorPresentation}
 */
public class ExportConfigurationEditorPresentationPopulator
{
	private static final String EXPORT_CONFIG_PRESENTATION_BEAN_ID = "defaultExportConfigurationEditorPresentation";

	/**
	 * Retrieves a bean of {@link ExportConfigurationEditorPresentation}
	 */
	public ExportConfigurationEditorPresentation getExportConfigEditorPresentation()
	{
		return ApplicationBeans.getFreshBean(EXPORT_CONFIG_PRESENTATION_BEAN_ID, ExportConfigurationEditorPresentation.class);
	}
}
