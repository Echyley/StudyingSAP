/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationservices.model.impl

import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModelUtils
import de.hybris.platform.integrationservices.model.IntegrationObjectModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import spock.lang.Unroll

@UnitTest
class IntegrationObjectItemModelUtilsUnitTest extends JUnitPlatformSpecification {

    @Unroll
    @Test
    def "IsEqual is #expected when #condition"() {
        expect:
        IntegrationObjectItemModelUtils.isEqual(item1, item2) == expected

        where:
        item1                               | item2                                | expected | condition
        itemModel("TestProduct", "Product") | itemModel("TestProduct", "Product")  | true     | "object code and item code are equal"
        itemModel("TestProduct", "Product") | itemModel("DiffProduct", "Product")  | false    | "only item code is equal"
        itemModel("TestProduct", "Product") | itemModel("DiffProduct", "Product2") | false    | "only object code is equal"
        Stub(IntegrationObjectItemModel)    | null                                 | false    | "item is not equal to null"
        null                                | Stub(IntegrationObjectItemModel)     | false    | "null is not equal to item"
        null                                | null                                 | true     | "null is equal to null"
    }


    private IntegrationObjectItemModel itemModel(ioCode, code) {
        Stub(IntegrationObjectItemModel) {
            getCode() >> code
            getIntegrationObject() >>
                    Stub(IntegrationObjectModel) {
                        getCode() >> ioCode
                    }
        }
    }
}
