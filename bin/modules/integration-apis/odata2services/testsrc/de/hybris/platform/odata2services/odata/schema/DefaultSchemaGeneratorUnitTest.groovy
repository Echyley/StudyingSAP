/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.odata2services.odata.schema

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.exception.IntegrationObjectItemAndClassConflictException
import de.hybris.platform.integrationservices.model.DescriptorFactory
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.model.TypeDescriptor
import de.hybris.platform.integrationservices.model.impl.ClassTypeDescriptor
import de.hybris.platform.integrationservices.model.impl.ItemTypeDescriptor
import de.hybris.platform.integrationservices.util.TestApplicationContext
import de.hybris.platform.odata2services.odata.InvalidODataSchemaException
import de.hybris.platform.odata2services.odata.schema.association.AssociationListGeneratorRegistry
import de.hybris.platform.odata2services.odata.schema.entity.EntityContainerGenerator
import de.hybris.platform.odata2services.odata.schema.utils.SchemaUtils
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.apache.olingo.odata2.api.edm.provider.Association
import org.apache.olingo.odata2.api.edm.provider.EntityContainer
import org.apache.olingo.odata2.api.edm.provider.EntityType
import org.junit.Rule
import org.junit.Test

@UnitTest
class DefaultSchemaGeneratorUnitTest extends JUnitPlatformSpecification {
    private static final def ODATA_EXCEPTION_MESSAGE =
            'The EDMX schema could not be generated. Please make sure that your Integration Object is defined correctly.'
    private static final def ODATA_ERROR_CODE = 'schema_generation_error'
    private static final def DETAILED_MESSAGE = 'Unable to generate schema for null'
    private static final def IO = 'TEST_IO'
    private static final def NO_ITEMS = []
    private static final def NO_DESCRIPTORS = []
    private static final def IO_ITEM = new IntegrationObjectItemModel()
    private static final def DEPRECATED_ENTITY_TYPES = [new EntityType()]
    private static final def ENTITY_TYPES = [new EntityType()]
    private static final def ASSOCIATIONS = [new Association()]
    private static final def ENTITY_CONTAINERS = [new EntityContainer()]

    @Rule
    TestApplicationContext applicationContext = new TestApplicationContext()

    private def typeDescriptor = Stub(TypeDescriptor)
    private def io = Stub(IntegrationObjectModel) {
        getCode() >> IO
    }

    private SchemaElementGenerator<List<EntityType>, Collection<IntegrationObjectItemModel>> entityTypeListGenerator =
            Stub(SchemaElementGenerator) {
                generate(_) >> { List args -> args[0] ? DEPRECATED_ENTITY_TYPES : [] }
            }
    private SchemaElementGenerator<List<EntityType>, Collection<TypeDescriptor>> entityTypeListElementGenerator =
            Stub(SchemaElementGenerator) {
                generate(_ as Collection<TypeDescriptor>) >> { List args -> args[0] ? ENTITY_TYPES : [] }
            }
    private def associationListGeneratorRegistry =
            Stub(AssociationListGeneratorRegistry) {
                generateFor(_ as Collection<TypeDescriptor>) >> { List args -> args[0] ? ASSOCIATIONS : [] }
            }
    private def entityContainerGenerator =
            Stub(EntityContainerGenerator) {
                generate(ENTITY_TYPES, ASSOCIATIONS) >> ENTITY_CONTAINERS
                generate(DEPRECATED_ENTITY_TYPES, ASSOCIATIONS) >> ENTITY_CONTAINERS
            }
    private def descriptorFactory = Mock(DescriptorFactory) {
        createItemTypeDescriptor(IO_ITEM) >> typeDescriptor
    }

    private DefaultSchemaGenerator schemaGenerator = new DefaultSchemaGenerator(
            entityTypeListElementGenerator: entityTypeListElementGenerator,
            associationListGeneratorRegistry: associationListGeneratorRegistry,
            entityContainerGenerator: entityContainerGenerator,
            descriptorFactory: descriptorFactory)

    @Test
    def 'generate schema delegates to the default generate when entity container generator is not injected'() {
        given: 'entity container generator is not injected'
        schemaGenerator.entityContainerGenerator = null

        and: 'entity container generator is present in the application context'
        def contextEntityContainerGenerator = Mock EntityContainerGenerator
        applicationContext.addBean 'oDataEntityContainerGenerator', contextEntityContainerGenerator

        when:
        schemaGenerator.generateSchema([IO_ITEM])

        then:
        1 * contextEntityContainerGenerator.generate(ENTITY_TYPES, ASSOCIATIONS)
    }

    @Test
    def 'generate schema delegates to the default generateFor when association list generator registry is not injected'() {
        given: 'association list generator registry is not injected'
        schemaGenerator.associationListGeneratorRegistry = null

        and: 'association list generator registry is present in the application context'
        def contextAssociationListGeneratorRegistry = Mock AssociationListGeneratorRegistry
        applicationContext.addBean 'oDataAssociationListGeneratorRegistry', contextAssociationListGeneratorRegistry

        when:
        schemaGenerator.generateSchema([IO_ITEM])

        then:
        1 * contextAssociationListGeneratorRegistry.generateFor(_ as Collection<TypeDescriptor>)
    }

