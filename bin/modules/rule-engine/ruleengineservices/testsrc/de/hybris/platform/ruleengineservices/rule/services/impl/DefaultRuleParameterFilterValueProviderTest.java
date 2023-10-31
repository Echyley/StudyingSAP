package de.hybris.platform.ruleengineservices.rule.services.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelEvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class DefaultRuleParameterFilterValueProviderTest
{
	@Spy
	private final ExpressionParser parser = new SpelExpressionParser();

	@Spy
	@InjectMocks
	private DefaultRuleParameterFilterValueProvider ruleParameterFilterValueProvider;

	@Spy
	private final CatalogModel catalogModel = new CatalogModel();

	@Test(expected = SpelEvaluationException.class)
	public void shouldThrowExceptionWhenEvaluateSpelEvaluationWithInvalidExpression()
	{
		final String invalidExpression = "catalog#T(java.lang.Runtime).getRuntime().exec('open -a Calculator')";

		ruleParameterFilterValueProvider.evaluate(invalidExpression, catalogModel);
	}

	@Test
	public void shouldGetValidResultWhenEvaluateSpelEvaluationWithValidExpression()
	{
		final String validExpression = "catalogVersion->catalog#catalogVersions";
		final Set mockCatalogVersions = mock(Set.class);
		BDDMockito.given(catalogModel.getCatalogVersions()).willReturn(mockCatalogVersions);

		final Object evaluateResult = ruleParameterFilterValueProvider.evaluate(validExpression, catalogModel);

		assertEquals(mockCatalogVersions, evaluateResult);
	}

}
