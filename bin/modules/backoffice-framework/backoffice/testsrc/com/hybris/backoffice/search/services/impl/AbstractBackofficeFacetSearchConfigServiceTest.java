/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.search.services.impl;

import de.hybris.platform.core.model.type.ComposedTypeModel;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class AbstractBackofficeFacetSearchConfigServiceTest
{
	@Mock
	private AbstractBackofficeFacetSearchConfigService abstractBackofficeFacetSearchConfigService;

	@Test
	public void shouldGetSuperTypeCodes()
	{
		final ComposedTypeModel composedType = mock(ComposedTypeModel.class);
		final List<ComposedTypeModel> typeCodes = Lists.newArrayList();
		typeCodes.add(composedType);
		typeCodes.addAll(composedType.getAllSuperTypes());
		doCallRealMethod().when(abstractBackofficeFacetSearchConfigService).getWithSuperTypeCodes(composedType);
		assertEquals(typeCodes, abstractBackofficeFacetSearchConfigService.getWithSuperTypeCodes(composedType));
	}

}
