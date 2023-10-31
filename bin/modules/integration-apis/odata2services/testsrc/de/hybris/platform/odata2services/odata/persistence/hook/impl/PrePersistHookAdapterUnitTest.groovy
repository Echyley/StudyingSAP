/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.persistence.hook.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test

@UnitTest
class PrePersistHookAdapterUnitTest extends JUnitPlatformSpecification {
    private static final def ITEM = new ItemModel()
    private static final def HOOK_RESULT = Optional.empty()

    def hook = Stub(PrePersistHook) {
        execute(ITEM) >> HOOK_RESULT
    }
    def adapter = new PrePersistHookAdapter(hook)

    @Test
    def "calling execute invokes old hook execute"() {
        when:
        def res = adapter.execute ITEM, Stub(PersistenceContext)

        then:
        res.is HOOK_RESULT
    }

    @Test
    def "constructor checks for an illegal argument"() {
        when:
        new PrePersistHookAdapter(null)

        then:
        def e = thrown IllegalArgumentException
        e.message.is "PrePersistHook is required"
    }
}
