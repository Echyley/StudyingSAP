/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.populators;

import de.hybris.platform.assistedservicefacades.customer360.CustomerOverviewData;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Optional;


/**
 * CustomerModel -> CustomerOverviewData populator
 *
 */
public class CustomerOverviewDataPopulator implements Populator<CustomerModel, CustomerOverviewData>
{
	private Converter<MediaModel, ImageData> imageConverter;
	private Converter<AddressModel, AddressData> addressConverter;

	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final CustomerModel customerModel, final CustomerOverviewData customerOverviewData)
	{
		customerOverviewData.setFullName(customerModel.getName());
		customerOverviewData.setEmail(Optional.ofNullable(customerModel.getUndecoratedUid()).orElse(customerModel.getUid()));
		customerOverviewData.setSignedUp(customerModel.getCreationtime());
		final AddressModel defaultShipmentAddress = customerModel.getDefaultShipmentAddress();

		if (defaultShipmentAddress != null)
		{
			customerOverviewData.setAddress(getAddressConverter().convert(defaultShipmentAddress));
		}

		if (null != customerModel.getProfilePicture())
		{
			customerOverviewData.setProfilePicture(getImageConverter().convert(customerModel.getProfilePicture()));
		}

	}

	protected Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}

	protected Converter<MediaModel, ImageData> getImageConverter()
	{
		return imageConverter;
	}

	public void setImageConverter(final Converter<MediaModel, ImageData> imageConverter)
	{
		this.imageConverter = imageConverter;
	}

}
