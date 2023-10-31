/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapinvoicebol.businessobject.impl;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import de.hybris.platform.sap.core.bol.businessobject.BackendInterface;
import de.hybris.platform.sap.core.bol.businessobject.BusinessObjectBase;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapinvoicebol.backend.SapInvoiceBackend;
import de.hybris.platform.sap.sapinvoicebol.businessobject.SapInvoiceBO;


/**
 *
 */
@BackendInterface(SapInvoiceBackend.class)
public class SapInvoiceBOImpl extends BusinessObjectBase implements SapInvoiceBO
{

	private final String prefix = "sapInvoiceBackendType";
	
	private String backendTypeVal;
	
	/**
	* SAP Configuration Service
	*/
 	@SuppressWarnings("squid:S1948")
	private SAPConfigurationService configurationService;

	/**
	 * @return the sapInvoiceBackend
	 * @throws BackendException
	 */
	public SapInvoiceBackend getSapInvoiceBackend() throws BackendException
	{
		return (SapInvoiceBackend) getBackendBusinessObject();
	}

	@Override
	public byte[] getPDF(final String billingDocNumber) throws BackendException
	{
		byte[] invoicePdfByteArray = null;
		//backed type can be send as property , default taking from sapbasesoteconfiguration->coredata
		if(StringUtils.isEmpty(backendTypeVal)){
					backendTypeVal = configurationService.getBackendType();
		}
		if (null != backendTypeVal && !backendTypeVal.isEmpty())
		{

			final SapInvoiceBackend sapInvoiceBackendType = (SapInvoiceBackend) genericFactory.getBean(prefix.concat(backendTypeVal));
			invoicePdfByteArray = sapInvoiceBackendType.getInvoiceInByte(billingDocNumber);
		}


		return invoicePdfByteArray;
	}
	

	@Required
	public void setConfigurationService(SAPConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
	
	public String getBackendType() {
		return backendTypeVal;
	}
	
	public void setBackendType(String backendType) {
		this.backendTypeVal = backendType;
	}
}
