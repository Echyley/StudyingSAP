/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.version.service.impl;

import static de.hybris.platform.cms2.jalo.contents.components.GeneratedAbstractCMSComponent.SLOTS;
import static de.hybris.platform.cms2.jalo.contents.contentslot.GeneratedContentSlot.CMSCOMPONENTS;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.version.ContainsAllArgumentMatcher;
import de.hybris.platform.cms2.version.service.CMSVersionHelper;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCMSVersionApplyAttributeValuesToSlotItemModelTest
{
	private static final String NAME = "name";

	@Mock
	private ModelService modelService;
	@Mock
	private CMSVersionHelper cmsVersionHelper;

	@InjectMocks
	private DefaultCMSVersionApplyAttributeValuesToSlotItemModel applyAttributeValuesToItemModel;

	private ContentSlotModel slot;

	private AbstractCMSComponentModel component;

	private Map<String, Object> values;


	@Before
	public void setup()
	{
		slot = new ContentSlotModel();
		component = new AbstractCMSComponentModel();

		values = new HashMap<>();
		values.put(NAME, NAME);
		values.put(CMSCOMPONENTS, Arrays.asList(component));
		slot.setCmsComponents(Arrays.asList(component));
		component.setSlots(new ArrayList<>());

		when(cmsVersionHelper.getAttributesNotVersion(slot)).thenReturn(new ArrayList<>());
	}

	@Test
	public void shouldApplyAttributeValuesToItemModel()
	{
		// WHEN
		applyAttributeValuesToItemModel.apply(slot, values);

		// THEN
		verify(modelService).setAttributeValue(slot, NAME, NAME);
		verify(modelService).setAttributeValue(eq(slot), eq(CMSCOMPONENTS), argThat(new ContainsAllArgumentMatcher<>(component)));

		verify(modelService).setAttributeValue(eq(component), eq(SLOTS), argThat(new ContainsAllArgumentMatcher<>(slot)));
	}

}
