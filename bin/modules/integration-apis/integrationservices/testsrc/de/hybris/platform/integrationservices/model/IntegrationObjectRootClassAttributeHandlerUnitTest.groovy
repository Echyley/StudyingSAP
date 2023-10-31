/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectRootClassAttributeHandlerUnitTest extends JUnitPlatformSpecification {

	private static final def ROOT_CLASS = new IntegrationObjectClassModel(root: true)
	private static final def NON_ROOT_CLASS = new IntegrationObjectClassModel(root: false)

	private final IntegrationObjectRootClassAttributeHandler handler = new IntegrationObjectRootClassAttributeHandler()

    @Test
    def "get returns #condition"() {
        expect:
        handler.get(new IntegrationObjectModel(classes: classes)) == expected

        where:
        classes                      | expected   | condition
        null                         | null       | "null when null classes are defined"
        []                           | null       | "null when no classes are defined"
        [NON_ROOT_CLASS]             | null       | "null when no rootClass is defined"
        [ROOT_CLASS]                 | ROOT_CLASS | "root class when 1 root class is defined on the integration object"
        [NON_ROOT_CLASS, ROOT_CLASS] | ROOT_CLASS | "root class when 1 root and other non root classes exist for the same integration object"
    }
}
