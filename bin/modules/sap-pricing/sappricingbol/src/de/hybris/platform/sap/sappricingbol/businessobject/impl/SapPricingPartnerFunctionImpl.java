/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricingbol.businessobject.impl;

import de.hybris.platform.sap.sappricingbol.businessobject.interf.SapPricingPartnerFunction;

public class SapPricingPartnerFunctionImpl implements SapPricingPartnerFunction {
	
	private String language;
	private String currency;
	private String soldTo;
	
	@Override
	public String getLanguage() {
		return language;
	}
	
	@Override
	public void setLanguage(String language) {
		this.language = language;
	}
	
	@Override
	public String getCurrency() {
		return currency;
	}
	
	@Override
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	
	@Override
	public String getSoldTo() {
		return soldTo;
	}

	@Override
	public void setSoldTo(String soldTo) {
		this.soldTo = soldTo;
	}
	
	public SapPricingPartnerFunctionImpl() {
		super();
	}
	
	public SapPricingPartnerFunctionImpl(String language, String currency,
			String soldTo) {
		super();
		this.language = language;
		this.currency = currency;
		this.soldTo = soldTo;
	}
	
	@Override
	public String toString() {
		return "DefaultSapPricingPartnerFunction [language=" + language
				+ ", currency=" + currency + ", soldTo=" + soldTo + "]";
	}
	
}
