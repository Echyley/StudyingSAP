/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.outboundsync.job.impl.info

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

@UnitTest
class DefaultInfoExpressionGeneratorUnitTest extends JUnitPlatformSpecification {
    @Shared
    def TYPE_DESCRIPTOR = Stub TypeDescriptor

    def generator = new DefaultInfoExpressionGenerator()

    @Test
    def 'expression is empty JSON when provided item type is null'() {
        given:
        def expr = generator.generateInfoExpression null

        expect:
        def exprAsJson = JsonObject.createFrom(expr)
        exprAsJson.empty
   }

    @Test
    @Unroll
    def "expression is empty JSON when expression generators are #generators"() {
        given:
        generator.expressionGenerators = generators as Map<String, ExpressionGenerator>

        when:
        def expr = generator.generateInfoExpression TYPE_DESCRIPTOR

        then:
        JsonObject.createFrom(expr).empty

        where:
        generators << [null, [:], [gen: null], [(null): expGenerator()],
                       [(''): expGenerator()], [(' '): expGenerator()]]
    }

    @Test
    @Unroll
    def "expression is empty JSON when plugged in expression generator returns '#expr'"() {
        given:
        def gen = Stub(ExpressionGenerator) {
            generateExpression(TYPE_DESCRIPTOR) >> expr
        }
        generator.expressionGenerators = [tag: gen]

        when:
        def infoExpr = generator.generateInfoExpression TYPE_DESCRIPTOR

        then:
        def exprAsJson = JsonObject.createFrom(infoExpr)
        exprAsJson.empty

        where:
        expr << [null, '', ' ']
    }

    @Test
    def 'info expression contains expressions from all provided generators'() {
        given:
        generator.expressionGenerators = [one: expGenerator('expression.one'), two: expGenerator('expression|two')]

        when:
        def expr = generator.generateInfoExpression TYPE_DESCRIPTOR

        then:
        def exprAsJson = JsonObject.createFrom(expr)
        exprAsJson.getString('one') == 'expression.one'
        exprAsJson.getString('two') == 'expression|two'
    }

    ExpressionGenerator expGenerator(String expr = 'some.expression') {
        Stub(ExpressionGenerator) {
            generateExpression(TYPE_DESCRIPTOR) >> expr
        }
    }
}
