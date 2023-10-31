/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.datahub.interceptor;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.sap.core.configuration.datahub.DataHubTransferConfiguration;
import de.hybris.platform.sap.core.configuration.datahub.DataHubTransferConfigurationManager;
import de.hybris.platform.sap.core.configuration.datahub.RemoveModelMap;
import de.hybris.platform.sap.core.configuration.populators.GenericModel2MapPopulator;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.interceptor.RemoveInterceptor;

import java.util.HashMap;
import java.util.List;


/**
 * Performed when an item gets removed within the backoffice/hmc.
 */
public class ConfigurationItemRemoveInterceptor implements RemoveInterceptor<ItemModel>
{

	private RemoveModelMap removeModelMap = null;
	private GenericModel2MapPopulator model2MapPopulator = null;
	private DataHubTransferConfigurationManager dataHubManager;


	@Override
	public void onRemove(final ItemModel model, final InterceptorContext ctx) throws InterceptorException
	{

		final List<DataHubTransferConfiguration> dataHubTransferConfigurations = dataHubManager
				.getDataHubTransferConfigurations(model.getItemtype());

		if (dataHubTransferConfigurations != null && !dataHubTransferConfigurations.isEmpty())
		{
			// the attributes of the model classes are implemented lazy load. That mean if the attribute is not used then
			// the value is not loaded to the model class. 
			// The next statement loads the attributes of the model class because these values 
			// are needed when the item gets deleted in the datahub. Please see class ConfigurationSaveListener
			model2MapPopulator.populate(model, new HashMap<String, Object>());
			removeModelMap.addModelToBeDeleted(model.getPk(), model);
		}
	}

	
	public void setRemoveModelMap(final RemoveModelMap modelMap)
	{
		this.removeModelMap = modelMap;
	}


	
	public void setGenericModel2MapPopulator(final GenericModel2MapPopulator model2MapPopulator)
	{
		this.model2MapPopulator = model2MapPopulator;
	}

	
	public void setDataHubManager(final DataHubTransferConfigurationManager dataHubManager)
	{
		this.dataHubManager = dataHubManager;
	}

}
