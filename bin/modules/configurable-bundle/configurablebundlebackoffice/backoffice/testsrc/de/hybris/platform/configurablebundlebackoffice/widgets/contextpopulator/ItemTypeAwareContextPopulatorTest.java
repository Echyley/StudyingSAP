/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.configurablebundlebackoffice.widgets.contextpopulator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.ItemModel;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ItemTypeAwareContextPopulatorTest
{
	private ItemTypeAwareContextPopulator populator = new ItemTypeAwareContextPopulator();

	@Test
	public void shouldPopulateItemTypeContextParamForAnItemModelEntity()
	{
		final ItemModel itemModel = mock(ItemModel.class);
		when(itemModel.getItemtype()).thenReturn("itemTypeValue");

		final Map<String, Object> result = populator.populate(itemModel);

		assertThat(result).containsKey("itemType");
		assertThat(result.get("itemType")).isEqualTo("itemTypeValue");
	}

	@Test
	public void shouldNotPopulateItemTypeContextParamForANonItemModelEntity()
	{
		final Map<String, Object> result = populator.populate(Integer.MIN_VALUE);
		assertThat(result).doesNotContainKey("itemType");
	}
}
