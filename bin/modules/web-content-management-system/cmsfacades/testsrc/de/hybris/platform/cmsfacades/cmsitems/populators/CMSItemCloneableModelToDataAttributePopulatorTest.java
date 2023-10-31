/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cloning.service.predicate.CMSItemCloneablePredicate;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemAttributeFilterEnablerService;
import de.hybris.platform.core.model.ItemModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_CLONEABLE_NAME;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSItemCloneableModelToDataAttributePopulatorTest
{
	@InjectMocks
	private CMSItemCloneableModelToDataAttributePopulator populator;

	@Mock
	private CMSItemCloneablePredicate cmsItemCloneablePredicate;

	@Mock
	private CMSItemAttributeFilterEnablerService cmsItemAttributeFilterEnablerService;

	@Mock
	private ItemModel itemModel;

	private Map<String, Object> itemMap = new HashMap<>();

	@Before
	public void setUp()
	{
		when(cmsItemAttributeFilterEnablerService.isAttributeAllowed(anyString(), anyString())).thenReturn(true);
	}

	@Test
	public void testItemModelIsCloneable()
	{
		// GIVEN
		when(cmsItemCloneablePredicate.test(itemModel)).thenReturn(true);
		when(itemModel.getItemtype()).thenReturn(FIELD_CLONEABLE_NAME);
		// WHEN
		populator.populate(itemModel, itemMap);

		// THEN
		assertThat(itemMap.get(FIELD_CLONEABLE_NAME), is(true));
	}

	@Test
	public void testItemModelIsNotCloneable()
	{
		// GIVEN
		when(cmsItemCloneablePredicate.test(itemModel)).thenReturn(false);
		when(itemModel.getItemtype()).thenReturn(FIELD_CLONEABLE_NAME);
		// WHEN
		populator.populate(itemModel, itemMap);

		// THEN
		assertThat(itemMap.get(FIELD_CLONEABLE_NAME), is(false));
	}
}
