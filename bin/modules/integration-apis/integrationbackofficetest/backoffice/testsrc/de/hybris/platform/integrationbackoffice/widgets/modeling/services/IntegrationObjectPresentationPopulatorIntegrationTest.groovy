/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.integrationbackoffice.widgets.modeling.services

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.integrationbackoffice.BeanRegisteringRule
import de.hybris.platform.integrationbackoffice.utility.DefaultItemTypeMatchSelector
import de.hybris.platform.integrationbackoffice.widgets.modeling.builders.DefaultDataStructureBuilder
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.impl.DefaultIntegrationObjectItemTypeMatchService
import de.hybris.platform.servicelayer.ServicelayerTest
import org.junit.Rule
import org.junit.Test

import javax.annotation.Resource

@IntegrationTest
class IntegrationObjectPresentationPopulatorIntegrationTest extends ServicelayerTest {

    static final def POPULATOR = 'integrationObjectPresentationPopulator'
    static final def PRESENTATION = 'defaultIntegrationEditorPresentation'
    static final def DATA_STRUCTURE_BUILDER = 'dataStructureBuilder'
    static final def ITEM_MATCH_SERVICE = 'itemTypeMatchService'
    static final def ITEM_MATCH_SELECTOR = 'itemTypeMatchSelector'

    @Resource
    private IntegrationObjectPresentationPopulator integrationObjectPresentationPopulator

    @Rule
    public BeanRegisteringRule rule = new BeanRegisteringRule()
            .register(IntegrationObjectPresentationPopulator, POPULATOR)
            .register(IntegrationObjectPresentation, PRESENTATION)
            .register(DefaultDataStructureBuilder, DATA_STRUCTURE_BUILDER)
            .register(DefaultIntegrationObjectItemTypeMatchService, ITEM_MATCH_SERVICE)
            .register(DefaultItemTypeMatchSelector, ITEM_MATCH_SELECTOR)

    @Test
    void "can populate correct bean"() {
        expect:
        integrationObjectPresentationPopulator.getIOEditorPresentation() != null
    }
}
