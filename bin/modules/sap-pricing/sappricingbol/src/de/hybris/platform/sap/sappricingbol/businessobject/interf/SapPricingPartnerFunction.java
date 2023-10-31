/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricingbol.businessobject.interf;
public interface SapPricingPartnerFunction {

	public abstract void setSoldTo(String soldTo);

	public abstract String getSoldTo();

	public abstract void setCurrency(String currency);

	public abstract String getCurrency();

	public abstract void setLanguage(String language);

	public abstract String getLanguage();
	
	

}