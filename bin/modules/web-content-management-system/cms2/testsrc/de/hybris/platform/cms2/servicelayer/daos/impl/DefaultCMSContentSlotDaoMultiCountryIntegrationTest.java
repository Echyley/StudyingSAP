/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.daos.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.contentslot.ContentSlotModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.PageTemplateModel;
import de.hybris.platform.cms2.model.relations.ContentSlotForTemplateModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageTemplateDao;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultCMSContentSlotDaoMultiCountryIntegrationTest extends ServicelayerTransactionalTest
{
	// catalogs
	private static final String GLOBAL_CONTENT_CATALOG = "MultiCountryTestContentCatalog";
	private static final String REGION_CONTENT_CATALOG = "MultiCountryTestContentCatalog-region";
	private static final String LOCAL_CONTENT_CATALOG = "MultiCountryTestContentCatalog-local";

	// catalog versions
	private static final String ONLINE_CATALOG_VERSION = "OnlineVersion";
	private static final String STAGED_CATALOG_VERSION = "StagedVersion";

	// local homepage
	private static final String LOCAL_HOMEPAGE = "homepage-local";
	// clone of local homepage
	private static final String CLONE_LOCAL_HOMEPAGE = "homepage-local-clone";
	// regional homepage
	private static final String REGIONAL_HOMEPAGE = "homepage-regional";

	private static final String LOCAL_FOOTER_SLOT = "FooterSlot-local";
	private static final String LOCAL_SITE_LOGO_SLOT = "SiteLogoSlot-local";
	private static final String REGIONAL_FOOTER_SLOT = "FooterSlot-region";
	private static final String LOCAL_HEADER_LINKS_SLOT = "HeaderLinks-local";

	@Resource
	private CatalogVersionService catalogVersionService;
	@Resource
	private CMSPageDao cmsPageDao;
	@Resource
	private CMSPageTemplateDao cmsPageTemplateDao;
	@Resource
	private DefaultCMSContenSlotDao cmsContentSlotDao;

	private CatalogVersionModel globalOnlineCatalogVersion;
	private CatalogVersionModel localStagedCatalogVersion;
	private CatalogVersionModel regionOnlineCatalogVersion;
	private CatalogVersionModel regionStagedCatalogVersion;
	private List<CatalogVersionModel> catalogVersions;

	private AbstractPageModel localHomePage;
	private AbstractPageModel cloneLocalHomePage;
	private AbstractPageModel regionalHomePage;

	private PageTemplateModel homePageTemplate;

	private List<ContentSlotModel> templateContentSlots;

	@Before
	public void setUp() throws Exception
	{
		importCsv("/test/cmsMultiCountryPagesTestData.csv", "utf-8");
		globalOnlineCatalogVersion = catalogVersionService.getCatalogVersion(GLOBAL_CONTENT_CATALOG, ONLINE_CATALOG_VERSION);
		regionOnlineCatalogVersion = catalogVersionService.getCatalogVersion(REGION_CONTENT_CATALOG, ONLINE_CATALOG_VERSION);
		regionStagedCatalogVersion = catalogVersionService.getCatalogVersion(REGION_CONTENT_CATALOG, STAGED_CATALOG_VERSION);
		localStagedCatalogVersion = catalogVersionService.getCatalogVersion(LOCAL_CONTENT_CATALOG, STAGED_CATALOG_VERSION);
	}

	@Test
	public void shouldFindAllMultiCountryContentSlotsForLocalHomePageByOriginalSlots()
	{
		// GIVEN
		catalogVersions = Arrays.asList(globalOnlineCatalogVersion, regionOnlineCatalogVersion, localStagedCatalogVersion);
		localHomePage = cmsPageDao.findPagesById(LOCAL_HOMEPAGE, catalogVersions).get(0);
		homePageTemplate = cmsPageTemplateDao.findPageTemplatesByIdAndCatalogVersion("HomePageTemplate", globalOnlineCatalogVersion).get(0);
		templateContentSlots = cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndCatalogVersions(homePageTemplate, catalogVersions).stream().map(ContentSlotForTemplateModel::getContentSlot).collect(Collectors.toList());

		// WHEN
		final List<ContentSlotModel> results = cmsContentSlotDao.findAllMultiCountryContentSlotsByOriginalSlots(templateContentSlots, catalogVersions, localHomePage);

		// THEN
		assertThat("Must contain 2 slots (from local template and from local page relations)",results, hasSize(2));
		final ContentSlotModel contentSlot1 = results.get(0);
		final ContentSlotModel contentSlot2 = results.get(1);
		assertThat("Must contain one slot from local template and another one from local page relation", Arrays.asList(contentSlot1.getUid(), contentSlot2.getUid()), containsInAnyOrder(
				LOCAL_SITE_LOGO_SLOT, LOCAL_HEADER_LINKS_SLOT));
	}

	@Test
	public void shouldNotReturnLocalSlotThatIsNotRelatedToLocalHomePageOrItsTemplate()
	{
		// GIVEN
		catalogVersions = Arrays.asList(globalOnlineCatalogVersion, regionOnlineCatalogVersion, localStagedCatalogVersion);
		localHomePage = cmsPageDao.findPagesById(LOCAL_HOMEPAGE, catalogVersions).get(0);
		homePageTemplate = cmsPageTemplateDao.findPageTemplatesByIdAndCatalogVersion("HomePageTemplate", globalOnlineCatalogVersion).get(0);
		templateContentSlots = cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndCatalogVersions(homePageTemplate, catalogVersions).stream().map(ContentSlotForTemplateModel::getContentSlot).collect(Collectors.toList());

		// WHEN
		final List<ContentSlotModel> results = cmsContentSlotDao.findAllMultiCountryContentSlotsByOriginalSlots(templateContentSlots, catalogVersions, localHomePage);

		// THEN
		assertThat("Must not contain slot which is not defined in any relation (template or page)", results, not(containsInAnyOrder(LOCAL_FOOTER_SLOT)));
	}

	@Test
	public void shouldOnlyReturnOverriddenSlotDefinedOnRegionalAndGlobalLevel()
	{
		// GIVEN
		catalogVersions = Arrays.asList(globalOnlineCatalogVersion, regionStagedCatalogVersion);
		regionalHomePage = cmsPageDao.findPagesById(REGIONAL_HOMEPAGE, catalogVersions).get(0);
		homePageTemplate = cmsPageTemplateDao.findPageTemplatesByIdAndCatalogVersion("HomePageTemplate", globalOnlineCatalogVersion).get(0);
		templateContentSlots = cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndCatalogVersions(homePageTemplate, catalogVersions).stream().map(ContentSlotForTemplateModel::getContentSlot).collect(Collectors.toList());

		// WHEN
		final List<ContentSlotModel> results = cmsContentSlotDao.findAllMultiCountryContentSlotsByOriginalSlots(templateContentSlots, catalogVersions, regionalHomePage);

		// THEN
		assertThat("Must contain 1 overridden slot from regional template", results, hasSize(1));
		final ContentSlotModel contentSlot1 = results.get(0);
		assertThat("Must contain one slot from regional template",
				Collections.singletonList(contentSlot1.getUid()), containsInAnyOrder(REGIONAL_FOOTER_SLOT));
	}

	@Test
	public void shouldFindAllMultiCountryContentSlotsForClonedLocalHomePageByOriginalSlots()
	{
		// GIVEN
		catalogVersions = Arrays.asList(globalOnlineCatalogVersion, regionOnlineCatalogVersion, localStagedCatalogVersion);
		cloneLocalHomePage = cmsPageDao.findPagesById(CLONE_LOCAL_HOMEPAGE, catalogVersions).get(0);
		homePageTemplate = cmsPageTemplateDao.findPageTemplatesByIdAndCatalogVersion("HomePageTemplate", globalOnlineCatalogVersion).get(0);
		templateContentSlots = cmsContentSlotDao.findContentSlotRelationsByPageTemplateAndCatalogVersions(homePageTemplate, catalogVersions).stream().map(ContentSlotForTemplateModel::getContentSlot).collect(Collectors.toList());

		// WHEN
		final List<ContentSlotModel> results = cmsContentSlotDao.findAllMultiCountryContentSlotsByOriginalSlots(templateContentSlots, catalogVersions, cloneLocalHomePage);

		// THEN
		assertThat("Must contain 1 overridden slot from local template", results, hasSize(1));
		final ContentSlotModel contentSlot1 = results.get(0);
		assertThat("Must contain one slot from local template",
				Collections.singletonList(contentSlot1.getUid()), containsInAnyOrder(LOCAL_SITE_LOGO_SLOT));
	}

}
