/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.inbound;

import de.hybris.platform.jalo.Item;

public interface SapERPVariantProductHelper {
	/**
     * Remove the regular product before importing the new ERPVariantProduct
     * @param cellValue
     * @param processedItem
     */
    void removeRegularProduct(String cellValue, Item processedItem);
}
