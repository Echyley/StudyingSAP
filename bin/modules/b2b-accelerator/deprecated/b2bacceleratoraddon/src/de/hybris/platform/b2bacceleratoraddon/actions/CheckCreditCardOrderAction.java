/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratoraddon.actions;

import de.hybris.platform.b2b.process.approval.actions.AbstractSimpleB2BApproveOrderDecisionAction;
import de.hybris.platform.b2b.process.approval.model.B2BApprovalProcessModel;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.task.RetryLaterException;
import org.apache.log4j.Logger;


public class CheckCreditCardOrderAction extends AbstractSimpleB2BApproveOrderDecisionAction
{

	protected static final Logger LOG = Logger.getLogger(CheckCreditCardOrderAction.class);

	/*
	 * Returns Transition.NOK if the order has any entries with inactive cost centers otherwise returns Transition.OK
	 */
	@Override
	public Transition executeAction(final B2BApprovalProcessModel process) throws RetryLaterException
	{
		OrderModel order = null;
		Transition transition = Transition.NOK;
		try
		{
			order = process.getOrder();
			final PaymentInfoModel paymentInfo = order.getPaymentInfo();

			if (CheckoutPaymentType.CARD.equals(order.getPaymentType()) && paymentInfo instanceof CreditCardPaymentInfoModel)
			{
				// this is a credit card payment, approval is not required
				transition = Transition.OK;
			}
		}
		catch (final Exception e)
		{
			this.handleError(order, e);
		}
		return transition;
	}

	protected void handleError(final OrderModel order, final Exception exception)
	{
		if (order != null)
		{
			this.setOrderStatus(order, OrderStatus.B2B_PROCESSING_ERROR);
		}
		LOG.error(exception.getMessage(), exception);
	}


}
