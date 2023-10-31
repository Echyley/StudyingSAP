/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapmodel.authors;

import de.hybris.platform.classification.ClassificationService;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.servicelayer.model.ModelService;

import org.springframework.beans.factory.annotation.Required;


public class SapClassificationAttributeAuthorHelperImpl implements SapClassificationAttributeAuthorHelper
{
	private ClassificationService classificationService;
	private ModelService modelService;
	private ProductService productService;

	@Override
	public ClassificationService getClassificationService()
	{
		return classificationService;
	}

	@Required
	public void setClassificationService(final ClassificationService classificationService)
	{
		this.classificationService = classificationService;
	}

	@Override
	public ModelService getModelService()
	{
		return modelService;
	}

	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	@Override
	public ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}
}
