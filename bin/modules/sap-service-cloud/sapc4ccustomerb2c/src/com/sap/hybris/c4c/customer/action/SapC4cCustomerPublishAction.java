/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.c4c.customer.action;

import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.task.RetryLaterException;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sap.hybris.c4c.customer.dto.C4CCustomerData;
import com.sap.hybris.c4c.customer.service.SapC4cCustomerPublicationService;
import com.sap.hybris.c4c.customer.util.SapC4cCustomerUtils;


/**
 * Action class to publish the registered customer to the SCPI
 */
public class SapC4cCustomerPublishAction extends AbstractSimpleDecisionAction<BusinessProcessModel>
{

	private SapC4cCustomerPublicationService c4cCustomerPublicationService;
	private SapC4cCustomerUtils customerUtil;
	private static final Logger LOGGER = LogManager.getLogger(SapC4cCustomerPublishAction.class);

	@Override
	public Transition executeAction(final BusinessProcessModel process) throws RetryLaterException
	{
		final StoreFrontCustomerProcessModel customerProcess = (StoreFrontCustomerProcessModel) process;
		final CustomerModel customerModel = customerProcess.getCustomer();
		final C4CCustomerData customerData = getCustomerUtil().getCustomerDataForCustomer(customerModel,
				customerModel.getAddresses());

		try
		{
			getC4cCustomerPublicationService().publishCustomerToCloudPlatformIntegration(customerData);
			return Transition.OK;
		}
		catch (final IOException e)
		{
			LOGGER.error("Failed to replicate customer " + customerModel.getCustomerID(), e);
			return Transition.NOK;
		}
	}

	/**
	 * @return the c4cCustomerPublicationService
	 */
	public SapC4cCustomerPublicationService getC4cCustomerPublicationService()
	{
		return c4cCustomerPublicationService;
	}

	/**
	 * @param c4cCustomerPublicationService
	 *           the c4cCustomerPublicationService to set
	 */
	public void setC4cCustomerPublicationService(final SapC4cCustomerPublicationService c4cCustomerPublicationService)
	{
		this.c4cCustomerPublicationService = c4cCustomerPublicationService;
	}

	/**
	 * @return the customerUtil
	 */
	public SapC4cCustomerUtils getCustomerUtil()
	{
		return customerUtil;
	}

	/**
	 * @param customerUtil the customerUtil to set
	 */
	public void setCustomerUtil(final SapC4cCustomerUtils customerUtil)
	{
		this.customerUtil = customerUtil;
	}



}
