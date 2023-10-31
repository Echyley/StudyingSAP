/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.integration.search.service

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.searchservices.constants.SearchservicesConstants
import de.hybris.platform.searchservices.integration.search.AbstractSnSearchDataDrivenSpec
import de.hybris.platform.searchservices.search.data.*

import org.junit.Test

@IntegrationTest
class SnSearchPromotedHitsRankRuleSpec extends AbstractSnSearchDataDrivenSpec {

	static final Float DEFAULT_SCORE = 1f
	static final Float UNDEFINED_SCORE = null

	@Override
	public Object loadData() {
		loadDefaultData()
	}

	@Test
	def "Add single promoted hit rank rule containing single promoted hit"() {
		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, this.&createSearchQuery, this.&addDefaultSearchQuerySort, { SnSearchQuery searchQuery ->
			final SnPromotedHitsRankRule rankRule = new SnPromotedHitsRankRule()
			rankRule.hits = [createPromotedHit("doc3")]
			searchQuery.rankRules.add rankRule
		})

		then:
		with(searchResult) {
			searchHits.size() == 4

			searchHits[0].score == UNDEFINED_SCORE
			searchHits[0].id == DOC3_ID

			searchHits[1].score == DEFAULT_SCORE

			searchHits[2].score == DEFAULT_SCORE

			searchHits[3].score == DEFAULT_SCORE
		}
	}

	@Test
	def "Add single promoted hit rank rule containing single promoted hit with sorting by code only"() {
		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, this.&createSearchQuery, this.&addDefaultSearchQuerySort, { SnSearchQuery searchQuery ->
			final SnPromotedHitsRankRule rankRule = new SnPromotedHitsRankRule()
			rankRule.hits = [createPromotedHit("doc3")]
			searchQuery.rankRules.add rankRule
			SnSortExpression codeSortExpression = new SnSortExpression()
			codeSortExpression.expression = SearchservicesConstants.ID_FIELD
			codeSortExpression.order = SnSortOrder.ASCENDING
			searchQuery.sort.expressions = [codeSortExpression]
		})

		then:
		with(searchResult) {
			searchHits.size() == 4

			searchHits[0].id == DOC3_ID
			searchHits[0].score == UNDEFINED_SCORE

			searchHits[1].id == DOC1_ID
			searchHits[1].score == UNDEFINED_SCORE

			searchHits[2].id == DOC2_ID
			searchHits[2].score == UNDEFINED_SCORE

			searchHits[3].id == DOC4_ID
			searchHits[3].score == UNDEFINED_SCORE
		}
	}

	@Test
	def "Add single promoted hit rank rule with applyPromotedHits=false and sorting by code only"() {
		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, this.&createSearchQuery, this.&addDefaultSearchQuerySort, { SnSearchQuery searchQuery ->
			final SnPromotedHitsRankRule rankRule = new SnPromotedHitsRankRule()
			rankRule.hits = [createPromotedHit("doc3")]
			searchQuery.rankRules.add rankRule
			SnSortExpression codeSortExpression = new SnSortExpression()
			codeSortExpression.expression = SearchservicesConstants.ID_FIELD
			codeSortExpression.order = SnSortOrder.ASCENDING
			searchQuery.sort.expressions = [codeSortExpression]
			searchQuery.sort.applyPromotedHits = false
		})

		then:
		with(searchResult) {
			searchHits.size() == 4

			searchHits[0].id == DOC1_ID
			searchHits[0].score == UNDEFINED_SCORE

			searchHits[1].id == DOC2_ID
			searchHits[1].score == UNDEFINED_SCORE

			searchHits[2].id == DOC3_ID
			searchHits[2].score == UNDEFINED_SCORE

			searchHits[3].id == DOC4_ID
			searchHits[3].score == UNDEFINED_SCORE
		}
	}

	def "Add two promoted hit rank rules containing three promoted hits"() {
		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, this.&createSearchQuery, this.&addDefaultSearchQuerySort, { SnSearchQuery searchQuery ->
			final SnPromotedHitsRankRule rankRule = new SnPromotedHitsRankRule()
			rankRule.hits = [createPromotedHit("doc4")]
			searchQuery.rankRules.add rankRule
			final SnPromotedHitsRankRule rankRule2 = new SnPromotedHitsRankRule()
			rankRule2.hits = [
				createPromotedHit("doc2"),
				createPromotedHit("doc1")
			]
			searchQuery.rankRules.add rankRule2
		})

		then:
		with(searchResult) {
			searchHits.size() == 4

			searchHits[0].score == UNDEFINED_SCORE
			searchHits[0].id == DOC4_ID

			searchHits[1].score == UNDEFINED_SCORE
			searchHits[1].id == DOC2_ID

			searchHits[2].score == UNDEFINED_SCORE
			searchHits[2].id == DOC1_ID

			searchHits[3].score == DEFAULT_SCORE
			searchHits[3].id == DOC3_ID
		}
	}

	def "Promoted hits that do not match query term, not included in the results"() {
		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, this.&createSearchQuery, this.&addDefaultSearchQuerySort, { SnSearchQuery searchQuery ->
			searchQuery.query = "RED"
			final SnPromotedHitsRankRule rankRule = new SnPromotedHitsRankRule()
			rankRule.hits = [createPromotedHit("doc4")]
			searchQuery.rankRules.add rankRule
		})

		then:
		with(searchResult) {
			count == 1
			size == 1
			searchHits[0].id == DOC1_ID
		}
	}

	def "Promoted hits that match query term, listed at the top"() {
		when:
		SnSearchResult searchResult = executeSearchQuery(INDEX_TYPE_ID, this.&createSearchQuery, this.&addDefaultSearchQuerySort, { SnSearchQuery searchQuery ->
			final SnMatchTermQuery matchTermQuery = new SnMatchTermQuery()
			matchTermQuery.expression = SearchservicesConstants.ID_FIELD
			searchQuery.query = "GREEN"
			final SnPromotedHitsRankRule rankRule = new SnPromotedHitsRankRule()
			rankRule.hits = [
				createPromotedHit("doc1"),
				createPromotedHit("doc3")
			]
			searchQuery.rankRules.add rankRule
		})

		then:
		with(searchResult) {
			count == 2
			size == 2
			searchHits[0].id == DOC3_ID
			searchHits[1].id == DOC2_ID
		}
	}

	def SnPromotedHit createPromotedHit(String id) {
		final SnPromotedHit hit = new SnPromotedHit()
		hit.setId(id)
		hit
	}
}
