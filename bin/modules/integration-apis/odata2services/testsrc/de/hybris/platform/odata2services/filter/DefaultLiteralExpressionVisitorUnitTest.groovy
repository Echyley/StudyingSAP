/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2services.filter

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.odata2services.filter.impl.DefaultLiteralExpressionVisitor
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.edm.EdmLiteral
import org.apache.olingo.odata2.api.edm.EdmSimpleType
import org.apache.olingo.odata2.api.uri.expression.LiteralExpression

@UnitTest
class DefaultLiteralExpressionVisitorUnitTest extends JUnitPlatformSpecification {
	def expressionVisitor = new DefaultLiteralExpressionVisitor();

	def "literal expression visitor is called"() {
		given:
		def expression = Stub(LiteralExpression)
		def literal = new EdmLiteral(Stub(EdmSimpleType), "stringAttributeValue")

		when:
		def resultingObject = expressionVisitor.visit(expression, literal)

		then:
		resultingObject == "stringAttributeValue"
	}
}
