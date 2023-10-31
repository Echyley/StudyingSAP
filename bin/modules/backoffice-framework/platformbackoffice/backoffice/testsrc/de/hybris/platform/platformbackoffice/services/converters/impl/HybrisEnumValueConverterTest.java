/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.converters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.catalog.enums.ArticleApprovalStatus;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.platformbackoffice.services.converters.SavedQueryValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;


public class HybrisEnumValueConverterTest
{
	private static final String TEST_ATTRIBUTE = "testAttribute";
	private static final String SPLIT_REGEX = "#";
	private static final String MULTI_VALUE_DELIMITER = "&";

	private HybrisEnumValueConverter converter;
	@Mock
	private DataType hybrisType;
	@Mock
	private DataAttribute dataAttribute;
	@Mock
	private EnumerationService enumerationService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		converter = new HybrisEnumValueConverter();
		converter.setEnumerationService(enumerationService);
		Mockito.when(hybrisType.getType()).thenReturn(DataType.Type.ENUM);
		Mockito.when(hybrisType.getCode()).thenReturn(ProductModel._TYPECODE);
		Mockito.when(hybrisType.getAttribute(TEST_ATTRIBUTE)).thenReturn(dataAttribute);
		Mockito.when(hybrisType.getClazz()).thenReturn(ArticleApprovalStatus.class);
		Mockito.when(dataAttribute.getValueType()).thenReturn(hybrisType);
		Mockito.when(dataAttribute.getDefinedType()).thenReturn(hybrisType);
	}

	@Test
	public void testCanHandle() throws Exception
	{
		Mockito.doReturn(Boolean.FALSE).when(dataAttribute).isLocalized();
		assertTrue(converter.canHandle(dataAttribute));
		Mockito.when(hybrisType.getType()).thenReturn(DataType.Type.ATOMIC);
		assertFalse("Not hybris enum type", converter.canHandle(dataAttribute));
	}

	@Test
	public void testConvertFromStringValue() throws Exception
	{
		final ArticleApprovalStatus articleApprovalStatus = ArticleApprovalStatus.APPROVED;
		Mockito.doReturn(Boolean.FALSE).when(dataAttribute).isLocalized();
		Mockito.when(enumerationService.getEnumerationValue(articleApprovalStatus.getType(), articleApprovalStatus.getCode()))
				.thenReturn(articleApprovalStatus);

		final String val = String.format("%s#%s", articleApprovalStatus.getType(), articleApprovalStatus.getCode());
		SavedQueryValue savedQueryValue = new SavedQueryValue(val);
		Object value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertEquals(ArticleApprovalStatus.APPROVED, value);

		savedQueryValue = new SavedQueryValue(converter.getLocaleCode(Locale.CHINESE), "notValueCode");
		value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNull(value);
	}

	@Test
	public void testConvertFromStringValueToMultiValues() throws Exception
	{
		final ArticleApprovalStatus articleApprovalStatus = ArticleApprovalStatus.APPROVED;
		Mockito.doReturn(Boolean.FALSE).when(dataAttribute).isLocalized();
		Mockito.when(enumerationService.getEnumerationValue(articleApprovalStatus.getType(), articleApprovalStatus.getCode()))
				.thenReturn(articleApprovalStatus);

		SavedQueryValue savedQueryValue = new SavedQueryValue(String.format("%s" + SPLIT_REGEX + "%s" + MULTI_VALUE_DELIMITER, articleApprovalStatus.getType(), articleApprovalStatus.getCode()));
		Object value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertTrue(value instanceof Collection);
		assertEquals(1, ((Collection)value).size());
		assertEquals(ArticleApprovalStatus.APPROVED, ((Collection)value).stream().findFirst().get());

		savedQueryValue = new SavedQueryValue(converter.getLocaleCode(Locale.CHINESE), "notValueCode");
		value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNull(value);
	}

	@Test
	public void testConvertLocalizedFromStringValue() throws Exception
	{
		final ArticleApprovalStatus articleApprovalStatus = ArticleApprovalStatus.APPROVED;
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();
		Mockito.when(enumerationService.getEnumerationValue(articleApprovalStatus.getType(), articleApprovalStatus.getCode()))
				.thenReturn(articleApprovalStatus);

		final String val = String.format("%s#%s", articleApprovalStatus.getType(), articleApprovalStatus.getCode());
		final SavedQueryValue savedQueryValue = new SavedQueryValue(converter.getLocaleCode(Locale.CHINESE), val);
		final Object value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertEquals(ArticleApprovalStatus.APPROVED, ((Map) value).values().iterator().next());
		assertEquals(Locale.CHINESE, ((Map) value).keySet().iterator().next());
	}

	@Test
	public void testConvertLocalizedFromStringValueToMultiValues() throws Exception
	{
		final ArticleApprovalStatus articleApprovalStatus = ArticleApprovalStatus.APPROVED;
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();
		Mockito.when(enumerationService.getEnumerationValue(articleApprovalStatus.getType(), articleApprovalStatus.getCode()))
				.thenReturn(articleApprovalStatus);

		final String val = String.format("%s" + SPLIT_REGEX + "%s" + MULTI_VALUE_DELIMITER, articleApprovalStatus.getType(), articleApprovalStatus.getCode());
		final SavedQueryValue savedQueryValue = new SavedQueryValue(converter.getLocaleCode(Locale.CHINESE), val);
		final Object value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertTrue(value instanceof Map);
		assertEquals(Locale.CHINESE, ((Map) value).keySet().stream().findFirst().get());
		final Object multiValues = ((Map) value).get(Locale.CHINESE);
		assertNotNull(multiValues);
		assertTrue(multiValues instanceof Collection);
		assertEquals(1, ((Collection)multiValues).size());
		assertEquals(ArticleApprovalStatus.APPROVED, ((Collection)multiValues).stream().findFirst().get());
	}

	@Test
	public void testConvertToStringValue() throws Exception
	{
		final ArticleApprovalStatus articleApprovalStatus = ArticleApprovalStatus.APPROVED;

		final SavedQueryValue savedQueryValue = converter.convertValue(articleApprovalStatus, dataAttribute);
		final String[] typeAndValue = savedQueryValue.getValue().split("#");
		assertEquals(articleApprovalStatus.getType(), typeAndValue[0]);
		assertEquals(articleApprovalStatus.getCode(), typeAndValue[1]);
	}

	@Test
	public void testConvertToStringValueFromMultiValues() throws Exception
	{
		final ArticleApprovalStatus articleApprovalStatus = ArticleApprovalStatus.APPROVED;
		final SavedQueryValue savedQueryValue = converter.convertValue(Arrays.asList(articleApprovalStatus), dataAttribute);
		assertNotNull(savedQueryValue);
		final String value = savedQueryValue.getValue();
		assertTrue(value.endsWith(MULTI_VALUE_DELIMITER));
		final String[] multiValues = value.split(MULTI_VALUE_DELIMITER);
		assertEquals(1, multiValues.length);
		final String[] typeAndValue = multiValues[0].split(SPLIT_REGEX);
		assertEquals(2, typeAndValue.length);
		assertEquals(articleApprovalStatus.getType(), typeAndValue[0]);
		assertEquals(articleApprovalStatus.getCode(), typeAndValue[1]);
	}

	@Test
	public void testConvertLocalizedToStringValue() throws Exception
	{
		final ArticleApprovalStatus articleApprovalStatus = ArticleApprovalStatus.APPROVED;
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();

		final SavedQueryValue savedQueryValue = converter
				.convertValue(converter.getLocalizedValue(Locale.CHINESE, articleApprovalStatus), dataAttribute);
		final String[] typeAndValue = savedQueryValue.getValue().split("#");
		assertEquals(articleApprovalStatus.getType(), typeAndValue[0]);
		assertEquals(articleApprovalStatus.getCode(), typeAndValue[1]);

		assertEquals(converter.getLocaleCode(Locale.CHINESE), savedQueryValue.getLocaleCode());
	}

	@Test
	public void testConvertLocalizedToStringValueFromMultiValues() throws Exception
	{
		final ArticleApprovalStatus articleApprovalStatus = ArticleApprovalStatus.APPROVED;
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();

		final SavedQueryValue savedQueryValue = converter
				.convertValue(converter.getLocalizedValue(Locale.CHINESE, Arrays.asList(articleApprovalStatus)), dataAttribute);

		assertNotNull(savedQueryValue);
		assertEquals(converter.getLocaleCode(Locale.CHINESE), savedQueryValue.getLocaleCode());
		final String value = savedQueryValue.getValue();
		assertTrue(value.endsWith(MULTI_VALUE_DELIMITER));
		final String[] multiValues = value.split(MULTI_VALUE_DELIMITER);
		assertEquals(1, multiValues.length);
		final String[] typeAndValue = multiValues[0].split(SPLIT_REGEX);
		assertEquals(2, typeAndValue.length);
		assertEquals(articleApprovalStatus.getType(), typeAndValue[0]);
		assertEquals(articleApprovalStatus.getCode(), typeAndValue[1]);
	}
}
