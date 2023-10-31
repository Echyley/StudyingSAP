/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemAttributeFilterEnablerService;
import de.hybris.platform.cmsfacades.data.ItemData;
import de.hybris.platform.cmsfacades.uniqueidentifier.UniqueItemIdentifierService;
import de.hybris.platform.core.model.ItemModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_UUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSItemUniqueIdentifierAttributePopulatorTest
{
	// --------------------------------------------------------------------------
	// Variables
	// --------------------------------------------------------------------------
	private final String SOME_ITEM_ID = "some item id";

	@Mock
	private ItemData itemData;

	@Mock
	private UniqueItemIdentifierService uniqueItemIdentifierService;

	@Mock
	private CMSItemAttributeFilterEnablerService cmsItemAttributeFilterEnablerService;

	@InjectMocks
	private CMSItemUniqueIdentifierAttributePopulator uniqueIdentifierAttributePopulator;

	// --------------------------------------------------------------------------
	// Test Setup
	// --------------------------------------------------------------------------
	@Before
	public void setUp()
	{
		when(itemData.getItemId()).thenReturn(SOME_ITEM_ID);
		when(uniqueItemIdentifierService.getItemData(any())).thenReturn(Optional.of(itemData));
		when(cmsItemAttributeFilterEnablerService.isAttributeAllowed(anyString(), anyString())).thenReturn(true);
	}

	// --------------------------------------------------------------------------
	// Test Methods
	// --------------------------------------------------------------------------
	@Test
	public void givenCMSItem_WhenPopulatorIsCalled_ThenItSetsItsUuid()
	{
		// GIVEN
		CMSItemModel itemModel = new CMSItemModel();
		Map<String, Object> resultMap = new HashMap<>();

		// WHEN
		uniqueIdentifierAttributePopulator.populate(itemModel, resultMap);

		// THEN
		assertThat(resultMap.get(FIELD_UUID), is(SOME_ITEM_ID));
	}

	@Test
	public void givenItem_WhenPopulatorIsCalled_ThenItSetsItsUuid()
	{
		// GIVEN
		ItemModel itemModel = new ItemModel();
		Map<String, Object> resultMap = new HashMap<>();

		// WHEN
		uniqueIdentifierAttributePopulator.populate(itemModel, resultMap);

		// THEN
		assertThat(resultMap.get(FIELD_UUID), is(SOME_ITEM_ID));
	}

}
