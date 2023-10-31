/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.cps.session.CPSResponseAttributeStrategy;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.KBKey;

import java.util.List;


/**
 * Facade around charon REST accesses
 */
public interface CharonFacade
{

	/**
	 * Create default configuration and handle cookies
	 *
	 * @param kbKey
	 *           key of the knowledgebase
	 * @return runtime configuration
	 */
	CPSConfiguration createDefaultConfiguration(KBKey kbKey);

	/**
	 * Retrieves the external representation for a given runtime configuration
	 *
	 * @param configId
	 *           id of the runtime configuration
	 *
	 * @return external representation of the runtime configuration
	 * @throws ConfigurationEngineException
	 *            Service has failed, e.g. because session timed out
	 */
	String getExternalConfiguration(String configId) throws ConfigurationEngineException;


	/**
	 * Deletes the session for the specified runtime configuration on the client
	 *
	 * @param configId
	 *           id of the runtime configuration to be deleted
	 * @param version
	 *           version of the runtime configuration to be deleted
	 */
	void releaseSession(String configId, String version);

	/**
	 * Updates configuration, sends cookies along with request. The cookies are handled internally and received from the
	 * {@link CPSResponseAttributeStrategy}
	 *
	 * @param configuration
	 *           runtime configuration that includes only the updates
	 * @return version of the updated runtime configuration
	 * @throws ConfigurationEngineException
	 *            when service call fails
	 */
	String updateConfiguration(CPSConfiguration configuration) throws ConfigurationEngineException;

	/**
	 * Gets configuration, sends cookies along with request. The cookies are handled internally and received from the
	 * {@link CPSResponseAttributeStrategy}
	 *
	 * @param configId
	 *           configuration id
	 * @return current state of the runtime configuration
	 * @throws ConfigurationEngineException
	 *            when service call fails
	 */
	CPSConfiguration getConfiguration(String configId) throws ConfigurationEngineException;


	/**
	 * Creates a new runtime configuration from the external representation of the configuration
	 *
	 * @param externalConfiguration
	 *           external representation of the configuration
	 * @param contextProduct
	 *           context product
	 * @return runtime configuration based on the external representation
	 */
	CPSConfiguration createConfigurationFromExternal(String externalConfiguration, String contextProduct);


	/**
	 * Creates a new runtime configuration from the external typed representation of the configuration
	 *
	 * @param externalConfigStructured
	 *           structured external configuration
	 * @param contextProduct
	 *           context product
	 * @return Runtime representation
	 */
	CPSConfiguration createConfigurationFromExternal(CPSExternalConfiguration externalConfigStructured, String contextProduct);

	/**
	 * Reads an item within a configuration and returns all details only for the specified groups of this instance
	 * 
	 * @param configId
	 *           Configuration ID
	 * @param instanceId
	 *           Instance Id for which group data are requested
	 * @param groupNames
	 *           List of requested group names for which we want to read the group details.
	 * @return Requested characteristic groups including domain values and all details
	 * @throws ConfigurationEngineException
	 *            Indicates that the CPS call could not be performed
	 */
	CPSItem getItemWithGroupDetails(String configId, String instanceId, List<String> groupNames)
			throws ConfigurationEngineException;

	/**
	 * Are domain values ready on demand, on group level?
	 * 
	 * @return Domain values read on demand?
	 */
	boolean isReadDomainValuesOnDemand();

}
