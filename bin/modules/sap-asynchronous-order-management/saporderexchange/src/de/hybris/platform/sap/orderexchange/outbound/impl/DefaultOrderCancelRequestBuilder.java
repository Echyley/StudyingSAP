/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;

import org.apache.log4j.Logger;


/**
 * Default raw item builder as AbstractRawItemBuilder and additional Logger
 */
public class DefaultOrderCancelRequestBuilder extends AbstractRawItemBuilder<OrderCancelRecordEntryModel>
{
	private static final Logger LOG = Logger.getLogger(DefaultOrderCancelRequestBuilder.class);

	@Override
	protected Logger getLogger()
	{
		return LOG;
	}

}
