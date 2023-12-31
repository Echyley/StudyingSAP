/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cissapdigitalpayment.client.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * SAP Digital payment authorization request fields
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CisSapDigitalPaymentAuthorizationRequest
{
	@JsonProperty("Source")
	private CisSapDigitalPaymentSource cisSapDigitalPaymentSource;

	@JsonProperty("AmountInAuthorizationCurrency")
	private String amountInAuthorizationCurrency;

	@JsonProperty("AuthorizationCurrency")
	private String authorizationCurrency;

	/**
	 * @return the cisSapDigitalPaymentSource
	 */
	public CisSapDigitalPaymentSource getCisSapDigitalPaymentSource()
	{
		return cisSapDigitalPaymentSource;
	}

	/**
	 * @param cisSapDigitalPaymentSource
	 *           the cisSapDigitalPaymentSource to set
	 */
	public void setCisSapDigitalPaymentSource(final CisSapDigitalPaymentSource cisSapDigitalPaymentSource)
	{
		this.cisSapDigitalPaymentSource = cisSapDigitalPaymentSource;
	}

	/**
	 * @return the amountInAuthorizationCurrency
	 */
	public String getAmountInAuthorizationCurrency()
	{
		return amountInAuthorizationCurrency;
	}

	/**
	 * @param amountInAuthorizationCurrency
	 *           the amountInAuthorizationCurrency to set
	 */
	public void setAmountInAuthorizationCurrency(final String amountInAuthorizationCurrency)
	{
		this.amountInAuthorizationCurrency = amountInAuthorizationCurrency;
	}

	/**
	 * @return the authorizationCurrency
	 */
	public String getAuthorizationCurrency()
	{
		return authorizationCurrency;
	}

	/**
	 * @param authorizationCurrency
	 *           the authorizationCurrency to set
	 */
	public void setAuthorizationCurrency(final String authorizationCurrency)
	{
		this.authorizationCurrency = authorizationCurrency;
	}

}
