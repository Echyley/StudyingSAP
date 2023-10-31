/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.adaptivesearch.services.impl

import static org.assertj.core.api.Assertions.assertThat

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.adaptivesearch.context.AsSearchProfileContext
import de.hybris.platform.adaptivesearch.context.impl.DefaultAsSearchProfileContext
import de.hybris.platform.adaptivesearch.data.AsSearchProfileActivationGroup
import de.hybris.platform.adaptivesearch.model.AbstractAsSearchProfileModel
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileActivationMapping
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileActivationStrategy
import de.hybris.platform.adaptivesearch.strategies.AsSearchProfileRegistry
import de.hybris.platform.adaptivesearch.strategies.impl.DefaultAsSearchProfileActivationMapping
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.core.PK
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.session.SessionService
import de.hybris.platform.testframework.JUnitPlatformSpecification

import org.junit.Test


@UnitTest
public class DefaultAsSearchProfileActivationServiceSpec extends JUnitPlatformSpecification {

	private static final int HIGH_PRIORITY = 200;
	private static final int LOW_PRIORITY = 50;

	static final String INDEX_TYPE = "indexType"

	static final String DEFAULT_QUERY_CONTEXT = "DEFAULT"
	static final String UNKNOWN_QUERY_CONTEXT = "UNKNOWN"

	final PK searchProfile1Pk = PK.fromLong(1)
	AbstractAsSearchProfileModel searchProfile1 = Mock()

	final PK searchProfile2Pk = PK.fromLong(2)
	AbstractAsSearchProfileModel searchProfile2 = Mock()

	CatalogVersionModel catalogVersion1 = Mock()
	CatalogVersionModel catalogVersion2 = Mock()

	List<CatalogVersionModel> catalogVersions = List.of(catalogVersion1, catalogVersion2)

	ModelService modelService = Mock()
	SessionService sessionService = Mock()
	AsSearchProfileRegistry asSearchProfileRegistry = Mock()

	DefaultAsSearchProfileActivationService asSearchProfileActivationService

	def setup() {
		searchProfile1.getPk() >> searchProfile1Pk
		modelService.get(searchProfile1Pk) >> searchProfile1

		searchProfile2.getPk() >> searchProfile2Pk
		modelService.get(searchProfile2Pk) >> searchProfile2

		asSearchProfileActivationService = new DefaultAsSearchProfileActivationService()
		asSearchProfileActivationService.setModelService(modelService)
		asSearchProfileActivationService.setSessionService(sessionService)
		asSearchProfileActivationService.setAsSearchProfileRegistry(asSearchProfileRegistry)
	}

