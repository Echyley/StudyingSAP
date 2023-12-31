/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.inbound;

import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.jalo.CatalogVersion;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.classification.features.FeatureList;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.Item;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.model.ModelService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


public class SapClassificationAttributeHelperImpl implements SapClassificationAttributeHelper
{

	private static final Logger LOG = Logger.getLogger(SapClassificationAttributeHelperImpl.class.getName());
	private ClassificationService classificationService;
	private ModelService modelService;
	private ProductService productService;
	private CatalogVersionService catalogVersionService;

	private boolean cleanCharacteristicsEnabled;

	@Override
	public void removeClassificationAttributeValues(String cellValue, Item processedItem)
	{

		if (isCleanCharacteristicsEnabled())
		{

			String productCode = null;

			try
			{

				productCode = (String) processedItem.getAttribute("code");
				CatalogVersion catalogVersion = (CatalogVersion) processedItem.getAttribute("catalogVersion");
				CatalogVersionModel catalogVersionModel = modelService.get(catalogVersion.getPK());
				modelService.remove(processedItem.getPK());

				ProductModel productModel = productService.getProductForCode(catalogVersionModel, productCode);
				FeatureList featureList = classificationService.getFeatures(productModel);

				//if cellValue is "<ignore>", it comes from old version datahub, which clean product feature based on product code and product catalog id/version; otherwise, the cellValue format is
				//classification catatlog id/system version, this is same format with entry.getCode()
				featureList.getFeatures().forEach(entry -> {
					if ("<ignore>".equalsIgnoreCase(cellValue) || entry.getCode()
					                                                   .substring(0, entry.getCode().lastIndexOf('/'))
					                                                   .equalsIgnoreCase(cellValue))
					{
						entry.removeAllValues();
					}
				});
				classificationService.replaceFeatures(productModel, featureList);

				LOG.info(String.format(
						"The current classification system attribute values for the product [%s] with system version [%s] have been removed before importing the new ones.",
						productCode, cellValue));

			}
			catch (UnknownIdentifierException ex)
			{
				LOG.info("The product has not been imported yet!" + ex.getMessage());
			}
			catch (Exception ex)
			{
				LOG.error(ex);
				LOG.error(String.format(
						"Something went wrong while removing classification system attribute values for the product [%s]!",
						productCode)
						+ ex.getMessage());
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

	protected ClassificationService getClassificationService()
	{
		return classificationService;
	}

	@Required
	public void setClassificationService(ClassificationService classificationService)
	{
		this.classificationService = classificationService;
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
