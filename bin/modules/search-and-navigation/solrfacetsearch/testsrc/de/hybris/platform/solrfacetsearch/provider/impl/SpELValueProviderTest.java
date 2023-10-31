/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.provider.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.solrfacetsearch.config.IndexConfig;
import de.hybris.platform.solrfacetsearch.config.IndexedProperty;
import de.hybris.platform.solrfacetsearch.config.exceptions.FieldValueProviderException;
import de.hybris.platform.solrfacetsearch.constants.SolrfacetsearchConstants;
import de.hybris.platform.solrfacetsearch.provider.FieldNameProvider;
import de.hybris.platform.solrfacetsearch.provider.FieldValue;

import java.util.Collection;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SpELValueProviderTest
{
	private static final String FIELD_NAME = "field";
	private static final String NAME = "test name";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private FieldNameProvider fieldNameProvider;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private ApplicationContext applicationContext;

	private IndexConfig indexConfig;
	private IndexedProperty indexedProperty;

	private SpELValueProvider spELValueProvider;

	@Before
	public void initalize()
	{
		indexConfig = new IndexConfig();

		indexedProperty = new IndexedProperty();
		indexedProperty.setName(FIELD_NAME);
		indexedProperty.setLocalized(false);
		indexedProperty.setCurrency(false);

		when(configurationService.getConfiguration()).thenReturn(configuration);
		when(fieldNameProvider.getFieldNames(indexedProperty, null)).thenReturn(List.of(FIELD_NAME));

		spELValueProvider = new SpELValueProvider();
		spELValueProvider.setConfigurationService(configurationService);
		spELValueProvider.setParser(new SpelExpressionParser());
		spELValueProvider.setFieldNameProvider(fieldNameProvider);
		spELValueProvider.setCommonI18NService(commonI18NService);
		spELValueProvider.setApplicationContext(applicationContext);
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElDisabled() throws FieldValueProviderException
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(false);

		final String expression = "T(java.time.Instant).now()";
		final LanguageModel model = mock(LanguageModel.class);

		indexedProperty.setValueProviderParameter(expression);

		// expect
		expectedException.expect(SpelEvaluationException.class);

		// when
		spELValueProvider.getFieldValues(indexConfig, indexedProperty, model);
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElDisabledMethodExecutionIsAllowed() throws FieldValueProviderException
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(false);

		final String expression = "getName()";
		final LanguageModel model = mock(LanguageModel.class);

		when(model.getName()).thenReturn(NAME);

		indexedProperty.setValueProviderParameter(expression);

		// when
		final Collection<FieldValue> result = spELValueProvider.getFieldValues(indexConfig, indexedProperty, model);

		// then
		Assert.assertNotNull(result);
		final FieldValue fieldValue = result.iterator().next();
		Assert.assertEquals(FIELD_NAME, fieldValue.getFieldName());
		Assert.assertEquals(NAME, fieldValue.getValue());
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElDisabledMethodExecutionOnClassIsNotAllowed()
			throws FieldValueProviderException
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(false);

		final String expression = "getClass()";
		final LanguageModel model = mock(LanguageModel.class);

		indexedProperty.setValueProviderParameter(expression);

		// expect
		expectedException.expect(SpelEvaluationException.class);

		// when
		spELValueProvider.getFieldValues(indexConfig, indexedProperty, model);
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElEnabled() throws FieldValueProviderException
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(true);

		final String expression = "T(java.time.Instant).now()";
		final LanguageModel model = mock(LanguageModel.class);

		indexedProperty.setValueProviderParameter(expression);

		// when
		final Collection<FieldValue> result = spELValueProvider.getFieldValues(indexConfig, indexedProperty, model);

		// then
		Assert.assertNotNull(result);
		final FieldValue fieldValue = result.iterator().next();
		Assert.assertEquals(FIELD_NAME, fieldValue.getFieldName());
		Assert.assertNotNull(fieldValue.getValue());
	}
}
