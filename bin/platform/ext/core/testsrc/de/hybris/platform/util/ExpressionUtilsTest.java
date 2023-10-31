package de.hybris.platform.util;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.servicelayer.exceptions.SystemException;

import java.util.Map;

import org.junit.Test;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.SimpleEvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

@UnitTest
public class ExpressionUtilsTest
{

	@Test
	public void shouldThrowExceptionWhenInstantiatingObject()
	{
		final String expression = "new java.util.Date().getTime()";
		testUnsecureExpression(expression);
	}

	@Test
	public void shouldThrowExceptionWhenUsingStaticProperty()
	{
		final String expression = "T(java.time.Instant).now().toEpochMilli()";
		testUnsecureExpression(expression);
	}

	@Test
	public void shouldUseProvidedVariables()
	{
		final String expression = "#param*2";
		final Object result = ExpressionUtils.evaluate(expression, Map.of("param", 2));
		assertThat(result).isInstanceOf(Integer.class);
		assertThat(((Integer) result)).isEqualTo(4);
	}


	@Test
	public void shouldThrowExceptionWhenInstantiatingObjectAndSimpleContextIsUsed()
	{
		final String expression = "new java.util.Date().getTime()";
		assertThatExpressionIsValid(expression);

		final EvaluationContext evaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();
		assertThatThrownBy(() -> ExpressionUtils.evaluate(expression, evaluationContext)).isInstanceOf(SystemException.class)
		                                                                                 .hasCauseInstanceOf(
				                                                                                 SpelEvaluationException.class);
	}

	@Test
	public void shouldSuccessWhenInstantiatingObjectAndStandardContextIsUsed()
	{
		final String expression = "new java.util.Date().getTime()";
		assertThatExpressionIsValid(expression);

		final EvaluationContext evaluationContext = new StandardEvaluationContext();
		assertThat(ExpressionUtils.evaluate(expression, evaluationContext)).isNotNull().isInstanceOf(Long.class);
	}

	@Test
	public void shouldThrowExceptionWhenUsingStaticPropertyAndSimpleContextIsUsed()
	{
		final String expression = "T(java.time.Instant).now().toEpochMilli()";
		assertThatExpressionIsValid(expression);

		final EvaluationContext evaluationContext = SimpleEvaluationContext.forReadOnlyDataBinding().build();
		assertThatThrownBy(() -> ExpressionUtils.evaluate(expression, evaluationContext)).isInstanceOf(SystemException.class)
		                                                                                 .hasCauseInstanceOf(
				                                                                                 SpelEvaluationException.class);
	}

	@Test
	public void shouldSuccessWhenUsingStaticPropertyAndStandardContextIsUsed()
	{
		final String expression = "T(java.time.Instant).now().toEpochMilli()";
		assertThatExpressionIsValid(expression);

		final EvaluationContext evaluationContext = new StandardEvaluationContext();
		assertThat(ExpressionUtils.evaluate(expression, evaluationContext)).isNotNull().isInstanceOf(Long.class);
	}

	@Test
	public void shouldUseProvidedVariablesWhenProvidedByEvaluationContext()
	{
		final String expression = "#param*2";

		final EvaluationContext evaluationContext = new SimpleEvaluationContext.Builder().build();
		evaluationContext.setVariable("param", 3);

		final Object result = ExpressionUtils.evaluate(expression, evaluationContext);
		assertThat(result).isInstanceOf(Integer.class);
		assertThat(((Integer) result)).isEqualTo(6);
	}

	private void testUnsecureExpression(final String expression)
	{
		assertThatExpressionIsValid(expression);

		final Map<String, Object> variables = Map.of();
		assertThatThrownBy(() -> ExpressionUtils.evaluate(expression, variables)).isInstanceOf(SystemException.class)
		                                                                         .hasCauseInstanceOf(
				                                                                         SpelEvaluationException.class);
	}

	private void assertThatExpressionIsValid(final String staticMethodExpression)
	{
		final ExpressionParser parser = new SpelExpressionParser();
		final Expression expr = parser.parseExpression(staticMethodExpression);
		assertThat(expr.getValue()).isNotNull();
	}
}
