/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;


/**
 * Product configuration parameter renderer.
 */
public class ProductConfigParameterRenderer implements ComboitemRenderer<Object>
{
	@Override
	public void render(final Comboitem item, final Object data, final int index)
	{
		item.setValue(data);
		final String label = String.valueOf(data);
		item.setLabel(label);
	}
}
