/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.search

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class NavigationPropertyWhereClauseConditionUnitTest extends JUnitPlatformSpecification {

    private static final String CONDITION_TEMPLATE = "{%s} %s (%s)"
    private static final String COMPARE_OPERATOR = "IN"

    @Test
    @Unroll
    def "extract attribute name and value from condition"() {
        given:
        def actualCondition = String.format(CONDITION_TEMPLATE, actualAttrName, COMPARE_OPERATOR, actualAttrValue)

        when:
        NavigationPropertyWhereClauseCondition whereCC = new NavigationPropertyWhereClauseCondition(actualAttrName, actualAttrValue)

        then:
        with(whereCC) {
            attributeName == actualAttrName
            attributeValue == conditionAttrValue
            condition == actualCondition
            conjunctiveOperator == ConjunctiveOperator.UNKNOWN
        }

        where:
        actualAttrName | actualAttrValue | conditionAttrValue
        "attrName"     | "attrValue"     | "(attrValue)"
        ""             | ""              | "()"
    }
}
