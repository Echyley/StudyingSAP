/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.converters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.platformbackoffice.services.converters.SavedQueryValue;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Maps;
import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;


public class AtomicValueConverterTest
{
	private static final String TEST_ATTRIBUTE = "testAttribute";
	private AtomicValueConverter converter;
	@Mock
	private DataType simpleAtomicMapType;
	@Mock
	private DataAttribute dataAttribute;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		converter = new AtomicValueConverter();
		Mockito.when(simpleAtomicMapType.getAttribute(TEST_ATTRIBUTE)).thenReturn(dataAttribute);
		Mockito.when(simpleAtomicMapType.getType()).thenReturn(DataType.Type.ATOMIC);
		Mockito.when(simpleAtomicMapType.getCode()).thenReturn("TestCode");
		Mockito.when(dataAttribute.getValueType()).thenReturn(simpleAtomicMapType);
	}

	@Test
	public void testCanHandle() throws Exception
	{
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.BOOLEAN);
		Mockito.when(dataAttribute.getMapType()).thenReturn(Mockito.mock(DataAttribute.MapType.class));
		assertFalse("Attribute is simple atomic", converter.canHandle(dataAttribute));
	}

	@Test
	public void testCannotHandleMap() throws Exception
	{
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.DATE);
		Mockito.when(dataAttribute.getDefinedType()).thenReturn(simpleAtomicMapType);
		Mockito.doReturn(Boolean.FALSE).when(dataAttribute).isLocalized();
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();
		assertTrue(converter.canHandle(dataAttribute));
		Mockito.when(dataAttribute.getValueType()).thenReturn(simpleAtomicMapType);
		assertFalse("Attribute is simple atomic", converter.canHandle(dataAttribute));
	}

	@Test
	public void testConvertFromStringValue() throws Exception
	{
		final Date date = new Date();
		Mockito.when(dataAttribute.getDefinedType()).thenReturn(simpleAtomicMapType);
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.DATE);
		SavedQueryValue savedQueryValue = new SavedQueryValue(String.valueOf(date.getTime()));
		Object value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertEquals(date, value);

		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.INTEGER);
		savedQueryValue = new SavedQueryValue("notIntValue");
		value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNull("Not int value should not be converted", value);
	}

	@Test
	public void testConvertToStringValue() throws Exception
	{
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.DATE);
		final Date date = new Date();
		SavedQueryValue savedQueryValue = converter.convertValue(date, dataAttribute);
		assertNotNull(savedQueryValue);
		assertEquals(String.valueOf(date.getTime()), savedQueryValue.getValue());

		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.INTEGER);
		final Integer intVal = Integer.valueOf(123123);
		savedQueryValue = converter.convertValue(intVal, dataAttribute);
		assertNotNull(savedQueryValue);
		assertEquals(intVal.toString(), savedQueryValue.getValue());
	}

	@Test
	public void testLocalizedConvertFromStringValue() throws Exception
	{
		final Date date = new Date();
		Mockito.when(dataAttribute.getDefinedType()).thenReturn(simpleAtomicMapType);
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.DATE);
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();

		SavedQueryValue savedQueryValue = new SavedQueryValue(Locale.ENGLISH.toLanguageTag(), String.valueOf(date.getTime()));
		Object value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertTrue(value instanceof Map);
		final Map<Object, Object> locVal = ((Map) value);
		assertEquals(Locale.ENGLISH, locVal.keySet().iterator().next());
		assertEquals(date, locVal.values().iterator().next());

		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.INTEGER);
		savedQueryValue = new SavedQueryValue(Locale.ENGLISH.toLanguageTag(), "notIntValue");
		value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNull("Not int value should not be converted", value);
	}

	@Test
	public void testLocalizedConvertToStringValue() throws Exception
	{
		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.DATE);
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();
		final Date date = new Date();
		final Map<Locale, Object> locMap = Maps.newHashMap();
		locMap.put(Locale.ENGLISH, date);
		SavedQueryValue savedQueryValue = converter.convertValue(locMap, dataAttribute);
		assertNotNull(savedQueryValue);
		assertEquals(String.valueOf(date.getTime()), savedQueryValue.getValue());
		assertEquals(String.valueOf(Locale.ENGLISH.toLanguageTag()), savedQueryValue.getLocaleCode());


		Mockito.when(dataAttribute.getValueType()).thenReturn(DataType.INTEGER);
		final Integer intVal = Integer.valueOf(123123);
		locMap.put(Locale.ENGLISH, intVal);
		savedQueryValue = converter.convertValue(locMap, dataAttribute);
		assertNotNull(savedQueryValue);
		assertEquals(intVal.toString(), savedQueryValue.getValue());
	}
}
