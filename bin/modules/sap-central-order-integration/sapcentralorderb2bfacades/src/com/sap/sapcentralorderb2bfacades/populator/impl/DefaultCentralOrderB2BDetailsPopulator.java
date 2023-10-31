/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderb2bfacades.populator.impl;

import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Address;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PaymentData;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants;
import com.sap.sapcentralorderfacades.populator.SapCpiOrderDetailPopulator;


/**
 *
 */
public class DefaultCentralOrderB2BDetailsPopulator implements SapCpiOrderDetailPopulator
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCentralOrderB2BDetailsPopulator.class);


	@Override
	public void populate(final CentralOrderDetailsResponse source, final OrderData target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		try
		{
			for (final Address address : source.getCustomer().getAddresses())
			{

				if (address.getAddressType().equalsIgnoreCase(SapcentralorderfacadesConstants.ADDRESS_TYPE_BILLTO))
				{
					populateOrderData(source, target);

				}
			}

		}

		catch (final Exception e)
		{
			LOG.warn(String.format(e.getMessage()));
		}

	}


	/**
	 *
	 */
	private void populateOrderData(final CentralOrderDetailsResponse source, final OrderData target)
	{
		for (final PaymentData paymentData : source.getPayment())
		{
			if (paymentData.getMethod().equalsIgnoreCase(SapcentralorderfacadesConstants.PAYMENT_TYPE_INVOICE))
			{

				if (StringUtils.isNotEmpty(target.getPurchaseOrderNumber()))
				{
					target.setPurchaseOrderNumber("");
				}
				populateCustomerData(source, target);
				populateCostCenter(target);
			}
		}
	}


	/**
	 *
	 */
	private void populateCostCenter(final OrderData target)
	{
		if (target.getCostCenter() != null && target.getCostCenter().getUnit() != null)
		{
			final B2BCostCenterData costCentreData = new B2BCostCenterData();
			costCentreData.setName(target.getCostCenter().getName());
			costCentreData.setCode(target.getCostCenter().getCode());

			final B2BUnitData unit = new B2BUnitData();
			unit.setName(target.getCostCenter().getUnit().getName());
			costCentreData.setUnit(unit);
			target.setCostCenter(costCentreData);
		}

	}


	/**
	 *
	 */
	private void populateCustomerData(final CentralOrderDetailsResponse source, final OrderData target)
	{
		final CustomerData b2BCustomerData = new CustomerData();
		if ((source.getCustomer() != null && source.getCustomer().getPerson() != null)
				&& (StringUtils.isNoneEmpty(source.getCustomer().getPerson().getFirstName())
						|| StringUtils.isNoneEmpty(source.getCustomer().getPerson().getLastName())))
		{
			b2BCustomerData.setTitleCode(source.getCustomer().getPerson().getAcademicTitle());
			b2BCustomerData.setFirstName(source.getCustomer().getPerson().getFirstName());
			b2BCustomerData.setLastName(source.getCustomer().getPerson().getLastName());
			target.setB2bCustomerData(b2BCustomerData);
		}


	}




}
