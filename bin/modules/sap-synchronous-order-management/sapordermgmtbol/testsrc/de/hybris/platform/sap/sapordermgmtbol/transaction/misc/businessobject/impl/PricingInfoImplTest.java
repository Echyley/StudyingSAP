/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.businessobject.impl;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


@SuppressWarnings("javadoc")
public class PricingInfoImplTest extends TestCase
{

	private final PricingInfoImpl classUnderTest = new PricingInfoImpl();

	private final String distributionChannel = "DistributionChannel";
	private final String distributionChannelOriginal = "DistributionChannelOriginal";
	private final String documentCurrencyUnit = "DocumentUnit";
	private final String fGProcedureName = "fGProcedure";
	private final Map<String, String> headerAttributes = new HashMap<String, String>();
	private final Map<String, Map<String, Map<String, String>>> itemAttributes = new HashMap<String, Map<String, Map<String, String>>>();
	private final String localCurrencyUnit = "localUnit";
	private final String procedureName = "Procedure";
	private final String salesOrganisation = "salesOrg";
	private final String salesOrganisationCrm = "salesOrgCRM";

	@Override
	public void setUp()
	{
		classUnderTest.setDistributionChannel(distributionChannel);
		classUnderTest.setDistributionChannelOriginal(distributionChannelOriginal);
		classUnderTest.setDocumentCurrencyUnit(documentCurrencyUnit);
		classUnderTest.setFGProcedureName(fGProcedureName);
		classUnderTest.setHeaderAttributes(headerAttributes);
		classUnderTest.setItemAttributes(itemAttributes);
		classUnderTest.setLocalCurrencyUnit(localCurrencyUnit);
		classUnderTest.setProcedureName(procedureName);
		classUnderTest.setSalesOrganisation(salesOrganisation);
		classUnderTest.setSalesOrganisationCrm(salesOrganisationCrm);
	}

	public void testValues()
	{
		assertEquals(distributionChannel, classUnderTest.getDistributionChannel());
		assertEquals(distributionChannelOriginal, classUnderTest.getDistributionChannelOriginal());
		assertEquals(documentCurrencyUnit, classUnderTest.getDocumentCurrencyUnit());
		assertEquals(fGProcedureName, classUnderTest.getFGProcedureName());
		assertTrue(headerAttributes == classUnderTest.getHeaderAttributes());
		assertTrue(itemAttributes == classUnderTest.getItemAttributes());
		assertEquals(localCurrencyUnit, classUnderTest.getLocalCurrencyUnit());
		assertEquals(procedureName, classUnderTest.getProcedureName());
		assertEquals(salesOrganisation, classUnderTest.getSalesOrganisation());
		assertEquals(salesOrganisationCrm, classUnderTest.getSalesOrganisationCrm());
	}
}
