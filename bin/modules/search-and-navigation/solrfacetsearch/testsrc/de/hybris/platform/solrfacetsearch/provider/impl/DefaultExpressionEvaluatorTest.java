/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.solrfacetsearch.provider.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.solrfacetsearch.constants.SolrfacetsearchConstants;

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
public class DefaultExpressionEvaluatorTest
{
	private static final String NAME = "test name";

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Mock
	private ConfigurationService configurationService;

	@Mock
	private Configuration configuration;

	@Mock
	private ApplicationContext applicationContext;

	private DefaultExpressionEvaluator expressionEvaluator;

	@Before
	public void initalize()
	{
		when(configurationService.getConfiguration()).thenReturn(configuration);

		expressionEvaluator = new DefaultExpressionEvaluator();
		expressionEvaluator.setConfigurationService(configurationService);
		expressionEvaluator.setParser(new SpelExpressionParser());
		expressionEvaluator.setApplicationContext(applicationContext);
	}

	@Test
	public void testEvaluatingExpression()
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(false);

		final String expression = "name";
		final LanguageModel model = mock(LanguageModel.class);

		when(model.getName()).thenReturn(NAME);

		// when
		final Object result = expressionEvaluator.evaluate(expression, model);

		// then
		Assert.assertNotNull(result);
		Assert.assertEquals(NAME, result);
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElDisabled()
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(false);

		final String expression = "T(java.time.Instant).now()";
		final LanguageModel model = mock(LanguageModel.class);

		// expect
		expectedException.expect(SpelEvaluationException.class);

		// when
		expressionEvaluator.evaluate(expression, model);
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElDisabledMethodExecutionIsAllowed()
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(false);

		final String expression = "getName()";
		final LanguageModel model = mock(LanguageModel.class);

		when(model.getName()).thenReturn(NAME);

		// when
		final Object result = expressionEvaluator.evaluate(expression, model);

		// then
		Assert.assertNotNull(result);
		Assert.assertEquals(NAME, result);
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElDisabledMethodExecutionOnClassIsNotAllowed()
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(false);

		final String expression = "getClass()";
		final LanguageModel model = mock(LanguageModel.class);

		// expect
		expectedException.expect(SpelEvaluationException.class);

		// when
		expressionEvaluator.evaluate(expression, model);
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElEnabled()
	{
		// given
		final String expression = "T(java.time.Instant).now()";
		final ItemModel model = new ItemModel();

		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(true);

		// when
		final Object result = expressionEvaluator.evaluate(expression, model);

		// then
		Assert.assertNotNull(result);
	}

	@Test
	public void testEvaluatingRestrictedExpressionWithFullSpElEnabledMethodExecutionOnClassIsAllowed()
	{
		// given
		when(configuration.getBoolean(SolrfacetsearchConstants.FULL_SPEL_SUPPPORT_PROPERTY, false)).thenReturn(true);

		final String expression = "getClass()";
		final LanguageModel model = mock(LanguageModel.class);

		// when
		final Object result = expressionEvaluator.evaluate(expression, model);

		// then
		Assert.assertNotNull(result);
		Assert.assertEquals(LanguageModel.class, result);
	}
}
