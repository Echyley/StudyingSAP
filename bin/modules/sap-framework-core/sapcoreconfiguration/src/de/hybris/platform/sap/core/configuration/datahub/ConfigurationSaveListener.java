/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.datahub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.hybris.datahub.core.rest.DataHubCommunicationException;

import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.constants.SapcoreconfigurationConstants;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.tx.AfterSaveEvent;
import de.hybris.platform.tx.AfterSaveListener;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.core.Initialization;


/**
 * This class handles the save events in order to create data hub configurations and to transfer them to the data hub.
 */
public class ConfigurationSaveListener implements AfterSaveListener
{
	private static final Logger LOGGER = LogManager.getLogger(ConfigurationSaveListener.class);

	private ModelService modelService;
	private DataHubTransferConfigurationManager dataHubManager;
	private DataHubTransferHandler dataHubTransferHandler;
	private Boolean dataHubOutboundEnabled;

	private RemoveModelMap removeModelMap;
	private DataHubPingService dataHubPingService;

	@Override
	public void afterSave(final Collection<AfterSaveEvent> events)
	{
		if (!isDataHubOutboundEnabled())
		{
			LOGGER.debug("Data hub outbound not enabled (sapcoreconfiguration.datahuboutbound.enabled=false).");
			return;
		}
		if (Initialization.isCurrentTenantInitializing()){
			LOGGER.info("Commerce Tenant is not initialized");
			return;
		}

		for (final AfterSaveEvent event : events)
		{
			LOGGER.info("Commerce Tenant is initialized");
			final PK pk = event.getPk();
			final int typeCode = pk.getTypeCode();
			// Prevent invoking data hub transfer for SAPAdministration
			if (typeCode != SapcoreconfigurationConstants.ITEM_TYPE_CODE_SAP_ADMINISTRATION)
			{
				doActionBasedOnCode(event, pk, typeCode);
			}
		}
	}

	private void doActionBasedOnCode(AfterSaveEvent event, PK pk, int typeCode) {
		final int type = event.getType();
		final String code = dataHubManager.getItemCode(typeCode);
		if (code != null)
        {
            final List<ItemModel> models = new ArrayList<>();
			if (type == AfterSaveEvent.REMOVE)
            {
				afterSaveEventRemove(pk, code, models);
			}
            else
            {
				invokeDatahubTransfer(event, code, models);
            }
        }
	}

	private void invokeDatahubTransfer(AfterSaveEvent event, String code, List<ItemModel> models) {
		ItemModel itemModel;
		itemModel = getItemModel(event.getPk());
		models.add(itemModel);
		final List<DataHubTransferConfiguration> dataHubTransferConfigurations = dataHubManager
                .getDataHubTransferConfigurations(code);
		try
        {
            if (getDataHubPingService().ping())
            {
                dataHubTransferHandler.invokeTransfer(code, models, dataHubTransferConfigurations, null);
            }

        }
        catch (final DataHubCommunicationException ex)
        {
            final String logMessage = Localization
                    .getLocalizedString("dataTransfer.dataHub.unableCommunication.exception") + ex.getLocalizedMessage();
            LOGGER.warn("{}: {}", code, logMessage);
            LOGGER.error(ex);
        }
	}

	private void afterSaveEventRemove(PK pk, String code, List<ItemModel> models) {
		ItemModel itemModel;// get the item model which will be deleted from a temporary list which will be set in
		// the class ItemRemoveInterceptor.
		itemModel = removeModelMap.getModelsToBeDeleted().get(pk);
		if (itemModel != null)
        {
            models.add(itemModel);
        }

		final List<DataHubTransferConfiguration> dataHubTransferConfigurations = dataHubManager
                .getDataHubTransferConfigurations(code);

		try
        {

            if (getDataHubPingService().ping())
            {
                dataHubTransferHandler.invokeDeleteItem(code, models, dataHubTransferConfigurations, null);
            }

        }
        catch (final DataHubCommunicationException ex)
        {
            final String logMessage = Localization
                    .getLocalizedString("dataTransfer.dataHub.unableCommunication.exception") + ex.getLocalizedMessage();
            LOGGER.warn("{}: {}", code, logMessage);
            LOGGER.error(ex);
        }

		// now, delete the model within the temporary list
		removeModelMap.deleteModel(pk);
	}

	/**
	 * Get item model with the latest properties.
	 * 
	 * @param pk
	 *           primary key
	 * @return item model
	 */
	protected ItemModel getItemModel(final PK pk)
	{
		final ItemModel model = modelService.get(pk);
		modelService.refresh(model);
		return model;
	}

	
	@Required
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	
	@Required
	public void setDataHubManager(final DataHubTransferConfigurationManager dataHubManager)
	{
		this.dataHubManager = dataHubManager;
	}

	/**
	 * Injection setter for {@link DataHubTransferHandler}.
	 * 
	 * @param dataHubTransferHandler
	 *           the data hub transfer handler to set
	 */
	@Required
	public void setDataHubTransferHandler(final DataHubTransferHandler dataHubTransferHandler)
	{
		this.dataHubTransferHandler = dataHubTransferHandler;
	}

	/**
	 * Injection setter for {@link RemoveModelMap}.
	 * 
	 * @param modelMap
	 *           the modelmap of items which will be deleted to set
	 */
	@Required
	public void setRemoveModelMap(final RemoveModelMap modelMap)
	{
		this.removeModelMap = modelMap;
	}

	/**
	 * Checks if the data hub outbound for configuration is enabled. <br>
	 * The enablement is done by setting property <code>sapcoreconfiguration.datahuboutbound.enabled</code> to true
	 * (default: false).
	 * 
	 * @return the dataHubOutboundEnabled
	 */
	private boolean isDataHubOutboundEnabled()
	{
		if (dataHubOutboundEnabled == null)
		{
			dataHubOutboundEnabled = Config.getBoolean("sapcoreconfiguration.datahuboutbound.enabled", false);
		}
		return dataHubOutboundEnabled;
	}

	public DataHubPingService getDataHubPingService()
	{
		return dataHubPingService;
	}

	@Required
	public void setDataHubPingService(DataHubPingService dataHubPingService)
	{
		this.dataHubPingService = dataHubPingService;
	}




}
