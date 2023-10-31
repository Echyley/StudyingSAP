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

public class SapERPVariantProductTranslator extends AbstractSpecialValueTranslator {

    final private static String BEAN_NAME = "sapERPVariantProductHelper";
    private SapERPVariantProductHelper sapERPVariantProductHelper;

    @Override
    public void performImport(String cellValue, Item processedItem) throws ImpExException {
        sapERPVariantProductHelper.removeRegularProduct(cellValue, processedItem);
    }

    @Override
    public void init(SpecialColumnDescriptor columnDescriptor) throws HeaderValidationException {

        if (sapERPVariantProductHelper == null) {
            sapERPVariantProductHelper = (SapERPVariantProductHelper) Registry.getApplicationContext().getBean(BEAN_NAME);
        }

    }
}
