/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cms2.catalogversion.service.impl;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.common.service.SessionSearchRestrictionsDisabler;
import de.hybris.platform.cms2.model.contents.ContentCatalogModel;
import de.hybris.platform.cms2.model.site.CMSSiteModel;
import de.hybris.platform.cms2.multicountry.service.CatalogLevelService;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class DefaultCMSCatalogVersionServiceTest
{
	@InjectMocks
	private DefaultCMSCatalogVersionService cmsCatalogVersionService;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@Mock
	private CatalogLevelService catalogLevelService;
	@Mock
	private UserService userService;
	@Mock
	private SessionSearchRestrictionsDisabler sessionSearchRestrictionDisabler;

	@Mock
	private UserModel user;
	@Mock
	private CatalogVersionModel catalogVersionGlobalOnline;
	@Mock
	private CatalogVersionModel catalogVersionOnline;
	@Mock
	private CatalogVersionModel catalogVersionStaged;
	@Mock
	private CatalogVersionModel catalogVersionUAT;
	@Mock
	private ContentCatalogModel globalContentCatalog;
	@Mock
	private ContentCatalogModel contentCatalog;
	@Mock
	private CatalogModel catalog;
	@Mock
	private BaseSiteModel baseSiteModel;
	@Mock
	private CMSSiteModel cmsSiteModel;
	@Mock
	private ContentCatalogModel contentCatalogOff;
	@Mock
	private ContentCatalogModel contentCatalogAnc;
	@Mock
	private Comparator<CatalogVersionModel> cmsCatalogVersionLevelComparator;
	@Mock
	private CatalogLevelService cmsCatalogLevelService;

	@Before
	public void setUp()
	{
		when(userService.getCurrentUser()).thenReturn(user);

		when(catalogVersionService.getAllCatalogVersions())
				.thenReturn(Arrays.asList(catalogVersionOnline, catalogVersionStaged, catalogVersionUAT));
		when(catalogVersionService.getAllReadableCatalogVersions(user)).thenReturn(Arrays.asList(catalogVersionStaged));
		when(catalogVersionService.getAllWritableCatalogVersions(user))
				.thenReturn(Arrays.asList(catalogVersionStaged, catalogVersionUAT));

		doAnswer(new Answer<Collection<ContentCatalogModel>>()
		{
			@Override
			public Collection answer(final InvocationOnMock invocation) throws Throwable
			{
				final Supplier<Collection<ContentCatalogModel>> supplier = (Supplier) invocation.getArguments()[0];
				return supplier.get();
			}
		}).when(sessionSearchRestrictionDisabler).execute(any());
	}

	protected void setUpContentCatalog()
	{
		when(cmsSiteModel.getContentCatalogs()).thenReturn(Arrays.asList(contentCatalog, globalContentCatalog));

		when(catalogVersionGlobalOnline.getCatalog()).thenReturn(globalContentCatalog);
		when(catalogVersionOnline.getCatalog()).thenReturn(contentCatalog);
		when(catalogVersionStaged.getCatalog()).thenReturn(contentCatalog);
		when(catalogVersionUAT.getCatalog()).thenReturn(contentCatalog);

		when(contentCatalog.getActiveCatalogVersion()).thenReturn(catalogVersionOnline);
		when(catalogVersionOnline.getActive()).thenReturn(true);

		when(globalContentCatalog.getActiveCatalogVersion()).thenReturn(catalogVersionGlobalOnline);
		when(catalogVersionGlobalOnline.getActive()).thenReturn(true);
	}

	protected void setUpProductCatalog()
	{
		when(baseSiteService.getProductCatalogs(baseSiteModel)).thenReturn(Arrays.asList(catalog));

		when(catalogVersionOnline.getCatalog()).thenReturn(catalog);
		when(catalogVersionStaged.getCatalog()).thenReturn(catalog);
		when(catalogVersionUAT.getCatalog()).thenReturn(catalog);
	}

	@Test
	public void shouldGetReadAndWriteContentCatalogsAndVersions()
	{
		setUpContentCatalog();

		final Map<CatalogModel, Set<CatalogVersionModel>> permittedCatalogsAndVersions = cmsCatalogVersionService
				.getContentCatalogsAndVersions(true, true, cmsSiteModel);

		verify(catalogVersionService).getAllReadableCatalogVersions(user);
		verify(catalogVersionService).getAllWritableCatalogVersions(user);
		assertThat(permittedCatalogsAndVersions.containsKey(contentCatalog), is(true));
		assertThat(permittedCatalogsAndVersions.get(contentCatalog),
				containsInAnyOrder(catalogVersionStaged, catalogVersionOnline, catalogVersionUAT));
	}

	@Test
	public void shouldGetReadContentCatalogsAndVersions()
	{
		setUpContentCatalog();

		final Map<CatalogModel, Set<CatalogVersionModel>> permittedCatalogsAndVersions = cmsCatalogVersionService
				.getContentCatalogsAndVersions(true, false, cmsSiteModel);

		verify(catalogVersionService).getAllReadableCatalogVersions(user);
		verify(catalogVersionService, times(0)).getAllWritableCatalogVersions(user);
		assertThat(permittedCatalogsAndVersions.containsKey(contentCatalog), is(true));
		assertThat(permittedCatalogsAndVersions.get(contentCatalog),
				containsInAnyOrder(catalogVersionOnline, catalogVersionStaged));
	}

	@Test
	public void shouldGetWriteContentCatalogsAndVersions()
	{
		setUpContentCatalog();

		final Map<CatalogModel, Set<CatalogVersionModel>> permittedCatalogsAndVersions = cmsCatalogVersionService
				.getContentCatalogsAndVersions(false, true, cmsSiteModel);

		verify(catalogVersionService, times(0)).getAllReadableCatalogVersions(user);
		verify(catalogVersionService).getAllWritableCatalogVersions(user);
		assertThat(permittedCatalogsAndVersions.containsKey(contentCatalog), is(true));
		assertThat(permittedCatalogsAndVersions.get(contentCatalog), containsInAnyOrder(catalogVersionStaged, catalogVersionUAT));
	}

	@Test
	public void shouldGetNoPermittedContentCatalogsAndVersions()
	{
		setUpContentCatalog();
		when(catalogVersionService.getAllReadableCatalogVersions(user)).thenReturn(Collections.emptyList());
		when(catalogVersionService.getAllWritableCatalogVersions(user)).thenReturn(Collections.emptyList());

		final Map<CatalogModel, Set<CatalogVersionModel>> permittedCatalogsAndVersions = cmsCatalogVersionService
				.getContentCatalogsAndVersions(true, true, cmsSiteModel);

		verify(catalogVersionService).getAllReadableCatalogVersions(user);
		verify(catalogVersionService).getAllWritableCatalogVersions(user);
		assertThat(permittedCatalogsAndVersions.get(contentCatalog), contains(catalogVersionOnline));
	}

	@Test
	public void shouldGetReadAndWriteProductCatalogsAndVersions()
	{
		setUpProductCatalog();

		final Map<CatalogModel, Set<CatalogVersionModel>> permittedCatalogsAndVersions = cmsCatalogVersionService
				.getProductCatalogsAndVersions(true, true, baseSiteModel);

		verify(catalogVersionService).getAllReadableCatalogVersions(user);
		verify(catalogVersionService).getAllWritableCatalogVersions(user);
		assertThat(permittedCatalogsAndVersions.containsKey(catalog), is(true));
		assertThat(permittedCatalogsAndVersions.get(catalog), containsInAnyOrder(catalogVersionStaged, catalogVersionUAT));
	}

	@Test
	public void shouldGetFullCatalogVersionHierarchyForMultiCountry()
	{
		setUpContentCatalog();

		// for multi-country, parent catalogs exists
		when(catalogLevelService.getAllSuperCatalogs(contentCatalog)).thenReturn(Arrays.asList(globalContentCatalog));

		final List<CatalogVersionModel> results = cmsCatalogVersionService.getFullHierarchyForCatalogVersion(catalogVersionStaged,
				cmsSiteModel);

		assertThat(results, contains(catalogVersionGlobalOnline, catalogVersionStaged));
	}

	@Test
	public void shouldGetOneCatalogVersionInHierarchyForSingleCountry()
	{
		setUpContentCatalog();

		// for non-multi-country setup, no parent catalog exists
		when(catalogLevelService.getAllSuperCatalogs(contentCatalog)).thenReturn(Collections.emptyList());

		final List<CatalogVersionModel> results = cmsCatalogVersionService.getFullHierarchyForCatalogVersion(catalogVersionStaged,
				cmsSiteModel);

		assertThat(results, contains(catalogVersionStaged));
	}

	@Test
	public void ContentCatalogAncIsAncestorContentCatalogOff()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogAnc)).thenReturn(0);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogOff)).thenReturn(1);
		when(contentCatalogOff.getSuperCatalog()).thenReturn(contentCatalogAnc);
		//when(contentCatalogAnc.getSuperCatalog()).thenReturn(contentCatalogAnc);
		when(contentCatalogAnc.getPk()).thenReturn(PK.fromLong(123456l));

		assertTrue(cmsCatalogVersionService.isContentCatalogModelAncestor(contentCatalogOff, contentCatalogAnc));
	}

	@Test
	public void ContentCatalogAncIsAncestorContentCatalogOff2()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogAnc)).thenReturn(3);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogOff)).thenReturn(5);
		when(contentCatalogOff.getSuperCatalog()).thenReturn(contentCatalogAnc);
		when(contentCatalogAnc.getSuperCatalog()).thenReturn(contentCatalogAnc);
		when(contentCatalogAnc.getPk()).thenReturn(PK.fromLong(123456l));

		assertTrue(cmsCatalogVersionService.isContentCatalogModelAncestor(contentCatalogOff, contentCatalogAnc));
	}

	@Test
	public void ContentCatalogOffIsNotAncestorContentCatalogAnc()
	{
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogAnc)).thenReturn(0);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogOff)).thenReturn(0);

		assertFalse(cmsCatalogVersionService.isContentCatalogModelAncestor(contentCatalogAnc, contentCatalogOff));
	}

	@Test
	public void ShouldAllCatalogVersionsShouldAreRelatives()
	{
		Collection<CatalogVersionModel> catalogVersionModels = new ArrayList<>();
		catalogVersionModels.add(catalogVersionGlobalOnline);
		catalogVersionModels.add(catalogVersionOnline);
		catalogVersionModels.add(catalogVersionStaged);

		when(cmsCatalogVersionLevelComparator.compare(catalogVersionGlobalOnline, catalogVersionOnline)).thenReturn(1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionOnline, catalogVersionGlobalOnline)).thenReturn(-1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionGlobalOnline, catalogVersionStaged)).thenReturn(1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionStaged, catalogVersionGlobalOnline)).thenReturn(-1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionOnline, catalogVersionStaged)).thenReturn(1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionStaged, catalogVersionOnline)).thenReturn(-1);

		when(catalogVersionGlobalOnline.getCatalog()).thenReturn(contentCatalogOff);
		when(catalogVersionOnline.getCatalog()).thenReturn(contentCatalogAnc);
		when(catalogVersionStaged.getCatalog()).thenReturn(contentCatalog);

		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogOff)).thenReturn(2);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogAnc)).thenReturn(1);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(0);

		when(contentCatalogOff.getSuperCatalog()).thenReturn(contentCatalogAnc);
		when(contentCatalogAnc.getSuperCatalog()).thenReturn(contentCatalog);

		when(contentCatalogAnc.getPk()).thenReturn(PK.fromLong(123456l));
		when(contentCatalog.getPk()).thenReturn(PK.fromLong(1234569l));


		assertTrue(cmsCatalogVersionService.areCatalogVersionsRelatives(catalogVersionModels));
	}

	@Test
	public void ShouldAllCatalogVersionsShouldAreNotRelatives()
	{
		Collection<CatalogVersionModel> catalogVersionModels = new ArrayList<>();
		catalogVersionModels.add(catalogVersionGlobalOnline);
		catalogVersionModels.add(catalogVersionOnline);
		catalogVersionModels.add(catalogVersionStaged);

		when(cmsCatalogVersionLevelComparator.compare(catalogVersionGlobalOnline, catalogVersionOnline)).thenReturn(1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionOnline, catalogVersionGlobalOnline)).thenReturn(-1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionGlobalOnline, catalogVersionStaged)).thenReturn(1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionStaged, catalogVersionGlobalOnline)).thenReturn(-1);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionOnline, catalogVersionStaged)).thenReturn(0);
		when(cmsCatalogVersionLevelComparator.compare(catalogVersionStaged, catalogVersionOnline)).thenReturn(0);

		when(catalogVersionGlobalOnline.getCatalog()).thenReturn(contentCatalogOff);
		when(catalogVersionOnline.getCatalog()).thenReturn(contentCatalogAnc);
		when(catalogVersionStaged.getCatalog()).thenReturn(contentCatalog);

		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogOff)).thenReturn(1);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalogAnc)).thenReturn(0);
		when(cmsCatalogLevelService.getCatalogLevel(contentCatalog)).thenReturn(0);

		when(contentCatalogOff.getSuperCatalog()).thenReturn(contentCatalogAnc);
		when(contentCatalogAnc.getSuperCatalog()).thenReturn(contentCatalog);

		when(contentCatalogAnc.getPk()).thenReturn(PK.fromLong(123456l));
		when(contentCatalog.getPk()).thenReturn(PK.fromLong(1234569l));

		assertFalse(cmsCatalogVersionService.areCatalogVersionsRelatives(catalogVersionModels));
	}

	@Test
	public void HasIntersectionOfCatalogVersions()
	{
		final Collection<CatalogVersionModel> catalogVersionModelsA = new ArrayList<>();
		final Collection<CatalogVersionModel> catalogVersionModelsB = new ArrayList<>();
		catalogVersionModelsA.add(catalogVersionGlobalOnline);
		catalogVersionModelsA.add(catalogVersionOnline);
		catalogVersionModelsA.add(catalogVersionStaged);

		catalogVersionModelsB.add(catalogVersionOnline);
		catalogVersionModelsB.add(catalogVersionStaged);

		when(catalogVersionStaged.getPk()).thenReturn(PK.fromLong(123456l));
		when(catalogVersionOnline.getPk()).thenReturn(PK.fromLong(1234569l));
		when(catalogVersionGlobalOnline.getPk()).thenReturn(PK.fromLong(12345690l));

		final Collection<CatalogVersionModel> intersections = cmsCatalogVersionService
				.getIntersectionOfCatalogVersions(catalogVersionModelsA, catalogVersionModelsB);
		assertEquals(2, intersections.size());
	}

	@Test
	public void NoIntersectionOfCatalogVersions()
	{
		final Collection<CatalogVersionModel> catalogVersionModelsA = new ArrayList<>();
		final Collection<CatalogVersionModel> catalogVersionModelsB = new ArrayList<>();
		catalogVersionModelsA.add(catalogVersionOnline);
		catalogVersionModelsA.add(catalogVersionStaged);

		catalogVersionModelsB.add(catalogVersionGlobalOnline);

		when(catalogVersionStaged.getPk()).thenReturn(PK.fromLong(123456l));
		when(catalogVersionOnline.getPk()).thenReturn(PK.fromLong(1234569l));
		when(catalogVersionGlobalOnline.getPk()).thenReturn(PK.fromLong(12345690l));

		final Collection<CatalogVersionModel> intersections = cmsCatalogVersionService
				.getIntersectionOfCatalogVersions(catalogVersionModelsA, catalogVersionModelsB);
		assertEquals(0, intersections.size());
	}
}
