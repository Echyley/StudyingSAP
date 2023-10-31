/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.proxy.DataQualityCalculationServiceProxy;


@RunWith(MockitoJUnitRunner.class)
public class DataQualitySnIndexerValueProviderTest
{

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private SnIndexerFieldWrapper fieldWrapper;

	@Mock
	private ItemModel source;

	@InjectMocks
	private DataQualitySnIndexerValueProvider provider;

	@Mock
	private SnExpressionEvaluator snExpressionEvaluator;

	@Mock
	private SnField snField;

	@Mock
	private DataQualityCalculationServiceProxy dataQualityCalculationServiceProxy;

	@Test
	public void shouldGetFieldValue() throws SnIndexerException, SnException
	{
		//give
		final String testDomainGroupId = "testDomainGroupId";
		final Double testDouble = 0.21035468;
		final Optional<Double> testOptionalDouble = Optional.of(testDouble);
		final Double testFormatDouble = 0.21;
		provider.setDataQualityCalculationServiceProxy(dataQualityCalculationServiceProxy);
		provider.setDomainGroupId(testDomainGroupId);
		when(dataQualityCalculationServiceProxy.calculate(source, testDomainGroupId)).thenReturn(testOptionalDouble);

		//then
		assertThat(provider.getFieldValue(indexerContext, fieldWrapper, source, null)).isEqualTo(testFormatDouble);
	}

	@Test(expected = SnIndexerException.class)
	public void shouldCatchExceptionWhenGetFieldValue() throws SnIndexerException
	{
		//give
		final String testDomainGroupId = "testDomainGroupId";
		final Optional<Double> testOptionalDouble = Optional.empty();
		provider.setDataQualityCalculationServiceProxy(dataQualityCalculationServiceProxy);
		provider.setDomainGroupId(testDomainGroupId);
		when(dataQualityCalculationServiceProxy.calculate(source, testDomainGroupId)).thenReturn(testOptionalDouble);

		//then
		provider.getFieldValue(indexerContext, fieldWrapper, source, null);
	}

}
