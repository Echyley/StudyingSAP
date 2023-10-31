/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.cmsitems.CMSItemAttributeFilterEnablerService;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSItemLinkToggleModelToDataAttributePopulatorTest
{
	@InjectMocks
	private CMSItemLinkToggleModelToDataAttributePopulator populator;

	@Mock
	private CMSItemAttributeFilterEnablerService cmsItemAttributeFilterEnablerService;

	private Map<String, Object> itemMap = new HashMap<>();

	@Before
	public void setUp()
	{
		when(cmsItemAttributeFilterEnablerService.isAttributeAllowed(anyString(), anyString())).thenReturn(true);
	}

	@Test
	public void testWhenLinkToggleIsCreated_shouldCreateObjectWithNewExternalAndUrlLinkFieldsAndRemoveOldFields()
	{
		// GIVEN
		itemMap.put("fakeParam", "fakeValue");
		itemMap.put(FIELD_URL_LINK_NAME, "urlLinkData");
		itemMap.put(FIELD_EXTERNAL_NAME, false);
		assertThat(itemMap.size(), is(3));

		// WHEN
		populator.populate(null, itemMap);

		// THEN
		assertThat(itemMap.size(), is(2));
		assertThat(itemMap.containsKey(FIELD_EXTERNAL_NAME), is(false));
		assertThat(itemMap.containsKey(FIELD_URL_LINK_NAME), is(false));

		assertThat(itemMap.get(FIELD_LINK_TOGGLE_NAME) instanceof Map, is(true));
		Map<String, Object> linkToggle = (HashMap<String, Object>) itemMap.get(FIELD_LINK_TOGGLE_NAME);
		assertThat(linkToggle.get(FIELD_EXTERNAL_NAME), is(false));
		assertThat(linkToggle.get(FIELD_URL_LINK_NAME), is("urlLinkData"));
	}
}
