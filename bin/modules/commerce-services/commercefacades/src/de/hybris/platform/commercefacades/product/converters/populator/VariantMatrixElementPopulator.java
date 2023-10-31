/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commercefacades.product.converters.populator;

import de.hybris.platform.variants.model.VariantCategoryModel;
import de.hybris.platform.variants.model.VariantValueCategoryModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.VariantCategoryData;
import de.hybris.platform.commercefacades.product.data.VariantMatrixElementData;
import de.hybris.platform.commercefacades.product.data.VariantOptionData;
import de.hybris.platform.commercefacades.product.data.VariantOptionQualifierData;
import de.hybris.platform.commercefacades.product.data.VariantValueCategoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;


public class VariantMatrixElementPopulator<SOURCE extends VariantValueCategoryModel, TARGET extends VariantMatrixElementData>
		implements Populator<SOURCE, TARGET>
{


	@Override
	public void populate(final VariantValueCategoryModel variantValueCategory,
			final VariantMatrixElementData variantMatrixElementData) throws ConversionException
	{
		final VariantCategoryData parent = new VariantCategoryData();

		final VariantCategoryModel variantCategoryModel = (VariantCategoryModel) variantValueCategory.getSupercategories().get(0);
		parent.setName(variantCategoryModel.getName());
		parent.setHasImage(variantCategoryModel.getHasImage());

		final VariantValueCategoryData data = new VariantValueCategoryData();
		data.setName(variantValueCategory.getName());
		data.setSequence(variantValueCategory.getSequence());

		variantMatrixElementData.setVariantValueCategory(data);
		variantMatrixElementData.setParentVariantCategory(parent);

		final VariantOptionData variantOptionData = new VariantOptionData();
		final VariantOptionQualifierData variantOptionQualifierData = new VariantOptionQualifierData();
		variantOptionQualifierData.setImage(new ImageData());
		variantOptionData.setVariantOptionQualifiers(Arrays.asList(new VariantOptionQualifierData[]
		{ variantOptionQualifierData }));

		variantMatrixElementData.setVariantOption(variantOptionData);
	}

}