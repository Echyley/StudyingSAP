/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.inbound;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.sapmodel.model.ERPVariantProductModel;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import java.util.Objects;

public class SapERPVariantProductHelperImpl implements SapERPVariantProductHelper
{

    private static final Logger LOG = Logger.getLogger(SapERPVariantProductHelperImpl.class.getName());
    private ModelService modelService;
    private ProductService productService;
    private CatalogVersionService catalogVersionService;

    private boolean cleanCharacteristicsEnabled;

    /**
     * Remove regular product before importing new ERPVariantProduct
     * @param cellValue
     * @param processedItem
     */
    @Override
    public void removeRegularProduct(String cellValue, Item processedItem)
    {
        if (isCleanCharacteristicsEnabled())
        {
            String productCode = null;

            try
            {
                productCode = (String) processedItem.getAttribute("code");

                CatalogVersion catalogVersion = (CatalogVersion) processedItem.getAttribute("catalogVersion");
                Objects.requireNonNull(catalogVersion,"catalogVersion can't be null.");

                CatalogVersionModel catalogVersionModel = modelService.get(catalogVersion.getPK());
                Objects.requireNonNull(catalogVersionModel,"catalogVersion (model) can't be null.");

                //Clean item of SAPInboundVariant from cache.
                modelService.remove(processedItem.getPK());

                ProductModel productModel = productService.getProductForCode(catalogVersionModel, productCode);

                if(!(productModel instanceof ERPVariantProductModel) ) {
                    modelService.remove(productModel);
                    LOG.info(String.format(
                            "The current Regular Product [%s] have been removed before importing the ERPVariant Product.",
                            productCode));
                }

            }
            catch (UnknownIdentifierException ex)
            {
                LOG.info("The product has not been imported yet!" + ex.getMessage());
            }
            catch (Exception ex)
            {
                LOG.error(String.format("Something went wrong while removing regular product [%s]!", productCode), ex);

            }
        }
    }

    protected boolean isCleanCharacteristicsEnabled()
    {
        return cleanCharacteristicsEnabled;
    }

    @Required
    public void setCleanCharacteristicsEnabled(boolean cleanCharacteristicsEnabled)
    {
        this.cleanCharacteristicsEnabled = cleanCharacteristicsEnabled;
    }

    protected ModelService getModelService()
    {
        return modelService;
    }

    @Required
    public void setModelService(ModelService modelService)
    {
        this.modelService = modelService;
    }

    protected ProductService getProductService()
    {
        return productService;
    }

    @Required
    public void setProductService(ProductService productService)
    {
        this.productService = productService;
    }

    protected CatalogVersionService getCatalogVersionService()
    {
        return catalogVersionService;
    }

    @Required
    public void setCatalogVersionService(CatalogVersionService catalogVersionService)
    {
        this.catalogVersionService = catalogVersionService;
    }
}
