/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.enumeration.EnumerationMetaTypeModel;
import de.hybris.platform.core.model.type.AtomicTypeModel;
import de.hybris.platform.core.model.type.CollectionTypeModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.type.MapTypeModel;
import de.hybris.platform.core.model.type.TypeModel;
import de.hybris.platform.integrationbackoffice.IntegrationbackofficetestUtils;
import de.hybris.platform.integrationbackoffice.TypeCreatorTestUtils;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultListItemDTOTypeServiceUnitTest
{
	@Mock
	private ReadService readService;

	@InjectMocks
	private DefaultListItemDTOTypeService service;

	@Test
	public void returnNullWhenDetermineTypeIsAtomic()
	{
		final AtomicTypeModel typeModel = mock(AtomicTypeModel.class);
		when(typeModel.getItemtype()).thenReturn("AtomicType");

		when(readService.isCollectionType("AtomicType")).thenReturn(false);
		when(readService.isMapType("AtomicType")).thenReturn(false);
		when(readService.isComposedType("AtomicType")).thenReturn(false);
		when(readService.isEnumerationMetaType("AtomicType")).thenReturn(false);

		final TypeModel result = service.determineType(typeModel);

		assertNull("Type is null.", result);
	}

	@Test
	public void returnElementTypeWhenDetermineTypeIsCollection()
	{
		final String code = "TestCode";
		final ComposedTypeModel elementType = IntegrationbackofficetestUtils.composedTypeModel(code);
		final CollectionTypeModel typeModel = TypeCreatorTestUtils.collectionTypeModel(elementType);
		when(typeModel.getItemtype()).thenReturn("CollectionType");

		when(readService.isCollectionType("CollectionType")).thenReturn(true);

		final TypeModel result = service.determineType(typeModel);

		assertEquals("Element type is returned." , elementType, result);
		assertEquals("Type has correct code", code, result.getCode());
	}

	@Test
	public void returnArgumentWhenDetermineTypeIsComposed()
	{
		final String code = "TestCode";
		final ComposedTypeModel typeModel = TypeCreatorTestUtils.composedTypeModel(code);
		when(typeModel.getItemtype()).thenReturn("ComposedType");

		when(readService.isCollectionType("ComposedType")).thenReturn(false);
		when(readService.isMapType("ComposedType")).thenReturn(false);
		when(readService.isComposedType("ComposedType")).thenReturn(true);

		final TypeModel result = service.determineType(typeModel);

		assertEquals("Argument is returned." , typeModel, result);
		assertEquals("Type has correct code", code, result.getCode());
	}

	@Test
	public void returnArgumentWhenDetermineTypeIsEnumeration()
	{
		final EnumerationMetaTypeModel typeModel = mock(EnumerationMetaTypeModel.class);
		final String code = "TestCode";
		when(typeModel.getCode()).thenReturn(code);
		when(typeModel.getItemtype()).thenReturn("EnumerationMetaType");

		when(readService.isCollectionType("EnumerationMetaType")).thenReturn(false);
		when(readService.isMapType("EnumerationMetaType")).thenReturn(false);
		when(readService.isComposedType("EnumerationMetaType")).thenReturn(false);
		when(readService.isEnumerationMetaType("EnumerationMetaType")).thenReturn(true);

		final TypeModel result = service.determineType(typeModel);

		assertEquals("Argument is returned." , typeModel, result);
		assertEquals("Type has correct code", code, result.getCode());
	}

	@Test
	public void returnElementTypeOfCollectionWhenDetermineTypeIsMapOfCollection()
	{
		final String code = "TestCode";
		final ComposedTypeModel elementType = IntegrationbackofficetestUtils.composedTypeModel(code);
		final CollectionTypeModel returnType = TypeCreatorTestUtils.collectionTypeModel(elementType);
		final MapTypeModel typeModel = TypeCreatorTestUtils.mapTypeModel(returnType);
		when(typeModel.getItemtype()).thenReturn("MapType");
		when(returnType.getItemtype()).thenReturn("CollectionType");

		when(readService.isCollectionType("MapType")).thenReturn(false);
		when(readService.isMapType("MapType")).thenReturn(true);
		when(readService.isCollectionType("CollectionType")).thenReturn(true);

		final TypeModel result = service.determineType(typeModel);

		assertEquals("Element type of map of collections is returned." , elementType, result);
		assertEquals("Type has correct code", code, result.getCode());
	}

	@Test
	public void returnNullWhenDetermineTypeIsMapOfAtomic()
	{
		final MapTypeModel typeModel = mock(MapTypeModel.class);
		final AtomicTypeModel returnType = mock(AtomicTypeModel.class);
		when(typeModel.getItemtype()).thenReturn("MapType");
		when(typeModel.getReturntype()).thenReturn(returnType);
		when(returnType.getItemtype()).thenReturn("AtomicType");

		when(readService.isCollectionType("MapType")).thenReturn(false);
		when(readService.isMapType("MapType")).thenReturn(true);
		when(readService.isCollectionType("AtomicType")).thenReturn(false);
		when(readService.isMapType("AtomicType")).thenReturn(false);
		when(readService.isComposedType("AtomicType")).thenReturn(false);
		when(readService.isEnumerationMetaType("AtomicType")).thenReturn(false);

		final TypeModel result = service.determineType(typeModel);

		assertNull("Type is null", result);
	}

	@Test
	public void returnNullWhenDetermineTypeIsNull()
	{
		assertNull("Type is null.", service.determineType(null));
	}
}
