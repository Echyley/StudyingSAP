/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.relateditems.impl;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;
import de.hybris.platform.cms2.model.restrictions.AbstractRestrictionModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultRelatedItemsOnPageServiceTest
{
	@InjectMocks
	private DefaultRelatedItemsOnPageService defaultRelatedItemsOnPageService;

	@Mock
	private CMSPageService cmsPageService;

	@Mock
	private CMSRestrictionService cmsRestrictionService;
	@Mock
	private CMSComponentService cmsComponentService;

	@Mock
	private AbstractPageModel pageModel;

	@Mock
	private ContentSlotForPageModel slotForPageModel1;

	@Mock
	private ContentSlotModel slot1;

	@Mock
	private AbstractCMSComponentModel componentModel;

	@Before
	public void setup()
	{
		final Collection<ContentSlotForPageModel> slotForPageModels = new HashSet<>();
		slotForPageModels.add(slotForPageModel1);
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		final Set<AbstractCMSComponentModel> componentModels = new HashSet<>();
		componentModels.add(componentModel);
		final Set<AbstractCMSComponentModel> childComponentModels = new HashSet<>();
		final AbstractCMSComponentModel childComponent = new AbstractCMSComponentModel();
		childComponentModels.add(childComponent);

		final Set<AbstractRestrictionModel> restrictionsForComponent = new HashSet<>();
		final Set<AbstractRestrictionModel> restrictionsForPage = new HashSet<>();
		final Set<AbstractRestrictionModel> restrictionsForChildComponent = new HashSet<>();
		final AbstractRestrictionModel restrictionForComponent = new AbstractRestrictionModel();
		final AbstractRestrictionModel restrictionForPage = new AbstractRestrictionModel();
		final AbstractRestrictionModel restrictionForChildComponent = new AbstractRestrictionModel();
		restrictionForComponent.setName("restrictionForComponent");
		restrictionsForComponent.add(restrictionForComponent);
		restrictionsForPage.add(restrictionForPage);
		restrictionForChildComponent.setName("restrictionForChildComponent");
		restrictionsForChildComponent.add(restrictionForChildComponent);


		given(pageModel.getCatalogVersion()).willReturn(catalogVersionModel);
		given(cmsPageService.getOwnContentSlotsForPage(pageModel)).willReturn(slotForPageModels);
		given(slotForPageModel1.getContentSlot()).willReturn(slot1);
		given(slot1.getCmsComponents()).willReturn(new ArrayList<>(componentModels));
		given(cmsComponentService.getAllChildren(componentModel)).willReturn(childComponentModels);
		given(cmsRestrictionService.getOwnRestrictionsForPage(pageModel, catalogVersionModel)).willReturn(restrictionsForPage);
		given(cmsRestrictionService.getOwnRestrictionsForComponents(componentModels, catalogVersionModel)).willReturn(restrictionsForComponent);
		given(cmsRestrictionService.getOwnRestrictionsForComponents(childComponentModels, catalogVersionModel)).willReturn(restrictionsForChildComponent);

	}

	@Test
	public void getRelatedItemsSlots()
	{
		Set<CMSItemModel> models = defaultRelatedItemsOnPageService.getRelatedItems(pageModel);
		Assert.assertThat(models, hasSize(6));
	}

}
