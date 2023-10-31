/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationservices.service;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor;

/**
 * Indicates that a data item does not match the specified integration object model. This happens for example, when
 * the data item needs to be converted to or from the integration object payload.
 */
public class IntegrationObjectAndItemMismatchException extends RuntimeException
{
	private final transient Object payloadObject;
	private final transient IntegrationObjectDescriptor integrationObject;

	/**
	 * @param item of type {@link ItemModel}
	 * @param io   {@link IntegrationObjectDescriptor}
	 * @deprecated use {@link #IntegrationObjectAndItemMismatchException(Object, IntegrationObjectDescriptor)} instead
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public IntegrationObjectAndItemMismatchException(final ItemModel item, final IntegrationObjectDescriptor io)
	{
		super("Item " + item + " is not present in " + io);
		payloadObject = item;
		integrationObject = io;
	}

	/**
	 * @param payload POJO or {@link ItemModel}
	 * @param io      {@link IntegrationObjectDescriptor}
	 */
	public IntegrationObjectAndItemMismatchException(final Object payload, final IntegrationObjectDescriptor io)
	{
		super("Object " + payload + " is not present in " + io);
		payloadObject = payload;
		integrationObject = io;
	}

	/**
	 * @deprecated use {@link #getPayloadObject()}
	 */
	@Deprecated(since = "2211.FP1", forRemoval = true)
	public ItemModel getDataItem()
	{
		return getPayloadObject() instanceof final ItemModel item ? item : null;
	}

	/**
	 * This will return the payload object according to the integration object specs. It can be traditional {@link ItemModel} or
	 * any POJO.
	 *
	 * @return the payload object.
	 */
	public Object getPayloadObject()
	{
		return payloadObject;
	}

	public IntegrationObjectDescriptor getIntegrationObject()
	{
		return integrationObject;
	}
}
