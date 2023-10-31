/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorderfacades.populators;

import org.springframework.util.Assert;

import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

public class DefaultSapServiceOrderCartPopulator implements Populator<CartModel, CartData> {

	@Override
	public void populate(CartModel source, CartData target) throws ConversionException {
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		target.setRequestedServiceStartDate(source.getRequestedServiceStartDate());
		
	}

}
