/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultExportConfigurationFilterUnitTest extends JUnitPlatformSpecification {

    def exportConfigurationFilter = new DefaultExportConfigurationFilter()

    @Test
    @Unroll
    def "verify that the filtered map #filteredMap has no sensitive information"() {
        when: "the  nullifySensitiveInformation method is called"
        exportConfigurationFilter.nullifySensitiveInformation(map)

        then:
        map == filteredMap

        where:
        map                                                                  | filteredMap
        [nonSensitive: 'value']                                              | [nonSensitive: 'value']
        [nonSensitive: [nonSensitiveNested: 'value']]                        | [nonSensitive: [nonSensitiveNested: 'value']]
        [credentialBasic: 'secret']                                          | [credentialBasic: null]
        [credentialBasic: [nestedAttribute: 'secret']]                       | [credentialBasic: null]
        [credentialBasicNested: [credentialBasic: 'secret']]                 | [credentialBasicNested: [credentialBasic: null]]
        [credentialConsumedOAuth: 'secret']                                  | [credentialConsumedOAuth: null]
        [credentialConsumedOAuth: [nestedAttribute: 'secret']]               | [credentialConsumedOAuth: null]
        [credentialConsumedOAuthNested: [credentialConsumedOAuth: 'secret']] | [credentialConsumedOAuthNested: [credentialConsumedOAuth: null]]
    }

}
