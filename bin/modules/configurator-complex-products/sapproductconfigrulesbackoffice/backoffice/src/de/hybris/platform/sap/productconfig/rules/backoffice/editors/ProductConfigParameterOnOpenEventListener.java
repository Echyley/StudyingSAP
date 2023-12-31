/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.backoffice.editors;

import org.apache.commons.lang3.ObjectUtils;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.OpenEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;

import com.hybris.cockpitng.editors.EditorListener;


/**
 * Product configuration parameter OnOpen event listener.
 */
public class ProductConfigParameterOnOpenEventListener implements EventListener<OpenEvent>
{

	private final EditorListener<Object> listener;
	private final Combobox box;

	private Object valueOnOpen = null;



	public ProductConfigParameterOnOpenEventListener(final EditorListener<Object> listener, final Combobox box)
	{
		super();
		this.listener = listener;
		this.box = box;
	}

	@Override
	public void onEvent(final OpenEvent event)
	{
		if (event.isOpen())
		{
			valueOnOpen = getSelectedItemValue();
		}
		else
		{
			final Object selectedVal = getSelectedItemValue();
			if (ObjectUtils.notEqual(selectedVal, valueOnOpen))
			{
				listener.onValueChanged(selectedVal);
			}
		}
	}

	private Object getSelectedItemValue()
	{
		Object selectedVal = null;

		final Comboitem selectedItem = box.getSelectedItem();

		if (selectedItem != null)
		{
			selectedVal = selectedItem.getValue();
		}

		return selectedVal;
	}

}
