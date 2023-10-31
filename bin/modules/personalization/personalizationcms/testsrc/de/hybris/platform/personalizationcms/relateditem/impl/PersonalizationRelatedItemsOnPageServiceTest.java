package de.hybris.platform.personalizationcms.relateditem.impl;

import de.hybris.bootstrap.annotations.UnitTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.HashSet;
import java.util.Set;

import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.personalizationcms.model.CxCmsComponentContainerModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.servicelayer.services.CMSComponentService;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.cms2.relateditems.impl.DefaultRelatedItemsOnPageService;
import de.hybris.platform.cms2.model.contents.components.SimpleCMSComponentModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForPageModel;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class PersonalizationRelatedItemsOnPageServiceTest
{
	@InjectMocks
	private PersonalizationRelatedItemsOnPageService personalizationRelatedItemsOnPageService;

	@Mock
	private Predicate<ItemModel> cxContainerPredicate;
	@Mock
	private CMSPageService cmsPageService;
	@Mock
	private CMSRestrictionService cmsRestrictionService;
	@Mock
	private CMSComponentService cmsComponentService;
	@Mock
	private AbstractPageModel pageModel;
	@Mock
	private CxCmsComponentContainerModel cxCmsComponentContainerModel;
	@Mock
	private SimpleCMSComponentModel componentModel;
	@Mock
	private ContentSlotModel slot1;
	@Mock
	private ContentSlotForPageModel slotForPageModel1;

	@Before
	public void setup()
	{
		final CatalogVersionModel catalogVersionModel = new CatalogVersionModel();
		final Set<AbstractCMSComponentModel> componentModels = new HashSet<>();
		componentModels.add(cxCmsComponentContainerModel);

		final Collection<ContentSlotForPageModel> slotForPageModels = new HashSet<>();
		slotForPageModels.add(slotForPageModel1);

		given(pageModel.getCatalogVersion()).willReturn(catalogVersionModel);
		given(cmsPageService.getOwnContentSlotsForPage(pageModel)).willReturn(slotForPageModels);
		given(slotForPageModel1.getContentSlot()).willReturn(slot1);
		given(slot1.getCmsComponents()).willReturn(new ArrayList<>(componentModels));
		given(cmsComponentService.getAllChildren(cxCmsComponentContainerModel)).willReturn(new HashSet<>());
		given(cmsRestrictionService.getOwnRestrictionsForPage(pageModel, catalogVersionModel)).willReturn(new HashSet<>());
		given(cmsRestrictionService.getOwnRestrictionsForComponents(componentModels, catalogVersionModel)).willReturn(new HashSet<>());

		given(cxContainerPredicate.test(cxCmsComponentContainerModel)).willReturn(true);
		given(cxCmsComponentContainerModel.getDefaultCmsComponent()).willReturn(componentModel);
		given(cmsComponentService.getAllChildren(componentModel)).willReturn(new HashSet<>());

		final Set<AbstractCMSComponentModel> componentModels2 = new HashSet<>();
		componentModels2.add(componentModel);
		given(cmsRestrictionService.getOwnRestrictionsForComponents(componentModels, catalogVersionModel)).willReturn(new HashSet<>());
	}

	@Test
	public void getRelatedItemsSlots()
	{
		Set<CMSItemModel> models = personalizationRelatedItemsOnPageService.getRelatedItems(pageModel);
		Assert.assertThat(models, hasSize(3));
	}
}
