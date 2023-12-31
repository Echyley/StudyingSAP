/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.erp;

import de.hybris.platform.sap.core.bol.backend.BackendType;
import de.hybris.platform.sap.core.bol.logging.Log4JWrapper;
import de.hybris.platform.sap.core.jco.connection.JCoConnection;
import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapordermgmtbol.transaction.misc.backend.impl.TransactionConfigurationBase;

import java.util.Iterator;
import java.util.Map;


/**
 * ERP specific implementation of TransactionConfigurationBackend
 */
@BackendType("ERP")
public class TransactionConfigurationERP extends TransactionConfigurationBase
{

	private static final Log4JWrapper SAP_LOGGER = Log4JWrapper.getInstance(TransactionConfigurationERP.class.getName());

	protected static final String CACHEKEY_TITLE_LIST = "TITLE_LIST";

	/** Name of cache region. */
	public static final String CACHE_NAME_FOR_CUSTOMIZING = "r3cust";

	/** Switches */
	protected static final String SWITCH_ECC60_EHP5 = "ERP_WEC_CRM_SFWS_1";
	protected static final String BF_ECC60_EHP5 = "ERP_WEB_CHANNEL_1";




	/*
	 * (non-Javadoc)
	 *
	 * @seecom.sap.wec.app.ecom.module.checkout.backend.interf.
	 * CheckoutConfigurationBackend#getAllowedDeliveryTypesFromBackend()
	 */
	@Override
	public Map<String, String> getAllowedDeliveryTypesFromBackend() throws BackendException
	{
		SAP_LOGGER.entering("getAllowedDeliveryTypesFromBackend");
		Map<String, String> result = null;
		// fetch a JCO-connection with the current Internet Explorer language of
		// the user
		final JCoConnection jConn = getDefaultJCoConnection();

		// does the search and the conversion of the result
		result = this.tools.executeSearchhelp(jConn, "H_TVSB", "VSBED", "VTEXT", Boolean.TRUE);
		SAP_LOGGER.exiting();
		return result;
	}

	/**
	 * Traces a map.
	 *
	 * @param log
	 *           the logger instance
	 * @param map
	 *           the map which we want to trace
	 */
	public static void performDebugOutput(final Log4JWrapper log, final Map<String, String> map)
	{
		if (log.isDebugEnabled())
		{
			final StringBuilder debugOutput = new StringBuilder(100);
			debugOutput.append("\n map content:");
			if (map != null && !map.isEmpty())
			{
				final Iterator<Map.Entry<String, String>> iter = map.entrySet().iterator();
				while (iter.hasNext())
				{
					debugOutput.append("\n").append(iter.next());
				}
			}
			// toString is not necessary but it eases usage of EasyMock
			log.debug(debugOutput.toString());
		}
	}



	@Override
	protected String getCacheRegionForCustomizing()
	{
		return CACHE_NAME_FOR_CUSTOMIZING;
	}

}
