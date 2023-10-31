/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.ordercancel.model.OrderCancelRecordEntryModel;


/**
 * Class definition as a workaround for Spring since it cannot handle parameterized types
 */
public class DefaultSendOrderCancelRequestToDataHubHelper extends AbstractSendToDataHubHelper<OrderCancelRecordEntryModel>
{

}