	@Test
	def "Set current search profiles"() {
		when:
		asSearchProfileActivationService.setCurrentSearchProfiles(Arrays.asList(searchProfile1, searchProfile2))

		then:
		1 * sessionService.setAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES, List.of(searchProfile1Pk, searchProfile2Pk))
	}

	@Test
	def "Get empty current search profiles"() {
		when:
		final Optional<List<AbstractAsSearchProfileModel>> searchProfilesResult = asSearchProfileActivationService
				.getCurrentSearchProfiles()

		then:
		searchProfilesResult.isEmpty()
	}

	@Test
	def "Get current search profiles"() {
		given:
		sessionService.getAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES) >> List.of(searchProfile1Pk, searchProfile2Pk)

		when:
		final Optional<List<AbstractAsSearchProfileModel>> searchProfilesResult = asSearchProfileActivationService
				.getCurrentSearchProfiles()

		then:
		searchProfilesResult.isPresent()
		assertThat(searchProfilesResult.get()).hasSize(2).containsExactly(searchProfile1, searchProfile2)
	}

	@Test
	def "Clear current search profiles"() {
		when:
		asSearchProfileActivationService.clearCurrentSearchProfiles()

		then:
		1 * sessionService.removeAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES)
	}

	@Test
	def "Get search profiles activation groups from current search profiles"() {
		given:
		sessionService.getAttribute(DefaultAsSearchProfileActivationService.CURRENT_SEARCH_PROFILES) >> List.of(searchProfile1Pk, searchProfile2Pk)
		AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withIndexType(INDEX_TYPE).withCatalogVersions(catalogVersions).build()

		when:
		final List<AsSearchProfileActivationGroup> searchProfileActivationGroups = asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)

		then:
		assertThat(searchProfileActivationGroups).hasSize(1)
	}

	@Test
	def "Get empty search profiles activation groups when no activation strategy is configured"() {
		given:
		asSearchProfileRegistry.getSearchProfileActivationMappings() >> List.of()
		AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withIndexType(INDEX_TYPE).withCatalogVersions(catalogVersions).build()

		when:
		final List<AsSearchProfileActivationGroup> searchProfileActivationGroups = asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)

		then:
		assertThat(searchProfileActivationGroups).isEmpty()
	}

	@Test
	def "Get search profiles activation groups from activation strategy using search profiles"() {
		given:
		AsSearchProfileActivationStrategy activationStrategy = new AsSearchProfileActivationStrategy() {
					@Override
					public List<AbstractAsSearchProfileModel> getActiveSearchProfiles(AsSearchProfileContext context) {
						return List.of(searchProfile1, searchProfile2)
					}
				}

		AsSearchProfileActivationMapping activationMapping = new DefaultAsSearchProfileActivationMapping(priority: HIGH_PRIORITY, activationStrategy: activationStrategy)
		asSearchProfileRegistry.getSearchProfileActivationMappings() >> List.of(activationMapping)

		AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withIndexType(INDEX_TYPE).withCatalogVersions(catalogVersions).build()

		when:
		final List<AsSearchProfileActivationGroup> searchProfileActivationGroups = asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)

		then:
		assertThat(searchProfileActivationGroups).hasSize(1)

		with(searchProfileActivationGroups.get(0)) {
			assertThat(getSearchProfiles()).containsExactly(searchProfile1, searchProfile2)
			assertThat(getGroups()).isEmpty()
		}
	}


	@Test
	def "Get search profiles activation groups from activation strategy using group"() {
		given:
		AsSearchProfileActivationStrategy activationStrategy = new AsSearchProfileActivationStrategy() {
					@Override
					public AsSearchProfileActivationGroup getSearchProfileActivationGroup(AsSearchProfileContext context) {
						return new AsSearchProfileActivationGroup(searchProfiles: List.of(searchProfile1, searchProfile2))
					}
				}

		AsSearchProfileActivationMapping activationMapping = new DefaultAsSearchProfileActivationMapping(priority: HIGH_PRIORITY, activationStrategy: activationStrategy)
		asSearchProfileRegistry.getSearchProfileActivationMappings() >> List.of(activationMapping)

		AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withIndexType(INDEX_TYPE).withCatalogVersions(catalogVersions).build()

		when:
		final List<AsSearchProfileActivationGroup> searchProfileActivationGroups = asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)

		then:
		assertThat(searchProfileActivationGroups).hasSize(1)

		with(searchProfileActivationGroups.get(0)) {
			assertThat(getSearchProfiles()).containsExactly(searchProfile1, searchProfile2)
			assertThat(getGroups()).isEmpty()
		}
	}

	@Test
	def "Get search profiles activation groups from mutiple activation strategies"() {
		given:
		AsSearchProfileActivationStrategy activationStrategy1 = new AsSearchProfileActivationStrategy() {
					@Override
					public List<AbstractAsSearchProfileModel> getActiveSearchProfiles(AsSearchProfileContext context) {
						return List.of(searchProfile1)
					}
				}

		AsSearchProfileActivationStrategy activationStrategy2 = new AsSearchProfileActivationStrategy() {
					@Override
					public AsSearchProfileActivationGroup getSearchProfileActivationGroup(AsSearchProfileContext context) {
						return new AsSearchProfileActivationGroup(searchProfiles: List.of(searchProfile2))
					}
				}

		AsSearchProfileActivationMapping activationMapping1 = new DefaultAsSearchProfileActivationMapping(priority: HIGH_PRIORITY, activationStrategy: activationStrategy1)
		AsSearchProfileActivationMapping activationMapping2 = new DefaultAsSearchProfileActivationMapping(priority: LOW_PRIORITY, activationStrategy: activationStrategy2)
		asSearchProfileRegistry.getSearchProfileActivationMappings() >> List.of(activationMapping1, activationMapping2)

		AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withIndexType(INDEX_TYPE).withCatalogVersions(catalogVersions).build()

		when:
		final List<AsSearchProfileActivationGroup> searchProfileActivationGroups = asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)

		then:
		assertThat(searchProfileActivationGroups).hasSize(2)

		with(searchProfileActivationGroups.get(0)) {
			assertThat(getSearchProfiles()).containsExactly(searchProfile1)
			assertThat(getGroups()).isEmpty()
		}

		with(searchProfileActivationGroups.get(1)) {
			assertThat(getSearchProfiles()).containsExactly(searchProfile2)
			assertThat(getGroups()).isEmpty()
		}
	}

	@Test
	def "Get search profiles activation groups with query context 1"() {
		given:
		AsSearchProfileActivationStrategy activationStrategy = new AsSearchProfileActivationStrategy() {
					@Override
					public AsSearchProfileActivationGroup getSearchProfileActivationGroup(AsSearchProfileContext context) {
						return new AsSearchProfileActivationGroup(searchProfiles: List.of(searchProfile1, searchProfile2))
					}
				}

		AsSearchProfileActivationMapping activationMapping = new DefaultAsSearchProfileActivationMapping(priority: HIGH_PRIORITY, activationStrategy: activationStrategy)
		asSearchProfileRegistry.getSearchProfileActivationMappings() >> List.of(activationMapping)

		AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withQueryContexts(List.of(DEFAULT_QUERY_CONTEXT)).withIndexType(INDEX_TYPE).withCatalogVersions(catalogVersions).build()

		when:
		final List<AsSearchProfileActivationGroup> searchProfileActivationGroups = asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)

		then:
		assertThat(searchProfileActivationGroups).hasSize(1)

		with(searchProfileActivationGroups.get(0)) {
			assertThat(getSearchProfiles()).containsExactly(searchProfile1, searchProfile2)
			assertThat(getGroups()).isEmpty()
		}
	}

	@Test
	def "Get search profiles activation groups with query context 2"() {
		given:
		searchProfile1.getQueryContext() >> DEFAULT_QUERY_CONTEXT

		AsSearchProfileActivationStrategy activationStrategy = new AsSearchProfileActivationStrategy() {
					@Override
					public AsSearchProfileActivationGroup getSearchProfileActivationGroup(AsSearchProfileContext context) {
						return new AsSearchProfileActivationGroup(searchProfiles: List.of(searchProfile1, searchProfile2))
					}
				}

		AsSearchProfileActivationMapping activationMapping = new DefaultAsSearchProfileActivationMapping(priority: HIGH_PRIORITY, activationStrategy: activationStrategy)
		asSearchProfileRegistry.getSearchProfileActivationMappings() >> List.of(activationMapping)

		AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withQueryContexts(List.of(DEFAULT_QUERY_CONTEXT)).withIndexType(INDEX_TYPE).withCatalogVersions(catalogVersions).build()

		when:
		final List<AsSearchProfileActivationGroup> searchProfileActivationGroups = asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)

		then:
		assertThat(searchProfileActivationGroups).hasSize(1)

		with(searchProfileActivationGroups.get(0)) {
			assertThat(getSearchProfiles()).containsExactly(searchProfile1, searchProfile2)
			assertThat(getGroups()).isEmpty()
		}
	}

	@Test
	def "Get search profiles activation groups with query context 3"() {
		given:
		searchProfile1.getQueryContext() >> UNKNOWN_QUERY_CONTEXT

		AsSearchProfileActivationStrategy activationStrategy = new AsSearchProfileActivationStrategy() {
					@Override
					public AsSearchProfileActivationGroup getSearchProfileActivationGroup(AsSearchProfileContext context) {
						return new AsSearchProfileActivationGroup(searchProfiles: List.of(searchProfile1, searchProfile2))
					}
				}

		AsSearchProfileActivationMapping activationMapping = new DefaultAsSearchProfileActivationMapping(priority: HIGH_PRIORITY, activationStrategy: activationStrategy)
		asSearchProfileRegistry.getSearchProfileActivationMappings() >> List.of(activationMapping)

		AsSearchProfileContext context = DefaultAsSearchProfileContext.builder().withQueryContexts(List.of(DEFAULT_QUERY_CONTEXT)).withIndexType(INDEX_TYPE).withCatalogVersions(catalogVersions).build()

		when:
		final List<AsSearchProfileActivationGroup> searchProfileActivationGroups = asSearchProfileActivationService.getSearchProfileActivationGroupsForContext(context)

		then:
		assertThat(searchProfileActivationGroups).hasSize(1)

		with(searchProfileActivationGroups.get(0)) {
			assertThat(getSearchProfiles()).containsExactly(searchProfile2)
			assertThat(getGroups()).isEmpty()
		}
	}
}
