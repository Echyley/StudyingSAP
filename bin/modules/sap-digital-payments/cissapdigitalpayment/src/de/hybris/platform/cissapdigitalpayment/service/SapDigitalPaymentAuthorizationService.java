/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.service;

import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationRequestList;
import de.hybris.platform.cissapdigitalpayment.client.model.CisSapDigitalPaymentAuthorizationResultList;
import de.hybris.platform.cissapdigitalpayment.strategies.SapDigitalPaymentAuthorizationStrategy;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

import java.math.BigDecimal;


/**
 * Defines the methods to handle SAP Digital Payment amount authorization
 */
public interface SapDigitalPaymentAuthorizationService
{

	/**
	 * Determines the payment amount authorization stategy for SAP Digital payments
	 *
	 * @return {@link SapDigitalPaymentAuthorizationStrategy}
	 */
	SapDigitalPaymentAuthorizationStrategy getAuthorisationStrategy();

	/**
	 * Process the authorization result from SAP Digital Payments
	 *
	 * @param parameter
	 *           - {@link CommerceCheckoutParameter}
	 * @param authorizationResultList
	 *           - {@link CisSapDigitalPaymentAuthorizationResultList} authorization response list
	 *
	 * @return {@link PaymentTransactionEntryModel}
	 *
	 */
	PaymentTransactionEntryModel processSapDigitalPaymentAuthorizationResult(final CommerceCheckoutParameter parameter,
			final CisSapDigitalPaymentAuthorizationResultList authorizationResultList);

	/**
	 * Creates the authorization payment request for SAP Digital Payments
	 *
	 * @param subscriptionId
	 *           - {@link CreditCardPaymentInfoModel}'s subscriptionId
	 * @param amount
	 *           - amount to be authorized
	 * @param currencyCode
	 *           - currency ISO code
	 * @return {@link CisSapDigitalPaymentAuthorizationRequestList}
	 *
	 */
	CisSapDigitalPaymentAuthorizationRequestList createAuthorizePaymentRequest(final String subscriptionId,
			final BigDecimal amount, final String currencyCode);

}
