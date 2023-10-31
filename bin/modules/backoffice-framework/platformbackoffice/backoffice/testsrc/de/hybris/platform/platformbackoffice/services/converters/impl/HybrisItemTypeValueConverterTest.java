/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.services.converters.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.platformbackoffice.services.converters.SavedQueryValue;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.hybris.cockpitng.dataaccess.facades.type.DataAttribute;
import com.hybris.cockpitng.dataaccess.facades.type.DataType;


public class HybrisItemTypeValueConverterTest
{
	private HybrisItemTypeValueConverter converter;
	@Mock
	private DataType hybrisType;
	@Mock
	private DataAttribute dataAttribute;
	@Mock
	private ModelService modelService;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		converter = new HybrisItemTypeValueConverter();
		converter.setModelService(modelService);
		Mockito.doReturn(Boolean.FALSE).when(modelService).isRemoved(Mockito.any());
		Mockito.when(hybrisType.getType()).thenReturn(DataType.Type.COMPOUND);
		Mockito.when(hybrisType.getCode()).thenReturn(ProductModel._TYPECODE);
		Mockito.when(hybrisType.getAttribute("testAttribute")).thenReturn(dataAttribute);
		Mockito.when(hybrisType.getClazz()).thenReturn(ProductModel.class);
		Mockito.when(dataAttribute.getValueType()).thenReturn(hybrisType);
		Mockito.when(dataAttribute.getDefinedType()).thenReturn(hybrisType);
	}

	@Test
	public void testCanHandle() throws Exception
	{
		Mockito.when(hybrisType.getType()).thenReturn(DataType.Type.COMPOUND);
		assertTrue(converter.canHandle(dataAttribute));
		Mockito.when(hybrisType.getType()).thenReturn(DataType.Type.LIST);
		assertFalse("Value is  not compound", converter.canHandle(dataAttribute));
	}

	@Test
	public void testConvertReferenceValue() throws Exception
	{
		final ProductModel productModel = Mockito.mock(ProductModel.class);

		SavedQueryValue savedQueryValue = new SavedQueryValue(productModel);
		Object value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertEquals(productModel, value);

		savedQueryValue = new SavedQueryValue(productModel);
		value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertEquals(productModel, value);
	}

	@Test
	public void testConvertLocalizedReferenceValue() throws Exception
	{
		final ProductModel productModel = Mockito.mock(ProductModel.class);
		final Map<Locale, Object> localizedValue = converter.getLocalizedValue(Locale.CHINESE, productModel);
		assertNotNull(localizedValue);
		assertEquals(productModel, localizedValue.values().iterator().next());
		assertEquals(Locale.CHINESE, localizedValue.keySet().iterator().next());
		Mockito.doReturn(Boolean.TRUE).when(dataAttribute).isLocalized();

		SavedQueryValue savedQueryValue = converter.convertValue(localizedValue, dataAttribute);
		assertNotNull(savedQueryValue);
		assertEquals(converter.getLocaleCode(Locale.CHINESE), savedQueryValue.getLocaleCode());
		assertEquals(productModel, savedQueryValue.getValueRef());

		savedQueryValue = new SavedQueryValue(converter.getLocaleCode(Locale.CHINESE), productModel);
		final Object value = converter.convertValue(savedQueryValue, dataAttribute);
		assertNotNull(value);
		assertEquals(productModel, ((Map) value).values().iterator().next());
		assertEquals(Locale.CHINESE, ((Map) value).keySet().iterator().next());
	}
}
