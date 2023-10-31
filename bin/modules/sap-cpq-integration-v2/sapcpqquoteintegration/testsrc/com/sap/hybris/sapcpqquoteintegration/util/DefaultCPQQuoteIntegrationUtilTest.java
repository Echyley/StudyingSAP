/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegration.util;

import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.spockframework.util.Assert;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;


/*
 * UnitTest for {@link DefaultCPQQuoteIntegrationUtils}
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCPQQuoteIntegrationUtilTest {

	@Mock
	private static FlexibleSearchService flexibleSearchService;
	
	@Test
	public void cpqOutboundConsumedDestinationAvailabilityTest() {
		Boolean value = DefaultCPQQuoteIntegrationUtil.cpqOutboundConsumedDestinationAvailability(flexibleSearchService);
		Mockito.verify(flexibleSearchService,Mockito.atMostOnce()).getModelByExample(Mockito.any());
		Assert.notNull(value);
	}
}

