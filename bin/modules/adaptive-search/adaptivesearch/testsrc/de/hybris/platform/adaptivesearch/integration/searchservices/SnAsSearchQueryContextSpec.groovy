/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearch.integration.searchservices

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel
import de.hybris.platform.adaptivesearch.services.AsSearchProfileService
import de.hybris.platform.catalog.CatalogVersionService
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.searchservices.integration.search.AbstractSnSearchDataDrivenSpec
import de.hybris.platform.searchservices.search.data.*
import de.hybris.platform.servicelayer.model.ModelService

import java.nio.charset.StandardCharsets

import javax.annotation.Resource

import org.junit.Test

@IntegrationTest
class SnAsSearchQueryContextSpec extends AbstractSnSearchDataDrivenSpec {

	static final String CATALOG_ID = "hwcatalog"
	static final String CATALOG_VERSION_STAGED = "Staged"

	static final String SEARCH_PROFILE_CODE = "searchProfile"

	@Resource
	ModelService modelService

	@Resource
	CatalogVersionService catalogVersionService

	@Resource
	AsSearchProfileService asSearchProfileService

	@Override
	public Object loadData() {
		loadDefaultData()
		importCsv("/searchservices/test/integration/snIndexTypeAddCatalogs.impex", StandardCharsets.UTF_8.name())
		importCsv("/adaptivesearch/test/integration/searchservices/asSimpleSearchProfile.impex", StandardCharsets.UTF_8.name())
		importCsv("/adaptivesearch/test/integration/asSearchProfileActivation.impex", StandardCharsets.UTF_8.name())
	}

	@Test
	def "Search with query context #testId"(testId, queryContexts, searchProfileQueryContext, searchProfileEnabled) {
		when:
		CatalogVersionModel catalogVersion = catalogVersionService.getCatalogVersion(CATALOG_ID, CATALOG_VERSION_STAGED)

		catalogVersionService.setSessionCatalogVersions(List.of(catalogVersion))

		AbstractAsSearchProfileModel searchProfile = asSearchProfileService.getSearchProfileForCode(catalogVersion, SEARCH_PROFILE_CODE).orElseThrow()
		searchProfile.setQueryContext(searchProfileQueryContext)
		modelService.save(searchProfile)

		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, this.&createSearchQuery, this.&addDefaultSearchQuerySort, { SnSearchQuery searchQuery ->
			searchQuery.getQueryContexts().addAll(queryContexts)
		})

		then:
		with(searchResult) {
			(searchHits[0].id == DOC2_ID) == searchProfileEnabled
		}

		where:
		testId | queryContexts          | searchProfileQueryContext || searchProfileEnabled
		1      | List.of()              | null                      || true
		2      | List.of()              | "DEFAULT"                 || false
		3      | List.of("DEFAULT")     | null                      || true
		4      | List.of("DEFAULT")     | "DEFAULT"                 || true
		5      | List.of("SUGGESTIONS") | "DEFAULT"                 || false
	}
}
