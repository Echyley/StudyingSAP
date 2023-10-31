/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.common.actions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hybris.cockpitng.actions.ActionContext;

/**
 * An action context object that extends the default implementation from {@link ActionContext} with some helper methods.
 */
public class IntegrationActionContext extends ActionContext<Object>
{
	/**
	 * Default constructor that passes up call to super.
	 *
	 * @param context Context object of action
	 */
	public IntegrationActionContext(final ActionContext<Object> context)
	{
		super(context);
	}

	/**
	 * Retrieves the context objects from the data property of the ActionContext object.
	 *
	 * @return List of object in context
	 */
	public List<Object> getContextObjects()
	{
		if (getData() == null)
		{
			return Collections.emptyList();
		}

		final List<Object> ctxObjects = new ArrayList<>();

		if (getData() instanceof Collection)
		{
			ctxObjects.addAll((Collection) getData());
		}
		else
		{
			ctxObjects.add(getData());
		}

		return ctxObjects;
	}

	/**
	 * Determines if the data property contains an empty collection.
	 *
	 * @return If data contains an empty collection.
	 */
	public boolean isDataNotPresent()
	{
		final Object data = getData();
		return data == null || (data instanceof Collection && ((Collection) data).isEmpty());
	}
}
