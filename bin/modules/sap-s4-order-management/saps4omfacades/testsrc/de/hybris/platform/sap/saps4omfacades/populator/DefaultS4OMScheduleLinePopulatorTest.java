/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omfacades.populator;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultS4OMScheduleLinePopulatorTest {

	@Spy
	@InjectMocks
	DefaultS4OMScheduleLinePopulator sapS4OMScheduleLinePopulator;
	
	@Test
	public void shouldPopulateScheduleLine() {
		
		final AbstractOrderEntryModel orderSource = spy(AbstractOrderEntryModel.class);
		final String scheduleLineJson = "{\"confirmedQuantity\":\" 3\", \"confirmedDate\":\"Mon Apr 11 05:30:00 +0530 2022\"}";
		orderSource.setDeliveryScheduleLines(Arrays.asList(scheduleLineJson));
		final OrderEntryData orderEntryData = new OrderEntryData();
		sapS4OMScheduleLinePopulator.populate(orderSource, orderEntryData);
		Assert.assertNotNull(orderEntryData.getDeliveryScheduleLines());
		
	}
}	
