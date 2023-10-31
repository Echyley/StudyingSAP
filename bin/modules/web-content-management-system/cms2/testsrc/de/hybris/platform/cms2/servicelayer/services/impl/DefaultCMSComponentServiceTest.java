/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSComponentDao;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;

import java.util.*;

import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.type.AttributeDescriptorModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.type.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


@UnitTest
public class DefaultCMSComponentServiceTest
{
	private static final String NON_COMPONENT_ITEM_TYPE = "some non-component type";

	@Mock
	private AbstractCMSComponentModel componentModel;

	@Mock
	private ItemModel nonComponentModel;

	@Mock
	private AbstractPageModel pageModel;

	@Mock
	private ComposedTypeModel composedTypeModel;

	@Mock
	private AttributeDescriptorModel readableAttributeDescriptorModel;

	@Mock
	private AttributeDescriptorModel nonreadableAttributeDescriptorModel;

	@Mock
	private CMSComponentDao cmsComponentDao;

	@Mock
	private TypeService typeService;

	@Mock
	private ModelService modelService;

	@Mock
	private CMSContentSlotService cmsContentSlotService;

	@Mock
	private AbstractCMSComponentModel parentModel;

	@Mock
	private List<String> systemProperties;

	@Mock
	private AbstractCMSComponentModel componentParentModel;
	@Mock
	private AbstractCMSComponentModel componentParentModel2;

	@Mock
	private AbstractCMSComponentModel componentChildModel;
	@Mock
	private AbstractCMSComponentModel componentChildModel2;

	@InjectMocks
	private final CMSComponentService cmsComponentService = new DefaultCMSComponentService();

