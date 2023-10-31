/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package com.hybris.backoffice.solrsearch.resolvers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ItemtypeLabelValueResolverTest
{
	@InjectMocks
	private ItemtypeLabelValueResolver itemtypeLabelValueResolver;

	@Mock
	private TypeService typeService;

	@Mock
	private ItemModel source;

	@Test
	public void shouldProvideModel()
	{
		//given
		itemtypeLabelValueResolver.setTypeService(typeService);
		final String itemtypeCode = "testItemtype";
		final ComposedTypeModel itemtypeModel = mock(ComposedTypeModel.class);
		when(source.getItemtype()).thenReturn(itemtypeCode);
		when(typeService.getComposedTypeForCode(itemtypeCode)).thenReturn(itemtypeModel);

		//then
		assertThat(itemtypeLabelValueResolver.provideModel(source)).isEqualTo(itemtypeModel);
	}
}
