/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.webhookservices.util;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.webhookservices.model.WebhookConfigurationModel;

/**
 * A utility for converting classes generated in the build process, which have no suitable toString() implementation, to a string.
 */
public final class ToStringUtil
{
	private static final String WEBHOOK_TEMPLATE = "Webhook {destination=%s, integrationObject=%s, filterLocation=%s}";

	private ToStringUtil()
	{
		// non-instantiable utility class
	}

	/**
	 * Converts to a string
	 *
	 * @param model a webhook instance to present as a string
	 * @return string presentation of the model instance.
	 */
	public static String toString(final WebhookConfigurationModel model)
	{
		return model == null
				? "null"
				: String.format(WEBHOOK_TEMPLATE,
						model.getDestination().getId(), model.getIntegrationObject().getCode(),	model.getFilterLocation());
	}

	/**
	 * Converts to a string
	 *
	 * @param model a customer instance to present as a string
	 * @return string presentation of the model instance.
	 */
	public static String toString(final CustomerModel model)
	{
		return model == null
				? "null"
				: (model + "{uid=" + model.getUid() + "}");
	}

	/**
	 * Converts to a string
	 *
	 * @param model an order instance to present as a string
	 * @return string presentation of the model instance.
	 */
	public static String toString(final OrderModel model)
	{
		return model == null
				? "null"
				: (model + "{code=" + model.getCode() + "}");
	}
}
