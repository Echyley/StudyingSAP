/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.inboundservices.persistence.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.locking.ItemLockedForProcessingException
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.suspend.SystemIsSuspendedException
import de.hybris.platform.inboundservices.persistence.ContextItemModelService
import de.hybris.platform.inboundservices.persistence.PersistenceContext
import de.hybris.platform.inboundservices.persistence.hook.PersistenceHookExecutor
import de.hybris.platform.integrationservices.search.ItemNotFoundException
import de.hybris.platform.servicelayer.exceptions.SystemException
import de.hybris.platform.servicelayer.model.ModelService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Shared
import spock.lang.Unroll

@UnitTest
class DefaultItemPersistenceServiceUnitTest extends JUnitPlatformSpecification {
    private static final def ERR_MSG = 'does not matter'
    private static final def ITEM = new ItemModel()

    @Shared
    def context = Stub PersistenceContext
    def itemModelService = Stub ContextItemModelService
    def modelService = Mock ModelService
    def hookExecutor = Mock PersistenceHookExecutor
    def itemPersistenceService = new DefaultItemPersistenceService(itemModelService, modelService, hookExecutor)

    @Test
    @Unroll
    def 'DefaultItemPersistenceService cannot be instantiated when #param is null'() {
        when:
        new DefaultItemPersistenceService(contextItemModelService, service, executor)

        then:
        def e = thrown IllegalArgumentException
        e.message.contains(param)

        where:
        contextItemModelService       | service            | executor                      | param
        null                          | Stub(ModelService) | Stub(PersistenceHookExecutor) | 'ItemModelService'
        Stub(ContextItemModelService) | null               | Stub(PersistenceHookExecutor) | 'ModelService'
        Stub(ContextItemModelService) | Stub(ModelService) | null                          | 'PersistenceHookExecutor'
    }

    @Test
    def 'saveItems throws exception when item is not found nor created'() {
        given: 'findOrCreateItem returns null'
        itemModelService.findOrCreateItem(context) >> null

        when:
        itemPersistenceService.persist(context)

        then:
        thrown ItemNotFoundException
        0 * modelService._
    }

    @Test
    def 'persist does nothing when runPrePersistHook returns empty'() {
        given:
        itemModelService.findOrCreateItem(context) >> ITEM
        hookExecutor.runPrePersistHook(ITEM, context) >> Optional.empty()

        when:
        itemPersistenceService.persist(context)

        then:
        0 * modelService._
        0 * hookExecutor.runPostPersistHook(ITEM, context)
    }

    @Test
    def 'persist calls saveAll when runPrePersistHook returns an item, then invokes runPostPersistHook'() {
        given:
        itemModelService.findOrCreateItem(context) >> ITEM
        hookExecutor.runPrePersistHook(ITEM, context) >> Optional.of(ITEM)

        when:
        itemPersistenceService.persist(context)

        then:
        1 * modelService.saveAll()

        then:
        1 * hookExecutor.runPostPersistHook(ITEM, context)
    }

    @Test
    @Unroll
    def "handles #cause.class.simpleName thrown during persistence"() {
        given:
        modelService.saveAll() >> { throw cause }
        and:
        itemModelService.findOrCreateItem(context) >> ITEM
        hookExecutor.runPrePersistHook(ITEM, context) >> Optional.of(ITEM)

        when:
        itemPersistenceService.persist(context)

        then:
        def e = thrown PersistenceFailedException
        e.persistenceContext.is context
        e.cause.is cause

        where:
        cause << [new SystemIsSuspendedException(ERR_MSG),
                  new ItemLockedForProcessingException(ERR_MSG),
                  new SystemException(ERR_MSG)]
    }
}
