/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2services.export.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.core.model.type.ComposedTypeModel
import de.hybris.platform.integrationservices.model.*
import de.hybris.platform.integrationservices.service.IntegrationObjectService
import de.hybris.platform.odata2services.config.ODataServicesConfiguration
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity
import de.hybris.platform.odata2services.dto.IntegrationObjectBundleEntity
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery
import de.hybris.platform.servicelayer.search.FlexibleSearchService
import de.hybris.platform.servicelayer.type.TypeService
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class DefaultExportConfigurationSearchServiceUnitTest extends JUnitPlatformSpecification {

    def integrationObjectService = Stub(IntegrationObjectService)
    def flexibleSearchService = Stub(FlexibleSearchService)
    def configurationService = Stub(ODataServicesConfiguration)
    def typeService = Stub(TypeService)
    def descriptorFactory = Stub(DescriptorFactory)

    def exportConfigurationSearchService = new DefaultExportConfigurationSearchService(integrationObjectService,
            flexibleSearchService,
            configurationService,
            typeService,
            descriptorFactory)

    @Test
    def "find exportable integration objects"() {
        given:
        def ioCodes = ["IO1", "IO2", "IO3", "IO4"]
        configurationService.getExportableIntegrationObjects() >> ioCodes
        integrationObjectService.findIntegrationObject(ioCodes[0]) >> Stub(IntegrationObjectModel) { getCode() >> ioCodes[0] }
        integrationObjectService.findIntegrationObject(ioCodes[1]) >> Stub(IntegrationObjectModel) { getCode() >> ioCodes[1] }
        integrationObjectService.findIntegrationObject(ioCodes[2]) >> Stub(IntegrationObjectModel) { getCode() >> ioCodes[2] }
        integrationObjectService.findIntegrationObject(ioCodes[3]) >> { throw new ModelNotFoundException("IO4 not found!") }

        when:
        def integrationObjects = exportConfigurationSearchService.getExportableIntegrationObjects()

        then:
        integrationObjects.size() == 3
        integrationObjects.stream().allMatch() { io -> ioCodes.contains(io.code) }.booleanValue()
    }

    @Test
    @Unroll
    def "given an integration object find its root item instances #instancePks"() {
        given:
        def io = getIntegrationObject()
        configurationService.getExportableIntegrationObjects() >> [io.code]
        integrationObjectService.findIntegrationObject(io.code) >> io
        flexibleSearchService.searchUnique(flexibleSearchQuery(io.rootItem.type.code, "1")) >> Stub(ItemModel)
        flexibleSearchService.searchUnique(flexibleSearchQuery(io.rootItem.type.code, "2")) >> Stub(ItemModel)
        integrationObjectService.findIntegrationObject(_ as String) >> Stub(IntegrationObjectModel) { getCode() >> io.code }
        def integrationObjectBundleEntity = Stub(IntegrationObjectBundleEntity) {
            getIntegrationObjectCode() >> io.code
            getRootItemInstancePks() >> instancePks
        }

        when:
        def rootItemModelInstances = exportConfigurationSearchService.findRootItemInstances(integrationObjectBundleEntity)

        then:
        rootItemModelInstances.size() == size

        where:
        instancePks | size
        []          | 0
        ["1"]       | 1
        ["1", "2"]  | 2
    }

    @Test
    def "throws an exception when the integration object is not found"() {
        given:
        def ioCode = "invalidCode"
        integrationObjectService.findIntegrationObject(ioCode) >> { throw new ModelNotFoundException(ioCode) }

        when:
        exportConfigurationSearchService.findExportableIntegrationObjectByCode(ioCode)

        then:
        thrown ModelNotFoundException
    }

    @Test
    def "find an exportable integration object given its code"() {
        given:
        def ioCode = "validCode"
        def exportableIO = Stub(IntegrationObjectModel) { getCode() >> ioCode }
        configurationService.getExportableIntegrationObjects() >> [ioCode]
        integrationObjectService.findIntegrationObject(ioCode) >> exportableIO

        when:
        def io = exportConfigurationSearchService.findExportableIntegrationObjectByCode(ioCode)

        then:
        io.code == ioCode
    }

    @Test
    def "throws an exception when trying to find a non exportable integration"() {
        given:
        def ioCode = "validCode"
        def exportableIO = Stub(IntegrationObjectModel) { getCode() >> ioCode }
        integrationObjectService.findIntegrationObject(ioCode) >> exportableIO

        when:
        exportConfigurationSearchService.findExportableIntegrationObjectByCode(ioCode)

        then:
        def ex = thrown(NonExportableIntegrationObjectException)
        with(ex) {
            ex.message.contentEquals("The integration object [$ioCode] is not exportable. Please check the configuration property odata2services.exportable.integration.objects.")
            ex.integrationObjectCode == ioCode
        }
    }

    @Test
    def "throws an exception when trying to find an exportable integration without a root item"() {
        given:
        def io = exportableIntegrationObjectWithoutRootItem()
        configurationService.getExportableIntegrationObjects() >> [io.code]
        integrationObjectService.findIntegrationObject(io.code) >> io

        when:
        exportConfigurationSearchService.findExportableIntegrationObjectByCode(io.code)

        then:
        def ex = thrown(NonExportableIntegrationObjectNoRootItemException)
        with(ex) {
            ex.message.contentEquals("The integration object [$io.code] is not exportable because it does not have a root item assigned.")
            ex.integrationObjectCode == io.code
        }
    }

    @Test
    @Unroll
    def "find runtime attribute descriptors for #runtimeAttribute when configured attribute is #configuredRuntimeAttribute"() {
        given:
        def io = getIntegrationObject()
        def rootItemInstancePk = "1"
        configurationService.getExportableIntegrationObjects() >> [io.code]
        integrationObjectService.findIntegrationObject(io.code) >> io
        flexibleSearchService.searchUnique(flexibleSearchQuery(io.rootItem.type.code, rootItemInstancePk)) >> itemModel(io.code)
        and:
        descriptorFactory.createIntegrationObjectDescriptor(_ as IntegrationObjectModel) >> integrationObjectDescriptor(runtimeAttribute)
        typeService.getRuntimeAttributeDescriptorsForType(_ as ComposedTypeModel) >> attributeDescriptors(configuredRuntimeAttribute)

        when:
        def descriptors = exportConfigurationSearchService.findRuntimeAttributeDescriptors(configBundle(io.code, rootItemInstancePk))

        then:
        descriptors.size() == size

        where:
        runtimeAttribute          | configuredRuntimeAttribute | size
        'runtimeAttribute'        | 'runtimeAttribute'         | 1
        'anotherRuntimeAttribute' | null                       | 0
    }

    def getIntegrationObject() {
        Stub(IntegrationObjectModel) {
            getCode() >> "IO1"
            getRootItem() >> Stub(IntegrationObjectItemModel) {
                getType() >> Stub(ComposedTypeModel) {
                    getCode() >> "ExportableIORootItem"
                }
            }
        }
    }

    def exportableIntegrationObjectWithoutRootItem() {
        Stub(IntegrationObjectModel) {
            getCode() >> "validCode"
            getRootItem() >> null
        }
    }

    def flexibleSearchQuery(String rootItemType, String pk) {
        def query = "SELECT {pk} FROM {$rootItemType} WHERE {pk}=?pk"
        new FlexibleSearchQuery(query, ["pk": pk])
    }

    def itemModel(def code) {
        Stub(ItemModel) {
            getProperty("integrationObject") >> Stub(IntegrationObjectModel) {
                getCode() >> code
            }
        }
    }

    def configBundle(def code, def pk) {
        Stub(ConfigurationBundleEntity) {
            getIntegrationObjectBundles() >> [Stub(IntegrationObjectBundleEntity) {
                getIntegrationObjectCode() >> code
                getRootItemInstancePks() >> [pk]
            }]
        }
    }

    def integrationObjectDescriptor(def attrQualifier) {
        Stub(IntegrationObjectDescriptor) {
            getItemTypeDescriptors() >> [Stub(TypeDescriptor) {
                getAttributes() >> [Stub(TypeAttributeDescriptor) {
                    getQualifier() >> attrQualifier
                }]
            }]
        }
    }

    def attributeDescriptors(def attrQualifier) {
        [Stub(AttributeDescriptorModel) {
            getQualifier() >> attrQualifier
        }]
    }

}
