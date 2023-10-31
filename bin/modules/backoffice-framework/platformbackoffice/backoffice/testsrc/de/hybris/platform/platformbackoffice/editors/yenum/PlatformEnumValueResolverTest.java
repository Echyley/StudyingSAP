/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editors.yenum;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import de.hybris.platform.catalog.enums.ArticleStatus;
import de.hybris.platform.classification.ClassificationSystemService;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.TestUtils;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


enum MyTestingEnum
{
	VALUE_1, VALUE_2, VALUE_3
}

@RunWith(MockitoJUnitRunner.class)
public class PlatformEnumValueResolverTest
{

	private static final String ARTICLE_STATUS = "ArticleStatus";
	private final List allArticleStatuses = (Arrays.asList(ArticleStatus.OLD_ARTICLE, ArticleStatus.NEW_ARTICLE));

	@Mock
	private ModelService modelService;

	@Mock
	private ClassificationSystemService classificationSystemService;

	@Mock
	private EnumerationService enumerationService;

	@InjectMocks
	private PlatformEnumValueResolver platformEnumValueResolver;

	@Before
	public void before()
	{
		given(enumerationService.getEnumerationValues(ARTICLE_STATUS)).willReturn(allArticleStatuses);
	}

	@Test
	public void testValueIsHybrisEnumValue()
	{
		// given
		final Object value = ArticleStatus.NEW_ARTICLE;
		given(modelService.getModelType(value)).willReturn(ARTICLE_STATUS);

		// when
		final List<Object> returnedArticleStatuses = platformEnumValueResolver.getAllValues(null, value);

		// then
		assertThat(returnedArticleStatuses).isEqualTo(allArticleStatuses);
	}

	@Test
	public void testValueTypeIsHybrisEnumValue()
	{
		// given
		final String valueType = "java.lang.Enum(de.hybris.platform.catalog.enums.ArticleStatus)";
		given(modelService.getModelType(ArticleStatus.class)).willReturn(ARTICLE_STATUS);

		// when
		final List<Object> returnedArticleStatuses = platformEnumValueResolver.getAllValues(valueType, null);

		// then
		assertThat(returnedArticleStatuses).isEqualTo(this.allArticleStatuses);
	}

	@Test
	public void testValueTypeIsHybrisEnumValueShortNotation()
	{
		final String valueType = "java.lang.Enum(ArticleStatus)";
		final List<Object> returnedArticleStatuses = platformEnumValueResolver.getAllValues(valueType, null);
		assertThat(returnedArticleStatuses).isEqualTo(this.allArticleStatuses);
	}

	@Test
	public void testShouldReturnEmptyList()
	{
		final String valueType = "java.lang.Enum";
		final List<Object> returnedValues = platformEnumValueResolver.getAllValues(valueType, null);
		assertThat(returnedValues).isEmpty();
	}


	@Test
	public void testValueTypeIsSimpleEnum()
	{
		final String valueType = "java.lang.Enum(de.hybris.platform.platformbackoffice.editors.yenum.MyTestingEnum)";
		final List<Object> allMyEnumValues = platformEnumValueResolver.getAllValues(valueType, null);
		assertThat(allMyEnumValues).isEqualTo(Arrays.asList(MyTestingEnum.values()));
	}

	@Test
	public void testValueIsSimpleEnum()
	{
		final List<Object> allMyEnumValues = platformEnumValueResolver.getAllValues(null, MyTestingEnum.VALUE_2);
		assertThat(allMyEnumValues).isEqualTo(Arrays.asList(MyTestingEnum.values()));
	}

	@Test
	public void testNPEResistance()
	{
		TestUtils.disableFileAnalyzer("Exception should be thrown");
		try
		{
			platformEnumValueResolver.getAllValues(null, null);
			Assert.fail("Exception should be thrown");
		}
		catch (final IllegalArgumentException ex)
		{
			// ok
		}
		TestUtils.enableFileAnalyzer();
	}

	@Test
	public void test()
	{
		TestUtils.disableFileAnalyzer("Exception should be thrown");
		try
		{
			platformEnumValueResolver.getAllValues(null, new Object());
			Assert.fail("Exception should be thrown");
		}
		catch (final IllegalArgumentException ex)
		{
			//ok
		}
		TestUtils.enableFileAnalyzer();

	}

}
