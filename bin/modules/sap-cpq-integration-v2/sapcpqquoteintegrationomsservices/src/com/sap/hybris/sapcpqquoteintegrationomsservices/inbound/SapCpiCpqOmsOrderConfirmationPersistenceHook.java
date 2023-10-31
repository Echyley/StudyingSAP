/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegrationomsservices.inbound;

import de.hybris.platform.core.model.ItemModel;
import de.hybris.platform.odata2services.odata.persistence.hook.PrePersistHook;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.sap.saporderexchangeoms.datahub.inbound.impl.SapOmsDataHubInboundOrderHelper;

/**
 *
 */
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SapCpiCpqOmsOrderConfirmationPersistenceHook implements PrePersistHook {

	private static final Logger LOG = LoggerFactory.getLogger(SapCpiCpqOmsOrderConfirmationPersistenceHook.class);
	private SapOmsDataHubInboundOrderHelper sapDataHubInboundOrderHelper;

	@Override
	public Optional<ItemModel> execute(final ItemModel item) {
		if (item instanceof SAPOrderModel)
		{
			LOG.info("The persistence hook SapCpiCpqOmsOrderConfirmationPersistenceHook is called!");
			final SAPOrderModel sapOrderModel = (SAPOrderModel) item;
			getSapDataHubInboundOrderHelper().processOrderConfirmationFromHub(sapOrderModel.getCode());
			return Optional.empty();
		}

		return Optional.of(item);
	}

	public SapOmsDataHubInboundOrderHelper getSapDataHubInboundOrderHelper() {
		return sapDataHubInboundOrderHelper;
	}

	public void setSapDataHubInboundOrderHelper(final SapOmsDataHubInboundOrderHelper sapDataHubInboundOrderHelper) {
		this.sapDataHubInboundOrderHelper = sapDataHubInboundOrderHelper;
	}



}
