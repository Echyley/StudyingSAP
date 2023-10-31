/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.InputEvent;

import com.hybris.cockpitng.editors.EditorListener;


/**
 * Product configuration parameter OnChange event listener.
 */
public class ProductConfigParameterOnChangeEventListener implements EventListener<InputEvent>
{

	private final EditorListener<Object> listener;

	public ProductConfigParameterOnChangeEventListener(final EditorListener<Object> listener)
	{
		super();
		this.listener = listener;
	}

	@Override
	public void onEvent(final InputEvent event)
	{
		final String value = event.getValue();
		listener.onValueChanged(value);
	}

}
