/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.impl;

import de.hybris.platform.sap.sapcommonbol.businesspartner.businessobject.interf.PartnerFunctionData;


/**
 * BO representation of a generic partner function. Cannot be instantiated
 * 
 */
public abstract class PartnerFunctionBase implements PartnerFunctionData
{
	@Override
	public abstract String getName();

}