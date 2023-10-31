/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.integration.search.service

import static de.hybris.platform.searchservices.support.util.ObjectUtils.matchContains

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.searchservices.integration.search.AbstractSnSearchDataDrivenSpec
import de.hybris.platform.searchservices.search.data.SnSearchQuery
import de.hybris.platform.searchservices.search.data.SnSearchResult

import java.nio.charset.StandardCharsets

import org.junit.Test

import com.fasterxml.jackson.databind.type.CollectionType
import com.fasterxml.jackson.databind.type.TypeFactory

import spock.lang.Unroll

@Unroll
@IntegrationTest
class SnSearchSpec extends AbstractSnSearchDataDrivenSpec {

	@Override
	public Object loadData() {
		loadDefaultData()
	}

	def setup() {
		importData("/searchservices/test/integration/snIndexTypeAddFields.impex", StandardCharsets.UTF_8.name())
	}

	@Test
	def "Pk field is returned by default"() {
		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, this.&createSearchQuery, this.&addDefaultSearchQuerySort)

		then:
		with(searchResult) {
			searchHits.size() == 4
			searchHits[0].fields.pk != null
			searchHits[1].fields.pk != null
			searchHits[2].fields.pk != null
			searchHits[3].fields.pk != null
		}
	}

	@Test
	def "Search with query (retrievable) #row.testId: description=#row.description, data=#row.data"(row) {
		given:
		resetFields({ field ->
			field.setRetrievable(false)
		})

		if (row.fields) {
			for (def field : row.fields) {
				patchField(field.id, field)
			}
		}

		executeFullIndexerOperation(INDEX_TYPE_ID)

		List<String> nonRetrievableFieldIds = getFields().findAll({ !it.retrievable }).collect({ it.id })

		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, {
			row.data
		})

		then:
		matchContains(searchResult, row.expectedData)
		searchResult.searchHits.every({ searchHit ->
			nonRetrievableFieldIds.every({ nonRetrievableFieldId ->
				!searchHit.fields.containsKey(nonRetrievableFieldId)
			})
		})

		where:
		row << loadSearchSpecJson("/searchservices/test/integration/search/validQueriesRetrievable.json")
	}

	@Test
	def "Search with query (searchable) #row.testId: description=#row.description, data=#row.data"(row) {
		given:
		resetFields({ field ->
			field.setSearchable(false)
			field.setWeight(1.0f)
		})

		if (row.fields) {
			for (def field : row.fields) {
				patchField(field.id, field)
			}
		}

		executeFullIndexerOperation(INDEX_TYPE_ID)

		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, {
			row.data
		})

		then:
		matchContains(searchResult, row.expectedData)

		where:
		row << loadSearchSpecJson("/searchservices/test/integration/search/validQueriesSearchable.json")
	}


	def "Search with query (searchable with search tolerance) #row.testId: description=#row.description, data=#row.data"(row) {
		given:
		resetFields({ field ->
			field.setSearchable(false)
			field.setWeight(1.0f)
			field.setSearchTolerance(null)
		})

		if (row.fields) {
			for (def field : row.fields) {
				patchField(field.id, field)
			}
		}

		executeFullIndexerOperation(INDEX_TYPE_ID)

		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, {
			row.data
		})

		then:
		matchContains(searchResult, row.expectedData)

		where:
		row << loadSearchSpecJson("/searchservices/test/integration/search/validQueriesSearchableForFuzziness.json")
	}

	def "Search with query (searchable with search tolerance for slop) #row.testId: description=#row.description, data=#row.data"(row) {
		given:
		def responses = [:]

		when:
		for(def variant : row.toleranceVariants){
			if (variant.fields) {
				resetFields({ field ->
					field.setSearchable(false)
					field.setWeight(1.0f)
					field.setSearchTolerance(null)
				})
				for (def field : variant.fields) {
					patchField(field.id, field)
				}

				executeFullIndexerOperation(INDEX_TYPE_ID)
			}
			SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, {
				row.data
			})
			responses[variant.searchTolerance] = searchResult
		}

		then:
		responses.each{response ->
			matchContains(response, row.expectedData)
		}

		def basicHits = responses["BASIC"].searchHits
		def mediumHits = responses["MEDIUM"].searchHits
		def relaxedHits = responses["RELAXED"].searchHits

		//uncomment when we will come up with proper test cases
		//basicHits[0].score < mediumHits[0].score && mediumHits[0].score < relaxedHits[0].score
		//basicHits[1].score < mediumHits[1].score && mediumHits[1].score < relaxedHits[1].score

		where:
		row << loadSearchSpecJsonForSlop("/searchservices/test/integration/search/validQueriesSearchableForSlop.json")
	}

	List<SnSearchSpec> loadSearchSpecJsonForSlop(String resource) {
		URL url = SnSearchSpec.class.getResource(resource)
		final TypeFactory typeFactory = objectMapper.getTypeFactory()
		final CollectionType type = typeFactory.constructCollectionType(List.class, SnSearchToleranceVariantsSpec.class)

		return objectMapper.readValue(url, type)
	}

	static class SnSearchToleranceVariantsSpec {
		String testId
		String description
		List<String> languages
		Map<String, String> qualifiers
		List<SnFieldsToleranceVariant> toleranceVariants
		SnSearchQuery data
		Object expectedData
	}

	static class SnFieldsToleranceVariant {
		String searchTolerance
		List<Map<String, Object>> fields
	}
}
