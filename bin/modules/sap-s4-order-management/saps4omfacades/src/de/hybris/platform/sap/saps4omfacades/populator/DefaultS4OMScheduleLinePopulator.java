/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omfacades.populator;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.sap.saps4omfacades.scheduleline.data.DeliveryScheduleLineData;

/**
 * Populates {@link OrderEntryData} with {@link AbstractOrderEntryModel}.
 */
public class DefaultS4OMScheduleLinePopulator implements Populator<AbstractOrderEntryModel, OrderEntryData> {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultS4OMScheduleLinePopulator.class);

	public void populate(AbstractOrderEntryModel item, OrderEntryData target) {

		if (!CollectionUtils.isEmpty(item.getDeliveryScheduleLines())) {
      LOG.debug("Populate schedule line info in OrderData");
			try {
				List<ObjectNode> nodes = new ObjectMapper().readValue(item.getDeliveryScheduleLines().toString(),
						new TypeReference<List<ObjectNode>>() {
						});
				List<DeliveryScheduleLineData> scheduleLineList = new ArrayList<>();
				for (ObjectNode node : nodes) {
					DeliveryScheduleLineData scheduleLineData = new DeliveryScheduleLineData();
					if (node.has("confirmedDate")) {
						String dateString = node.get("confirmedDate").textValue();
						formatDate(dateString, scheduleLineData);

					}
					if (node.has("confirmedQuantity")) {
						scheduleLineData.setConfirmedQuantity(node.get("confirmedQuantity").textValue());
						LOG.debug("Schedule line confirmed quantity {}", scheduleLineData.getConfirmedQuantity());
					}
					scheduleLineList.add(scheduleLineData);
				}
				target.setDeliveryScheduleLines(scheduleLineList);
			} catch (IOException e) {
				LOG.error("Error while parsing delivery scheduleline");
			}

		}
	}

	protected void formatDate(String dateString, DeliveryScheduleLineData scheduleLineData) {
		DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
		try {
			Date date = formatter.parse(dateString);
			scheduleLineData.setConfirmedDate(date);
			LOG.debug("Schedule line confirmed date {}", scheduleLineData.getConfirmedDate());
		} catch (ParseException e) {
			LOG.error("Error while parsing date {}", dateString);
		}

	}

}
