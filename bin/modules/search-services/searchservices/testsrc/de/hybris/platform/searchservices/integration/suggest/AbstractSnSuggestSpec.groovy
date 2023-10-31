/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.integration.suggest

import de.hybris.platform.searchservices.integration.AbstractSnIntegrationSpec
import de.hybris.platform.searchservices.suggest.data.SnSuggestQuery
import de.hybris.platform.searchservices.suggest.data.SnSuggestResult
import de.hybris.platform.searchservices.suggest.service.SnSuggestRequest
import de.hybris.platform.searchservices.suggest.service.SnSuggestResponse
import de.hybris.platform.searchservices.suggest.service.SnSuggestService
import de.hybris.platform.searchservices.util.JsonUtils

import java.nio.charset.StandardCharsets

import javax.annotation.Resource

import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.core.type.TypeReference

abstract class AbstractSnSuggestSpec extends AbstractSnIntegrationSpec {

	@Resource
	SnSuggestService snSuggestService

	def loadCoreData() {
		importData("/searchservices/test/integration/snLanguages.impex", StandardCharsets.UTF_8.name())
		importData("/searchservices/test/integration/snCurrencies.impex", StandardCharsets.UTF_8.name())
		importData("/searchservices/test/integration/snIndexConfiguration.impex", StandardCharsets.UTF_8.name())
		importData("/searchservices/test/integration/snIndexConfigurationAddLanguages.impex", StandardCharsets.UTF_8.name())
		importData("/searchservices/test/integration/snIndexConfigurationAddCurrencies.impex", StandardCharsets.UTF_8.name())
		importData("/searchservices/test/integration/snIndexType.impex", StandardCharsets.UTF_8.name())
		importData("/searchservices/test/integration/snIndexTypeAddFields.impex", StandardCharsets.UTF_8.name())
	}

	def loadDefaultData() {
		importData("/searchservices/test/integration/snCatalogBase.impex", StandardCharsets.UTF_8.name())
		importData("/searchservices/test/integration/snCatalogSampleProducts.impex", StandardCharsets.UTF_8.name())
		importData("/searchservices/test/integration/suggest/snIndexTypeUpdatesForSuggest.impex", StandardCharsets.UTF_8.name())
	}

	SnSuggestResult executeSuggestQuery(String indexTypeId, Closure creator, Closure... decorators) {
		final SnSuggestQuery suggestQuery = creator()

		for (Closure decorator : decorators) {
			decorator(suggestQuery)
		}

		final SnSuggestRequest suggestRequest = snSuggestService.createSuggestRequest(indexTypeId, suggestQuery)
		final SnSuggestResponse suggestResponse = snSuggestService.suggest(suggestRequest)

		return suggestResponse.getSuggestResult()
	}

	SnSuggestQuery createSuggestQuery() {
		return new SnSuggestQuery()
	}

	def void loadFieldSetupData(SuggestDataDrivenSpec row) {
		resetFields({ field -> field.setUseForSuggesting(false) })

		if (row.fieldSetup) {
			if (row.fieldSetup.patches) {
				for (def patch : row.fieldSetup.patches) {
					patchField(patch.fieldId, patch.data)
				}
			}
		}
	}

	protected final List<SuggestDataDrivenSpec> loadSuggestDataDrivenSpecJson(String resource, Set<String> skipTestIds = []) {
		JsonUtils.loadJson(resource, new TypeReference<List<SuggestDataDrivenSpec>>() {})
				.findAll { skipTestIds.contains(it.testId) == false }
	}

	static class SuggestDataDrivenSpec {
		String testId
		String description

		SuggestFieldSetupDataDrivenSpec fieldSetup
		SuggestDocumentSetupDataDrivenSpec documentSetup

		List<String> languages

		@JsonRawValue
		String data

		Integer expectedHttpStatus

		@JsonRawValue
		String expectedData
	}

	static final class SuggestFieldSetupDataDrivenSpec {
		List<Map<String, Object>> patches
	}

	static class SuggestFieldPatchDataDrivenSpec {
		String fieldId

		@JsonRawValue
		String data
	}

	static final class SuggestDocumentSetupDataDrivenSpec {
		List<SuggestDocumentUpdateDataDrivenSpec> updates
		List<SuggestDocumentPatchDataDrivenSpec> patches
	}

	static class SuggestDocumentUpdateDataDrivenSpec {
		String documentId

		@JsonRawValue
		String data
	}

	static class SuggestDocumentPatchDataDrivenSpec {
		String documentId

		@JsonRawValue
		String data
	}
}