	private Set<AttributeDescriptorModel> attributeDescriptorModelSet = new HashSet<>();


	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);

		attributeDescriptorModelSet.add(readableAttributeDescriptorModel);
		attributeDescriptorModelSet.add(nonreadableAttributeDescriptorModel);

		given(componentModel.getItemtype()).willReturn(AbstractCMSComponentModel.ITEMTYPE);
		given(componentModel.getTypeCode()).willReturn(AbstractCMSComponentModel.ITEMTYPE);
		given(nonComponentModel.getItemtype()).willReturn(NON_COMPONENT_ITEM_TYPE);

		given(typeService.getComposedTypeForCode(AbstractCMSComponentModel.ITEMTYPE)).willReturn(composedTypeModel);
		given(typeService.getAttributeDescriptorsForType(composedTypeModel)).willReturn(attributeDescriptorModelSet);
		given(readableAttributeDescriptorModel.getReadable()).willReturn(Boolean.TRUE);
		given(nonreadableAttributeDescriptorModel.getReadable()).willReturn(Boolean.FALSE);

		given(typeService.isAssignableFrom(AbstractCMSComponentModel._TYPECODE, AbstractCMSComponentModel.ITEMTYPE))
				.willReturn(true);
		given(typeService.isAssignableFrom(AbstractCMSComponentModel._TYPECODE, NON_COMPONENT_ITEM_TYPE)).willReturn(false);
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isCmsComponentRestricted(de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)}
	 * .
	 */
	@Test
	public void shouldBeNotRestrictedWhenRestrictionsAreNull()
	{
		// given
		given(componentModel.getRestrictions()).willReturn(null);

		// when
		final boolean restricted = cmsComponentService.isComponentRestricted(componentModel);

		// then
		assertThat(restricted).isFalse();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isCmsComponentRestricted(de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)}
	 * .
	 */
	@Test
	public void shouldBeNotRestrictedWhenRestrictionsAreEmptyList()
	{
		// given
		given(componentModel.getRestrictions()).willReturn(Collections.EMPTY_LIST);

		// when
		final boolean restricted = cmsComponentService.isComponentRestricted(componentModel);

		// then
		assertThat(restricted).isFalse();
	}

	/**
	 * Test method for
	 * {@link de.hybris.platform.cms2.servicelayer.services.impl.DefaultCMSComponentService#isCmsComponentRestricted(de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel)}
	 * .
	 */
	@Test
	public void shouldBeRestrictedWhenRestrictionsAreNotEmptyList()
	{
		// given
		final List<AbstractRestrictionModel> restrictionsMock = mock(ArrayList.class);
		given(Boolean.valueOf(restrictionsMock.isEmpty())).willReturn(Boolean.FALSE);
		given(componentModel.getRestrictions()).willReturn(restrictionsMock);

		// when
		final boolean restricted = cmsComponentService.isComponentRestricted(componentModel);

		// then
		assertThat(restricted).isTrue();
	}

	@Test
	public void shouldReturnAllEditorProperties()
	{
		// when
		final Collection<String> properties = cmsComponentService.getEditorProperties(componentModel);

		// then
		assertThat(properties.size()).isEqualTo(2);
	}

	@Test
	public void shouldReturnOnlyReadableEditorProperties()
	{
		// when
		final Collection<String> properties = cmsComponentService.getReadableEditorProperties(componentModel);

		// then
		assertThat(properties.size()).isEqualTo(1);
	}

	@Test
	public void givenComponentHasOneOrMoreReferencesOusidePage_WhenIsComponentUsedOutsidePageIsCalled_ThenItReturnsTrue()
	{
		// GIVEN
		given(cmsComponentDao.getComponentReferenceCountOutsidePage(componentModel, pageModel)).willReturn(1L);

		// WHEN
		boolean componentIsUsedOutsidePage = cmsComponentService.isComponentUsedOutsidePage(componentModel, pageModel);

		// THEN
		assertTrue(componentIsUsedOutsidePage);
	}

	@Test
	public void givenComponentHasZeroReferencesOusidePage_WhenIsComponentUsedOutsidePageIsCalled_ThenItReturnsFalse()
	{
		// GIVEN
		given(cmsComponentDao.getComponentReferenceCountOutsidePage(componentModel, pageModel)).willReturn(0L);

		// WHEN
		boolean componentIsUsedOutsidePage = cmsComponentService.isComponentUsedOutsidePage(componentModel, pageModel);

		// THEN
		assertFalse(componentIsUsedOutsidePage);
	}

	@Test
	public void ReturnEmptyArrayWhenComponentHasNoParents()
	{
		// GIVEN
		given(componentModel.getParents()).willReturn(null);
		// WHEN
		Set<AbstractCMSComponentModel> parents = cmsComponentService.getAllParents(componentModel);

		// THEN
		assertThat(parents.size()).isEqualTo(0);
	}

	@Test
	public void ReturnArrayWhenComponentHasParents()
	{
		// GIVEN
		List<AbstractCMSComponentModel> parents = new ArrayList<>();
		AbstractCMSComponentModel parent1 = new AbstractCMSComponentModel();
		AbstractCMSComponentModel parent2 = new AbstractCMSComponentModel();
		parents.add(parent1);
		parents.add(parent2);
		given(componentModel.getParents()).willReturn(parents);
		given(modelService.isNew(parent1)).willReturn(true);
		given(modelService.isNew(parent2)).willReturn(true);

		// WHEN
		Set<AbstractCMSComponentModel> getParents = cmsComponentService.getAllParents(componentModel);

		// THEN
		assertThat(getParents.size()).isEqualTo(2);
	}

	@Test
	public void SuccessReturnWhenComponentParentsHasCycle()
	{
		// GIVEN
		List<AbstractCMSComponentModel> parents = new ArrayList<>();
		parents.add(componentParentModel);
		parents.add(componentParentModel2);
		given(componentParentModel2.getParents()).willReturn(new ArrayList<>());
		given(componentModel.getParents()).willReturn(parents);

		List<AbstractCMSComponentModel> grandParents = new ArrayList<>();
		grandParents.add(componentParentModel2);
		given(componentParentModel.getParents()).willReturn(grandParents);
		// WHEN
		Set<AbstractCMSComponentModel> getParents = cmsComponentService.getAllParents(componentModel);

		// THEN
		assertThat(getParents.size()).isEqualTo(2);
	}

	@Test
	public void SuccessReturnWhenComponentParentsHasCycle2()
	{
		// GIVEN
		List<AbstractCMSComponentModel> parents = new ArrayList<>();
		parents.add(componentParentModel);
		parents.add(componentParentModel2);
		given(componentModel.getParents()).willReturn(parents);
		given(componentParentModel.getParents()).willReturn(parents);
		// WHEN
		Set<AbstractCMSComponentModel> getParents = cmsComponentService.getAllParents(componentModel);

		// THEN
		assertThat(getParents.size()).isEqualTo(2);
	}

	public void ReturnEmptyArrayWhenComponentHasNoChildren()
	{
		// GIVEN
		given(componentModel.getChildren()).willReturn(null);
		// WHEN
		Set<AbstractCMSComponentModel> children = cmsComponentService.getAllChildren(componentModel);

		// THEN
		assertThat(children.size()).isEqualTo(0);
	}

	@Test
	public void ReturnArrayWhenComponentHasChildren()
	{
		// GIVEN
		List<AbstractCMSComponentModel> children = new ArrayList<>();
		AbstractCMSComponentModel child1 = new AbstractCMSComponentModel();
		AbstractCMSComponentModel child2 = new AbstractCMSComponentModel();
		children.add(child1);
		children.add(child2);
		given(componentModel.getChildren()).willReturn(children);
		given(modelService.isNew(child1)).willReturn(true);
		given(modelService.isNew(child2)).willReturn(true);

		// WHEN
		Set<AbstractCMSComponentModel> allChildren = cmsComponentService.getAllChildren(componentModel);

		// THEN
		assertThat(allChildren.size()).isEqualTo(2);
	}

	@Test
	public void SuccessReturnWhenComponentChildrenHasCycle()
	{
		// GIVEN
		List<AbstractCMSComponentModel> children = new ArrayList<>();
		children.add(componentChildModel);
		children.add(componentChildModel2);
		given(componentModel.getChildren()).willReturn(children);

		List<AbstractCMSComponentModel> grandChildren = new ArrayList<>();
		grandChildren.add(componentModel);
		given(componentChildModel.getParents()).willReturn(grandChildren);
		// WHEN
		Set<AbstractCMSComponentModel> getChildren = cmsComponentService.getAllChildren(componentModel);

		// THEN
		assertThat(getChildren.size()).isEqualTo(2);
	}

	@Test
	public void ComponentInSharedSlots()
	{
		//GIVEN
		List<ContentSlotModel> slots = new ArrayList<>();
		ContentSlotModel sharedSlot = new ContentSlotModel();
		slots.add(sharedSlot);
		given(componentModel.getSlots()).willReturn(slots);
		given(modelService.isNew(sharedSlot)).willReturn(false);
		given(cmsContentSlotService.isSharedSlot(sharedSlot)).willReturn(true);

		assertTrue(cmsComponentService.inSharedSlots(componentModel));
	}

	@Test
	public void ComponentParentInSharedSlots()
	{
		//GIVEN
		given(componentModel.getSlots()).willReturn(null);

		List<AbstractCMSComponentModel> parents = new ArrayList<>();

		parents.add(parentModel);
		given(componentModel.getParents()).willReturn(parents);
		given(modelService.isNew(parentModel)).willReturn(true);

		List<ContentSlotModel> slots = new ArrayList<>();
		ContentSlotModel sharedSlot = new ContentSlotModel();
		slots.add(sharedSlot);
		given(parentModel.getSlots()).willReturn(slots);
		given(modelService.isNew(sharedSlot)).willReturn(false);
		given(cmsContentSlotService.isSharedSlot(sharedSlot)).willReturn(true);

		assertTrue(cmsComponentService.inSharedSlots(componentModel));
	}

	@Test
	public void ComponentNotInSharedSlots()
	{
		//GIVEN
		given(componentModel.getSlots()).willReturn(null);

		assertFalse(cmsComponentService.inSharedSlots(componentModel));
	}
}
