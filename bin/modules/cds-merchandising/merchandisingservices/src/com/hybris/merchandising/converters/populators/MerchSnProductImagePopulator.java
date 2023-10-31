/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.converters.populators;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.hybris.merchandising.model.MerchSnDocumentContainer;
import com.hybris.merchandising.model.Product;
import com.hybris.merchandising.model.ProductImage;


/**
 * Class populating merch product images
 */
public class MerchSnProductImagePopulator implements Populator<MerchSnDocumentContainer, Product>
{
	@Override
	public void populate(final MerchSnDocumentContainer source, final Product target) throws ConversionException
	{
		final ProductImage image = new ProductImage();

		populateProductImage(source, Product.THUMBNAIL_IMAGE).ifPresent(image::setThumbnailImage);
		populateProductImage(source, Product.MAIN_IMAGE).ifPresent(image::setMainImage);

		target.setImages(image);
	}

	protected Optional<String> populateProductImage(final MerchSnDocumentContainer source, final String fieldName)
	{
		return Optional.ofNullable(source.getMerchContext().getMerchPropertiesMapping().get(fieldName))
		               .map(indexedField -> source.getInputDocument().getFields().get(indexedField))
		               .map(imageUrl -> StringUtils.join(source.getMerchConfig().getBaseImageUrl(), imageUrl));
	}

}
