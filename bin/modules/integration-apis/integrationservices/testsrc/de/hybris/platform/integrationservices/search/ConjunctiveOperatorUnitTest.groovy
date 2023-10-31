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
package de.hybris.platform.integrationservices.search

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class ConjunctiveOperatorUnitTest extends JUnitPlatformSpecification {

    @Test
    @Unroll
    def "from string '#strOp' is '#expected'"() {
        expect:
        expected == ConjunctiveOperator.fromString(strOp)

        where:
        strOp | expected
        "AND" | ConjunctiveOperator.AND
        "OR"  | ConjunctiveOperator.OR
        ""    | ConjunctiveOperator.UNKNOWN
        null  | ConjunctiveOperator.UNKNOWN
    }
}
