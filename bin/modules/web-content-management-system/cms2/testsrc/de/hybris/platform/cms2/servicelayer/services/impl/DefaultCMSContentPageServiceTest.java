/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.servicelayer.services.impl;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.catalogversion.service.CMSCatalogVersionService;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.data.PagePreviewCriteriaData;
import de.hybris.platform.cms2.enums.CmsPageStatus;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.CMSVersionModel;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.servicelayer.daos.CMSPageDao;
import de.hybris.platform.cms2.servicelayer.services.CMSPreviewService;
import de.hybris.platform.cms2.version.service.CMSVersionSessionContextProvider;
import de.hybris.platform.cms2.servicelayer.daos.CMSVersionDao;
import de.hybris.platform.cms2.servicelayer.services.CMSRestrictionService;
import de.hybris.platform.cms2.servicelayer.services.CMSSiteService;
import de.hybris.platform.cms2.version.service.CMSVersionService;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCMSContentPageServiceTest
{
	private final String PAGE1_ID = "my-page-default";
	private final String PAGE1_LABEL = "/mypage/default";
	private final List<CmsPageStatus> ACTIVE_STATUS = Arrays.asList(CmsPageStatus.ACTIVE);

	@InjectMocks
	private DefaultCMSContentPageService contentPageService;

	@Mock
	private CMSPageDao cmsPageDao;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CMSRestrictionService cmsRestrictionService;
	@Mock
	private CMSVersionService cmsVersionService;
	@Mock
	private CMSVersionDao cmsVersionDao;
	@Mock
	private CMSVersionSessionContextProvider cmsVersionSessionContextProvider;
	@Mock
	private Comparator<AbstractPageModel> cmsItemCatalogLevelComparator;
	@Mock
	private CMSSiteService cmsSiteService;

	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionsDisabler;

	@Mock
	private CatalogVersionModel catalogVersion1;
	@Mock
	private CatalogVersionModel catalogVersion2;
	@Mock
	private CatalogVersionModel catalogVersion3;
	@Mock
	private CMSVersionModel cmsVersionModel;
	@Mock
	private ContentPageModel page1;
	@Mock
	private ContentPageModel page2;
	@Mock
	private ContentCatalogModel contentCatalogAncestor;
	@Mock
	private CMSSiteModel site;
	@Mock
	private CMSCatalogVersionService cmsCatalogVersionService;
	@Mock
	private CMSPreviewService cmsPreviewService;


	private final PagePreviewCriteriaData pagePreviewCriteria = new PagePreviewCriteriaData();
	private final ContentPageModel versionedPage = new ContentPageModel();
	private final ContentCatalogModel contentCatalog = new ContentCatalogModel();
	private Collection<CatalogVersionModel> sessionCatalogVersions;

	@Before
	public void setUp() throws Exception
	{
		when(catalogVersion1.getCatalog()).thenReturn(contentCatalog);
		when(catalogVersion2.getCatalog()).thenReturn(contentCatalog);
		sessionCatalogVersions = asList(catalogVersion1, catalogVersion2);

		doAnswer(invocation -> {
			final Object[] args = invocation.getArguments();
			final Supplier<?> supplier = (Supplier<?>) args[0];
			return supplier.get();
		}).when(sessionSearchRestrictionsDisabler).execute(any());

		when(cmsPreviewService.getPreviewContentCatalogVersion()).thenReturn(null);
	}

	@Test
	public void testShouldReturnThePageByLabelWhenOnlyOnePageWithoutRestrictionsIsFound() throws Exception
	{
		// given
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(page1.getRestrictions()).thenReturn(Collections.EMPTY_LIST);
		when(cmsPageDao.findPagesByLabelsAndPageStatuses(Arrays.asList(PAGE1_LABEL, PAGE1_LABEL.substring(1)),
				sessionCatalogVersions, ACTIVE_STATUS)).thenReturn(Arrays.asList(page1));

		// when
		final ContentPageModel resultContentPageModel = contentPageService.getPageForLabelAndStatuses(PAGE1_LABEL, ACTIVE_STATUS);

		// then
		assertThat(page1, equalTo(resultContentPageModel));
	}

	@Test
	public void testShouldReturnLastPageByLabelWhenThereAreMultiplePages() throws Exception
	{
		// use case for primary and variation pages having the same page label

		// given
		final Collection<AbstractPageModel> pages = asList(page1, page2);
		when(page1.getLabel()).thenReturn(PAGE1_LABEL);
		when(page2.getLabel()).thenReturn(PAGE1_LABEL);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelsAndPageStatuses(Arrays.asList(PAGE1_LABEL, PAGE1_LABEL.substring(1)),
				sessionCatalogVersions, ACTIVE_STATUS)).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		// when
		final ContentPageModel contentPageModel = contentPageService.getPageForLabelAndStatuses(PAGE1_LABEL, ACTIVE_STATUS);

		// then
		assertThat(contentPageModel, equalTo(page2));
	}

	@Test
	public void testShouldReturnLastPageByLabelWhenThereAreMultiplePrimaryPages() throws Exception
	{
		// use case for 2 primary pages having the same label name:
		// one starting with a forward slash, the other without the slash

		// given
		final Collection<AbstractPageModel> pages = asList(page1, page2);
		when(page1.getLabel()).thenReturn(PAGE1_LABEL);
		when(page2.getLabel()).thenReturn(PAGE1_LABEL.substring(1));
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelsAndPageStatuses(Arrays.asList(PAGE1_LABEL, PAGE1_LABEL.substring(1)),
				sessionCatalogVersions, ACTIVE_STATUS)).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		// when
		final ContentPageModel contentPageModel = contentPageService.getPageForLabelAndStatuses(PAGE1_LABEL, ACTIVE_STATUS);

		// then
		assertThat(contentPageModel, equalTo(page2));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void testShouldThrowExceptionWhenNoPageWithLabelFound() throws Exception
	{
		// given
		final Collection<AbstractPageModel> pages = asList(page1, page2);

		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE1_LABEL, sessionCatalogVersions, ACTIVE_STATUS)).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(Collections.EMPTY_LIST);

		// when
		contentPageService.getPageForLabelAndStatuses(PAGE1_LABEL, ACTIVE_STATUS);
	}

	@Test
	public void testShouldReturnFirstPageByIdWhenPageByExactLabelHasNotBeenFound() throws Exception
	{
		// given
		final List<AbstractPageModel> pages = asList(page1, page2);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE1_LABEL, sessionCatalogVersions, ACTIVE_STATUS)).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(Collections.EMPTY_LIST);
		when(cmsPageDao.findPagesByIdAndPageStatuses(PAGE1_ID, sessionCatalogVersions, ACTIVE_STATUS)).thenReturn(pages);

		// when
		final ContentPageModel contentPageModel = contentPageService.getPageForLabelOrIdAndMatchType(PAGE1_ID, true);

		// then
		assertThat(contentPageModel, equalTo(page1));
	}

	@Test(expected = CMSItemNotFoundException.class)
	public void testShouldThrowExceptionWhenNoPageWithIdFound() throws Exception
	{
		// given
		final List<AbstractPageModel> pages = asList(page1, page2);
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE1_LABEL, sessionCatalogVersions, ACTIVE_STATUS)).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(Collections.EMPTY_LIST);
		when(cmsPageDao.findPagesByIdAndPageStatuses(PAGE1_ID, sessionCatalogVersions, ACTIVE_STATUS))
				.thenReturn(Collections.EMPTY_LIST);

		// when
		contentPageService.getPageForLabelOrIdAndMatchType(PAGE1_ID, true);
	}

	@Test
	public void shouldReturnTheVersionedPageWhenAValidVersionUidIsPassedToGetPageForLabel() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		// when
		final AbstractPageModel result = contentPageService.getPageForLabelAndPreview("someLabel", pagePreviewCriteria);

		// then
		assertThat(result, equalTo(versionedPage));

	}

	@Test
	public void shouldReturnTheVersionedPageWhenAValidVersionUidIsPassedToGetPageForExactLabelOrId() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		// when
		final ContentPageModel result = contentPageService.getPageForLabelOrIdAndMatchType("someLabelOrId", pagePreviewCriteria,
				true);

		// then
		assertThat(result, equalTo(versionedPage));

	}

	@Test
	public void shouldFindHomepageForMultiCountry()
	{
		when(page1.getLabel()).thenReturn(PAGE1_LABEL);
		when(page1.getCatalogVersion()).thenReturn(catalogVersion2);

		// in multi-country, multiple content pages can have the homepage flag set to TRUE (one from each catalog in the site)
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsCatalogVersionService.areCatalogVersionsRelatives(sessionCatalogVersions)).thenReturn(true);
		when(cmsPageDao.findHomepagesByPageStatuses(sessionCatalogVersions, ACTIVE_STATUS)).thenReturn(Arrays.asList(page1, page2));

		// after finding all homepages in the catalog hierarchy, sort the homepages and select the one from the bottom catalog
		when(cmsItemCatalogLevelComparator.compare(page1, page2)).thenReturn(1);
		when(cmsItemCatalogLevelComparator.compare(page2, page1)).thenReturn(-1);

		// find all pages with the same label as the homepage and run the cms restrictions evaluation
		final Collection<AbstractPageModel> pages = asList(page1);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE1_LABEL, asList(catalogVersion2), ACTIVE_STATUS)).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		final ContentPageModel result = contentPageService.getHomepage();

		assertThat(result, equalTo(page1));
	}

	@Test
	public void shouldFindHomepageForMultiContent()
	{
		when(page1.getLabel()).thenReturn(PAGE1_LABEL);
		when(page1.getCatalogVersion()).thenReturn(catalogVersion2);

		//this means the request comes from storefront
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsCatalogVersionService.areCatalogVersionsRelatives(sessionCatalogVersions)).thenReturn(false);

		//get the default content catalog active version of site
		when(cmsSiteService.getCurrentSiteDefaultContentCatalogActiveVersion()).thenReturn(catalogVersion2);
		when(cmsSiteService.getCurrentSite()).thenReturn(site);
		//get the full hierarchy of the default content catalog active version
		final List<CatalogVersionModel> hierarchyCatalog = asList(catalogVersion1, catalogVersion2);
		when(cmsCatalogVersionService.getFullHierarchyForCatalogVersion(catalogVersion2, site)).thenReturn(hierarchyCatalog);

		final List<CatalogVersionModel> catalogVersionModels = asList(catalogVersion2);
		when(cmsCatalogVersionService.getIntersectionOfCatalogVersions(sessionCatalogVersions, hierarchyCatalog))
				.thenReturn(catalogVersionModels);
		when(cmsPageDao.findHomepagesByPageStatuses(catalogVersionModels, ACTIVE_STATUS)).thenReturn(Arrays.asList(page1));

		// find all pages with the same label as the homepage and run the cms restrictions evaluation
		final Collection<AbstractPageModel> pages = asList(page1);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE1_LABEL, asList(catalogVersion2), ACTIVE_STATUS)).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		final ContentPageModel result = contentPageService.getHomepage();

		assertThat(result, equalTo(page1));
	}

	@Test
	public void shouldNotFindHomepageForMultiContentWhenDefaultPageNotInSiteContentCatalogList()
	{
		//this means the request comes from storefront
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsCatalogVersionService.areCatalogVersionsRelatives(sessionCatalogVersions)).thenReturn(false);

		//get the default content catalog active version of site
		when(cmsSiteService.getCurrentSiteDefaultContentCatalogActiveVersion()).thenReturn(catalogVersion3);

		final List<CatalogVersionModel> hierarchyCatalog = asList(catalogVersion3, catalogVersion2);
		when(cmsCatalogVersionService.getFullHierarchyForCatalogVersion(catalogVersion3, site)).thenReturn(hierarchyCatalog);

		when(cmsCatalogVersionService.areCatalogVersionsRelatives(sessionCatalogVersions)).thenReturn(false);
		when(cmsCatalogVersionService.getIntersectionOfCatalogVersions(sessionCatalogVersions, hierarchyCatalog))
				.thenReturn(asList());

		final ContentPageModel result = contentPageService.getHomepage();

		assertNull(result);
	}

	@Test
	public void givenAValidVersionUidThenGetHomepageShouldReturnPageForTheProvidedVersionId() throws Exception
	{
		// given
		setupValidVersionUidMocks();

		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(page1);

		// when
		final ContentPageModel result = contentPageService.getHomepage(pagePreviewCriteria);

		// then
		assertThat(result, equalTo(page1));
		verify(cmsVersionService, times(1)).createItemFromVersion(cmsVersionModel);
	}

	protected void setupValidVersionUidMocks()
	{
		pagePreviewCriteria.setVersionUid("validVersionUid");

		when(cmsVersionDao.findByUid("validVersionUid")).thenReturn(Optional.of(cmsVersionModel));
		when(cmsVersionService.createItemFromVersion(cmsVersionModel)).thenReturn(versionedPage);
	}

	@Test
	public void testFindBestMatchLabelVariationsForLabelStartWithSlash()
	{
		final List<String> result = contentPageService.findLabelVariations("/mypage/default", false);

		assertThat(result, contains("/mypage/default", "mypage/default", "/mypage/", "/mypage"));
	}

	@Test
	public void testFindBestMatchLabelVariationsForLabelEndsWithSlash()
	{
		final List<String> result = contentPageService.findLabelVariations("/mypage/default/", false);

		assertThat(result, contains("/mypage/default/", "mypage/default/", "/mypage/default", "/mypage/", "/mypage"));
	}

	@Test
	public void testFindBestMatchLabelVariationsForLabelNotStartWithSlash()
	{
		final List<String> result = contentPageService.findLabelVariations("mypage/default/", false);

		assertThat(result, contains("/mypage/default/", "mypage/default/", "mypage/default", "mypage/", "mypage"));
	}

	@Test
	public void testFindExactMatchLabelVariationsForLabelStartWithSlash()
	{
		final List<String> result = contentPageService.findLabelVariations("/mypage", true);

		assertThat(result, contains("/mypage", "mypage"));
	}

	@Test
	public void testFindExactMatchLabelVariationsForLabelNotStartWithSlash()
	{
		final List<String> result = contentPageService.findLabelVariations("mypage", true);

		assertThat(result, contains("mypage", "/mypage"));
	}

	@Test
	public void shouldFindHomepageForMultiContentWhenPreview()
	{
		final List<CatalogVersionModel> catalogVersionModels = asList(catalogVersion2);
		when(page1.getLabel()).thenReturn(PAGE1_LABEL);
		when(page1.getCatalogVersion()).thenReturn(catalogVersion2);

		//get preview content catalog version
		when(catalogVersionService.getSessionCatalogVersions()).thenReturn(sessionCatalogVersions);
		when(cmsPreviewService.getPreviewContentCatalogVersion()).thenReturn(catalogVersion2);
		when(cmsSiteService.getCurrentSite()).thenReturn(site);
		when(cmsCatalogVersionService.getFullHierarchyForCatalogVersion(catalogVersion2, site)).thenReturn(catalogVersionModels);

		when(cmsCatalogVersionService.getIntersectionOfCatalogVersions(sessionCatalogVersions, catalogVersionModels))
				.thenReturn(catalogVersionModels);

		when(cmsCatalogVersionService.areCatalogVersionsRelatives(catalogVersionModels)).thenReturn(true);

		when(cmsPageDao.findHomepagesByPageStatuses(catalogVersionModels, ACTIVE_STATUS)).thenReturn(Arrays.asList(page1));
		final Collection<AbstractPageModel> pages = asList(page1);
		when(cmsPageDao.findPagesByLabelAndPageStatuses(PAGE1_LABEL, asList(catalogVersion2), ACTIVE_STATUS)).thenReturn(pages);
		when(cmsRestrictionService.evaluatePages(pages, null)).thenReturn(pages);

		final ContentPageModel result = contentPageService.getHomepage();

		assertThat(result, equalTo(page1));
	}
}
