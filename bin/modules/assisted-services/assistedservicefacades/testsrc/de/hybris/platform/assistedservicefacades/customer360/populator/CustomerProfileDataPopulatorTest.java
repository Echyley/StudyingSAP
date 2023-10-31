/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicefacades.customer360.populator;

import static org.mockito.ArgumentMatchers.any;
import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.assistedservicefacades.customer360.CustomerProfileData;
import de.hybris.platform.assistedservicefacades.customer360.populators.CustomerProfileDataPopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
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
import java.util.Collections;

@UnitTest
public class CustomerProfileDataPopulatorTest
{
    @InjectMocks
    private CustomerProfileDataPopulator customerProfileDataPopulator;

    @Mock
    private Converter<AddressModel, AddressData> addressConverter;
    @Mock
    private CustomerAccountService customerAccountService;

    MockitoSession mockito;

    @Before
    public void setup()
    {
        customerProfileDataPopulator = new CustomerProfileDataPopulator();
        mockito = Mockito.mockitoSession().initMocks(this).startMocking();
    }

    @After
	public void cleanUp() throws Exception
	{
		mockito.finishMocking();
	}

    @Test
    public void getModelTest()
    {
        // shipment population
        final String shipPhone = "phone 1";
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final AddressModel defaultShipmentAddress = Mockito.mock(AddressModel.class);
        final AddressData addressData = Mockito.mock(AddressData.class);

        Mockito.when(customerModel.getDefaultShipmentAddress()).thenReturn(defaultShipmentAddress);
        Mockito.when(defaultShipmentAddress.getPhone1()).thenReturn(shipPhone);
        Mockito.when(customerAccountService.getCreditCardPaymentInfos(customerModel, true)).thenReturn(Collections.emptyList());

        final PaymentInfoModel paymentInfoModel = Mockito.mock(PaymentInfoModel.class);
        final AddressModel defaultBillingAddress = Mockito.mock(AddressModel.class);
        Mockito.when(paymentInfoModel.getBillingAddress()).thenReturn(defaultBillingAddress);
        Mockito.when(defaultBillingAddress.getPhone1()).thenReturn(null);
        Mockito.when(customerModel.getDefaultPaymentInfo()).thenReturn(paymentInfoModel);

        Mockito.when(addressConverter.convert(any())).thenReturn(addressData);

        final CustomerProfileData profileData = new CustomerProfileData();
        customerProfileDataPopulator.populate(customerModel, profileData);

        Assert.assertEquals(shipPhone, profileData.getPhone2());
        Assert.assertEquals(addressData, profileData.getDeliveryAddress());
    }

    @Test
    public void getModelWhenDefaultPaymentInfoAndBillingAddressIsNull()
    {
        // shipment population
        final String shipPhone = "phone 1";
        final CustomerModel customerModel = Mockito.mock(CustomerModel.class);
        final AddressModel defaultShipmentAddress = Mockito.mock(AddressModel.class);
        final AddressData addressData = Mockito.mock(AddressData.class);

        Mockito.when(customerModel.getDefaultShipmentAddress()).thenReturn(defaultShipmentAddress);
        Mockito.when(defaultShipmentAddress.getPhone1()).thenReturn(shipPhone);
        Mockito.when(customerAccountService.getCreditCardPaymentInfos(customerModel, true)).thenReturn(Collections.emptyList());

        Mockito.when(addressConverter.convert(any())).thenReturn(addressData);

        final PaymentInfoModel paymentInfoModel = Mockito.mock(PaymentInfoModel.class);
        Mockito.when(paymentInfoModel.getBillingAddress()).thenReturn(null);
        Collection<PaymentInfoModel> paymentInfos = new ArrayList<>();
        paymentInfos.add(paymentInfoModel);
        Mockito.when(customerModel.getPaymentInfos()).thenReturn(paymentInfos);
        Mockito.when(customerModel.getDefaultPaymentInfo()).thenReturn(null);

        final CustomerProfileData profileData = new CustomerProfileData();
        customerProfileDataPopulator.populate(customerModel, profileData);

        Assert.assertEquals(shipPhone, profileData.getPhone2());
        Assert.assertEquals(addressData, profileData.getDeliveryAddress());
        Assert.assertNull(profileData.getBillingAddress());
    }
}

