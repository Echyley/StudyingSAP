/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.exception

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class AbstractAttributeAutoCreateOrPartOfExceptionUnitTest extends JUnitPlatformSpecification {
	@Test
	def "Message is formatted."() {
		given:
		def exception = new AbstractAttributeAutoCreateOrPartOfException("User", "createdComments", null)

		expect:
		exception.getMessage().contains("Attribute [User.createdComments] cannot be autoCreate or partOf, because it references an abstract type in the integration object.")
	}

	@Test
	def "Fields are set."() {
		given:
		def exception = new AbstractAttributeAutoCreateOrPartOfException("User", "createdComments", null)

		expect:
		exception.getTypeCode() == "User"
		exception.getAttributeName() == "createdComments"
	}
}
