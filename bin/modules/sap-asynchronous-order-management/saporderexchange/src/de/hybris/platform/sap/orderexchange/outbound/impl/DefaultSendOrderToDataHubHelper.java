/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.orderexchange.outbound.impl;

import de.hybris.platform.core.model.order.OrderModel;


/**
 * Class definition as a workaround for Spring since it cannot handle parameterized types
 */
public class DefaultSendOrderToDataHubHelper extends AbstractSendToDataHubHelper<OrderModel>
{

}
