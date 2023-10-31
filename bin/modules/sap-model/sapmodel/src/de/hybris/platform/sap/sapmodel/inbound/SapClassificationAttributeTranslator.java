/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.inbound;


import de.hybris.platform.core.Registry;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.jalo.header.HeaderValidationException;
import de.hybris.platform.impex.jalo.header.SpecialColumnDescriptor;
import de.hybris.platform.impex.jalo.translators.AbstractSpecialValueTranslator;
import de.hybris.platform.jalo.Item;

/**
 *
 * The SapClassificationAttributeTranslator translator is called from data hub
 * before the ClassificationAttributeTranslator is being called.
 *
 * This translator removes the product current classification attribute values before importing the new ones from the SAP/ERP backend.
 *
 * The target item of type BaseProductAttributes depends on the item type CleanBaseProductAttributes which calls (SapClassificationAttributeTranslator)
 * The target item of type SalesProductAttributes depends on the item type CleanSalesProductAttributes which calls (SapClassificationAttributeTranslator)
 *
 * The target item of type BaseVariantAttributes depends on the item type CleanBaseVariantAttributes which calls (SapClassificationAttributeTranslator)
 * The target item of type SalesVariantAttributes depends on the item type CleanSalesVariantAttributes which calls (SapClassificationAttributeTranslator)
 *
 */
public class SapClassificationAttributeTranslator extends AbstractSpecialValueTranslator {

    final private static String BEAN_NAME = "sapClassificationAttributeHelper";
    private SapClassificationAttributeHelper sapClassificationAttributeHelper;

    @Override
    public void performImport(String cellValue, Item processedItem) throws ImpExException {
        sapClassificationAttributeHelper.removeClassificationAttributeValues(cellValue, processedItem);
    }

    @Override
    public void init(SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException {

        if (sapClassificationAttributeHelper == null) {
            sapClassificationAttributeHelper = (SapClassificationAttributeHelper) Registry.getApplicationContext().getBean(BEAN_NAME);
        }

    }
}
