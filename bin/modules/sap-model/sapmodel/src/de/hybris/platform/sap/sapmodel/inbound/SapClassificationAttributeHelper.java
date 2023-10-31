/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.inbound;

import de.hybris.platform.jalo.Item;

public interface SapClassificationAttributeHelper {
    /**
     * Remove the product current classification attribute values before importing the new ones
     * @param cellValue
     * @param processedItem
     */
    void removeClassificationAttributeValues(String cellValue, Item processedItem);

}
