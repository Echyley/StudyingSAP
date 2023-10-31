/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderservices.services.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.outboundservices.facade.OutboundServiceFacade;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundConfigModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sap.sapcentralorderservices.constants.SapcentralorderservicesConstants;

import rx.Observable;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderCpiOutboundServiceImplTest
{


	DefaultCentralOrderCpiOutboundServiceImpl defaultCentralOrderCpiOutboundServiceImpl = new DefaultCentralOrderCpiOutboundServiceImpl();

	SAPCpiOutboundOrderModel sapCpiOutboundOrderModel = new SAPCpiOutboundOrderModel();

	Observable<ResponseEntity<Map>> result;

	HttpHeaders responseHeaders = new HttpHeaders();
	Map<String, Map> map = new HashMap();
	ResponseEntity<Map> responseEntity = new ResponseEntity(map, responseHeaders, HttpStatus.OK);
	Observable<ResponseEntity<Map>> output = Observable.just(responseEntity);

	SAPCpiOutboundConfigModel sapCpiOutboundConfigModel = new SAPCpiOutboundConfigModel();


	OutboundServiceFacade outboundServiceFacade = mock(OutboundServiceFacade.class);

	@Test
	public void test()
	{
		defaultCentralOrderCpiOutboundServiceImpl.setOutboundServiceFacade(outboundServiceFacade);
		defaultCentralOrderCpiOutboundServiceImpl.setOutboundServiceFacade(outboundServiceFacade);
		sapCpiOutboundConfigModel.setCentralOrderSourceSystemId("centralOrderSourceSystemId");
		sapCpiOutboundOrderModel.setSapCpiConfig(sapCpiOutboundConfigModel);

		when(outboundServiceFacade.send(any(SAPCpiOutboundOrderModel.class),
				eq(SapcentralorderservicesConstants.OUTBOUND_ORDER_OBJECT),
				eq(SapcentralorderservicesConstants.OUTBOUND_CENTRAL_ORDER_DESTINATION))).thenReturn(output);

		defaultCentralOrderCpiOutboundServiceImpl.setOutboundServiceFacade(outboundServiceFacade);
		result = defaultCentralOrderCpiOutboundServiceImpl.sendOrder(sapCpiOutboundOrderModel);

		Assert.assertNotNull(result);

	}

}
