/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.providers;

import de.hybris.platform.assistedservicefacades.customer360.FragmentModelProvider;
import de.hybris.platform.assistedservicefacades.customer360.StoreLocationData;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.storelocator.model.PointOfServiceModel;

import java.util.Map;

import org.apache.commons.lang.StringUtils;


/**
 * This class provides the nearest stores that the as agent has been mapped to, otherwise the browsers location will be
 * considered.
 */
public class StoreLocationsProvider implements FragmentModelProvider<StoreLocationData>
{
	private AssistedServiceService assistedServiceService;

	@Override
	public StoreLocationData getModel(final Map<String, String> parameters)
	{
		final PointOfServiceModel agentStore = getAssistedServiceService().getAssistedServiceAgentStore();

		final StoreLocationData storeLocationData = new StoreLocationData();
		if (agentStore != null && agentStore.getAddress() != null)
		{
			storeLocationData.setAddress(prepareAddressData(agentStore.getAddress()));
		}
		return storeLocationData;
	}

	protected String prepareAddressData(final AddressModel address)
	{
		final StringBuilder qBulder = new StringBuilder(address.getTown());

		if (address.getCountry() != null)
		{
			qBulder.append(' ').append(address.getCountry().getName());
		}

		if (StringUtils.isNotEmpty(address.getPostalcode()))
		{
			qBulder.append(' ').append(address.getPostalcode());
		}
		return qBulder.toString();
	}

	protected AssistedServiceService getAssistedServiceService()
	{
		return assistedServiceService;
	}

	public void setAssistedServiceService(final AssistedServiceService assistedServiceService)
	{
		this.assistedServiceService = assistedServiceService;
	}
}
