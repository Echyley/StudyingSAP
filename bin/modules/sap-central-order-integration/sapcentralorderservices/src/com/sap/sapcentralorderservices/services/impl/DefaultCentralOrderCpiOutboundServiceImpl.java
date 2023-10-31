/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.services.impl;

import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiadapter.service.impl.SapCpiOutboundServiceImpl;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.sap.sapcentralorderservices.constants.SapcentralorderservicesConstants;

import rx.Observable;


/**
 *
 */
public class DefaultCentralOrderCpiOutboundServiceImpl extends SapCpiOutboundServiceImpl
{
	@Override
	public Observable<ResponseEntity<Map>> sendOrder(final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel)
	{
		//This ensures central order is in use
		if (null != sapCpiOutboundOrderModel.getSapCpiConfig().getCentralOrderSourceSystemId())
		{
			return getOutboundServiceFacade().send(sapCpiOutboundOrderModel, SapcentralorderservicesConstants.OUTBOUND_ORDER_OBJECT,
					SapcentralorderservicesConstants.OUTBOUND_CENTRAL_ORDER_DESTINATION);
		}
		else
		{
			return super.sendOrder(sapCpiOutboundOrderModel);
		}
	}
}
