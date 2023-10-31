/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.services.impl;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.data.ContentSlotData;
import de.hybris.platform.cms2.servicelayer.services.CMSPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminContentSlotService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminPageService;
import de.hybris.platform.cms2.servicelayer.services.admin.CMSAdminSiteService;
import de.hybris.platform.core.servicelayer.data.PaginationData;
import de.hybris.platform.core.servicelayer.data.SearchPageData;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSPageServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String ACTIVE_CATALOG_VERSION = "activeCatalogVersion";
	private static final String CONTENT_SLOT = "contentSlot";
	private static final String UID = "uid";
	private static final String STAGED_VERSION = "StagedVersion";
	private static final String ONLINE_VERSION = "OnlineVersion";
	private static final String MULTI_COUNTRY_TEST_CONTENT_CATALOG = "MultiCountryTestContentCatalog";
	private static final String MULTI_COUNTRY_TEST_CONTENT_CATALOG_REGION =  "MultiCountryTestContentCatalog-region";
	private static final String MULTI_COUNTRY_TEST_CONTENT_CATALOG_LOCAL =  "MultiCountryTestContentCatalog-local";
	private static final String FOOTER_SLOT_LOCAL = "FooterSlot-local";
	private static final String SITE_LOGO_SLOT_REGION = "SiteLogoSlot-region";
	private static final String MINI_CART_SLOT = "MiniCartSlot";

	@Resource
	private SessionService sessionService;
	@Resource
	private ModelService modelService;
	@Resource
	private CMSPageService cmsPageService;
	@Resource
	private CMSAdminSiteService cmsAdminSiteService;
	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CMSAdminPageService cmsAdminPageService;
	@Resource
	private CMSAdminContentSlotService cmsAdminContentSlotService;

	private ContentSlotModel globalSlot;
	private ContentSlotModel regionSlot;
	private ContentSlotModel localSlot;
	private CatalogVersionModel globalOnlineCatalog;
	private CatalogVersionModel globalStagedCatalog;
	private CatalogVersionModel regionStagedCatalog;
	private CatalogVersionModel regionOnlineCatalog;
	private CatalogVersionModel localStagedCatalog;

	private List<CatalogVersionModel> allCatalogVersions;

	@Before
	public void setUp() throws ImpExException
	{
		importCsv("/test/cmsMultiCountryTestData.csv", "UTF-8");

		globalOnlineCatalog = catalogVersionService.getCatalogVersion(MULTI_COUNTRY_TEST_CONTENT_CATALOG, ONLINE_VERSION);
		globalStagedCatalog = catalogVersionService.getCatalogVersion(MULTI_COUNTRY_TEST_CONTENT_CATALOG, STAGED_VERSION);
		regionStagedCatalog = catalogVersionService.getCatalogVersion(MULTI_COUNTRY_TEST_CONTENT_CATALOG_REGION, STAGED_VERSION);
		regionOnlineCatalog = catalogVersionService.getCatalogVersion(MULTI_COUNTRY_TEST_CONTENT_CATALOG_REGION, ONLINE_VERSION);
		localStagedCatalog = catalogVersionService.getCatalogVersion(MULTI_COUNTRY_TEST_CONTENT_CATALOG_LOCAL, STAGED_VERSION);
		allCatalogVersions = Arrays.asList(globalOnlineCatalog, regionStagedCatalog, localStagedCatalog);

		globalSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("FooterSlot",
				Arrays.asList(globalOnlineCatalog));
		regionSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("FooterSlot-region",
				Arrays.asList(regionStagedCatalog));
		localSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions(FOOTER_SLOT_LOCAL,
				Arrays.asList(localStagedCatalog));

		final CMSSiteModel site = new CMSSiteModel();
		site.setContentCatalogs(Arrays.asList((ContentCatalogModel) globalOnlineCatalog.getCatalog(),
				(ContentCatalogModel) regionStagedCatalog.getCatalog(), (ContentCatalogModel) localStagedCatalog.getCatalog()));
		site.setActive(Boolean.TRUE);
		site.setUid("MultiCountrySite");
		modelService.save(site);
		cmsAdminSiteService.setActiveSite(site);
	}

	@Test
	public void shouldSortLocalBeforeRegionContentSlot()
	{
		final List<ContentSlotModel> results = cmsPageService
				.getSortedMultiCountryContentSlots(Arrays.asList(globalSlot, regionSlot, localSlot), allCatalogVersions);

		assertThat(results, containsInAnyOrder(localSlot, regionSlot));
	}

	@Test
	public void shouldSortContentSlotBeEmpty_NoLocalizationForRootSlot()
	{
		final List<ContentSlotModel> results = cmsPageService.getSortedMultiCountryContentSlots(Arrays.asList(globalSlot),
				Arrays.asList(globalOnlineCatalog));

		assertThat(results, empty());
	}

	@Test
	public void shouldGetContentSlotsForPage()
	{
		// This test should find all content slots defined for the page template and 1 content defined for the page
		sessionService.setAttribute(ACTIVE_CATALOG_VERSION, regionStagedCatalog.getPk());
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(globalStagedCatalog, regionStagedCatalog));
		final AbstractPageModel regionPage = cmsAdminPageService.getPageForIdFromActiveCatalogVersion("TestHomePageRegionEU");

		final Collection<ContentSlotData> results = cmsPageService.getContentSlotsForPage(regionPage);

		final ContentSlotModel expectedOverrideSlot = cmsAdminContentSlotService
				.getContentSlotForIdAndCatalogVersions("Section1Slot-TestHomePageRegionEU", Arrays.asList(regionStagedCatalog));
		final ContentSlotModel expectedGlobalSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("SiteLogoSlot",
				Arrays.asList(globalStagedCatalog));
		final ContentSlotModel expectedRegionSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("FooterSlot-region",
				Arrays.asList(regionStagedCatalog));

		assertThat(results,
				hasItems(
						allOf(hasProperty(UID, equalTo(expectedOverrideSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedOverrideSlot))),
						allOf(hasProperty(UID, equalTo(expectedGlobalSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedGlobalSlot))),
						allOf(hasProperty(UID, equalTo(expectedRegionSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedRegionSlot)))
						));
	}

	/**
	 * In multi-country, a position of one page template may have multi-slot.
	 * If in current content catalog doesn't have slot at position SiteLogo,
	 * it should use the online parent content catalog slot at the same position.
	 * For example, if region catalog at SiteLogo does not have slot, it should use the global online version slot at the SiteLogo.
	 * if local catalog at SiteLogo does not have slot, it should use the region online version slot at the siteLogo;
	 * if region online also has no slot, it should use the online global version slot at the SiteLogo.
	 */
	@Test
	public void shouldGetContentSlotsForPageInMultiCountry()
	{
		sessionService.setAttribute(ACTIVE_CATALOG_VERSION, localStagedCatalog.getPk());

		final AbstractPageModel localPage = cmsAdminPageService.getPageForIdFromActiveCatalogVersion("TestHomePageLocalIT");

		final Collection<ContentSlotData> results = cmsPageService.getContentSlotsForPage(localPage);

		//page slot
		final ContentSlotModel expectedOverrideSlot = cmsAdminContentSlotService
				.getContentSlotForIdAndCatalogVersions("Section1Slot-TestHomePageLocalIT", Arrays.asList(localStagedCatalog));

		//parent template slot of global
		final ContentSlotModel expectedGlobalSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions(MINI_CART_SLOT,
				Arrays.asList(globalStagedCatalog));

		//parent template slot of region
		final ContentSlotModel expectedRegionSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions(SITE_LOGO_SLOT_REGION,
				Arrays.asList(regionOnlineCatalog));

		// template slot of local
		final ContentSlotModel expectedLocalSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions(FOOTER_SLOT_LOCAL,
				Arrays.asList(localStagedCatalog));
      // a non-shared slot replace the template slot
		final ContentSlotModel nonSharedSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("SearchBoxSlot-TestHomePageLocalIT",
				Arrays.asList(localStagedCatalog));

		assertThat(results,
				hasItems(
						allOf(hasProperty(UID, equalTo(expectedOverrideSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedOverrideSlot))),
						allOf(hasProperty(UID, equalTo(expectedGlobalSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedGlobalSlot))),
						allOf(hasProperty(UID, equalTo(expectedRegionSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedRegionSlot))),
						allOf(hasProperty(UID, equalTo(expectedLocalSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedLocalSlot))),
						allOf(hasProperty(UID, equalTo(nonSharedSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(nonSharedSlot)))));
	}

	@Test
	public void shouldGetContentSlotsForPageInMultiCountryAndPageHaveNoNonSharedSlotsOnOtherPage()
	{
		sessionService.setAttribute(ACTIVE_CATALOG_VERSION, localStagedCatalog.getPk());

		final AbstractPageModel localPage = cmsAdminPageService.getPageForIdFromActiveCatalogVersion("TestSearchPageLocalIT");

		final Collection<ContentSlotData> results = cmsPageService.getContentSlotsForPage(localPage);

		//parent template slot of global
		final ContentSlotModel expectedGlobalSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions(MINI_CART_SLOT,
				Arrays.asList(globalStagedCatalog));

		//parent template slot of region
		final ContentSlotModel expectedRegionSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions(SITE_LOGO_SLOT_REGION,
				Arrays.asList(regionOnlineCatalog));

		// template slot of local
		final ContentSlotModel expectedLocalSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions(FOOTER_SLOT_LOCAL,
				Arrays.asList(localStagedCatalog));

		// a non-shared slot replace the template slot
		final ContentSlotModel nonSharedSlot = cmsAdminContentSlotService.getContentSlotForIdAndCatalogVersions("SearchBoxSlot-TestHomePageLocalIT",
				Arrays.asList(localStagedCatalog));

		assertThat(results,
				hasItems(
						allOf(hasProperty(UID, equalTo(expectedGlobalSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedGlobalSlot))),
						allOf(hasProperty(UID, equalTo(expectedRegionSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedRegionSlot))),
						allOf(hasProperty(UID, equalTo(expectedLocalSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(expectedLocalSlot)))));

		assertThat(results,
				not(hasItems(
						allOf(hasProperty(UID, equalTo(nonSharedSlot.getUid())),
								hasProperty(CONTENT_SLOT, equalTo(nonSharedSlot)))
						)));

	}

	@Test
	public void shouldFindAllPages()
	{
		final SearchPageData pageableData = createPaginationData();
		catalogVersionService.setSessionCatalogVersions(Arrays.asList(globalStagedCatalog));

		final SearchPageData<AbstractPageModel> result = cmsPageService.findAllPages(AbstractPageModel._TYPECODE, pageableData);

		assertThat(result.getPagination().getHasNext(), equalTo(Boolean.TRUE));
		assertThat(result.getPagination().getTotalNumberOfResults(), greaterThanOrEqualTo(15l));
		assertThat(result.getPagination().getNumberOfPages(), greaterThanOrEqualTo(3));
	}

	protected SearchPageData createPaginationData()
	{
		final SearchPageData pageableData = new SearchPageData();
		final PaginationData paginationData = new PaginationData();
		paginationData.setCurrentPage(0);
		paginationData.setPageSize(5);
		paginationData.setNeedsTotal(true);
		pageableData.setPagination(paginationData);
		return pageableData;
	}
}
