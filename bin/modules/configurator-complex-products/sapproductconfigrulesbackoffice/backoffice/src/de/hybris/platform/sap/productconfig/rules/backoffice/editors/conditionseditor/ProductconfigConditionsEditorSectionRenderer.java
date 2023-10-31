/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors.conditionseditor;

import de.hybris.platform.rulebuilderbackoffice.editors.conditionseditor.ConditionsEditorSectionRenderer;


public class ProductconfigConditionsEditorSectionRenderer extends ConditionsEditorSectionRenderer
{
	protected static final String PRODUCTCONFIG_CONDITIONS_EDITOR_ID = "conditionsEditor";

	@Override
	public String getEditorId()
	{
		return PRODUCTCONFIG_CONDITIONS_EDITOR_ID;
	}
}
