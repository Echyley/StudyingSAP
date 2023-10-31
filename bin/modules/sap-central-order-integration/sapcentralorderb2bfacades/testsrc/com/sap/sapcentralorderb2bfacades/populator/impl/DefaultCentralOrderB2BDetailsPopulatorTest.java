/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderb2bfacades.populator.impl;

import static com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants.ADDRESS_TYPE_BILLTO;
import static com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants.PAYMENT_TYPE_INVOICE;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Address;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Customer;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PaymentData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Person;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderB2BDetailsPopulatorTest
{


	CentralOrderDetailsResponse source = mock(CentralOrderDetailsResponse.class);


	Address address = mock(Address.class);


	Customer customer = mock(Customer.class);


	Person person = mock(Person.class);


	CustomerData b2BCustomerData = mock(CustomerData.class);


	B2BCostCenterData costCenter = mock(B2BCostCenterData.class);


	B2BUnitData unitData = mock(B2BUnitData.class);


	PaymentData paymentData = mock(PaymentData.class);


	DefaultCentralOrderB2BDetailsPopulator defaultCentralOrderB2BDetailsPopulator = new DefaultCentralOrderB2BDetailsPopulator();
	private static final String ORDERID = "orderID";
	private static final String FIRSTNAME = "firstName";
	private static final String ACADEMIC = "academic";
	private static final String LASTNAME = "lastName";
	private static final String COSTCENTERNAME = "costCenterName";
	private static final String COSTCENTERCODE = "costCenterCode";

	@Test
	public void shouldPopulate()
	{

		//Mock customer
		final List<Address> addresses = List.of(address);
		when(customer.getAddresses()).thenReturn(addresses);
		when(customer.getPerson()).thenReturn(person);

		//Mock Address
		when(address.getAddressType()).thenReturn(ADDRESS_TYPE_BILLTO);

		//Mock Payment Data
		when(paymentData.getMethod()).thenReturn(PAYMENT_TYPE_INVOICE);

		//Mock Person
		when(person.getFirstName()).thenReturn(FIRSTNAME);
		when(person.getAcademicTitle()).thenReturn(ACADEMIC);
		when(person.getLastName()).thenReturn(LASTNAME);

		//Mock Cost Center
		when(costCenter.getUnit()).thenReturn(unitData);
		when(costCenter.getName()).thenReturn(COSTCENTERNAME);
		when(costCenter.getCode()).thenReturn(COSTCENTERCODE);

		//execute
		final List<PaymentData> paymentDataList = List.of(paymentData);
		source = new CentralOrderDetailsResponse();
		source.setPayment(paymentDataList);
		source.setCustomer(customer);
		final OrderData target = new OrderData();
		target.setPurchaseOrderNumber(ORDERID);
		target.setCostCenter(costCenter);
		defaultCentralOrderB2BDetailsPopulator.populate(source, target);

		//verify
		assertEquals(FIRSTNAME, target.getB2bCustomerData().getFirstName());
		assertEquals(ACADEMIC, target.getB2bCustomerData().getTitleCode());
		assertEquals(LASTNAME, target.getB2bCustomerData().getLastName());
		assertEquals(COSTCENTERNAME, target.getCostCenter().getName());
	}

}
