/*
 *
 *  * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 *
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.utility

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class EditorBlacklistsUnitTest extends JUnitPlatformSpecification {
    @Test
    def "getAttributeBlackList returns a copy of set"(){
        when:
        def set = EditorBlacklists.getAttributeBlackList()
        set.add("notMatter")

        then:
        set.size() != EditorBlacklists.getAttributeBlackList().size()
    }

    @Test
    def "getTypesBlackList returns a copy of set"(){
        when:
        def set = EditorBlacklists.getTypesBlackList()
        set.add("notMatter")

        then:
        set.size() != EditorBlacklists.getTypesBlackList().size()
    }

}
