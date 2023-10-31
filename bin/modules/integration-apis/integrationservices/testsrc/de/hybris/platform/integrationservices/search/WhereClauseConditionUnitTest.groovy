/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.search

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

@UnitTest
class WhereClauseConditionUnitTest extends JUnitPlatformSpecification {

    private static final def INCOMPLETE_TIME_PATTERN = Pattern.compile(":[0-9]'")
    private static final def DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("''yyyy-MM-dd HH:mm:ss.SSS''")
    private static final String CONDITION_TEMPLATE = 'condition template'
    private static final String ATTRIBUTE = 'attribute'
    private static final String OPERATOR = '!='
    private static final String VALUE = 'value'

    @Test
    @Unroll
    def "condition == #other is #equal"() {
        given:
        def sample = new WhereClauseCondition(CONDITION_TEMPLATE, ATTRIBUTE, OPERATOR, VALUE)

        expect:
        (sample == other) == equal
        (sample.hashCode() == other.hashCode()) == equal

        where:
        other                                                                                                 | equal
        new WhereClauseCondition(CONDITION_TEMPLATE, ATTRIBUTE, OPERATOR, VALUE)                              | true
        new WhereClauseCondition(CONDITION_TEMPLATE, ATTRIBUTE, OPERATOR, VALUE, ConjunctiveOperator.UNKNOWN) | true
        new WhereClauseCondition(CONDITION_TEMPLATE, ATTRIBUTE, OPERATOR, VALUE, null)                        | true
        new WhereClauseCondition(CONDITION_TEMPLATE, ATTRIBUTE, OPERATOR, VALUE, ConjunctiveOperator.AND)     | false
        new WhereClauseCondition("different template", ATTRIBUTE, OPERATOR, VALUE)                            | false
        new WhereClauseCondition(CONDITION_TEMPLATE, 'different attribute', OPERATOR, VALUE)                  | true
        new WhereClauseCondition(CONDITION_TEMPLATE, ATTRIBUTE, '<>', VALUE)                                  | true
        new WhereClauseCondition(CONDITION_TEMPLATE, ATTRIBUTE, OPERATOR, 'different value')                  | true
    }

    @Test
    @Unroll
    def "extract attribute name from condition: '#filter'"() {
        when:
        def name = filter.getAttributeName()

        then:
        expectedName == name

        where:
        filter                                               | expectedName
        SimplePropertyWhereClauseCondition.eq('code', '123') | "code"
        SimplePropertyWhereClauseCondition.eq('', '')        | ""
    }

    @Test
    @Unroll
    def "extract attribute value from condition: '#filter'"() {
        when:
        def value = filter.getAttributeValue()

        then:
        expectedValue == value

        where:
        filter                                               | expectedValue
        SimplePropertyWhereClauseCondition.eq('code', '123') | "'123'"
        SimplePropertyWhereClauseCondition.eq('', '')        | "''"
    }

    @Test
    def "parameters passed to the constructor can be read back"() {
        given:
        def whereClauseCondition = new WhereClauseCondition(CONDITION_TEMPLATE, ATTRIBUTE, OPERATOR, VALUE, ConjunctiveOperator.AND)

        expect:
        with(whereClauseCondition) {
            attributeName == ATTRIBUTE
            attributeValue == VALUE
            compareOperator == OPERATOR
            conjunctiveOperator == ConjunctiveOperator.AND
        }
    }

    @Test
    @Unroll
    def "extract compare operator from condition: '#filter'"() {
        when:
        def operator = filter.getCompareOperator()

        then:
        expectedOperator == operator

        where:
        filter                                                     | expectedOperator
        new SimplePropertyWhereClauseCondition('code', '=', '123') | "="
        new SimplePropertyWhereClauseCondition('', '', '')         | ""
        new SimplePropertyWhereClauseCondition('code', 'IN', '')   | "IN"
    }

    @Test
    def 'attribute name can be changed in a condition'() {
        given:
        def original = new WhereClauseCondition('template', 'attributeName', '=', 'code', ConjunctiveOperator.OR)

        when:
        def updated = original.changeAttributeName('qualifier')

        then: 'all fields are copied except the attribute name'
        updated?.condition == original.condition
        updated?.attributeName == 'qualifier'
        updated?.compareOperator == original.compareOperator
        updated?.attributeValue == original.attributeValue
        updated?.conjunctiveOperator == original.conjunctiveOperator
        and: 'the original did not mutate'
        !original.is(updated)
        original.attributeName == 'attributeName'
    }

    @Test
    @Unroll
    def "format and convert UTC time [#utcTimestamp] to local system time [#systemTimestamp] for flexible search"() {
        when:
        def condition = new WhereClauseCondition('template', 'attributeName', '=', utcTimestamp, ConjunctiveOperator.AND)
        def formattedCondition = condition.changeAttributeNameAndFormatDateValue("aliasName")

        then:
        systemTimestamp == formattedCondition.attributeValue

        where:
        utcTimestamp                 | systemTimestamp
        "'2020-02-18T16:33:12.0000'" | formatDateValue(utcTimestamp)
        "'2020-02-18T16:33:12.000'"  | formatDateValue(utcTimestamp)
        "'2020-02-18T16:33:12.0'"    | formatDateValue(utcTimestamp)
        "'2020-02-18T16:33:12'"      | formatDateValue(utcTimestamp)
        "'2020-02-18T16:33:1'"       | formatDateValue(utcTimestamp)
        "'2020-02-18T16:33'"         | formatDateValue(utcTimestamp)
        "'2020-02-18T16:3'"          | formatDateValue(utcTimestamp)
    }

    def formatDateValue(final String localDateTime) {
        def dateTimeValue = LocalDateTime.parse(prepareDateTimeValue(localDateTime))
        def utcDateTime = dateTimeValue.atZone(ZoneOffset.UTC)
        def systemDateTime = utcDateTime.withZoneSameInstant(ZoneId.systemDefault())
        return DATE_TIME_FORMATTER.format(systemDateTime.toLocalDateTime())
    }

    def prepareDateTimeValue(final String dateTimeValue) {
        final def preparedDateTimeValue = new StringBuilder(dateTimeValue.replace("'", ""))
        return INCOMPLETE_TIME_PATTERN.matcher(dateTimeValue).find() ?
                preparedDateTimeValue.append("0").toString() : preparedDateTimeValue.toString()
    }

}
