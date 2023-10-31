/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.company.populators;

import de.hybris.platform.b2bacceleratorfacades.document.data.B2BDocumentTypeData;
import de.hybris.platform.b2bacceleratorservices.model.B2BDocumentTypeModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;


public class B2BDocumentTypePopulator implements Populator<B2BDocumentTypeModel, B2BDocumentTypeData>
{
	@Override
	public void populate(final B2BDocumentTypeModel source, final B2BDocumentTypeData target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setDisplayInAllList(source.getDisplayInAllList());
		target.setIncludeInOpenBalance(source.getIncludeInOpenBalance());
		if (source.getPayableOrUsable() != null) {
			target.setPayableOrUsable(source.getPayableOrUsable().getCode());
		}

	}
}
