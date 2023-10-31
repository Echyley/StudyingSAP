/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps;

import de.hybris.platform.sap.productconfig.runtime.cps.model.external.CPSExternalConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.pricing.PricingDocumentResult;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSConfiguration;
import de.hybris.platform.sap.productconfig.runtime.cps.model.runtime.CPSItem;
import de.hybris.platform.sap.productconfig.runtime.interf.ConfigurationEngineException;
import de.hybris.platform.sap.productconfig.runtime.interf.PricingEngineException;

import java.util.List;

import com.hybris.charon.exp.HttpException;


/**
 * Used to wrap exceptions originating from REST calls through the charon framework.
 */
public interface RequestErrorHandler
{
	/**
	 * processes exception from configuration update request
	 *
	 * @param ex
	 *           exception to process
	 * @param configId
	 *           configuration runtime id of the configuration that was attempted to be updated
	 * @throws ConfigurationEngineException
	 *
	 */
	void processUpdateConfigurationError(HttpException ex, String configId) throws ConfigurationEngineException;

	/**
	 * processes exception from configuration create default configuration request
	 *
	 * @param ex
	 *           exception to process
	 * @return default configuration
	 *
	 */
	CPSConfiguration processCreateDefaultConfigurationError(HttpException ex);

	/**
	 * processes exception from configuration create default configuration request
	 *
	 * @param ex
	 *           exception to process
	 * @param configId
	 *           configuration runtime id of the configuration that was attempted to be retrieved
	 * @return configuration update result
	 * @throws ConfigurationEngineException
	 */
	CPSConfiguration processGetConfigurationError(HttpException ex, String configId) throws ConfigurationEngineException;

	/**
	 * processes exception from delete configuration request
	 *
	 * @param ex
	 *           exception to process
	 */
	void processDeleteConfigurationError(HttpException ex);

	/**
	 * processes exception from get external configuration request
	 *
	 * @param ex
	 *           exception to process
	 * @param configId
	 *           configuration runtime id of the external configuration that was attempted to be retrieved
	 * @return external representation of configuration
	 * @throws ConfigurationEngineException
	 *            Exception on calling the configuration service (e.g. when the session has expired)
	 */
	CPSExternalConfiguration processGetExternalConfigurationError(HttpException ex, String configId)
			throws ConfigurationEngineException;

	/**
	 * processes exception from create configuration from external request
	 *
	 * @param ex
	 *           exception to process
	 * @return runtime configuration
	 */
	CPSConfiguration processCreateRuntimeConfigurationFromExternalError(HttpException ex);

	/**
	 * processes exception from create pricing document request
	 *
	 * @param ex
	 *           exception to process
	 * @return pricing document result
	 *
	 * @throws PricingEngineException
	 *            which indicates the error towards higher layers
	 */
	PricingDocumentResult processCreatePricingDocumentError(HttpException ex) throws PricingEngineException;

	/**
	 * processes exception from get knowledgebase request
	 *
	 * @param ex
	 *           exception to process
	 * @return whether kb exists
	 */
	boolean processHasKbError(HttpException ex);

	/**
	 * Processes runtime exceptions like timeout or non-existing server. Default behavior: Timeout exceptions are handled
	 * gracefully in the sense that they are converted into {@link PricingEngineException} while other runtime exceptions
	 * are re-thrown.
	 *
	 * @param ex
	 *           Runtime exception that wraps the actual root cause
	 * @return Dummy result of the pricing call
	 * @throws PricingEngineException
	 */
	PricingDocumentResult processCreatePricingDocumentRuntimeException(RuntimeException ex) throws PricingEngineException;

	/**
	 * Processes runtime exceptions like timeout or non-existing server. Default behavior: Timeout exceptions are handled
	 * gracefully in the sense that they are converted into {@link ConfigurationEngineException} while other runtime
	 * exceptions are re-thrown.
	 *
	 * @param e
	 *           Runtime exception that wraps the actual root cause
	 * @param configId
	 *           configuration runtime id of the configuration that was requested
	 * @throws ConfigurationEngineException
	 *
	 */
	void processConfigurationRuntimeException(RuntimeException e, String configId) throws ConfigurationEngineException;

	/**
	 * processes exception from get item with group details request
	 *
	 * @param ex
	 *                     exception to process
	 * @param configId
	 *                     configuration runtime id of the configuration that was attempted to be retrieved
	 * @param itemId
	 *                     item runtime id of the configuration that was attempted to be retrieved
	 * @param groupList
	 *                     list of group names of the configuration that was attempted to be retrieved
	 * @return configuration update result
	 * @throws ConfigurationEngineException
	 */
	CPSItem processGetItemWithGroupDetailsError(HttpException ex, String configId, String itemId, List<String> groupList)
			throws ConfigurationEngineException;
}
