/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.unit.provider.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.commerceservices.search.searchservices.provider.impl.ProductReviewAverageRatingSnIndexerValueProvider
import de.hybris.platform.commerceservices.search.searchservices.unit.AbstractSpockTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.c2l.LanguageModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.searchservices.admin.data.SnField
import de.hybris.platform.searchservices.core.service.SnQualifier
import de.hybris.platform.searchservices.document.data.SnDocument
import de.hybris.platform.searchservices.enums.SnDocumentOperationType
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSource
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerFieldWrapper
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.search.SearchResult
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

@UnitTest
public class ProductReviewAverageRatingSnIndexerValueProviderSpec extends AbstractSpockTest {

	static final String FIELD_1_ID = "field1"
	static final String FIELD_2_ID = "field2"

	static final String QUALIFIER_TYPE_ID = "qualifierType"

	static final Double RATING_1 = 1d

	ProductModel product = Mock()
	SnDocument document = new SnDocument()
	SearchResult searchResult = Mock()

	FlexibleSearchService flexibleSearchService = Mock()
	CommonI18NService commonI18NService = Mock()

	LanguageModel language = Mock()
	PK productPk = new PK(1l)

	SnIndexerContext indexerContext

	ProductReviewAverageRatingSnIndexerValueProvider valueProvider

	def setup() {
		indexerContext = createIndexerContext(SnDocumentOperationType.CREATE, [productPk])

		valueProvider = new ProductReviewAverageRatingSnIndexerValueProvider()
		valueProvider.setFlexibleSearchService(flexibleSearchService)
		valueProvider.setCommonI18NService(commonI18NService)
	}

	@Test
	def "Return supported qualifier classes"() {
		when:
		Set<Class<?>> qualifierClasses = valueProvider.getSupportedQualifierClasses()

		then:
		assertThat(qualifierClasses).contains(LanguageModel)
	}

	@Test
	def "Fail to modify supported qualifier classes"() {
		given:
		Set<Class<?>> qualifierClasses = valueProvider.getSupportedQualifierClasses()

		when:
		qualifierClasses.add(this.getClass())

		then:
		thrown(UnsupportedOperationException)
	}

	@Test
	def "Provide null value for empty result"() {
		given:
		SnField field = new SnField(id: FIELD_1_ID)

		List<SnIndexerFieldWrapper> fieldWrappers = [
			new DefaultSnIndexerFieldWrapper(field: field,
				valueProviderId: ProductReviewAverageRatingSnIndexerValueProvider.ID)
		]
		flexibleSearchService.search(_ as FlexibleSearchQuery) >> searchResult

		when:
		valueProvider.provide(indexerContext, fieldWrappers, product, document)

		then:
		document.getFields().get(FIELD_1_ID) == null
	}

	@Test
	def "Provide qualified null value"() {
		given:
		SnField field = new SnField(id: FIELD_1_ID, localized: true, qualifierTypeId: QUALIFIER_TYPE_ID)
		SnQualifier qualifier1 = Mock()
		List<SnQualifier> qualifiers = [qualifier1]

		List<SnIndexerFieldWrapper> fieldWrappers = [
			new DefaultSnIndexerFieldWrapper(field: field,
				valueProviderId: ProductReviewAverageRatingSnIndexerValueProvider.ID,
				qualifiers: qualifiers)
		]
		qualifier1.getAs(Locale) >> Locale.ENGLISH
		qualifier1.getAs(LanguageModel) >> language

		flexibleSearchService.search(_ as FlexibleSearchQuery) >> searchResult

		when:
		valueProvider.provide(indexerContext, fieldWrappers, product, document)

		then:
		document.getFields().get(FIELD_1_ID) == null
	}

	@Test
	def "Provide values for multiple fields"() {
		given:
		SnField field1 = new SnField(id: FIELD_1_ID)

		SnField field2 = new SnField(id: FIELD_2_ID)

		List<SnIndexerFieldWrapper> fieldWrappers = [
			new DefaultSnIndexerFieldWrapper(field: field1,
				valueProviderId: ProductReviewAverageRatingSnIndexerValueProvider.ID),
			new DefaultSnIndexerFieldWrapper(field: field2,
				valueProviderId: ProductReviewAverageRatingSnIndexerValueProvider.ID)
		]

		product.getPk() >> productPk
		searchResult.getCount() >> 1
		searchResult.getResult() >> [[productPk, RATING_1]]
		flexibleSearchService.search(_ as FlexibleSearchQuery) >> searchResult

		when:
		valueProvider.provide(indexerContext, fieldWrappers, product, document)

		then:
		document.getFields().get(FIELD_1_ID) == RATING_1
		document.getFields().get(FIELD_2_ID) == RATING_1
	}

	private SnIndexerContext createIndexerContext(final SnDocumentOperationType operationType,
		final List<PK> productPks) {
		final SnIndexerItemSource itemSource = Mock()
		itemSource.getPks(_) >> productPks
		final SnIndexerItemSourceOperation operation = Mock()
		operation.getDocumentOperationType() >> operationType
		operation.getIndexerItemSource() >> itemSource
		final SnIndexerContext indexerContext = Mock()
		indexerContext.getIndexerItemSourceOperations() >> [operation]
		indexerContext.getAttributes() >> new HashMap<String, Object>()
		indexerContext
	}

}
