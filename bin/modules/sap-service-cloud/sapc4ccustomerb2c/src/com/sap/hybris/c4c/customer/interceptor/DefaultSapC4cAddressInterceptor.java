/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4c.customer.interceptor;

import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.event.EventService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;
import de.hybris.platform.servicelayer.interceptor.ValidateInterceptor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;
import java.util.Collection;

import com.sap.hybris.c4c.customer.constants.Sapc4ccustomerb2cConstants;
import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.event.SapC4cCustomerUpdateEvent;
import com.sap.hybris.c4c.customer.util.SapC4cCustomerUtils;



/**
 * Intercepts any changes in address and creates customer update event if any replicated relavant fields are changed
 */
public class DefaultSapC4cAddressInterceptor implements ValidateInterceptor<AddressModel>, RemoveInterceptor<AddressModel>
{

	private EventService eventService;
	private SapC4cCustomerUtils customerUtil;
	private List<String> addressAttributeList;
	private ConfigurationService configurationService;

	@Override
	public void onValidate(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{
		String strReplicationEnabled = getConfigurationService().getConfiguration().getString(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_CPI_REPLICATE);
		boolean isReplicationEnabled = false;
		if(!StringUtils.isBlank(strReplicationEnabled)){
			isReplicationEnabled = Boolean.parseBoolean(strReplicationEnabled);
		}

		if(!isReplicationEnabled) {
			return;
		}

		if (addressModel.getOwner() != null && addressModel.getOwner().getClass() != CustomerModel.class)
		{
			return;
		}
		final CustomerModel customerModel = (CustomerModel) addressModel.getOwner();
		C4CCustomerData customerData;

		Collection<AddressModel> addressModelCollection = customerModel.getAddresses();
		if(addressModelCollection == null){
			addressModelCollection = new LinkedList<>();
		}
		final List<AddressModel> addressModels = new ArrayList<>(addressModelCollection);

		if (ctx.isNew(addressModel))
		{
			addressModels.add(addressModel);
			customerData = getCustomerUtil().getCustomerDataForCustomer(customerModel, addressModels);
		}
		else
		{
			if (shouldReplicate(addressModel, ctx))
			{
				addressModels.remove(addressModel);
				addressModels.add(addressModel);
				customerData = getCustomerUtil().getCustomerDataForCustomer(customerModel, addressModels);
			}
			else
			{
				return;
			}
		}

		final SapC4cCustomerUpdateEvent event = new SapC4cCustomerUpdateEvent();
		event.setCustomerData(customerData);
		getEventService().publishEvent(event);
	}

	@Override
	public void onRemove(final AddressModel addressModel, final InterceptorContext ctx) throws InterceptorException
	{
		String strReplicationEnabled = getConfigurationService().getConfiguration().getString(Sapc4ccustomerb2cConstants.C4C_CUSTOMER_CPI_REPLICATE);
		boolean isReplicationEnabled = false;
		if(!StringUtils.isBlank(strReplicationEnabled)){
			isReplicationEnabled = Boolean.parseBoolean(strReplicationEnabled);
		}

		if(!isReplicationEnabled) {
			return;
		}
		
		if ((addressModel.getOwner().getClass() != CustomerModel.class))
		{
			return;
		}
		final CustomerModel customerModel = (CustomerModel) addressModel.getOwner();

		final List<AddressModel> addressModels = new ArrayList<>(customerModel.getAddresses());
		addressModels.remove(addressModel);

		final C4CCustomerData customerData = getCustomerUtil().getCustomerDataForCustomer(customerModel, addressModels);

		final SapC4cCustomerUpdateEvent event = new SapC4cCustomerUpdateEvent();
		event.setCustomerData(customerData);
		getEventService().publishEvent(event);
	}

	/**
	 * Checks if any replicated relevant fields are modified and returns a boolean
	 */
	protected boolean shouldReplicate(final AddressModel addressModel, final InterceptorContext ctx)
	{
		return getAddressAttributeList().stream().anyMatch(attribute -> ctx.isModified(addressModel, attribute));
	}


	/**
	 * @return the eventService
	 */
	public EventService getEventService()
	{
		return eventService;
	}

	/**
	 * @param eventService
	 *           the eventService to set
	 */
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

	/**
	 * @return the customerUtil
	 */
	public SapC4cCustomerUtils getCustomerUtil()
	{
		return customerUtil;
	}

	/**
	 * @param customerUtil
	 *           the customerUtil to set
	 */
	public void setCustomerUtil(final SapC4cCustomerUtils customerUtil)
	{
		this.customerUtil = customerUtil;
	}

	/**
	 * @return list of address attributes
	 */
	public List<String> getAddressAttributeList()
	{
		return addressAttributeList;
	}

	/**
	 * @param addressAttributeList
	 *           list of address attributes
	 */
	public void setAddressAttributeList(final List<String> addressAttributeList)
	{
		this.addressAttributeList = addressAttributeList;
	}

	public ConfigurationService getConfigurationService() {
		return configurationService;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
