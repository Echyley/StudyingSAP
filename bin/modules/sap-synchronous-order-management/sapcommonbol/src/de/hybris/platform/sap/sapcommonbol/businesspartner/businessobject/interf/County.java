/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf;

/**
 * BO representation of a county (relevant for countries where tax jurisdiction
 * code determination based on county is necessary)
 * 
 */
public interface County extends Cloneable {

    /**
     * @return county description
     */
    String getCountyText();

    /**
     * @param countyText county description
     */
    void setCountyText(String countyText);

    /**
     * @return tax jurisdiction code. Will be determined in ERP or CRM backend
     */
    String getTaxJurCode();

    /**
     * @param taxJurCode tax jurisdiction code. Will be determined in ERP or CRM
     *            backend
     */
    void setTaxJurCode(String taxJurCode);

    /**
     * @return a clone of the county
     */
    @SuppressWarnings({"squid:S1161","squid:S2975"})
    County clone();

}
