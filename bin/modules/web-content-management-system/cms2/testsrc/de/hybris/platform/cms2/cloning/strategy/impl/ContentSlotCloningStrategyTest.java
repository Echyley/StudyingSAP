/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.cloning.strategy.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.cloning.service.CMSItemCloningService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.constants.Cms2Constants;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.servicelayer.services.CMSContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.enums.CloneAction;
import de.hybris.platform.servicelayer.model.ModelService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class ContentSlotCloningStrategyTest
{
	private static final String CONTENT_SLOT_POSITION = "contentSlotPosition";
	private static final boolean CONTENT_SLOT_ACTIVE = true;
	private static final Date CONTENT_SLOT_ACTIVE_FROM = new Date();
	private static final Date CONTENT_SLOT_ACTIVE_UNTIL = new Date();

	@Spy
	@InjectMocks
	private ContentSlotCloningStrategy strategy;

	@Mock
	private SessionSearchRestrictionsDisabler cmsSessionSearchRestrictionsDisabler;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CMSItemCloningService cmsItemCloningService;
	@Mock
	private CMSAdminContentSlotService cmsAdminContentSlotService;
	@Mock
	private ModelService modelService;
	@Mock
	private CatalogVersionModel targetCatalogVersionModel;
	@Mock
	private AbstractPageModel sourcePageModel;
	@Mock
	private ContentSlotModel sourceContentSlotModel;
	@Mock
	private ContentSlotForPageModel newContentSlotForPageModel;
	@Mock
	private ContentSlotForTemplateModel newContentSlotForTemplateModel;
	@Mock
	private AbstractCMSComponentModel cloneableCmsComponentModel;
	@Mock
	private AbstractCMSComponentModel nonCloneableCmsComponentModel;
	@Mock
	private CMSContentSlotService cmsContentSlotService;
	@Mock
	private PageTemplateModel pageTemplateModel;
	@Mock
	protected ContentSlotModel clonedContentSlotModel;

	@Before
	public void setUp()
	{
		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier<?> supplier = (Supplier<?>) args[0];
			return supplier.get();
		}).when(cmsSessionSearchRestrictionsDisabler).execute(any());

		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(Collections.singletonList(targetCatalogVersionModel));

		when(cmsAdminContentSlotService.getContentSlotPosition(sourcePageModel, sourceContentSlotModel)).thenReturn(CONTENT_SLOT_POSITION);

		when(sourcePageModel.getMasterTemplate()).thenReturn(pageTemplateModel);

		when(modelService.create(ContentSlotForPageModel.class)).thenReturn(newContentSlotForPageModel);
		when(modelService.create(ContentSlotForTemplateModel.class)).thenReturn(newContentSlotForTemplateModel);

		when(sourceContentSlotModel.getActive()).thenReturn(CONTENT_SLOT_ACTIVE);
		when(sourceContentSlotModel.getActiveFrom()).thenReturn(CONTENT_SLOT_ACTIVE_FROM);
		when(sourceContentSlotModel.getActiveUntil()).thenReturn(CONTENT_SLOT_ACTIVE_UNTIL);
		when(sourceContentSlotModel.getCmsComponents()).thenReturn(Arrays.asList(cloneableCmsComponentModel, nonCloneableCmsComponentModel));

		when(cmsContentSlotService.getDefinedContentSlotPositions(sourcePageModel)).thenReturn(Arrays.asList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenTemplateIsEmpty() throws CMSItemNotFoundException
	{
		strategy.clone(sourceContentSlotModel, Optional.empty(), Optional.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenContextIsEmpty() throws CMSItemNotFoundException
	{
		strategy.clone(sourceContentSlotModel, Optional.of(new ContentSlotModel()), Optional.empty());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCloneWhenPageNotInContext() throws CMSItemNotFoundException
	{
		strategy.clone(sourceContentSlotModel, Optional.of(new ContentSlotModel()), Optional.of(new HashMap<>()));
	}

	@Test
	public void shouldCreateCustomComponent() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePageModel);
		context.put(Cms2Constants.IS_SLOT_CUSTOM, true);
		context.put(Cms2Constants.CLONE_ACTION, CloneAction.CLONE);

		// WHEN
		strategy.clone(sourceContentSlotModel, Optional.of(clonedContentSlotModel), Optional.of(context));

		// THEN
		verify(cmsSessionSearchRestrictionsDisabler).execute(any());
		verify(modelService).create(ContentSlotForPageModel.class);
		verify(newContentSlotForPageModel).setPosition(CONTENT_SLOT_POSITION);
		verify(newContentSlotForPageModel).setCatalogVersion(targetCatalogVersionModel);
		verify(newContentSlotForPageModel).setPage(sourcePageModel);
		verify(newContentSlotForPageModel).setContentSlot(clonedContentSlotModel);
	}

	@Test
	public void shouldCreateLocalComponent() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePageModel);
		context.put(Cms2Constants.IS_SLOT_CUSTOM, false);
		context.put(Cms2Constants.CLONE_ACTION, CloneAction.CLONE);

		// WHEN
		strategy.clone(sourceContentSlotModel, Optional.of(clonedContentSlotModel), Optional.of(context));

		// THEN
		verify(cmsSessionSearchRestrictionsDisabler).execute(any());
		verify(modelService, never()).create(ContentSlotForPageModel.class);
		verify(modelService).create(ContentSlotForTemplateModel.class);
		verify(newContentSlotForTemplateModel).setPosition(CONTENT_SLOT_POSITION);
		verify(newContentSlotForTemplateModel).setCatalogVersion(targetCatalogVersionModel);
		verify(newContentSlotForTemplateModel).setPageTemplate(pageTemplateModel);
		verify(newContentSlotForTemplateModel).setContentSlot(clonedContentSlotModel);
	}

	@Test
	public void shouldCloneComponentsInsideNewSlot() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePageModel);
		context.put(Cms2Constants.IS_SLOT_CUSTOM, true);
		context.put(Cms2Constants.CLONE_ACTION, CloneAction.CLONE);

		// WHEN
		strategy.clone(sourceContentSlotModel, Optional.of(clonedContentSlotModel), Optional.of(context));

		// THEN
		verify(cmsItemCloningService).cloneContentSlotComponents(sourceContentSlotModel, clonedContentSlotModel, targetCatalogVersionModel);
	}

	@Test
	public void shouldRemoveComponentsInsideNewSlot() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePageModel);
		context.put(Cms2Constants.IS_SLOT_CUSTOM, true);
		context.put(Cms2Constants.CLONE_ACTION, CloneAction.REMOVE);

		// WHEN
		strategy.clone(sourceContentSlotModel, Optional.of(clonedContentSlotModel), Optional.of(context));

		// THEN
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(clonedContentSlotModel).setCmsComponents(captor.capture());
		assertTrue("The list of components inside new slot must be empty", captor.getValue().isEmpty());
	}

	@Test
	public void shouldReferenceComponentsInsideNewSlot() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePageModel);
		context.put(Cms2Constants.IS_SLOT_CUSTOM, true);
		context.put(Cms2Constants.CLONE_ACTION, CloneAction.REFERENCE);

		// WHEN
		strategy.clone(sourceContentSlotModel, Optional.of(clonedContentSlotModel), Optional.of(context));

		// THEN
		ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
		verify(clonedContentSlotModel).setCmsComponents(captor.capture());
		assertFalse("The list of elements must not be empty", captor.getValue().isEmpty());
		assertTrue("The list must contain references to elements from source content slot", captor.getValue().containsAll(Arrays.asList(cloneableCmsComponentModel, nonCloneableCmsComponentModel)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionIfNewCustomSLotPositionIsTaken() throws CMSItemNotFoundException
	{
		// GIVEN
		final Map<String, Object> context = new HashMap<>();
		context.put(Cms2Constants.PAGE_CONTEXT_KEY, sourcePageModel);
		context.put(Cms2Constants.IS_SLOT_CUSTOM, true);
		context.put(Cms2Constants.CLONE_ACTION, CloneAction.REFERENCE);
		when(cmsContentSlotService.getDefinedContentSlotPositions(sourcePageModel)).thenReturn(Arrays.asList(CONTENT_SLOT_POSITION));

		// WHEN
		strategy.clone(sourceContentSlotModel, Optional.of(clonedContentSlotModel), Optional.of(context));

		// THEN
	}
}
