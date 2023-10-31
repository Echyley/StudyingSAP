/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence.hook.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.odata2services.odata.persistence.hook.PostPersistHook
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class PostPersistHookAdapterUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM = new ItemModel()

    def hook = Mock PostPersistHook
    def adapter = new PostPersistHookAdapter(hook)

    @Test
    def "calling execute invokes old hook execute"() {
        when:
        adapter.execute ITEM, Stub(PersistenceContext)

        then:
        1 * hook.execute(ITEM)
    }

    @Test
    def "constructor checks for an illegal argument"() {
        when:
        new PostPersistHookAdapter(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.is "PostPersistHook is required"
    }
}
