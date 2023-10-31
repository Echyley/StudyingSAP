/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.facades.converters.populator;

import org.springframework.util.Assert;

import com.sap.retail.oaa.commerce.facades.schedulelines.OaaScheduleLineData;
import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.model.model.ScheduleLineModel;

import de.hybris.platform.commercefacades.order.converters.populator.OrderEntryPopulator;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.converters.Converters;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

/**
 * Populator for OrderEntryModel to OrderEntryData conversion Add Oaa Specific
 * Fields to OrderEntryContributor
 */
public class DefaultOaaOrderEntryPopulator extends OrderEntryPopulator {

	private Converter<ScheduleLineModel, OaaScheduleLineData> scheduleLineConverter;
	private CommonUtils commonUtils;

	@Override
	public void populate(final AbstractOrderEntryModel source, final OrderEntryData target) {
		super.populate(source, target);

		if (getCommonUtils().isCAREnabled() || getCommonUtils().isCOSEnabled()) {
			Assert.notNull(source, "Parameter source cannot be null.");
			Assert.notNull(target, "Parameter target cannot be null.");

			if (source.getScheduleLines() != null) {
				target.setOaaScheduleLines(
						Converters.convertAll(source.getScheduleLines(), getScheduleLineConverter()));
			}

			if (source.getSapSource() != null) {
				target.setSapSource(getPointOfServiceConverter().convert(source.getSapSource()));
			}

			if (source.getSapVendor() != null) {
				target.setSapVendor(source.getSapVendor());
			}
		}
	}

	public Converter<ScheduleLineModel, OaaScheduleLineData> getScheduleLineConverter() {
		return scheduleLineConverter;
	}

	public void setScheduleLineConverter(
			final Converter<ScheduleLineModel, OaaScheduleLineData> scheduleLineConverter) {
		this.scheduleLineConverter = scheduleLineConverter;
	}

	/**
	 * @return the commonUtils
	 */
	public CommonUtils getCommonUtils() {
		return commonUtils;
	}

	/**
	 * @param commonUtils the commonUtils to set
	 */
	public void setCommonUtils(CommonUtils commonUtils) {
		this.commonUtils = commonUtils;
	}
}
