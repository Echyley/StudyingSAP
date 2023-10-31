/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.order.populator.impl;

import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.sap.sapcentralorderfacades.order.mapper.SapCpiOrderOutboundAdditionalAttributeMapper;

/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderOutboundAdditionalAttributePopulatorTest
{

	OrderModel orderModel = mock(OrderModel.class);


	SAPCpiOutboundOrderModel sapCpiOutboundOrderModel = mock(SAPCpiOutboundOrderModel.class);


	SapCpiOrderOutboundAdditionalAttributeMapper additionalAttributeMapper = mock(
			SapCpiOrderOutboundAdditionalAttributeMapper.class);


	DefaultCentralOrderOutboundAdditionalAttributePopulator defaultCentralOrderOutboundAdditionalAttributePopulator = new DefaultCentralOrderOutboundAdditionalAttributePopulator();

	@Test
	public void test()
	{
		final List<SapCpiOrderOutboundAdditionalAttributeMapper> centralOrderAdditionalAttributeMappers = new ArrayList<SapCpiOrderOutboundAdditionalAttributeMapper>();
		centralOrderAdditionalAttributeMappers.add(additionalAttributeMapper);
		defaultCentralOrderOutboundAdditionalAttributePopulator
				.setCentralOrderAdditionalAttributeMappers(centralOrderAdditionalAttributeMappers);
		defaultCentralOrderOutboundAdditionalAttributePopulator.addAdditionalAttributesToSapCpiOrder(orderModel,
				sapCpiOutboundOrderModel);
		Mockito.verify(additionalAttributeMapper).mapAdditionalAttributes(orderModel, sapCpiOutboundOrderModel);
	}

}
