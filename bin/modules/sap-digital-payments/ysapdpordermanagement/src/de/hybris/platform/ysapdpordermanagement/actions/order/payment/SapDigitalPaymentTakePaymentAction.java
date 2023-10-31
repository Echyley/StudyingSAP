/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.ysapdpordermanagement.actions.order.payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import de.hybris.platform.cissapdigitalpayment.exceptions.SapDigitalPaymentCaptureException;
import de.hybris.platform.cissapdigitalpayment.service.SapDigitalPaymentService;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
/**
 * The TakePayment step captures the payment transaction.
 */
public class SapDigitalPaymentTakePaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = LoggerFactory.getLogger(SapDigitalPaymentTakePaymentAction.class);
	private SapDigitalPaymentService sapDigitalPaymentService;
	private PaymentService paymentService;
	@Override
	public Transition executeAction(final OrderProcessModel process) throws SapDigitalPaymentCaptureException
	{
		LOG.info("Process: " + process.getCode() + " in step " + getClass().getSimpleName());
		PaymentTransactionEntryModel txnEntry;
		final OrderModel order = process.getOrder();
		boolean paymentFailed = false;
		for (final PaymentTransactionModel txn : order.getPaymentTransactions())
		{
			if (txn.getPaymentProvider() == null)
			{
				LOG.info("Payment Provider not available in the Payment Transaction.");
			}
			else if(getSapDigitalPaymentService().isSapDigitalPaymentTransaction(txn))
			{
				txnEntry = getSapDigitalPaymentService().capture(txn);
				if (TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
				{
					LOG.debug("The digital payment transaction has been captured. Order: {}. Txn: {}", order.getCode(), txn.getCode());
				}
				else
				{
					paymentFailed = true;
					LOG.info("The digital payment transaction capture has failed. Order: {}. Txn: {}", order.getCode(), txn.getCode());
				}
			}
			else
			{
				txnEntry = getPaymentService().capture(txn);
				if (TransactionStatus.ACCEPTED.name().equals(txnEntry.getTransactionStatus()))
				{
					LOG.debug("The payment transaction has been captured. Order: {}. Txn: {}", order.getCode(), txn.getCode());
				}
				else
				{
					paymentFailed = true;
					LOG.info("The payment transaction capture has failed. Order: {}. Txn: {}", order.getCode(), txn.getCode());
				}
			}
		}
		if (paymentFailed)
		{
			setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
			return Transition.NOK;
		}
		else
		{
			setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
			return Transition.OK;
		}
	}
	/**
	 * @return the sapDigitalPaymentService
	 */
	public SapDigitalPaymentService getSapDigitalPaymentService()
	{
		return sapDigitalPaymentService;
	}
	/**
	 * @param sapDigitalPaymentService the sapDigitalPaymentService to set
	 */
	public void setSapDigitalPaymentService(SapDigitalPaymentService sapDigitalPaymentService)
	{
		this.sapDigitalPaymentService = sapDigitalPaymentService;
	}
	/**
	 * @return the paymentService
	 */
	public PaymentService getPaymentService()
	{
		return paymentService;
	}
	/**
	 * @param paymentService the paymentService to set
	 */
	public void setPaymentService(PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}
}