/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcommonbol.common.backendobject.interf;

import de.hybris.platform.sap.core.jco.exceptions.BackendException;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMapping;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DistChannelMappingKey;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMapping;
import de.hybris.platform.sap.sapcommonbol.common.salesarea.businessobject.interf.DivisionMappingKey;

import java.util.Map;


/**
 * Interface to get the backend information for the common sales area. Gets common distribution channel and division
 * from back end. <br>
 * 
 */
public interface CommonSalesArea
{

	/**
	 * Back end object for the backendobject-config
	 */
	String BE_TYPE = "CommonSalesArea";

	/**
	 * Loads common distribution channel data
	 * 
	 * @return content of the table TVKOS
	 * @throws BackendException
	 */
	Map<DistChannelMappingKey, DistChannelMapping> loadDistChannelMappingFromBackend() throws BackendException;

	/**
	 * Loads common division data
	 * 
	 * @return content of the table TVKOV
	 * @throws BackendException
	 */
	Map<DivisionMappingKey, DivisionMapping> loadDivisionMappingFromBackend() throws BackendException;

	/**
	 * Get shop configuration key from BE.<br>
	 * 
	 * @return configuration key
	 * @throws BackendException
	 */
	String getConfigurationKey() throws BackendException;
}
