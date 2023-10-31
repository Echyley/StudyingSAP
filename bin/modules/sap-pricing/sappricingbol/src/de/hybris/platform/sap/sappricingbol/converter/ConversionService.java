/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sappricingbol.converter;

/**
 Interface for conversion
 *
 */
public interface ConversionService
{
/**
 * Method to get SAP unit for ISO
 * 
 * @param code value
 * @return sap unit for ISO
 */
public String getSAPUnitforISO(String code);
/**
 * Method to get ISO Unit for SAP
 * @param code value
 * @return iso unit for SAP
 */
public String getISOUnitforSAP(String code);
}
