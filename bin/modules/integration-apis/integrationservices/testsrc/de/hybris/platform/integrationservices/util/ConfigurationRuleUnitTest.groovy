package de.hybris.platform.integrationservices.util

import de.hybris.platform.testframework.JUnitPlatformSpecification


import de.hybris.bootstrap.annotations.UnitTest

import org.junit.Test

@UnitTest
class ConfigurationRuleUnitTest extends JUnitPlatformSpecification {
    private static final ConfigurationRuleTestJavaBean managedBean = new ConfigurationRuleTestJavaBean()

    def rule = ConfigurationRule.createFor(managedBean)

    @Test
    def "manages #condition property"() {
        given:
        managedBean."$property" = originalValue

        when:
        rule.configuration()."$property" = newValue
        then:
        managedBean."$property" == newValue
        rule.configuration()."$property" == newValue

        when:
        rule.after()
        then:
        managedBean."$property" == originalValue
        rule.configuration()."$property" == originalValue

        where:
        property          | originalValue      | newValue           | condition
        'intProperty'     | 5                  | 25                 | 'a non-boxed primitive'
        'integerProperty' | Integer.valueOf(3) | Integer.valueOf(9) | 'a boxed primitive'
        'property'        | new Object()       | new Date()         | 'a non-primitive'
    }

    @Test
    def 'does not manage a write-only property'() {
        given:
        def originalValue = 'original'
        managedBean.property = originalValue
        and:
        def newValue = 'new'

        when:
        rule.configuration().writeOnlyProperty = newValue
        then:
        managedBean.property == newValue

        when:
        rule.after()
        then:
        managedBean.property != originalValue
    }
}
