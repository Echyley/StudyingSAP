/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.version.service.impl;

import static de.hybris.platform.cms2.jalo.contents.components.GeneratedAbstractCMSComponent.RESTRICTIONS;
import static de.hybris.platform.cms2.jalo.restrictions.GeneratedAbstractRestriction.COMPONENTS;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
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
public class DefaultCMSVersionApplyAttributeValuesToComponentItemModelTest
{
	private static final String NAME = "name";

	@Mock
	private ModelService modelService;
	@Mock
	private CMSVersionHelper cmsVersionHelper;

	@InjectMocks
	private DefaultCMSVersionApplyAttributeValuesToComponentItemModel applyAttributeValuesToItemModel;

	private AbstractRestrictionModel restriction;

	private AbstractCMSComponentModel component;

	private Map<String, Object> values;


	@Before
	public void setup()
	{
		restriction = new AbstractRestrictionModel();
		component = new AbstractCMSComponentModel();

		values = new HashMap<>();
		values.put(NAME, NAME);
		values.put(RESTRICTIONS, Arrays.asList(restriction));
		component.setRestrictions(Arrays.asList(restriction));
		restriction.setComponents(new ArrayList<>());

		when(cmsVersionHelper.getAttributesNotVersion(component)).thenReturn(new ArrayList<>());
	}

	@Test
	public void shouldApplyAttributeValuesToItemModel()
	{
		// WHEN
		applyAttributeValuesToItemModel.apply(component, values);

		// THEN
		verify(modelService).setAttributeValue(component, NAME, NAME);
		verify(modelService).setAttributeValue(eq(component), eq(RESTRICTIONS), argThat(new ContainsAllArgumentMatcher<>(restriction)) );

		verify(modelService).setAttributeValue(eq(restriction), eq(COMPONENTS), argThat(new ContainsAllArgumentMatcher<>(component)));
	}

}
