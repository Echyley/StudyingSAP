/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved
 */
package de.hybris.platform.platformbackoffice.editorsearchfacade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.hybris.cockpitng.search.data.SearchQueryData;
import com.hybris.cockpitng.search.data.pageable.Pageable;


@RunWith(MockitoJUnitRunner.class)
public class DefaultWritableCatalogVersionSearchFacadeTest
{
	private static final int PAGE_SIZE = 1;
	private static final String CATALOG_VERSION = "version";
	private static final String CATALOG_ID = "catalogId";
	@Mock
	private UserService userService;
	@Mock
	private CatalogVersionService catalogVersionService;
	@InjectMocks
	private DefaultWritableCatalogVersionSearchFacade facade;

	@Test
	public void shouldReturnAllCatalogVersionsForAdminUser()
	{
		//given
		final String searchText = StringUtils.EMPTY;
		final CatalogVersionModel catalogVersion = mockCatalogVersion(CATALOG_VERSION, CATALOG_ID);
		final SearchQueryData searchQueryData = mockDataForUser(searchText, List.of(), List.of(catalogVersion), true);

		//when
		final Pageable<CatalogVersionModel> result = facade.search(searchQueryData);

		//then
		assertThat(result.getAllResults()).containsExactly(catalogVersion);
	}

	private CatalogVersionModel mockCatalogVersion(final String version, final String catalogId)
	{
		final CatalogVersionModel catalogVersion = mock(CatalogVersionModel.class);
		final CatalogModel catalog = mock(CatalogModel.class);
		given(catalogVersion.getVersion()).willReturn(version);
		given(catalogVersion.getCatalog()).willReturn(catalog);
		return catalogVersion;
	}

	@Test
	public void shouldReturnFilteredCatalogVersionsForAdminUser()
	{
		//given
		final String searchText = CATALOG_VERSION;
		final CatalogVersionModel catalogVersion = mockCatalogVersion(CATALOG_VERSION, CATALOG_ID);
		final SearchQueryData searchQueryData = mockDataForUser(searchText, List.of(catalogVersion), List.of(), true);

		//when
		final Pageable<CatalogVersionModel> result = facade.search(searchQueryData);

		//then
		assertThat(result.getAllResults()).containsExactly(catalogVersion);
	}

	@Test
	public void shouldReturnEmptyResultsIfSearchTextDoNotMatchCatalogVersion()
	{
		//given
		final String searchText = "invalidText";
		final CatalogVersionModel catalogVersion = mockCatalogVersion(CATALOG_VERSION, CATALOG_ID);
		final SearchQueryData searchQueryData = mockDataForUser(searchText, List.of(), List.of(catalogVersion), true);

		//when
		final Pageable<CatalogVersionModel> result = facade.search(searchQueryData);

		//then
		assertThat(result.getAllResults()).isEmpty();
	}

	@Test
	public void shouldReturnOnlyWritableCatalogVersionForEmployee()
	{
		//given
		final String searchText = CATALOG_VERSION;
		final CatalogVersionModel employeeCatalogVersion = mockCatalogVersion("version1", CATALOG_ID);
		final CatalogVersionModel adminCatalogVersion = mockCatalogVersion("version2", CATALOG_ID);
		final SearchQueryData searchQueryData = mockDataForUser(searchText, List.of(employeeCatalogVersion),
				List.of(adminCatalogVersion), false);
		//when
		final Pageable<CatalogVersionModel> result = facade.search(searchQueryData);

		//then
		assertThat(result.getAllResults()).containsExactly(employeeCatalogVersion);
	}

	private SearchQueryData mockDataForUser(final String searchText, final List<CatalogVersionModel> employeeCatalogVersions,
			final List<CatalogVersionModel> onlyAdminCatalogVersions, final boolean isAdmin)
	{
		final SearchQueryData searchQueryData = mock(SearchQueryData.class);
		final UserModel user = mock(UserModel.class);

		given(searchQueryData.getSearchQueryText()).willReturn(searchText);
		given(searchQueryData.getPageSize()).willReturn(PAGE_SIZE);
		given(userService.getCurrentUser()).willReturn(user);
		given(userService.isAdmin(user)).willReturn(isAdmin);
		if (isAdmin)
		{
			given(catalogVersionService.getAllCatalogVersions()).willReturn(Stream
					.of(employeeCatalogVersions, onlyAdminCatalogVersions).flatMap(Collection::stream).collect(Collectors.toList()));
		}
		else
		{
			given(catalogVersionService.getAllWritableCatalogVersions(user)).willReturn(employeeCatalogVersions);
		}
		return searchQueryData;
	}


}
