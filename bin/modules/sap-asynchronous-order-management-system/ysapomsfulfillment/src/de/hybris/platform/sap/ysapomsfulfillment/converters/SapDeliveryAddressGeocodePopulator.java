/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.ysapomsfulfillment.converters;


import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.data.AddressData;
import org.springframework.util.Assert;

public class SapDeliveryAddressGeocodePopulator implements Populator<AddressModel, AddressData>
{
    @Override
    public void populate(final AddressModel source, final AddressData target)
    {
        Assert.notNull(source, "Parameter source cannot be null.");
        Assert.notNull(target, "Parameter target cannot be null.");

        if (source.getCountry() != null)
        {
            target.setCountryCode(source.getCountry().getIsocode());
        }
        target.setCity(source.getTown());
        target.setStreet(source.getStreetname());
        target.setZip(source.getPostalcode());
    }
}
