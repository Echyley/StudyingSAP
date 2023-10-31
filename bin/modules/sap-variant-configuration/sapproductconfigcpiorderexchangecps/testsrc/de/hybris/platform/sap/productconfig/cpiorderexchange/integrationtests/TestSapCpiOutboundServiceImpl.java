/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.cpiorderexchange.integrationtests;

import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiadapter.service.impl.SapCpiOutboundServiceImpl;

import java.util.Arrays;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Maps;

import rx.Observable;


public class TestSapCpiOutboundServiceImpl extends SapCpiOutboundServiceImpl
{
	@Override
	public Observable<ResponseEntity<Map>> sendOrder(final SAPCpiOutboundOrderModel sapCpiOutboundOrderModel)
	{

		final Map<String, Map> map = Maps.newHashMap();
		final Map<String, String> innerMap = Maps.newHashMap();
		innerMap.put("responseMessage", "The order has been sent successfully to S/4HANA through SCPI!");
		innerMap.put("responseStatus", "Success");
		map.put("SAPCpiOutboundOrder", innerMap);
		final ResponseEntity<Map> objectResponseEntity = new ResponseEntity<>(map, HttpStatus.OK);


		return Observable.from(Arrays.asList(objectResponseEntity));


	}
}
