/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.populator;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer.converters.populator.CustomersListCustomerPopulator;
import de.hybris.platform.assistedserviceservices.AssistedServiceService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.site.BaseSiteService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UnitTest
public class CustomersListCustomerPopulatorTest
{
    @InjectMocks
    private CustomersListCustomerPopulator populator;

    @Mock
    private CustomerAccountService customerAccountService;
    @Mock
    private Converter<AddressModel, AddressData> addressConverter;
    @Mock
    private Converter<MediaModel, ImageData> imageConverter;
    @Mock
    private AssistedServiceService assistedServiceService;
    @Mock
    private BaseSiteService baseSiteService;

    MockitoSession mockito;

    @Before
    public void setup()
    {
        populator = new CustomersListCustomerPopulator();
        mockito = Mockito.mockitoSession().initMocks(this).startMocking();
    }

    @After
	public void cleanUp() throws Exception
	{
		mockito.finishMocking();
	}

    @Test
    public void shouldNotPopulateEmptyCart()
    {
        final AddressModel addressModel = Mockito.mock(AddressModel.class);
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final CartModel cartModel = Mockito.mock(CartModel.class);
        final CustomerData data = new CustomerData();
        final String cartId = "cardId";

        Mockito.lenient().when(cartModel.getCode()).thenReturn(cartId);
        Mockito.when(customerAccountService.getDefaultAddress(customerModel)).thenReturn(addressModel);
        Mockito.when(assistedServiceService.getLatestModifiedCart(customerModel)).thenReturn(cartModel);

        populator.populate(customerModel, data);

        Assert.assertEquals(null, data.getLatestCartId());
    }

    @Test
    public void shouldNotPopulateNonEmptyCart()
    {
        final AddressModel addressModel = Mockito.mock(AddressModel.class);
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final CartModel cartModel = Mockito.mock(CartModel.class);
        final AbstractOrderEntryModel entry = Mockito.mock(AbstractOrderEntryModel.class);
        final List<AbstractOrderEntryModel> list = new ArrayList<AbstractOrderEntryModel>();
        list.add(entry);
        final CustomerData data = new CustomerData();
        final String cartId = "cardId";

        Mockito.when(customerAccountService.getDefaultAddress(customerModel)).thenReturn(addressModel);
        Mockito.when(assistedServiceService.getLatestModifiedCart(customerModel)).thenReturn(cartModel);
        Mockito.when(cartModel.getEntries()).thenReturn(list);
        Mockito.when(cartModel.getCode()).thenReturn(cartId);

        populator.populate(customerModel, data);

        Assert.assertEquals(data.getLatestCartId(), cartId);
    }

    @Test
    public void shouldNotRaiseErrorWhenOrderModelHasNoSite()
    {
        final AddressModel addressModel = Mockito.mock(AddressModel.class);
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final CartModel cartModel = Mockito.mock(CartModel.class);
        final AbstractOrderEntryModel entry = Mockito.mock(AbstractOrderEntryModel.class);
        final List<AbstractOrderEntryModel> list = new ArrayList<AbstractOrderEntryModel>();
        list.add(entry);
        final CustomerData data = new CustomerData();
        final String cartId = "cardId";

        final Collection<OrderModel> orders = new ArrayList<>();
        final OrderModel order = Mockito.mock(OrderModel.class);
        orders.add(order);
        BaseSiteModel currentBaseSite = Mockito.mock(BaseSiteModel.class);

        // ensure when order.getSite() return null, we still can return cartModel
        Mockito.when(customerModel.getOrders()).thenReturn(orders);
        Mockito.when(order.getSite()).thenReturn(null);
        Mockito.when(baseSiteService.getCurrentBaseSite()).thenReturn(currentBaseSite);
        Mockito.when(currentBaseSite.getUid()).thenReturn("fakeBasesite");

        Mockito.when(customerAccountService.getDefaultAddress(customerModel)).thenReturn(addressModel);
        Mockito.when(assistedServiceService.getLatestModifiedCart(customerModel)).thenReturn(cartModel);
        Mockito.when(cartModel.getEntries()).thenReturn(list);
        Mockito.when(cartModel.getCode()).thenReturn(cartId);

        populator.populate(customerModel, data);

        Assert.assertEquals(data.getLatestCartId(), cartId);
        Assert.assertEquals(data.getHasOrder(), false);
    }
}
