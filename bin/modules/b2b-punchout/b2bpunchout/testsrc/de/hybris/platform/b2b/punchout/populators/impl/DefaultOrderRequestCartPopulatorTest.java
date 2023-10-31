/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.punchout.populators.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.punchout.util.CXmlDateUtil;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;

import org.cxml.Address;
import org.cxml.BillTo;
import org.cxml.Money;
import org.cxml.OrderRequestHeader;
import org.cxml.ShipTo;
import org.cxml.Shipping;
import org.cxml.Tax;
import org.cxml.Total;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderRequestCartPopulatorTest
{
	private static final String ORDER_ID = "1234";
	private static final String MONEY_VALUE = "10";
	private static final String CURRENCY_CODE = "EUR";

	@InjectMocks
	private DefaultOrderRequestCartPopulator defaultOrderRequestCartPopulator;

	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CXmlDateUtil cXmlDateUtil;
	@Mock
	private Converter<Address, AddressModel> addressModelConverter;
	@Mock
	private Populator<Tax, Collection<TaxValue>> taxValuePopulator;

	@Mock
	private AddressModel addressModel;
	@Mock
	private CurrencyModel currencyModel;

	private OrderRequestHeader source;
	private CartModel cartModel;

	@Before
	public void setUp()
	{
		cartModel = spy(CartModel.class);
		source = new OrderRequestHeader();
		source.setOrderID(ORDER_ID);

		final Address address = new Address();

		final ShipTo shipTo = new ShipTo();
		shipTo.setAddress(address);
		source.setShipTo(shipTo);

		final BillTo billTo = new BillTo();
		billTo.setAddress(address);
		source.setBillTo(billTo);

		final Shipping shipping = new Shipping();
		Money money = new Money();
		money.setvalue(MONEY_VALUE);
		money.setCurrency(CURRENCY_CODE);
		shipping.setMoney(money);
		source.setShipping(shipping);

		final Total total = new Total();
		total.setMoney(money);
		source.setTotal(total);

		final TaxValue taxValue = new TaxValue("VAT", Double.parseDouble(money.getvalue()), true, money.getCurrency());
		final Collection<TaxValue> taxValues = new ArrayList<>();
		taxValues.add(taxValue);

		when(addressModelConverter.convert(address)).thenReturn(addressModel);
		when(cartModel.getTotalTaxValues()).thenReturn(taxValues);
		when(commonI18NService.getCurrency(CURRENCY_CODE)).thenReturn(currencyModel);
	}

	@Test
	public void testUnsupportedMethodException()
	{
		source.setType("old");
		assertThatThrownBy(() -> defaultOrderRequestCartPopulator.populate(source, cartModel))
			.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void testPopulation()
	{
		defaultOrderRequestCartPopulator.populate(source, cartModel);

		assertThat(cartModel).hasFieldOrPropertyWithValue("purchaseOrderNumber", source.getOrderID())
							 .hasFieldOrPropertyWithValue("deliveryAddress", addressModel)
							 .hasFieldOrPropertyWithValue("paymentAddress", addressModel)
							 .hasFieldOrPropertyWithValue("deliveryCost", Double.valueOf(MONEY_VALUE))
							 .hasFieldOrPropertyWithValue("totalPrice", Double.valueOf(MONEY_VALUE))
							 .hasFieldOrPropertyWithValue("currency", currencyModel);
	}
}
