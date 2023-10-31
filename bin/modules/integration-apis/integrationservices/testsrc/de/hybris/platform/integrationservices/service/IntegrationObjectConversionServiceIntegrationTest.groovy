/*
 *  Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.service

import de.hybris.bootstrap.annotations.IntegrationTest
import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum
import de.hybris.platform.catalog.model.CatalogModel
import de.hybris.platform.catalog.model.CatalogVersionModel
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel
import de.hybris.platform.catalog.model.classification.ClassificationSystemModel
import de.hybris.platform.category.model.CategoryModel
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.core.model.c2l.LanguageModel
import de.hybris.platform.core.model.order.QuoteModel
import de.hybris.platform.core.model.user.CustomerModel
import de.hybris.platform.core.model.user.EmployeeModel
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder
import de.hybris.platform.integrationservices.populator.ItemToMapConversionContext
import de.hybris.platform.integrationservices.populator.PrimitiveCollectionElement
import de.hybris.platform.integrationservices.util.IntegrationObjectTestUtil
import de.hybris.platform.integrationservices.util.IntegrationTestUtil
import de.hybris.platform.integrationservices.util.ItemTracker
import de.hybris.platform.servicelayer.ServicelayerSpockSpecification
import de.hybris.platform.servicelayer.i18n.I18NService
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import spock.lang.AutoCleanup
import spock.lang.Issue
import spock.lang.Shared
import spock.lang.Unroll

import javax.annotation.Resource

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject

@IntegrationTest
class IntegrationObjectConversionServiceIntegrationTest extends ServicelayerSpockSpecification {

    private static final String TEST_NAME = "IntegrationObjectConversionService"
    private static final String INTEGRATION_OBJECT = "${TEST_NAME}_IO"
    private static final String CATALOG_ID = "${TEST_NAME}_Catalog"
    private static final String CATEGORY = "${TEST_NAME}_Category"
    private static final String FLEX_IO = "${TEST_NAME}_Flexible"

    @Shared
    @ClassRule
    public IntegrationObjectModelBuilder categoriesIO = integrationObject().withCode(INTEGRATION_OBJECT)
            .withItem(integrationObjectItem().withCode('TestCatalog').withType('Catalog')
                    .withAttribute(integrationObjectItemAttribute('tstId').withQualifier('id'))
                    .withAttribute(integrationObjectItemAttribute('tstUrlPatterns').withQualifier('urlPatterns')))
            .withItem(integrationObjectItem().withCode('TestCatalogVersion').withType('CatalogVersion')
                    .withAttribute(integrationObjectItemAttribute('tstVersion').withQualifier('version'))
                    .withAttribute(integrationObjectItemAttribute('tstCatalog').withQualifier('catalog').withReturnItem('TestCatalog')))
            .withItem(integrationObjectItem().withCode('TestCategory').withType('Category')
                    .withAttribute(integrationObjectItemAttribute('tstCode').withQualifier('code'))
                    .withAttribute(integrationObjectItemAttribute('tstName').withQualifier('name'))
                    .withAttribute(integrationObjectItemAttribute('tstDescription').withQualifier('description'))
                    .withAttribute(integrationObjectItemAttribute('tstProducts').withQualifier('products').withReturnItem('TestProduct')))
            .withItem(integrationObjectItem().withCode('TestProduct').withType('Product')
                    .withAttribute(integrationObjectItemAttribute('tstCode').withQualifier('code')))
    @AutoCleanup('cleanup')
    def flexIO = integrationObject().withCode(FLEX_IO)
    @Rule
    public ItemTracker itemTracker = ItemTracker.track CategoryModel

    @Resource(name = "integrationObjectConversionService")
    private IntegrationObjectConversionService conversionService
    @Resource
    private I18NService i18NService

    private Locale defaultLocale

    def setupSpec() {
        IntegrationTestUtil.importImpEx(
                '# For localized attribute test case',
                'INSERT_UPDATE Language; isocode[unique = true]',
                '                      ; fr',
                'INSERT_UPDATE Catalog; id[unique = true];',
                "                     ; $CATALOG_ID",
                'INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]',
                "                            ; $CATALOG_ID               ; Staged")
    }

    def cleanupSpec() {
        IntegrationTestUtil.removeSafely LanguageModel, { it.isocode == 'fr' }
        IntegrationTestUtil.removeSafely CatalogVersionModel, { it.version == 'Staged' && it.catalog.id == CATALOG_ID }
        IntegrationTestUtil.removeSafely CatalogModel, { it.id == CATALOG_ID }
    }

    def setup() {
        defaultLocale = i18NService.currentLocale
    }

    def cleanup() {
        i18NService.currentLocale = defaultLocale
        IntegrationTestUtil.remove CategoryModel, { it.code == CATEGORY }
    }

    @Test
    def "converts simple item model without nested items"() {
        given:
        def catalog = new CatalogModel(id: CATALOG_ID, urlPatterns: ['url1', 'url2'])

        when:
        def converted = conversionService.convert conversionContext(catalog)

        then:
        def expectedAttributes = [tstId: CATALOG_ID, tstUrlPatterns: [PrimitiveCollectionElement.create('url1'), PrimitiveCollectionElement.create('url2')]]
        converted.intersect(expectedAttributes) == expectedAttributes // contains all expected attributes
    }

    @Test
    def "converted model contains generated integration key"() {
        given:
        def catalog = new CatalogModel(id: CATALOG_ID)

        when:
        def converted = conversionService.convert conversionContext(catalog)

        then:
        converted['integrationKey'] == CATALOG_ID
    }

    @Test
    def "converted model does not contain null attributes"() {
        given:
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version)',
                "                      ; $CATEGORY          ; $CATALOG_ID:Staged")
        when:
        def converted = conversionService.convert conversionContext(findCategoryByCode(CATEGORY))

        then:
        !converted.keySet().containsAll(['tstName', 'tstProducts'])
    }

    @Test
    def "can convert properties of Enum type"() {
        given: 'the integration object contains an attribute of Enum type'
        flexIO.withItem(integrationObjectItem().withCode('ClassAttributeAssignment')
                .withAttribute(integrationObjectItemAttribute('attributeType').withReturnItem('ClassificationAttributeTypeEnum').unique()))
                .withItem(integrationObjectItem().withCode('ClassificationAttributeTypeEnum')
                        .withAttribute(integrationObjectItemAttribute('code').withQualifier('code').unique())
                        .withAttribute(integrationObjectItemAttribute('codex').withQualifier('code')))
                .build()
        and: 'there is an item for that integration object'
        def model = new ClassAttributeAssignmentModel(attributeType: ClassificationAttributeTypeEnum.STRING)

        when:
        def map = conversionService.convert conversionContext(model, FLEX_IO)

        then:
        map["attributeType"] == [code: 'string', codex: 'string']
    }

    @Test
    def "converts localized attributes into nested entities"() {
        given: 'default locale in the system is FRENCH'
        i18NService.setCurrentLocale(Locale.FRENCH)
        and: 'there is a category with multiple locales set'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version); name[lang = en]; name[lang = fr]',
                "                      ; $CATEGORY          ; $CATALOG_ID:Staged                  ; english value  ; french value")

        when:
        def converted = conversionService.convert conversionContext(findCategoryByCode(CATEGORY))

        then: 'default language populated in localized properties'
        converted['tstName'] == 'french value'
        converted['localizedAttributes'] == [[language: 'en', tstName: 'english value'], [language: 'fr', tstName: 'french value']]
    }

    @Test
    def "converted model contains empty localized attribute values but no null localized attribute values"() {
        given: 'default locale in the system is ENGLISH'
        i18NService.setCurrentLocale(Locale.ENGLISH)
        and: 'there a category with some localized attributes set'
        IntegrationTestUtil.importImpEx(
                'INSERT_UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version); name[lang = en]',
                "                      ; $CATEGORY          ; $CATALOG_ID:Staged                  ; english name",
                'UPDATE Category; code[unique = true]; catalogVersion(catalog(id), version); description[lang = fr]',
                "               ; $CATEGORY          ; $CATALOG_ID:Staged                  ; french description")
        // Cannot set value to empty string via impex so have to do it programmatically
        def category = findCategoryByCode(CATEGORY)
        category.setName("", Locale.FRENCH)

        when:
        def converted = conversionService.convert conversionContext(category)

        then:
        converted['tstName'] == 'english name'
        !converted.hasProperty('tstDescription')
        converted['localizedAttributes'] == [[language: 'fr', tstName: '', tstDescription: 'french description'], [language: 'en', tstName: 'english name']]
    }

    @Test
    @Unroll
    def "throws exception when the specified IntegrationObject code is '#objCode'"() {
        when:
        conversionService.convert new ItemModel(), objCode as String

        then:
        def e = thrown(IllegalArgumentException)
        e.message.contains('null')
        e.message.contains('empty')

        where:
        objCode << [null, '']
    }

    @Test
    def "throws exception when the IntegrationObject does not contain item definition for the specified item model"() {
        when:
        conversionService.convert new EmployeeModel(), INTEGRATION_OBJECT

        then:
        thrown IntegrationObjectAndItemMismatchException
    }

    @Test
    def "converts attribute value that is subclass of the declared integration object item attribute type"() {
        given:
        def catalog = new ClassificationSystemModel(id: 'classifications') // subtype of CatalogModel
        def catalogVersion = new CatalogVersionModel(version: 'test', catalog: catalog)

        when:
        def converted = conversionService.convert conversionContext(catalogVersion, INTEGRATION_OBJECT)

        then:
        def expectedClassificationAttributes = [tstId: 'classifications']
        converted['tstCatalog'].intersect(expectedClassificationAttributes) == expectedClassificationAttributes
    }

    @Test
    @Issue('https://cxjira.sap.com/browse/IAPI-5057')
    def 'converts items with different properties of the same super type'() {
        given: 'an integration object with two different attributes for the same attribute of their supertype in the platform'
        flexIO.withItem(integrationObjectItem('Quote').root()
                .withAttribute(integrationObjectItemAttribute('code').unique())
                .withAttribute(integrationObjectItemAttribute('employee').withQualifier('user').withReturnItem('Employee'))
                .withAttribute(integrationObjectItemAttribute('customer').withQualifier('user').withReturnItem('Customer')))
                .withItem(integrationObjectItem('Customer')
                        .withAttribute(integrationObjectItemAttribute().withName('uid').unique()))
                .withItem(integrationObjectItem('Employee')
                        .withAttribute(integrationObjectItemAttribute().withName('uid').unique()))
                .build()
        and: 'there is an item to convert'
        def customer = new CustomerModel(uid: 'customer1')
        def quote = new QuoteModel(user: customer)

        when:
        def converted = conversionService.convert conversionContext(quote, FLEX_IO)

        then: 'converted item has only one attribute populated'
        !converted['employee']
        converted['customer']['uid'] == customer.uid
    }

    private static CategoryModel findCategoryByCode(String code) {
        IntegrationTestUtil.findAny(CategoryModel, { code == it.code }).orElse(null) as CategoryModel
    }

    private static ItemToMapConversionContext conversionContext(ItemModel item, String ioCode = INTEGRATION_OBJECT) {
        def io = IntegrationObjectTestUtil.findIntegrationObjectDescriptorByCode ioCode
        def type = io.getTypeDescriptor(item).orElse(null)
        new ItemToMapConversionContext(item, type)
    }
}
