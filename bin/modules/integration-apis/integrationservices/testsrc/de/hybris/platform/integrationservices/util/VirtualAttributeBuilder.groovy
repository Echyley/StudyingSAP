/*
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.util

import de.hybris.platform.impex.jalo.ImpExException
import de.hybris.platform.integrationservices.model.IntegrationObjectItemVirtualAttributeModel
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel
import groovy.transform.Canonical
import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import org.junit.rules.ExternalResource

class VirtualAttributeBuilder extends ExternalResource {
    private final Set<Key> createdVirtualAttributes = []
    private final Set<VirtualAttributeDescriptor> createdDescriptors = []
    private String integrationObjectCode
    private String itemCode
    private IntegrationObjectVirtualAttributeDescriptorModel retrievalDescriptor
    private VirtualAttributeDescriptor descriptorBuilder
    private String attributeName

    static VirtualAttributeBuilder attribute() {
        new VirtualAttributeBuilder()
    }

    VirtualAttributeBuilder withName(final String name)
    {
        tap { attributeName = name }
    }

    VirtualAttributeBuilder withIntegrationObject(String code) {
        tap {integrationObjectCode = code }
    }
    VirtualAttributeBuilder withItem(final String code)
    {
        tap { itemCode = code }
    }

    VirtualAttributeBuilder withRetrievalDescriptor(VirtualAttributeDescriptor descriptor) {
        tap {
            descriptorBuilder = descriptor
            retrievalDescriptor = null
        }
    }

    VirtualAttributeBuilder withRetrievalDescriptor(final String code)
    {
        def desc = IntegrationTestUtil.findAny(IntegrationObjectVirtualAttributeDescriptorModel, { it.code == code })
                .orElseThrow { new IllegalArgumentException("Cannot build a Virtual Attribute with a descriptor code that does not exist yet.") }
        withRetrievalDescriptor desc
    }

    VirtualAttributeBuilder withRetrievalDescriptor(final IntegrationObjectVirtualAttributeDescriptorModel desc)
    {
        tap {
            retrievalDescriptor = desc
            descriptorBuilder = null
        }
    }

    void setup() throws ImpExException {
        def descriptor = deriveDescriptor()
        IntegrationTestUtil.importImpEx(
                '$item = integrationObjectItem(integrationObject(code), code)',
                'INSERT_UPDATE IntegrationObjectItemVirtualAttribute; $item[unique = true]            ; attributeName[unique = true]; retrievalDescriptor',
                "                                                   ; $integrationObjectCode:$itemCode; $attributeName              ; $descriptor.pk")
        createdVirtualAttributes << new Key(integrationObjectCode: integrationObjectCode,
                itemCode: itemCode, attributeName: attributeName)
    }

    private IntegrationObjectVirtualAttributeDescriptorModel deriveDescriptor() {
        retrievalDescriptor ?: buildDescriptor()
    }

    private IntegrationObjectVirtualAttributeDescriptorModel buildDescriptor() {
        if (descriptorBuilder) {
            retrievalDescriptor = descriptorBuilder.setup()
            createdDescriptors << descriptorBuilder
            descriptorBuilder = null
        }
        retrievalDescriptor
    }

    @Override
    protected void after() {
        cleanup()
    }

    void cleanup() {
        createdVirtualAttributes.each {key ->
            IntegrationTestUtil.remove(IntegrationObjectItemVirtualAttributeModel) {key.matches it }
        }
        createdVirtualAttributes.clear()
        createdDescriptors.each {it.cleanup() }
        createdDescriptors.clear()
    }

    static VirtualAttributeDescriptor logicDescriptor() {
        new VirtualAttributeDescriptor()
    }

    @EqualsAndHashCode(includes = ['code'])
    static class VirtualAttributeDescriptor extends ExternalResource {
        final Set<String> createdDescriptorCodes = []
        String code
        String logicLocation
        String type

        VirtualAttributeDescriptor withCode(String code) {
            tap { this.code = code }
        }

        VirtualAttributeDescriptor withLogicLocation(String logicLocation) {
            tap { this.logicLocation = logicLocation }
        }

        VirtualAttributeDescriptor withType(String type) {
            tap { this.type = type }
        }

        IntegrationObjectVirtualAttributeDescriptorModel setup() throws ImpExException {
            IntegrationTestUtil.importImpEx(
                    "INSERT_UPDATE IntegrationObjectVirtualAttributeDescriptor; code[unique = true]; logicLocation ; ${type ? ' type(code)' : ''}",
                    "                                                         ; $code              ; $logicLocation; ${type ?: ''}")
            createdDescriptorCodes << code
            IntegrationTestUtil.findAny(IntegrationObjectVirtualAttributeDescriptorModel, { it.code == code })
                    .orElse(null)
        }

        @Override
        protected void after() {
            cleanup()
        }

        void cleanup() {
            IntegrationTestUtil.remove(IntegrationObjectVirtualAttributeDescriptorModel) {createdDescriptorCodes.contains it.code }
            createdDescriptorCodes.clear()
        }
    }

    @Canonical
    @Immutable
    private static class Key {
        private String integrationObjectCode
        private String itemCode
        private String attributeName

        boolean matches(IntegrationObjectItemVirtualAttributeModel m) {
            def item = m.integrationObjectItem
            (item.integrationObject.code == integrationObjectCode
                    && item.code == itemCode
                    && m.attributeName == attributeName)
        }
    }
}