    @Test
    def 'generate schema delegates to the default generate when entity types generator is not injected'() {
        given: 'entity types generator is not injected'
        schemaGenerator.entityTypeListElementGenerator = null

        and: 'entity types generator is present in the application context'
        def contextEntityTypesGenerator = Mock SchemaElementGenerator<List<EntityType>, Collection<TypeDescriptor>>
        applicationContext.addBean 'oDataEntityTypeListElementGenerator', contextEntityTypesGenerator

        when:
        schemaGenerator.generateSchema([IO_ITEM])

        then:
        1 * contextEntityTypesGenerator.generate(_ as Collection<TypeDescriptor>)
    }

    @Test
    def 'generate schema delegates to the default createItemTypeDescriptor when descriptor factory is not injected'() {
        given: 'descriptor factory is not injected'
        schemaGenerator.descriptorFactory = null

        and: 'descriptor factory is present in the application context'
        def contextDescriptorFactory = Mock DescriptorFactory
        applicationContext.addBean 'integrationServicesDescriptorFactory', contextDescriptorFactory

        when:
        schemaGenerator.generateSchema([IO_ITEM])

        then:
        1 * contextDescriptorFactory.createItemTypeDescriptor(IO_ITEM)
    }

    @Test
    def 'generateSchema uses descriptorFactory to generate type descriptors from item models'() {
        when:
        schemaGenerator.generateSchema([IO_ITEM])

        then:
        1 * descriptorFactory.createItemTypeDescriptor(IO_ITEM)
    }

    @Test
    def 'generateSchema with null collection of item models throws exception'() {
        when:
        schemaGenerator.generateSchema null

        then:
        def e = thrown InvalidODataSchemaException
        e.message == ODATA_EXCEPTION_MESSAGE
        e.cause instanceof IllegalArgumentException
        e.cause.message == DETAILED_MESSAGE
        e.code == ODATA_ERROR_CODE
        e.httpStatus == HttpStatusCodes.INTERNAL_SERVER_ERROR
    }

    @Test
    def 'generate with null collection of type descriptors throws exception'() {
        when:
        schemaGenerator.generate null

        then:
        def e = thrown InvalidODataSchemaException
        e.message == ODATA_EXCEPTION_MESSAGE
        e.cause instanceof IllegalArgumentException
        e.cause.message == DETAILED_MESSAGE
        e.code == ODATA_ERROR_CODE
        e.httpStatus == HttpStatusCodes.INTERNAL_SERVER_ERROR
    }

    @Test
    def 'generateSchema returns schema with empty elements when passed collection of items is empty'() {
        given:
        def schema = schemaGenerator.generateSchema NO_ITEMS

        expect:
        with(schema) {
            namespace == SchemaUtils.NAMESPACE
            entityTypes.empty
            associations.empty
            entityContainers.empty
        }
    }

    @Test
    def 'generate returns schema with empty elements when passed collection of type descriptors is empty'() {
        given:
        def schema = schemaGenerator.generate NO_DESCRIPTORS

        expect:
        with(schema) {
            namespace == SchemaUtils.NAMESPACE
            entityTypes.empty
            associations.empty
            entityContainers.empty
        }
    }

    @Test
    def 'generateSchema returns schema that contains non-empty elements when passed collection of items is not empty'() {
        given:
        def schema = schemaGenerator.generateSchema([IO_ITEM])

        expect:
        with(schema) {
            namespace == SchemaUtils.NAMESPACE
            entityTypes == ENTITY_TYPES
            associations == ASSOCIATIONS
            entityContainers == ENTITY_CONTAINERS
        }
    }

    @Test
    def 'generate returns schema that contains non-empty elements when passed collection of type descriptors is not empty'() {
        given:
        def schema = schemaGenerator.generate([typeDescriptor])

        expect:
        with(schema) {
            namespace == SchemaUtils.NAMESPACE
            entityTypes == ENTITY_TYPES
            associations == ASSOCIATIONS
            entityContainers == ENTITY_CONTAINERS
        }
    }

    @Test
    def 'sap commerce namespace is present in the annotation attributes'() {
        given:
        def schema = schemaGenerator.generateSchema NO_ITEMS

        expect:
        schema.annotationAttributes.size() == 1
        with(schema.annotationAttributes[0]) {
            name == 'schema-version'
            namespace == 'http://schemas.sap.com/commerce'
            prefix == 's'
            text == '1'
        }
    }

    @Test
    def 'an exception is thrown when the IO contains Item and Class entities'() {
        when:
        schemaGenerator.generate([itemDescriptor(), classDescriptor()])

        then:
        def e = thrown IntegrationObjectItemAndClassConflictException
        e.message.contains(IO)
    }

    @Test
    def 'generateSchema returns schema that contains entity types generated by the entityTypeListGenerator when one is set'() {
        given:
        schemaGenerator.entityTypesGenerator = entityTypeListGenerator

        when:
        def schema = schemaGenerator.generateSchema([IO_ITEM])

        then:
        with(schema) {
            namespace == SchemaUtils.NAMESPACE
            entityTypes == DEPRECATED_ENTITY_TYPES
            associations == ASSOCIATIONS
            entityContainers == ENTITY_CONTAINERS
        }
    }

    private TypeDescriptor itemDescriptor() {
        Stub(ItemTypeDescriptor) { integrationObjectCode >> IO }
    }

    private TypeDescriptor classDescriptor() {
        Stub(ClassTypeDescriptor) { integrationObjectCode >> IO }
    }
}
