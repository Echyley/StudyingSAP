/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class IntegrationObjectUtilsUnitTest extends JUnitPlatformSpecification {

	private static final def IO_CODE_1 = "TestProduct"
	private static final def IO_CODE_2 = "DiffProduct"
	private static final def IO_CLASS_CODE_1 = "Product"
	private static final def IO_CLASS_CODE_2 = "Product2"

	@Test
	def "IsEqual is #expected when #condition"() {
		expect:
		IntegrationObjectUtils.isEqual(class1, class2) == expected

		where:
		class1                                 | class2                                 | expected | condition
		classModel(IO_CODE_1, IO_CLASS_CODE_1) | classModel(IO_CODE_1, IO_CLASS_CODE_1) | true     | "integration object code and class code are equal"
		classModel(IO_CODE_1, IO_CLASS_CODE_1) | classModel(IO_CODE_2, IO_CLASS_CODE_1) | false    | "only class code is equal"
		classModel(IO_CODE_1, IO_CLASS_CODE_1) | classModel(IO_CODE_1, IO_CLASS_CODE_2) | false    | "only integration object code is equal"
		new IntegrationObjectClassModel()      | null                                   | false    | "class is not equal to null"
		null                                   | new IntegrationObjectClassModel()      | false    | "null is not equal to class"
		null                                   | null                                   | true     | "null is equal to null"
	}


	private IntegrationObjectClassModel classModel(ioCode, code) {
		def io = new IntegrationObjectModel(code: ioCode)
		def ioClass = new IntegrationObjectClassModel(code: code, integrationObject: io)
		ioClass
	}
}
