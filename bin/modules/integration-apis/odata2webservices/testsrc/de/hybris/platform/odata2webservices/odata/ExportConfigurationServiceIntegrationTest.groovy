/*
 *  Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.odata2webservices.odata

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.type.AttributeDescriptorModel
import de.hybris.platform.integrationservices.enums.AuthenticationType
import de.hybris.platform.integrationservices.model.InboundChannelConfigurationModel
import de.hybris.platform.integrationservices.model.IntegrationObjectDescriptor
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.JsonObject
import de.hybris.platform.integrationservices.util.impex.IntegrationServicesEssentialData
import de.hybris.platform.odata2services.dto.ConfigurationBundleEntity
import de.hybris.platform.odata2services.dto.ExportEntity
import de.hybris.platform.odata2services.dto.IntegrationObjectBundleEntity
import de.hybris.platform.odata2services.export.ExportConfigurationService
import de.hybris.platform.odata2services.export.PostmanCollectionGenerator
import de.hybris.platform.odata2webservices.odata.builders.ODataRequestBuilder
import de.hybris.platform.odata2webservices.odata.builders.PathInfoBuilder
import de.hybris.platform.scripting.model.ScriptModel
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.model.ModelService
import groovy.json.JsonBuilder
import org.apache.olingo.odata2.api.commons.HttpStatusCodes
import org.junit.ClassRule
import org.junit.Test
import spock.lang.Shared

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.util.impex.IntegrationServicesEssentialData.integrationServicesEssentialData
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE

@IntegrationTest
class ExportConfigurationServiceIntegrationTest extends ServicelayerSpockSpecification {

    private static final String TEST_NAME = "ExportConfigurationServiceIntegrationTest"
    private static final String EXPORTABLE_IO = "${TEST_NAME}_ICCExportableIO"
    private static final String QUALIFIER = "configExportRuntimeAttribute"
    private static final String ITEM_NAME = "InboundChannelConfigurations_${EXPORTABLE_IO}"
    private static final String SCRIPT_CODE = "productVirtualAttributeScript"
    private static final String SCRIPT_ITEM_NAME = "Scripts_${SCRIPT_CODE}"
    private static final String DESCRIPTOR_CODE = "${SCRIPT_CODE}Descriptor"
    private static final String ITEM_TYPE = "Product"
    private static final String RUNTIME_ATTRIBUTE_NAME = "AttributeDescriptors_${QUALIFIER}|$ITEM_TYPE"

    private static final String INTEGRATION_SERVICE = "IntegrationService"
    private static final String SCRIPT_SERVICE = "ScriptService"
    private static final String RUNTIME_ATTRIBUTE_SERVICE = "RuntimeAttributeService"

    private static final String ICC_URL = "{{hostUrl}}/odata2webservices/IntegrationService/InboundChannelConfigurations"
    private static final String SCRIPT_URL = "{{hostUrl}}/odata2webservices/ScriptService/Scripts"
    private static final String RUNTIME_ATTRIBUTE_URL = "{{hostUrl}}/odata2webservices/RuntimeAttributeService/AttributeDescriptors"
    private static final String POSTMAN_COLLECTION_SCHEMA = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
    private static final String POSTMAN_COLLECTION_NAME = "ImportConfiguration"

    private static final String AUTHENTICATION_TYPE = "basic"
    private static final String HTTP_METHOD = "POST"
    private static final String HOST_URL_KEY = "hostUrl"
    private static final String PASSWORD_KEY = "password"
    private static final String USERNAME_KEY = "username"
    private static final String PASSWORD_VALUE = "{{password}}"
    private static final String USERNAME_VALUE = "{{username}}"
    private static final String MODE = "raw"
    private static final String VIRTUAL_ATTRIBUTE = "exportableVirtualAttribute"
    private static final String SCRIPT = """ 
    import de.hybris.platform.core.model.product.ProductModel
            
    def product = itemModel as ProductModel
    product.code.toUpperCase() 
    """
    @Resource(name = "defaultODataFacade")
    private ODataFacade facade
    @Resource(name = "exportConfigurationService")
    private ExportConfigurationService exportConfigurationService
    @Resource(name = "integrationObjectConversionService")
    private IntegrationObjectConversionService integrationObjectConversionService
    @Resource(name = "postmanCollectionGenerator")
    private PostmanCollectionGenerator postmanCollectionGenerator
    @Resource(name = "modelService")
    private ModelService modelService

    @Shared
    @ClassRule
    IntegrationServicesEssentialData essentialData = integrationServicesEssentialData()

    def setup() {
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Script; code[unique=true]; scriptType(code); autodisabling; content',
                "                           ; $SCRIPT_CODE     ; GROOVY          ; false        ; \"$SCRIPT\"",

                ' INSERT_UPDATE AttributeDescriptor; qualifier[unique = true]; attributeType(code); enclosingType(code)[unique = true]; partOf[default = false]; unique[default = false]; optional[default = true]; generate[default = false]; localized[default = false]',
                "                                  ; $QUALIFIER              ; java.lang.String   ; $ITEM_TYPE"
        )

        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE IntegrationObjectVirtualAttributeDescriptor; code[unique = true]; logicLocation       ; type(code)',
                "                                                                ; $DESCRIPTOR_CODE   ; model://$SCRIPT_CODE; java.lang.String",
                'INSERT_UPDATE IntegrationObject; code[unique = true]',
                "                               ; $EXPORTABLE_IO",

                'INSERT_UPDATE IntegrationObjectItem; integrationObject(code)[unique = true]; code[unique = true]; type(code)',
                "                                   ; $EXPORTABLE_IO                        ; Product            ; Product",
                "                                   ; $EXPORTABLE_IO                        ; CatalogVersion     ; CatalogVersion",
                "                                   ; $EXPORTABLE_IO                        ; Catalog            ; Catalog",
                "                                   ; $EXPORTABLE_IO                        ; Unit               ; Unit",

                '$item = integrationObjectItem(integrationObject(code), code)',
                '$attributeDescriptor = attributeDescriptor(enclosingType(code), qualifier)',
                'INSERT_UPDATE IntegrationObjectItemAttribute; $item[unique = true]          ; attributeName[unique = true]; $attributeDescriptor   ; returnIntegrationObjectItem(integrationObject(code), code)',
                "                                            ; $EXPORTABLE_IO:Catalog        ; id                          ; Catalog:id             ;",
                "                                            ; $EXPORTABLE_IO:CatalogVersion ; version                     ; CatalogVersion:version ;",
                "                                            ; $EXPORTABLE_IO:CatalogVersion ; catalog                     ; CatalogVersion:catalog ; $EXPORTABLE_IO:Catalog",
                "                                            ; $EXPORTABLE_IO:Product        ; code                        ; Product:code           ;",
                "                                            ; $EXPORTABLE_IO:Product        ; catalogVersion              ; Product:catalogVersion ; $EXPORTABLE_IO:CatalogVersion",
                "                                            ; $EXPORTABLE_IO:Product        ; $QUALIFIER                  ; Product:$QUALIFIER     ;",
                "                                            ; $EXPORTABLE_IO:Unit           ; code                        ; Unit:code              ;",

                'INSERT_UPDATE InboundChannelConfiguration; integrationObject(code)[unique = true]; authenticationType(code)',
                "                                         ; $EXPORTABLE_IO                        ; OAUTH",

                'INSERT_UPDATE IntegrationObjectItemVirtualAttribute; $item[unique = true]      ; attributeName[unique = true]; retrievalDescriptor(code)',
                "                                                   ; $EXPORTABLE_IO:$ITEM_TYPE ; $VIRTUAL_ATTRIBUTE          ; $DESCRIPTOR_CODE"
        )
    }

    def cleanupSpec() {
        IntegrationTestUtil.remove(InboundChannelConfigurationModel) { it.integrationObject.code == EXPORTABLE_IO }
        IntegrationTestUtil.remove(IntegrationObjectModel) { it.code == EXPORTABLE_IO }
        IntegrationTestUtil.remove(ScriptModel) { it.code == SCRIPT_CODE }
        IntegrationTestUtil.remove(AttributeDescriptorModel) { it.qualifier == QUALIFIER  && it.enclosingType.code == ITEM_TYPE }
    }

    @Test
    def "import the exported InboundChannelConfiguration"() {
        when: "generate export entities from an existing ICC"
        Set<ExportEntity> exportEntities = exportConfigurationService.generateExportConfiguration(getConfigurationBundleEntity())
        and: "delete the existing ICC and its related entities"
        deleteICCAndRelatedEntities()

        then: "the ICC was deleted"
        findExistingChannelConfigWithIO(EXPORTABLE_IO) == null
        and: "the virtual attribute script was deleted"
        findActiveScript(SCRIPT_CODE) == null
        and: "the runtime attribute was deleted"
        findRuntimeAttribute(QUALIFIER, ITEM_TYPE) == null

        and: "verify that the export entities include the ICC and its related entities"
        exportEntities.size() == 3
        exportEntities[0].requestUrl == RUNTIME_ATTRIBUTE_URL
        exportEntities[1].requestUrl == SCRIPT_URL
        exportEntities[2].requestUrl == ICC_URL
        and: "import the export entities to recreate the ICC and its related entities"
        exportEntities.forEach({ exportEntity ->
            def postRequest = postRequest(exportEntity.requestUrl).withBody(exportEntity.requestBodies[0])
            def response = facade.handleRequest(createContext(postRequest))
            response.status == HttpStatusCodes.CREATED
        })

        and: "verify the recreated ICC"
        def recreatedICC = findExistingChannelConfigWithIO(EXPORTABLE_IO)
        recreatedICC.authenticationType == AuthenticationType.OAUTH
        recreatedICC.integrationObject.code == EXPORTABLE_IO
        and: "verify the recreated virtual attribute script"
        findActiveScript(SCRIPT_CODE) != null
        and: "verify the recreated runtime attribute"
        findRuntimeAttribute(QUALIFIER, ITEM_TYPE) != null
    }

    @Test
    def "export an InboundChannelConfiguration and verify the generated payload"() {
        when: "generate an exportable payload from an existing ICC"
        def exportEntities = exportConfigurationService.generateExportConfiguration(getConfigurationBundleEntity())

        then: "verify the content of the export entities"
        exportEntities.size() == 3
        and: "verify the generated runtime attribute"
        exportEntities[0].requestUrl == RUNTIME_ATTRIBUTE_URL
        exportEntities[0].requestBodies.size() == 1
        exportEntities[0].requestBodies[0].contentEquals(generatedRuntimeAttributeBody())

        and: "verify the generated virtual attribute script"
        exportEntities[1].requestUrl == SCRIPT_URL
        exportEntities[1].requestBodies.size() == 1
        exportEntities[1].requestBodies[0].contentEquals(generatedScriptBody())

        and: "verify the generated ICC"
        exportEntities[2].requestUrl == ICC_URL
        exportEntities[2].requestBodies.size() == 1
        exportEntities[2].requestBodies[0].contentEquals(generatedICCBody())
    }

    @Test
    def "generate a Postman collection and verify the generated payload"() {
        when: "generate the Postman collection from the given configuration bundle entity"
        def postmanCollection = postmanCollectionGenerator.generate(getConfigurationBundleEntity())

        then: "verify the generate Postman collection"
        def jsonPostmanCollection = JsonObject.createFrom(postmanCollection)
        with(jsonPostmanCollection) {
            getString("info.name") == POSTMAN_COLLECTION_NAME
            getString("info.schema") == POSTMAN_COLLECTION_SCHEMA
            exists("item")
            getCollection("item[*]").size() == 3

            and: "verify the generated runtime attribute"
            getString("item[0].name") == RUNTIME_ATTRIBUTE_NAME
            getString("item[0].request.url.raw") == RUNTIME_ATTRIBUTE_URL
            getString("item[0].request.method") == HTTP_METHOD
            getString("item[0].request.body.mode") == MODE
            getString("item[0].request.auth.type") == AUTHENTICATION_TYPE
            getString("item[0].request.body.raw") == generatedRuntimeAttributeBody()
            getCollection("item[0].request.url.host").contains("{{hostUrl}}")
            getCollection("item[0].request.url.path").containsAll("odata2webservices", "RuntimeAttributeService", "AttributeDescriptors")
            getCollection("item[0].request.auth.basic").size() == 2
            getCollection("item[0].request.auth.basic").containsAll([[key: PASSWORD_KEY, value: PASSWORD_VALUE, type: "string"],
                                                                     [key: USERNAME_KEY, value: USERNAME_VALUE, type: "string"]])
            and: "verify the generated virtual attribute script"
            getString("item[1].name") == SCRIPT_ITEM_NAME
            getString("item[1].request.url.raw") == SCRIPT_URL
            getString("item[1].request.method") == HTTP_METHOD
            getString("item[1].request.body.mode") == MODE
            getString("item[1].request.auth.type") == AUTHENTICATION_TYPE
            getString("item[1].request.body.raw") == generatedScriptBody()
            getCollection("item[1].request.url.host").contains("{{hostUrl}}")
            getCollection("item[1].request.url.path").containsAll("odata2webservices", "ScriptService", "Scripts")
            getCollection("item[1].request.auth.basic").size() == 2
            getCollection("item[1].request.auth.basic").containsAll([[key: PASSWORD_KEY, value: PASSWORD_VALUE, type: "string"],
                                                                     [key: USERNAME_KEY, value: USERNAME_VALUE, type: "string"]])
            and: "verify the generated ICC"
            getString("item[2].name") == ITEM_NAME
            getString("item[2].request.url.raw") == ICC_URL
            getString("item[2].request.method") == HTTP_METHOD
            getString("item[2].request.body.mode") == MODE
            getString("item[2].request.auth.type") == AUTHENTICATION_TYPE
            getString("item[2].request.body.raw") == generatedICCBody()
            getCollection("item[2].request.url.host").contains("{{hostUrl}}")
            getCollection("item[2].request.url.path").containsAll("odata2webservices", "IntegrationService", "InboundChannelConfigurations")
            getCollection("item[2].request.auth.basic").size() == 2
            getCollection("item[2].request.auth.basic").containsAll([[key: PASSWORD_KEY, value: PASSWORD_VALUE, type: "string"],
                                                                     [key: USERNAME_KEY, value: USERNAME_VALUE, type: "string"]])

            getCollection("variable").size() == 3
            getCollection("variable").containsAll([[id: PASSWORD_KEY, key: PASSWORD_KEY, value: null],
                                                   [id: USERNAME_KEY, key: USERNAME_KEY, value: null],
                                                   [id: HOST_URL_KEY, key: HOST_URL_KEY, value: null]])
        }
    }

    def generatedICCBody() {
        def inboundChannelConfig = findExistingChannelConfigWithIO(EXPORTABLE_IO)
        def ioMap = integrationObjectConversionService.convert(getConversionContext(inboundChannelConfig, INTEGRATION_SERVICE))
        return new JsonBuilder(ioMap).toPrettyString()
    }

    def generatedScriptBody() {
        def activeScript = findActiveScript(SCRIPT_CODE)
        def ioMap = integrationObjectConversionService.convert(getConversionContext(activeScript, SCRIPT_SERVICE))
        return new JsonBuilder(ioMap).toPrettyString()
    }

    def generatedRuntimeAttributeBody() {
        def runtimeAttribute = findRuntimeAttribute(QUALIFIER, ITEM_TYPE)
        def ioMap = integrationObjectConversionService.convert(getConversionContext(runtimeAttribute, RUNTIME_ATTRIBUTE_SERVICE))
        return new JsonBuilder(ioMap).toPrettyString()
    }

    def getConfigurationBundleEntity() {
        def inboundChannelConfig = findExistingChannelConfigWithIO(EXPORTABLE_IO)
        def integrationObjectBundleEntity = new IntegrationObjectBundleEntity()
        integrationObjectBundleEntity.integrationObjectCode = INTEGRATION_SERVICE
        integrationObjectBundleEntity.rootItemInstancePks = Set.of(inboundChannelConfig.pk.toString())
        def configurationBundleEntity = new ConfigurationBundleEntity()
        configurationBundleEntity.integrationObjectBundles = Set.of(integrationObjectBundleEntity)
        return configurationBundleEntity
    }

    def findExistingChannelConfigWithIO(def objectCode) {
        IntegrationTestUtil.findAny(InboundChannelConfigurationModel, {
            it?.integrationObject?.code == objectCode
        }).orElse(null) as InboundChannelConfigurationModel
    }

    def findActiveScript(def code) {
        IntegrationTestUtil.findAny(ScriptModel, {
            it?.code == code && it?.active
        }).orElse(null) as ScriptModel
    }

    def findRuntimeAttribute(def qualifier, def code) {
        IntegrationTestUtil.findAny(AttributeDescriptorModel, {
            it?.qualifier == qualifier && it.enclosingType.code == code
        }).orElse(null) as AttributeDescriptorModel
    }

    def postRequest(def url) {
        def urlSplit = url.split("/")
        ODataRequestBuilder.oDataPostRequest()
                .withContentType(APPLICATION_JSON_VALUE)
                .withAccepts(APPLICATION_JSON_VALUE)
                .withPathInfo(PathInfoBuilder.pathInfo()
                        .withServiceName(urlSplit[2])
                        .withEntitySet(urlSplit[3]))
    }

    def getConversionContext(final ItemModel item, final IntegrationObjectDescriptor ioDescriptor) {
        return ioDescriptor.getTypeDescriptor(item)
                .map({ type -> new ItemToMapConversionContext(item, type) })
    }

    def getConversionContext(final ItemModel item, final String ioCode) {
        def io = IntegrationObjectTestUtil.findIntegrationObjectDescriptorByCode(ioCode)
        def type = io.getTypeDescriptor(item).orElse(null)
        return new ItemToMapConversionContext(item, type)
    }

    def deleteICCAndRelatedEntities() {
        IntegrationTestUtil.remove(InboundChannelConfigurationModel) { it.integrationObject.code == EXPORTABLE_IO }
        IntegrationTestUtil.remove(IntegrationObjectModel) { it.code == EXPORTABLE_IO }
        IntegrationTestUtil.remove(ScriptModel) { it.code == SCRIPT_CODE }
        modelService.remove(findRuntimeAttribute(QUALIFIER, ITEM_TYPE))
    }

}
