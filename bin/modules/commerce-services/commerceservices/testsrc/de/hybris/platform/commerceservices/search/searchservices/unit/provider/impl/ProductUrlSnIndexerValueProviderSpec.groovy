/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.search.searchservices.unit.provider.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.commerceservices.search.searchservices.provider.impl.ProductUrlSnIndexerValueProvider
import de.hybris.platform.commerceservices.search.searchservices.unit.AbstractSpockTest
import de.hybris.platform.commerceservices.url.UrlResolver
import de.hybris.platform.core.model.c2l.LanguageModel
import de.hybris.platform.core.model.product.ProductModel
import de.hybris.platform.searchservices.admin.data.SnField
import de.hybris.platform.searchservices.admin.data.SnIndexType
import de.hybris.platform.searchservices.core.service.SnQualifier
import de.hybris.platform.searchservices.core.service.SnSessionService
import de.hybris.platform.searchservices.document.data.SnDocument
import de.hybris.platform.searchservices.indexer.service.SnIndexerContext
import de.hybris.platform.searchservices.indexer.service.SnIndexerFieldWrapper
import de.hybris.platform.searchservices.indexer.service.impl.DefaultSnIndexerFieldWrapper
import de.hybris.platform.servicelayer.i18n.I18NService
import org.junit.Test

import static org.assertj.core.api.Assertions.assertThat

@UnitTest
public class ProductUrlSnIndexerValueProviderSpec extends AbstractSpockTest {

	static final String INDEX_TYPE_ID = "indexType"

	static final String FIELD_1_ID = "field1"
	static final String FIELD_2_ID = "field2"

	static final String QUALIFIER_TYPE_ID = "qualifierType"

	static final String URL_1 = "https://store.com/product/11"

	SnIndexerContext indexerContext = Mock()
	ProductModel product = Mock()
	SnDocument document = new SnDocument()

	UrlResolver<ProductModel> urlResolver = Mock()
	I18NService i18nService = Mock()
	SnSessionService snSessionService = Mock()

	ProductUrlSnIndexerValueProvider valueProvider

	def setup() {
		indexerContext.getIndexType() >> new SnIndexType(id: INDEX_TYPE_ID)

		valueProvider = new ProductUrlSnIndexerValueProvider()
		valueProvider.setUrlResolver(urlResolver)
		valueProvider.setI18nService(i18nService)
		valueProvider.setSnSessionService(snSessionService)
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
	def "Provide value"() {
		given:
		SnField field = new SnField(id: FIELD_1_ID)
		Map<String, String> valueProviderParameters = Map.of()

		List<SnIndexerFieldWrapper> fieldWrappers = [
				new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)
		]
		urlResolver.resolve(product) >> URL_1

		when:
		valueProvider.provide(indexerContext, fieldWrappers, product, document)

		then:
		document.getFields().get(FIELD_1_ID) == URL_1
	}

	@Test
	def "Provide null value"() {
		given:
		SnField field = new SnField(id: FIELD_1_ID)
		Map<String, String> valueProviderParameters = Map.of()

		List<SnIndexerFieldWrapper> fieldWrappers = [
				new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters)
		]

		urlResolver.resolve(product) >> null

		when:
		valueProvider.provide(indexerContext, fieldWrappers, product, document)

		then:
		document.getFields().get(FIELD_1_ID) == null
	}

	@Test
	def "Provide qualified value"() {
		given:
		SnField field = new SnField(id: FIELD_1_ID, localized: true, qualifierTypeId: QUALIFIER_TYPE_ID)
		Map<String, String> valueProviderParameters = Map.of()
		SnQualifier qualifier1 = Mock()
		List<SnQualifier> qualifiers = List.of(qualifier1)

		List<SnIndexerFieldWrapper> fieldWrappers = [
				new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters, qualifiers: qualifiers)
		]
		qualifier1.getAs(Locale) >> Locale.ENGLISH

		urlResolver.resolve(product) >> URL_1

		when:
		valueProvider.provide(indexerContext, fieldWrappers, product, document)

		then:
		document.getFields().get(FIELD_1_ID) == [
				(Locale.ENGLISH): URL_1
		]
		1 * i18nService.setCurrentLocale(Locale.ENGLISH)
		1 * snSessionService.initializeSession()
		1 * snSessionService.destroySession()
	}

	@Test
	def "Provide null as qualified value"() {
		given:
		SnField field = new SnField(id: FIELD_1_ID, localized: true, qualifierTypeId: QUALIFIER_TYPE_ID)
		Map<String, String> valueProviderParameters = Map.of()
		SnQualifier qualifier1 = Mock()
		List<SnQualifier> qualifiers = List.of(qualifier1)

		List<SnIndexerFieldWrapper> fieldWrappers = [
			new DefaultSnIndexerFieldWrapper(field: field, valueProviderParameters: valueProviderParameters, qualifiers: qualifiers)
		]
		qualifier1.getAs(Locale) >> Locale.ENGLISH

		when:
		valueProvider.provide(indexerContext, fieldWrappers, product, document)

		then:
		document.getFields().get(FIELD_1_ID) == [
			(Locale.ENGLISH): null
		]
		1 * i18nService.setCurrentLocale(Locale.ENGLISH)
		1 * snSessionService.initializeSession()
		1 * snSessionService.destroySession()
	}

	@Test
	def "Provide values for multiple fields"() {
		given:
		SnField field1 = new SnField(id: FIELD_1_ID)
		Map<String, String> valueProviderParameters1 = Map.of()

		SnField field2 = new SnField(id: FIELD_2_ID)
		Map<String, String> valueProviderParameters2 = Map.of()

		List<SnIndexerFieldWrapper> fieldWrappers = [
				new DefaultSnIndexerFieldWrapper(field: field1, valueProviderParameters: valueProviderParameters1),
				new DefaultSnIndexerFieldWrapper(field: field2, valueProviderParameters: valueProviderParameters2)
		]

		urlResolver.resolve(product) >> URL_1

		when:
		valueProvider.provide(indexerContext, fieldWrappers, product, document)

		then:
		document.getFields().get(FIELD_1_ID) == URL_1
		document.getFields().get(FIELD_2_ID) == URL_1
	}
}
