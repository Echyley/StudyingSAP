/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.exceptions

import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

class ModelingAbstractAttributeAutoCreateOrPartOfExceptionUnitTest extends JUnitPlatformSpecification {
	private static final def ERROR_MESSAGE = "Attribute [User.createdComments] cannot be autoCreate or partOf, because it references an abstract type in the integration object."
	@Test
	def "default error message is formatted."() {
		given:
		def exception = new ModelingAbstractAttributeAutoCreateOrPartOfException(null, "User", "createdComments")

		expect:
		exception.getMessage().contains(ERROR_MESSAGE)
	}

	@Test
	def "localized error message is formatted."() {
		given:
		def exception = new ModelingAbstractAttributeAutoCreateOrPartOfException(null, "User", "createdComments")

		expect:
		exception.getLocalizedMessage().contains(ERROR_MESSAGE)
	}
}
