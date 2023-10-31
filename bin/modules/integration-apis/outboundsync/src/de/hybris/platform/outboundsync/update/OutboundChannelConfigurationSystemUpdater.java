/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.outboundsync.update;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.outboundsync.constants.OutboundsyncConstants;
import de.hybris.platform.outboundsync.model.OutboundChannelConfigurationModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import org.slf4j.Logger;

@SystemSetup(extension = OutboundsyncConstants.EXTENSIONNAME)
public class OutboundChannelConfigurationSystemUpdater
{
	private static final Logger LOG = Log.getLogger(OutboundChannelConfigurationSystemUpdater.class);
	private static final boolean SYNCHRONIZE_DELETE_DEFAULT_VALUE = false;
	private final ModelService modelService;
	private final FlexibleSearchService flexibleSearchService;

	public OutboundChannelConfigurationSystemUpdater(final ModelService modelService,
	                                                 final FlexibleSearchService flexibleSearchService)
	{
		this.modelService = modelService;
		this.flexibleSearchService = flexibleSearchService;
	}

	@SystemSetup(type = SystemSetup.Type.ESSENTIAL, process = SystemSetup.Process.UPDATE)
	public void populateNullSynchronizeDeleteWithDefaultValues()
	{
		final List<OutboundChannelConfigurationModel> nullSyncDeleteOCC = findOCCsWithoutSynchronizeDelete();
		if (!nullSyncDeleteOCC.isEmpty())
		{
			LOG.info("Setting default values for synchronizeDelete for OutboundChannelConfigurations.");
			nullSyncDeleteOCC.forEach(this::updateSynchronizeDeleteFlag);
			LOG.info("Finished setting default values for OutboundChannelConfigurations.");
		}
	}

	private void updateSynchronizeDeleteFlag(final OutboundChannelConfigurationModel occ)
	{
		LOG.debug("Setting default value for synchronizeDelete for OutboundChannelConfiguration with code: {}",
				occ.getCode());
		occ.setSynchronizeDelete(SYNCHRONIZE_DELETE_DEFAULT_VALUE);
		modelService.save(occ);
	}

	private List<OutboundChannelConfigurationModel> findOCCsWithoutSynchronizeDelete()
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery(
				"SELECT {occ." + ItemModel.PK + "} " +
						"FROM {" + OutboundChannelConfigurationModel._TYPECODE + " AS occ} " +
						"WHERE ({occ." + OutboundChannelConfigurationModel.SYNCHRONIZEDELETE + "} is null)");

		return flexibleSearchService.<OutboundChannelConfigurationModel>search(query).getResult();
	}
}
