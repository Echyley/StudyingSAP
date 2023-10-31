/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.orderexchange.action;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.integrationservices.service.IntegrationObjectConversionService;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderModel;
import de.hybris.platform.sap.sapcpiorderexchange.actions.SapCpiOmmOrderOutboundAction;
import de.hybris.platform.task.RetryLaterException;

import java.util.Map;

import javax.annotation.Resource;


/**
 * Test implementation that stops after sending payload in case test mode is active
 */
public class SapCpiOmmOrderOutboundTestAction extends SapCpiOmmOrderOutboundAction
{

	private boolean testMode = false;
	private boolean success;

	/**
	 * @return the success
	 */
	public boolean isSuccess()
	{
		return success;
	}

	/**
	 * @return the testMode
	 */
	public boolean isTestMode()
	{
		return testMode;
	}

	/**
	 * @param testMode
	 *           the testMode to set
	 */
	public void setTestMode(final boolean testMode)
	{
		this.testMode = testMode;
	}

	@Resource(name = "integrationObjectConversionService")
	IntegrationObjectConversionService conversionService;


	private Map<String, Object> payLoad;

	/**
	 * @return the payLoad
	 */
	public Map<String, Object> getPayLoad()
	{
		return payLoad;
	}

	/**
	 * @param b
	 */
	protected void setSuccess(final boolean b)
	{
		this.success = b;

	}

	/**
	 * @param payLoad
	 *           the payLoad to set
	 */
	public void setPayLoad(final Map<String, Object> payLoad)
	{
		this.payLoad = payLoad;
	}

	@Override
	public void executeAction(final OrderProcessModel process) throws RetryLaterException
	{

		if (!testMode)
		{
			super.executeAction(process);
		}
		else
		{
			final OrderModel order = process.getOrder();
			final SAPCpiOutboundOrderModel outboundOrderModel = getSapCpiOrderOutboundConversionService()
					.convertOrderToSapCpiOrder(order);
			final Map<String, Object> payLoad = conversionService.convert(outboundOrderModel, "OutboundOMMOrderOMSOrder");

			this.setPayLoad(payLoad);
			this.setSuccess(true);
		}


	}


}