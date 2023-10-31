/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapentitlementsocctests.service.impl;


import java.util.ArrayList;
import java.util.Date;

import com.sap.hybris.sapentitlementsintegration.pojo.Data;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlement;
import com.sap.hybris.sapentitlementsintegration.pojo.Entitlements;
import com.sap.hybris.sapentitlementsintegration.service.SapEntitlementService;


/**
 * Mock class for SapEntitlementService
 */
public class MockSapEntitlementService implements SapEntitlementService
{

	private final Entitlements entitlements = new Entitlements();
	private final Entitlement entitlement = new Entitlement();
	private final ArrayList<Entitlement> dummyResponse = new ArrayList<Entitlement>();
	private final Data data = new Data();
	private final int quantity = 5;


	@Override
	public Entitlements getEntitlementsForCurrentCustomer(final int pageNumber, final int pageSize)
	{

		return setEntitlements();
	}

	@Override
	public Entitlements getEntitlementForNumber(final String entitlementNumber)
	{
		return setEntitlements();
	}

	protected Entitlements setEntitlements()
	{
		entitlement.setEntitlementNo(Integer.valueOf(1));
		entitlement.setEntitlementGuid("qwert1234");
		entitlement.setValidFrom(new Date());
		entitlement.setValidTo(new Date());
		entitlement.setStatusName("Active");
		entitlement.setRefDocNo("Ref_1234");
		entitlement.setGeolocation("Europe");
		entitlement.setOfferingID("Sch001");
		entitlement.setEntitlementModelName("ModelName");
		entitlement.setQuantity(quantity);
		entitlement.setEntitlementTypeName("Service");
		dummyResponse.add(entitlement);
		data.setResponse(dummyResponse);
		entitlements.setData(data);

		return entitlements;
	}

}
