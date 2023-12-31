/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpicustomerexchange.inbound.events;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import com.sap.hybris.sapcustomerb2c.inbound.CustomerImportService;


public class SapCpiCustomerPersistenceHook implements PrePersistHook
{

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCustomerPersistenceHook.class);
	private CustomerImportService CustomerImportService;

	@Override
	public Optional<ItemModel> execute(ItemModel item)
	{
		if (item instanceof CustomerModel)
		{
			LOG.info("The persistence hook sapCpiCustomerPersistenceHook is called!");
			CustomerModel customerModel = (CustomerModel) item;
			getCustomerImportService().processConsumerReplicationNotificationFromHub(customerModel.getCustomerID());
			return Optional.empty();
		}
		return Optional.of(item);
	}

	protected CustomerImportService getCustomerImportService()
	{
		return CustomerImportService;
	}

	@Required
	public void setCustomerImportService(CustomerImportService dataHubConsumerImportService)
	{
		this.CustomerImportService = dataHubConsumerImportService;
	}

}
