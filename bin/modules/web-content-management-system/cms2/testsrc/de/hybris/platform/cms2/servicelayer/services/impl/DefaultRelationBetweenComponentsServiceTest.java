/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.services.impl;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultRelationBetweenComponentsServiceTest
{
	@InjectMocks
	private DefaultRelationBetweenComponentsService defaultRelationBetweenComponentsService;
	@Mock
	private TypeService typeService;
	@Mock
	private ComposedTypeModel composedType;
	@Mock
	private ModelService modelService;
	@Mock
	private AttributeDescriptorModel attribute;

	@Before
	public void setUp()
	{
		final Collection<AttributeDescriptorModel> attributes = new ArrayList<>();
		attributes.add(attribute);

		when(typeService.getComposedTypeForCode("AbstractCMSComponent")).thenReturn(composedType);
		when(composedType.getDeclaredattributedescriptors()).thenReturn(attributes);
		when(attribute.getQualifier()).thenReturn("simpleBanner");
		when(attribute.getWritable()).thenReturn(true);
	}

	@Test
	public void maintainRelationBetweenComponentsOnComponentArray()
	{
		final AbstractCMSComponentModel parent = new AbstractCMSComponentModel();
		final AbstractCMSComponentModel child = new AbstractCMSComponentModel();
		final List<AbstractCMSComponentModel> children = new ArrayList<>();
		children.add(child);
		children.add(new AbstractCMSComponentModel());
		when(modelService.getAttributeValue(parent, "simpleBanner")).thenReturn(children);

		defaultRelationBetweenComponentsService.maintainRelationBetweenComponentsOnComponent(parent);

		assertThat(parent.getChildren()).hasSize(2);
		assertThat(child.getParents()).hasSize(1);
	}

	@Test
	public void maintainRelationBetweenComponentsOnComponent()
	{
		final AbstractCMSComponentModel parent = new AbstractCMSComponentModel();
		final AbstractCMSComponentModel child = new AbstractCMSComponentModel();
		when(modelService.getAttributeValue(parent, "simpleBanner")).thenReturn(child);

		defaultRelationBetweenComponentsService.maintainRelationBetweenComponentsOnComponent(parent);

		assertThat(parent.getChildren()).hasSize(1);
		assertThat(child.getParents()).hasSize(1);
	}

	@Test
	public void maintainRelationBetweenComponentsOnSlot()
	{
		final AbstractCMSComponentModel parent = new AbstractCMSComponentModel();
		final List<AbstractCMSComponentModel> parents = new ArrayList<>();
		final AbstractCMSComponentModel child = new AbstractCMSComponentModel();
		final List<AbstractCMSComponentModel> children = new ArrayList<>();
		final ContentSlotModel slotModel = new ContentSlotModel();
		parents.add(parent);
		children.add(child);
		children.add(new AbstractCMSComponentModel());
		slotModel.setCmsComponents(parents);
		when(modelService.getAttributeValue(parent, "simpleBanner")).thenReturn(children);

		defaultRelationBetweenComponentsService.maintainRelationBetweenComponentsOnSlot(slotModel);

		assertThat(parent.getChildren()).hasSize(2);
		assertThat(child.getParents()).hasSize(1);
	}

	@Test
	public void removeRelationBetweenComponentsByChildren()
	{
		final AbstractCMSComponentModel parent = new AbstractCMSComponentModel();
		final AbstractCMSComponentModel child = new AbstractCMSComponentModel();
		final List<AbstractCMSComponentModel> children = new ArrayList<>();
		final List<AbstractCMSComponentModel> parents = new ArrayList<>();
		parents.add(parent);
		parent.setChildren(children);
		child.setParents(parents);

		defaultRelationBetweenComponentsService.removeRelationBetweenComponentsOnModel(child);

		assertThat(child.getParents()).isEmpty();
	}

	@Test
	public void removeRelationBetweenComponentsByParent()
	{
		final AbstractCMSComponentModel parent = new AbstractCMSComponentModel();
		final AbstractCMSComponentModel child = new AbstractCMSComponentModel();
		final List<AbstractCMSComponentModel> children = new ArrayList<>();
		final List<AbstractCMSComponentModel> parents = new ArrayList<>();
		children.add(child);
		parent.setChildren(children);
		child.setParents(parents);

		defaultRelationBetweenComponentsService.removeRelationBetweenComponentsOnModel(parent);

		assertThat(parent.getChildren()).isEmpty();
	}
}
