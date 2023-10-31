/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.searchservices.providers.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.searchservices.admin.data.SnField;
import de.hybris.platform.searchservices.core.SnException;
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator;
import de.hybris.platform.searchservices.core.service.SnQualifier;
import de.hybris.platform.searchservices.indexer.SnIndexerException;
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext;
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper;
import de.hybris.platform.servicelayer.type.TypeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.backoffice.proxy.LabelServiceProxy;



@RunWith(MockitoJUnitRunner.class)
public class ItemtypeLabelSnIndexerValueProviderTest
{

	@Mock
	private SnIndexerContext indexerContext;

	@Mock
	private SnIndexerFieldWrapper fieldWrapper;

	@Mock
	private ItemModel source;

	@InjectMocks
	private ItemtypeLabelSnIndexerValueProvider provider;

	@Mock
	private SnExpressionEvaluator snExpressionEvaluator;

	@Mock
	private SnField snField;

	@Mock
	private TypeService typeService;

	@Mock
	private LabelServiceProxy labelServiceProxy;

	@Mock
	private ComposedTypeModel itemtypeModel;

	@Before
	public void setUp()
	{
		provider.setLabelServiceProxy(labelServiceProxy);
		provider.setTypeService(typeService);
		final String itemtypeCode = "testItemtype";
		when(source.getItemtype()).thenReturn(itemtypeCode);
		when(typeService.getComposedTypeForCode(itemtypeCode)).thenReturn(itemtypeModel);
	}

	@Test
	public void shouldGetFieldValue() throws SnException
	{
		//give
		final List<SnQualifier> qualifiers = new ArrayList<>();
		final SnQualifier testQualifier1 = mock(SnQualifier.class);
		final SnQualifier testQualifier2 = mock(SnQualifier.class);
		qualifiers.add(testQualifier1);
		qualifiers.add(testQualifier2);
		when(fieldWrapper.isLocalized()).thenReturn(true);
		when(testQualifier1.getAs(Locale.class)).thenReturn(Locale.ENGLISH);
		when(testQualifier2.getAs(Locale.class)).thenReturn(Locale.GERMAN);
		when(fieldWrapper.getQualifiers()).thenReturn(qualifiers);

		final String testLabelEn = "testLabelEn";
		final String testLabelDe = "testLabelDe";
		when(labelServiceProxy.getObjectLabel(itemtypeModel, Locale.ENGLISH)).thenReturn(testLabelEn);
		when(labelServiceProxy.getObjectLabel(itemtypeModel, Locale.GERMAN)).thenReturn(testLabelDe);

		//then
		final Object fieldValue = provider.getFieldValue(indexerContext, fieldWrapper, source, null);
		assertThat(fieldValue).isInstanceOf(Map.class);
		final Map fieldValueMap = (Map) fieldValue;
		assertThat(fieldValueMap).hasSize(2).containsEntry(Locale.ENGLISH, testLabelEn).containsEntry(Locale.GERMAN, testLabelDe);
	}

	@Test(expected = SnIndexerException.class)
	public void shouldCatchExceptionWhenGetFieldValue() throws SnIndexerException
	{
		//give
		when(fieldWrapper.isLocalized()).thenReturn(false);

		//then
		provider.getFieldValue(indexerContext, fieldWrapper, source, null);
	}

}
