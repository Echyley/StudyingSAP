/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.searchservices.unit.core.service.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.PK
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.searchservices.admin.data.SnIndexType
import de.hybris.platform.searchservices.core.service.SnContext
import de.hybris.platform.searchservices.core.service.SnExpressionEvaluator
import de.hybris.platform.searchservices.core.service.impl.DefaultSnIdentityProvider
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test


@UnitTest
public class DefaultSnIdentityProviderSpec extends JUnitPlatformSpecification {

	static final String EXPRESSION = "expression"

	ModelService modelService = Mock()
	SnExpressionEvaluator snExpressionEvaluator = Mock()

	DefaultSnIdentityProvider identityProvider

	def setup() {
		identityProvider = new DefaultSnIdentityProvider()
		identityProvider.setModelService(modelService)
		identityProvider.setSnExpressionEvaluator(snExpressionEvaluator);
	}

	@Test
	def "Get identifier from item"() {
		given:
		SnIndexType indexType = Mock()
		Map<String, String> identityProviderParameters = Map.of()
		SnContext context = Mock()

		PK pk = PK.fromLong(1)
		ItemModel item = Mock()

		indexType.getIdentityProviderParameters() >> identityProviderParameters
		context.getIndexType() >> indexType
		item.getPk() >> pk

		when:
		def result = identityProvider.getIdentifier(context, item)

		then:
		result == "1"
	}

	@Test
	def "Get identifier from item using expression"() {
		given:
		SnIndexType indexType = Mock()
		Map<String, String> identityProviderParameters = Map.of(DefaultSnIdentityProvider.EXPRESSION_PARAM, EXPRESSION)
		SnContext context = Mock()

		PK pk = PK.fromLong(1)
		ItemModel item = Mock()

		Object value = 2

		indexType.getIdentityProviderParameters() >> identityProviderParameters
		context.getIndexType() >> indexType
		item.getPk() >> pk
		snExpressionEvaluator.evaluate(item, EXPRESSION) >> value

		when:
		def result = identityProvider.getIdentifier(context, item)

		then:
		result == "2"
	}

	@Test
	def "Get identifier from pk"() {
		given:
		SnIndexType indexType = Mock()
		Map<String, String> identityProviderParameters = Map.of()
		SnContext context = Mock()

		PK pk = PK.fromLong(2)

		indexType.getIdentityProviderParameters() >> identityProviderParameters
		context.getIndexType() >> indexType

		when:
		def result = identityProvider.getIdentifier(context, pk)

		then:
		result == "2"
	}

	@Test
	def "Get identifier from pk using expression"() {
		given:
		SnIndexType indexType = Mock()
		Map<String, String> identityProviderParameters = Map.of(DefaultSnIdentityProvider.EXPRESSION_PARAM, EXPRESSION)
		SnContext context = Mock()

		PK pk = PK.fromLong(2)
		ItemModel item = Mock()

		Object value = 4

		indexType.getIdentityProviderParameters() >> identityProviderParameters
		context.getIndexType() >> indexType
		modelService.get(pk) >> item
		snExpressionEvaluator.evaluate(item, EXPRESSION) >> value

		when:
		def result = identityProvider.getIdentifier(context, pk)

		then:
		result == "4"
	}
}
