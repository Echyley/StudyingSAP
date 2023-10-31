/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */

package de.hybris.platform.odata2services.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;

import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConversionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOData2ServicesConfigurationUnitTest
{
	private static final int DEFAULT_VALUE = 200;
	private static final String BATCH_LIMIT_PROPERTY_KEY = "odata2services.batch.limit";
	private static final int INITIAL_MAX_PAGE_SIZE_PROPERTY_VALUE = 1000;
	private static final String DEFAULT_PAGE_SIZE_PROPERTY = "odata2services.page.size.default";
	private static final String MAX_PAGE_SIZE_PROPERTY = "odata2services.page.size.max";
	private static final String EXPORTABLE_INTEGRATION_OBJECTS = "odata2services.exportable.integration.objects";
	private static final String DELIMITER = ",";
	@Mock(lenient = true)
	private Configuration configuration;
	@Mock(lenient = true)
	private ConfigurationService configurationService;
	@InjectMocks
	private DefaultOData2ServicesConfiguration defaultOData2ServicesConfiguration;

	@Before
	public void setUp()
	{
		lenient().when(configurationService.getConfiguration()).thenReturn(configuration);
	}

	@Test
	public void testBatchLimit()
	{
		lenient().when(configuration.getInt(BATCH_LIMIT_PROPERTY_KEY)).thenReturn(100);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(100);
	}

	@Test
	public void testSetBatchLimit()
	{
		final int newLimit = 10;

		defaultOData2ServicesConfiguration.setBatchLimit(newLimit);

		verify(configuration).setProperty(BATCH_LIMIT_PROPERTY_KEY, String.valueOf(newLimit));
	}

	@Test
	public void testNegativeBatchLimit()
	{
		lenient().when(configuration.getInt(BATCH_LIMIT_PROPERTY_KEY)).thenReturn(-100);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(DEFAULT_VALUE);
	}

	@Test
	public void testZeroBatchLimit()
	{
		lenient().when(configuration.getInt(BATCH_LIMIT_PROPERTY_KEY)).thenReturn(0);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(DEFAULT_VALUE);
	}

	@Test
	public void testConversionExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new ConversionException())
				.when(configuration).getInt(BATCH_LIMIT_PROPERTY_KEY);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(DEFAULT_VALUE);
	}

	@Test
	public void testNoSuchElementExceptionFromConfigurationTriggersDefaultValue()
	{
		doThrow(new NoSuchElementException())
				.when(configuration).getInt(BATCH_LIMIT_PROPERTY_KEY);
		assertThat(defaultOData2ServicesConfiguration.getBatchLimit()).isEqualTo(DEFAULT_VALUE);
	}

	@Test
	public void testValidDefaultPageSize()
	{
		lenient().when(configuration.getInt(DEFAULT_PAGE_SIZE_PROPERTY)).thenReturn(15);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(15);
	}

	@Test
	public void testInvalidDefaultPageSize()
	{
		lenient().when(configuration.getInt(DEFAULT_PAGE_SIZE_PROPERTY)).thenReturn(-5);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(10);
	}

	@Test
	public void testMissingDefaultPageSize()
	{
		doThrow(new NoSuchElementException()).when(configuration).getInt(DEFAULT_PAGE_SIZE_PROPERTY);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(10);
	}

	@Test
	public void testInvalidDefaultPageSizeFormat()
	{
		doThrow(new ConversionException()).when(configuration).getInt(DEFAULT_PAGE_SIZE_PROPERTY);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(10);
	}

	@Test
	public void testValidMaxPageSize()
	{
		lenient().when(configuration.getInt(MAX_PAGE_SIZE_PROPERTY)).thenReturn(15);

		assertThat(defaultOData2ServicesConfiguration.getMaxPageSize()).isEqualTo(15);
	}

	@Test
	public void testInvalidMaxPageSize()
	{
		lenient().when(configuration.getInt(MAX_PAGE_SIZE_PROPERTY)).thenReturn(-5);

		assertThat(defaultOData2ServicesConfiguration.getMaxPageSize()).isEqualTo(INITIAL_MAX_PAGE_SIZE_PROPERTY_VALUE);
	}

	@Test
	public void testMissingMaxPageSize()
	{
		doThrow(new NoSuchElementException()).when(configuration).getInt(MAX_PAGE_SIZE_PROPERTY);

		assertThat(defaultOData2ServicesConfiguration.getMaxPageSize()).isEqualTo(INITIAL_MAX_PAGE_SIZE_PROPERTY_VALUE);
	}

	@Test
	public void testInvalidMaxPageSizeFormat()
	{
		doThrow(new ConversionException()).when(configuration).getInt(MAX_PAGE_SIZE_PROPERTY);

		assertThat(defaultOData2ServicesConfiguration.getMaxPageSize()).isEqualTo(INITIAL_MAX_PAGE_SIZE_PROPERTY_VALUE);
	}

	@Test
	public void testDefaultPageSizeExceedsMaxValue()
	{
		lenient().when(configuration.getInt(DEFAULT_PAGE_SIZE_PROPERTY)).thenReturn(15);
		lenient().when(configuration.getInt(MAX_PAGE_SIZE_PROPERTY)).thenReturn(12);

		assertThat(defaultOData2ServicesConfiguration.getDefaultPageSize()).isEqualTo(12);
		verify(configuration).setProperty(DEFAULT_PAGE_SIZE_PROPERTY, String.valueOf(12));
	}

	@Test
	public void testGetExportableIntegrationObjects()
	{
		final String exportableIOs = "IO1,IO2,IO3,IO4";
		lenient().when(configuration.getString(EXPORTABLE_INTEGRATION_OBJECTS)).thenReturn(exportableIOs);
		assertThat(defaultOData2ServicesConfiguration.getExportableIntegrationObjects()).isEqualTo(List.of(exportableIOs.split(DELIMITER)));
	}

	@Test
	public void testSetExportableIntegrationObjects()
	{
		final String exportableIOs = "IO1,IO2,IO3,IO4";
		defaultOData2ServicesConfiguration.setExportableIntegrationObjects(List.of(exportableIOs));
		verify(configuration).setProperty(EXPORTABLE_INTEGRATION_OBJECTS, exportableIOs);
	}

}
