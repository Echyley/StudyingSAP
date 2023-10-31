/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.LanguageModel;

import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.merchandising.model.ProductMetadata;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MerchSnProductMetadataPopulatorTest extends AbstractMerchSnPopulatorTest
{
	protected static String EN_PRODUCT_NAME = "enName";
	protected static String EN_PRODUCT_SUMMARY = "enSummary";
	protected static String EN_PRODUCT_DESC = "enDesc";
	protected static String DE_PRODUCT_NAME = "deName";
	protected static String DE_PRODUCT_SUMMARY = "deSummary";
	protected static String DE_PRODUCT_DESC = "deDesc";

	protected MerchSnProductMetadataPopulator populator;

	@Mock
	LanguageModel enLanguage;
	@Mock
	LanguageModel deLanguage;

	@Override
	@Before
	public void setUp()
	{
		super.setUp();
		populator = new MerchSnProductMetadataPopulator();

		when(merchContext.getLocales()).thenReturn(Map.of(enLanguage, Locale.US, deLanguage, Locale.GERMANY));
		when(enLanguage.getIsocode()).thenReturn(EN);
		when(deLanguage.getIsocode()).thenReturn(DE);
	}

	@Test
	public void testPopulateMetadata()
	{
		//given
		final Map nameMap = Map.of(EN, EN_PRODUCT_NAME, DE, DE_PRODUCT_NAME);
		final Map summaryMap = Map.of(EN, EN_PRODUCT_SUMMARY, DE, DE_PRODUCT_SUMMARY);
		final Map descriptionMap = Map.of(EN, EN_PRODUCT_DESC, DE, DE_PRODUCT_DESC);
		when(snDocument.getFields()).thenReturn(Map.of(NAME_FIELD, nameMap, SUMMARY_FIELD, summaryMap, DESCRIPTION_FIELD, descriptionMap));

		//when
		populator.populate(source, target);

		//then
		final Map<String, ProductMetadata> result = target.getMetadata();
		assertNotNull(result);
		assertEquals(2, result.size());
		validateMetadata(result.get(EN), EN_PRODUCT_NAME, EN_PRODUCT_SUMMARY, EN_PRODUCT_DESC);
		validateMetadata(result.get(DE), DE_PRODUCT_NAME, DE_PRODUCT_SUMMARY, DE_PRODUCT_DESC);
	}

	@Test
	public void testPopulateMetadataForLocaleMap()
	{
		//given
		final Map nameMap = Map.of(Locale.US, EN_PRODUCT_NAME, Locale.GERMANY, DE_PRODUCT_NAME);
		final Map summaryMap = Map.of(Locale.US, EN_PRODUCT_SUMMARY, Locale.GERMANY, DE_PRODUCT_SUMMARY);
		final Map descriptionMap = Map.of(Locale.US, EN_PRODUCT_DESC, Locale.GERMANY, DE_PRODUCT_DESC);

		when(snDocument.getFields()).thenReturn(Map.of(NAME_FIELD, nameMap, SUMMARY_FIELD, summaryMap, DESCRIPTION_FIELD, descriptionMap));

		//when
		populator.populate(source, target);

		//then
		final Map<String, ProductMetadata> result = target.getMetadata();
		assertNotNull(result);
		assertEquals(2, result.size());
		validateMetadata(result.get(EN), EN_PRODUCT_NAME, EN_PRODUCT_SUMMARY, EN_PRODUCT_DESC);
		validateMetadata(result.get(DE), DE_PRODUCT_NAME, DE_PRODUCT_SUMMARY, DE_PRODUCT_DESC);
	}

	@Test
	public void testPopulateMetadataWhenNoValueForLocale()
	{
		//given
		final Map nameMap = Map.of(Locale.US, EN_PRODUCT_NAME);
		final Map summaryMap = Map.of(Locale.US, EN_PRODUCT_SUMMARY);
		final Map descriptionMap = Map.of(Locale.US, EN_PRODUCT_DESC);
		when(snDocument.getFields()).thenReturn(Map.of(NAME_FIELD, nameMap, SUMMARY_FIELD, summaryMap, DESCRIPTION_FIELD, descriptionMap));

		//when
		populator.populate(source, target);

		//then
		final Map<String, ProductMetadata> result = target.getMetadata();
		assertNotNull(result);
		assertEquals(2, result.size());
		validateMetadata(result.get(EN), EN_PRODUCT_NAME, EN_PRODUCT_SUMMARY, EN_PRODUCT_DESC);
		validateMetadata(result.get(DE), "", "", "");
	}

	@Test
	public void testPopulateWhenNoValues()
	{
		//given
		when(snDocument.getFields()).thenReturn(Map.of());

		//when
		populator.populate(source, target);

		//then
		final Map<String, ProductMetadata> result = target.getMetadata();
		assertNotNull(result);
		assertEquals(2, result.size());
		validateMetadata(result.get(EN), "", "", "");
		validateMetadata(result.get(DE), "", "", "");
	}

	private void validateMetadata(final ProductMetadata metadata, final String name, final String summary,
	                              final String description)
	{
		assertNotNull(metadata);
		assertEquals(name, metadata.getName());
		assertEquals(summary, metadata.getSummary());
		assertEquals(description, metadata.getDescription());
	}
}
