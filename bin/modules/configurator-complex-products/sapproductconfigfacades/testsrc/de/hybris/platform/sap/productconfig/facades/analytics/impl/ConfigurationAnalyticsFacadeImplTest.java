/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.analytics.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticCsticData;
import de.hybris.platform.sap.productconfig.facades.analytics.AnalyticsPopulatorInput;
import de.hybris.platform.sap.productconfig.facades.populator.analytics.AnalyticsPopulator;
import de.hybris.platform.sap.productconfig.runtime.interf.analytics.model.AnalyticsDocument;
import de.hybris.platform.sap.productconfig.services.analytics.impl.AnalyticsServiceImpl;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link ConfigurationAnalyticsFacadeImpl}
 */
@UnitTest
public class ConfigurationAnalyticsFacadeImplTest
{

	private ConfigurationAnalyticsFacadeImpl classUnderTest;
	@Mock
	private AnalyticsServiceImpl mockedAnalyticsService;

	@Mock
	private AnalyticsPopulator mockedAnalyticsPopolator;
	private AnalyticsDocument analyticsDocument;


	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		classUnderTest = new ConfigurationAnalyticsFacadeImpl();
		classUnderTest.setAnalyticsPopulator(mockedAnalyticsPopolator);
		classUnderTest.setAnalyticsService(mockedAnalyticsService);
		analyticsDocument = new AnalyticsDocument();
		given(mockedAnalyticsService.getAnalyticData("123")).willReturn(analyticsDocument);

	}

	@Test
	public void testGetAnalyticData()
	{
		final List<String> csticUiKeys = Collections.singletonList("instanceId-InstanceName.groupName.csticName");
		final List<AnalyticCsticData> analyticData = classUnderTest.getAnalyticData(csticUiKeys, "123");
		assertNotNull(analyticData);

		final ArgumentCaptor<AnalyticsPopulatorInput> sourceArgument = ArgumentCaptor.forClass(AnalyticsPopulatorInput.class);

		verify(mockedAnalyticsPopolator).populate(sourceArgument.capture(), any(List.class));
		assertSame(analyticsDocument, sourceArgument.getValue().getDocument());
		assertSame(csticUiKeys, sourceArgument.getValue().getCsticUiKeys());
	}
}
