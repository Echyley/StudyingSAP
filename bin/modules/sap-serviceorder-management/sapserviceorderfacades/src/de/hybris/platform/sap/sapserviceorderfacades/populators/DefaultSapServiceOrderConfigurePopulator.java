/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapserviceorderfacades.populators;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import de.hybris.platform.commercefacades.basestore.SAPConfiguration.data.SAPConfigurationData;
import de.hybris.platform.commercefacades.basestore.data.BaseStoreData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.store.BaseStoreModel;

public class DefaultSapServiceOrderConfigurePopulator implements Populator<BaseStoreModel, BaseStoreData> {
	
	@Resource(name = "sapServiceScheduleTimeList")
	private List<String> scheduleTimeList;

	@Override
	public void populate(BaseStoreModel source, BaseStoreData target) throws ConversionException {
	
		SAPConfigurationData sapServiceOrderConfigData = new SAPConfigurationData();
		if(source.getSAPConfiguration()!=null) {
			sapServiceOrderConfigData.setLeadDays(source.getSAPConfiguration().getLeadDays());
		}
		sapServiceOrderConfigData.setServiceScheduleTimes(new ArrayList<String>(scheduleTimeList));
		
		target.setSapServiceOrderConfig(sapServiceOrderConfigData);
		
	}

}
