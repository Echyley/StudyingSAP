/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.warehousingfacades.order.converters.populator;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class WarehousingConsignmentEntrySourceOrderEntryPopulatorTest
{
	@Mock
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryConverter;
	@Spy
	private ConsignmentEntryModel source;

	private final OrderEntryModel orderEntryModel = mock(OrderEntryModel.class);
	private final OrderEntryData orderEntryData = mock(OrderEntryData.class);
	private ConsignmentEntryData result;
	private WarehousingConsignmentEntrySourceOrderEntryPopulator warehousingConsignmentEntrySourceOrderEntryPopulator;

	@Before
	public void setUp()
	{
		warehousingConsignmentEntrySourceOrderEntryPopulator = new WarehousingConsignmentEntrySourceOrderEntryPopulator();
		warehousingConsignmentEntrySourceOrderEntryPopulator.setOrderEntryConverter(orderEntryConverter);
		given(orderEntryConverter.convert(orderEntryModel)).willReturn(orderEntryData);
		result = new ConsignmentEntryData();
	}

	@Test
	public void testPopulate()
	{
		given(source.getSourceOrderEntry()).willReturn(orderEntryModel);
		warehousingConsignmentEntrySourceOrderEntryPopulator.populate(source, result);
		Assert.assertEquals(result.getOrderEntry(), orderEntryData);
	}

	@Test
	public void testWhenSourceOrderEntryIsNullPopulate()
	{
		given(source.getSourceOrderEntry()).willReturn(null);
		warehousingConsignmentEntrySourceOrderEntryPopulator.populate(source, result);
		Assert.assertNull(result.getOrderEntry());
	}
}
