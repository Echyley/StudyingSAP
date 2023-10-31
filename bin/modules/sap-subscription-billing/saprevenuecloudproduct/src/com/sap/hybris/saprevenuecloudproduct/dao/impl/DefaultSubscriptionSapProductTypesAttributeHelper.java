/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.saprevenuecloudproduct.dao.impl;
import java.util.HashSet;
import java.util.Set;

import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.sap.sapmodel.daos.SapProductTypesAttributeHelper;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;


public class DefaultSubscriptionSapProductTypesAttributeHelper implements SapProductTypesAttributeHelper {

    @Override
    public Set<SAPProductType> getSapProductTypes(ProductModel product) {

        Set<SAPProductType> result = new HashSet<>();

        if(product.getSubscriptionCode() != null) {
            result.add(SAPProductType.SUBSCRIPTION);
        }

        return result;
    }
}