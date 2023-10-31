/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.metrics.dropwizard;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;

import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.codahale.metrics.Metric;
import com.codahale.metrics.MetricFilter;

@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class Slf4jMdcMetricsReporterFactoryTest
{
	private static final int TEST_RANDOM_NAME_ITERATIONS = 20;
	@Mock
	private Metric metric;

	@Test
	public void shouldCreateAlwaysTrueMetricFilterForNoFilters()
	{
		final MetricFilter metricFilter = new Slf4jMdcMetricsReporterFactory().getMetricFilter(Map.of());
		assertThatMatchesForRandomNames(metricFilter);
	}

	@Test
	public void shouldCreateAlwaysTrueMetricFilterForFiltersAreNull()
	{
		final MetricFilter metricFilter = new Slf4jMdcMetricsReporterFactory().getMetricFilter(null);
		assertThatMatchesForRandomNames(metricFilter);
	}

	@Test
	public void shouldCreateAlwaysTrueMetricFilterIfFiltersAreAllBlank()
	{
		final MetricFilter metricFilter = new Slf4jMdcMetricsReporterFactory().getMetricFilter(
				Map.of("filter1", "", "filter2", "   "));
		assertThatMatchesForRandomNames(metricFilter);
	}

	@Test
	public void shouldCreateSimpleMetricFilterWhenOnlyOneFilter()
	{
		final MetricFilter metricFilter = new Slf4jMdcMetricsReporterFactory().getMetricFilter(
				Map.of("filter1", "false"));

		assertThat(metricFilter).isInstanceOf(SpelExpressionMetricFilter.class);

		assertThatDoesNotMatchForRandomNames(metricFilter);
	}

	@Test
	public void shouldCreateSimpleMetricFilterWhenOnlyOneFilterIsNotBlank()
	{

		final MetricFilter metricFilter = new Slf4jMdcMetricsReporterFactory().getMetricFilter(
				Map.of("filter1", "false", "filter2", "", "filter3", "     "));

		assertThat(metricFilter).isInstanceOf(SpelExpressionMetricFilter.class);

		assertThatDoesNotMatchForRandomNames(metricFilter);
	}

	@Test
	public void shouldCreateMetricFilterThatReturnTrueWhenAtLeastOneFilterIsTrue()
	{
		final MetricFilter metricFilter = new Slf4jMdcMetricsReporterFactory().getMetricFilter(
				Map.of("filter1", "true", "filter2", "false", "filter3", "false"));

		assertThat(metricFilter).isInstanceOf(Slf4jMdcMetricsReporterFactory.OrMetricsFilter.class);

		assertThatMatchesForRandomNames(metricFilter);
	}

	@Test
	public void shouldCreateMetricFilterThatReturnFalseWhenAllFiltersAreFalse()
	{
		final MetricFilter metricFilter = new Slf4jMdcMetricsReporterFactory().getMetricFilter(
				Map.of("filter1", "false", "filter2", "false", "filter3", "false"));

		assertThat(metricFilter).isInstanceOf(Slf4jMdcMetricsReporterFactory.OrMetricsFilter.class);

		assertThatDoesNotMatchForRandomNames(metricFilter);
	}

	@Test
	public void shouldThrowExceptionWhenInvalidSpelExpressionIsUsed()
	{
		final MetricsReporterFactory metricsReporterFactory = new Slf4jMdcMetricsReporterFactory();
		final Map<String, String> filters = Map.of("filter1", "invalidSpelExpression");

		assertThatThrownBy(() -> metricsReporterFactory.getMetricFilter(filters)).isInstanceOf(SpelEvaluationException.class);
	}

	@Test
	public void shouldThrowExceptionWhenExpressionWithStaticMethodIsUsed()
	{
		final String staticMethodExpression = "T(java.time.Instant).now().toEpochMilli() == 0";
		testUnsecureExpression(staticMethodExpression);
	}

	@Test
	public void shouldFailWithNewObjectCondition()
	{
		final String staticMethodExpression = "new java.util.Date().getTime() == 0";
		testUnsecureExpression(staticMethodExpression);
	}

	private void testUnsecureExpression(final String expression)
	{
		assertThatExpressionIsValid(expression);

		final MetricsReporterFactory metricsReporterFactory = new Slf4jMdcMetricsReporterFactory();
		final Map<String, String> filters = Map.of("filter1", expression);

		assertThatThrownBy(() -> metricsReporterFactory.getMetricFilter(filters)).isInstanceOf(SpelEvaluationException.class);
	}

	private void assertThatExpressionIsValid(final String staticMethodExpression)
	{
		final ExpressionParser parser = new SpelExpressionParser();
		final Expression expr = parser.parseExpression(staticMethodExpression);
		assertThat(expr.getValue()).isNotNull();
	}

	private void assertThatDoesNotMatchForRandomNames(final MetricFilter metricFilter)
	{
		for (int i = 0; i < TEST_RANDOM_NAME_ITERATIONS; i++)
		{
			final boolean result = metricFilter.matches(RandomStringUtils.randomAlphabetic(15), metric);
			assertThat(result).isFalse();
		}
	}

	private void assertThatMatchesForRandomNames(final MetricFilter metricFilter)
	{
		for (int i = 0; i < TEST_RANDOM_NAME_ITERATIONS; i++)
		{
			final boolean result = metricFilter.matches(RandomStringUtils.randomAlphabetic(15), metric);
			assertThat(result).isTrue();
		}
	}
}
