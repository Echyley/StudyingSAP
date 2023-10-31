/**
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.integration.provider.impl

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.commerceservices.search.searchservices.provider.impl.ProductReviewAverageRatingSnIndexerValueProvider
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.c2l.LanguageModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.core.model.user.UserModel
import de.hybris.platform.customerreview.enums.CustomerReviewApprovalType
import de.hybris.platform.customerreview.model.CustomerReviewModel
import de.hybris.platform.product.ProductService
import de.hybris.platform.searchservices.admin.data.SnField
import de.hybris.platform.searchservices.core.service.SnQualifier
import de.hybris.platform.searchservices.core.service.impl.LanguageSnQualifierProvider
import de.hybris.platform.searchservices.document.data.SnDocument
import de.hybris.platform.searchservices.enums.SnDocumentOperationType
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper
import de.hybris.platform.searchservices.indexer.service.SnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.SnIndexerValueProvider
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerContext
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerFieldWrapper
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerItemSourceOperation
import de.hybris.platform.searchservices.indexer.service.impl.PksSnIndexerItemSource
import de.hybris.platform.servicelayer.ServicelayerTransactionalSpockSpecification
import de.hybris.platform.servicelayer.i18n.CommonI18NService
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.servicelayer.user.UserService
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class ProductReviewAverageRatingSnIndexerValueProviderSpec extends ServicelayerTransactionalSpockSpecification {

	static final Double RATING_1 = 1d
	static final Double RATING_2 = 3d
	static final Double AVG_RATING = 2d
	static final String FIELD_1_ID = "field1"
	static final String FIELD_2_ID = "field2"
	static final String QUALIFIER_TYPE_ID = "qualifierType"

	@Resource
	ModelService modelService
	@Resource
	ProductService productService
	@Resource
	CommonI18NService commonI18NService
	@Resource
	UserService userService
	@Resource
	SnIndexerValueProvider<ProductModel> productReviewAverageRatingSnIndexerValueProvider

	ProductModel product01
	ProductModel product02
	LanguageModel languageEn
	LanguageModel languageDe
	UserModel user

	SnDocument document = new SnDocument()
	List<SnIndexerFieldWrapper> fieldWrappers
	List<SnIndexerFieldWrapper> localizedFieldWrappers

	def setup() {
		createCoreData()
		createDefaultCatalog()
		product01 = productService.getProductForCode("testProduct1")
		product02 = productService.getProductForCode("testProduct2")
		user = userService.getCurrentUser()
		languageEn = commonI18NService.getLanguage("en")
		languageDe = commonI18NService.getLanguage("de")

		SnField field = new SnField(id: FIELD_1_ID)
		fieldWrappers = [
			new DefaultSnIndexerFieldWrapper(field: field,
				valueProviderId: ProductReviewAverageRatingSnIndexerValueProvider.ID)
		]

		SnField localizedField = new SnField(id: FIELD_2_ID, localized: true, qualifierTypeId: QUALIFIER_TYPE_ID)
		SnQualifier qualifier = new LanguageSnQualifierProvider.LanguageSnQualifier(languageEn,
			commonI18NService.getLocaleForLanguage(languageEn))
		localizedFieldWrappers = [
			new DefaultSnIndexerFieldWrapper(field: localizedField,
				valueProviderId: ProductReviewAverageRatingSnIndexerValueProvider.ID,
				qualifiers: [qualifier]
			)]
	}

	@Test
	def "Provides review rating"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, null)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE,
			[product01.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, fieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_1_ID) == RATING_1
	}

	@Test
	def "Provides review rating for multiple products"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, null)
		createCustomerReview(RATING_2, CustomerReviewApprovalType.APPROVED, product02, null)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE,
			[product01.getPk(), product02.getPk()])
		SnDocument document2 = new SnDocument()

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, fieldWrappers, product01, document)
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, fieldWrappers, product02, document2)

		then:
		document.getFields().get(FIELD_1_ID) == RATING_1
		document2.getFields().get(FIELD_1_ID) == RATING_2
	}

	@Test
	def "Calculates the average review rating"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, null)
		createCustomerReview(RATING_2, CustomerReviewApprovalType.APPROVED, product01, null)
		createCustomerReview(RATING_2, CustomerReviewApprovalType.APPROVED, product02, null)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE,
			[product01.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, fieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_1_ID) == AVG_RATING
	}

	@Test
	def "Ignores document operation type DELETE"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, null)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.DELETE,
			[product01.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, fieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_1_ID) == null
	}

	@Test
	def "Filters review ratings on product PK list"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, null)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE,
			[product02.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, fieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_1_ID) == null
	}

	@Test
	def "Ignores unapproved review ratings"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.PENDING, product01, null)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE,
			[product01.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, fieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_1_ID) == null
	}

	@Test
	def "Provides qualified review rating"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, languageEn)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE_UPDATE,
			[product01.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, localizedFieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_2_ID) == [
			(Locale.ENGLISH): RATING_1
		]
	}

	@Test
	def "Provides qualified review rating for multiple products"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, languageEn)
		createCustomerReview(RATING_2, CustomerReviewApprovalType.APPROVED, product02, languageEn)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE_UPDATE,
			[product01.getPk(), product02.getPk()])
		SnDocument document2 = new SnDocument()

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, localizedFieldWrappers, product01, document)
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, localizedFieldWrappers, product02, document2)

		then:
		document.getFields().get(FIELD_2_ID) == [
			(Locale.ENGLISH): RATING_1
		]
		document2.getFields().get(FIELD_2_ID) == [
			(Locale.ENGLISH): RATING_2
		]
	}

	@Test
	def "Calculates the average qualified review rating"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, languageEn)
		createCustomerReview(RATING_2, CustomerReviewApprovalType.APPROVED, product01, languageEn)
		createCustomerReview(RATING_2, CustomerReviewApprovalType.APPROVED, product01, languageDe)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE_UPDATE,
			[product01.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, localizedFieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_2_ID) == [
			(Locale.ENGLISH): AVG_RATING
		]
	}

	@Test
	def "Filters qualified review ratings on product PK list"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.APPROVED, product01, languageEn)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE_UPDATE,
			[product02.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, localizedFieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_2_ID) == null
	}

	@Test
	def "Ignores unapproved qualified review ratings"() {
		given:
		createCustomerReview(RATING_1, CustomerReviewApprovalType.PENDING, product01, languageEn)
		DefaultSnIndexerContext indexerContext = createIndexerContext(SnDocumentOperationType.CREATE_UPDATE,
			[product01.getPk()])

		when:
		productReviewAverageRatingSnIndexerValueProvider.provide(indexerContext, localizedFieldWrappers, product01, document)

		then:
		document.getFields().get(FIELD_2_ID) == null
	}

	private createCustomerReview(final Double rating, final CustomerReviewApprovalType approvalStatus, final ProductModel product,
		final LanguageModel language) {
		CustomerReviewModel review = (CustomerReviewModel) this.getModelService().create(CustomerReviewModel.class)
		review.setRating(rating)
		review.setApprovalStatus(approvalStatus)
		review.setProduct(product)
		review.setLanguage(language)
		review.setUser(user)
		modelService.save(review)
	}

	private DefaultSnIndexerContext createIndexerContext(final SnDocumentOperationType operationType,
		final List<PK> productPks) {
		SnIndexerItemSourceOperation operation = new DefaultSnIndexerItemSourceOperation(operationType,
			new PksSnIndexerItemSource(productPks))
		SnIndexerContext indexerContext = new DefaultSnIndexerContext()
		indexerContext.setIndexerItemSourceOperations([operation])
		indexerContext
	}
}
