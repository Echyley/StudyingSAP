/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omfacades.populator;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.commercefacades.order.data.OrderHistoryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.enums.DeliveryStatus;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.saps4omservices.services.SapS4OrderManagementConfigService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;


/**
 * Populates {@link OrderHistoryData} with {@link OrderModel}.
 */
public class DefaultSapS4OMOrderHistoryPopulator implements Populator<OrderModel, OrderHistoryData> {
	
	private static final Logger LOG = LoggerFactory.getLogger(DefaultSapS4OMOrderHistoryPopulator.class);
	
	public static final String PROCESSING = "processing";
	private SapS4OrderManagementConfigService sapS4OrderManagementConfigService;
	
	@Override
	public void populate(OrderModel source, OrderHistoryData target) throws ConversionException {
		if(getSapS4OrderManagementConfigService().isS4SynchronousOrderHistoryEnabled()|| getSapS4OrderManagementConfigService().isS4SynchronousOrderEnabled()) {
			LOG.debug("Synchronous order management is enabled");
			DeliveryStatus deliveryStatus = source.getDeliveryStatus();
			if(DeliveryStatus.PARTSHIPPED == deliveryStatus) {
				LOG.debug("Populate order history status display as {} when delivery status is {}", PROCESSING, deliveryStatus);
				target.setStatusDisplay(PROCESSING);
			}
			else {
				OrderStatus orderStatus= source.getStatus();
				if(orderStatus!= null) {
					LOG.debug("Populate order history status display {}", orderStatus);
					target.setStatusDisplay(source.getStatus().toString().toLowerCase(Locale.ENGLISH));
				}
			}
		}			
	}

	public SapS4OrderManagementConfigService getSapS4OrderManagementConfigService() {
		return sapS4OrderManagementConfigService;
	}

	public void setSapS4OrderManagementConfigService(SapS4OrderManagementConfigService sapS4OrderManagementConfigService) {
		this.sapS4OrderManagementConfigService = sapS4OrderManagementConfigService;
	}

}
