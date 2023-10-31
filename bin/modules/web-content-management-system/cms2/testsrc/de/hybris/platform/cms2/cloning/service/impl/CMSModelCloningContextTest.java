/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.cloning.service.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.cloning.service.preset.AttributePresetHandler;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.CMSParagraphComponentModel;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.servicelayer.model.ItemModelContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.Silent.class)
@UnitTest
public class CMSModelCloningContextTest
{
	private static String COMPONENT_QUALIFIER = "qualifier";
	private static String COMPONENT_QUALIFIER_ORIGINAL_VALUE = "original value";
	@Mock
	private BiPredicate<ItemModel, String> predicate;
	@Mock
	private AttributePresetHandler<Object> presetHandler;
	@Mock
	private CMSItemModel component;
	@Mock
	private ItemModelContext componentContext;
	@InjectMocks
	private CMSModelCloningContext modelCloningContext;

	@Before
	public void setUp()
	{
		final List<AttributePresetHandler<Object>> mockAttributePresetHandlers = Arrays.asList(presetHandler);
		modelCloningContext.setTreatAsPartOfPredicates(Arrays.asList(predicate));
		modelCloningContext.setPresetValueHandlers(mockAttributePresetHandlers);
		when(component.getItemModelContext()).thenReturn(componentContext);
	}

	@Test
	public void shouldNotTreatAsPartOfWithEmptyPredicates()
	{
		modelCloningContext.setTreatAsPartOfPredicates(Collections.emptyList());

		final boolean result = modelCloningContext.treatAsPartOf(new CMSParagraphComponentModel(),
				CMSParagraphComponentModel.CONTENT);

		assertFalse(result);
	}

	@Test
	public void shouldNotTreatAsPartOfWithPredicateFalse()
	{
		when(predicate.test(any(), anyString())).thenReturn(false);

		final boolean result = modelCloningContext.treatAsPartOf(new CMSParagraphComponentModel(),
				CMSParagraphComponentModel.CONTENT);

		assertFalse(result);
	}

	@Test
	public void shouldTreatAsPartOfWithPredicateTrue()
	{
		when(predicate.test(any(), anyString())).thenReturn(true);

		final boolean result = modelCloningContext.treatAsPartOf(new CMSParagraphComponentModel(),
				CMSParagraphComponentModel.CONTENT);

		assertTrue(result);
	}

	@Test
	public void shouldGetPresetValueWithOriginalValueForMediaContainer()
	{
		// GIVEN
		when(componentContext.getOriginalValue(COMPONENT_QUALIFIER)).thenReturn(COMPONENT_QUALIFIER_ORIGINAL_VALUE);
		when(presetHandler.test(any(), anyString())).thenReturn(true);

		// WHEN
		modelCloningContext.getPresetValue(component, COMPONENT_QUALIFIER);

		// THEN
		verify(presetHandler).test(any(), anyString());
		verify(presetHandler).get(COMPONENT_QUALIFIER_ORIGINAL_VALUE);
	}

	@Test
	public void shouldGetPresetValueWithoutOriginalValueForMediaContainer()
	{
		// GIVEN
		when(componentContext.getOriginalValue(COMPONENT_QUALIFIER)).thenReturn(null);
		when(presetHandler.test(any(), anyString())).thenReturn(true);

		// WHEN
		modelCloningContext.getPresetValue(component, COMPONENT_QUALIFIER);

		// THEN
		verify(presetHandler).test(any(), anyString());
		verify(presetHandler).get();
	}

	@Test
	public void shouldGetNullForPresetValueIfPresetHandlerDoesNotExist()
	{
		// GIVEN
		when(presetHandler.test(any(), anyString())).thenReturn(false);

		// WHEN
		Object result = modelCloningContext.getPresetValue(component, COMPONENT_QUALIFIER);

		// THEN
		assertNull(result);
	}
}
