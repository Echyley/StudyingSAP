/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.order.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.enums.CheckoutPaymentType;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BCartService;
import de.hybris.platform.b2b.services.B2BCostCenterService;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bacceleratorservices.order.B2BCommerceCartService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.basecommerce.enums.OrderEntryStatus;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.order.data.ZoneDeliveryModeData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.user.converters.populator.AddressReversePopulator;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.commerceservices.delivery.DeliveryService;
import de.hybris.platform.commerceservices.order.CommerceCheckoutService;
import de.hybris.platform.commerceservices.service.data.CommerceCheckoutParameter;
import de.hybris.platform.commerceservices.service.data.CommerceOrderResult;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.HybrisEnumValue;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.dto.CardType;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;



@UnitTest
public class DefaultB2BCheckoutFacadeTest
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BCheckoutFacadeTest.class);

	private DefaultB2BCheckoutFacade b2BCheckoutFacade;
	@Mock
	private B2BCostCenterService<B2BCostCenterModel, B2BCustomerModel> b2bCostCenterService;
	@Mock
	private Converter<B2BCostCenterModel, B2BCostCenterData> b2bCostCenterConverter;
	@Mock
	private B2BOrderService orderService;
	@Mock(extraInterfaces = {B2BCartService.class})
	private CartService cartService;
	@Mock
	private GenericDao<AbstractOrderModel> abstractOrderGenericDao;
	@Mock
	private ModelService modelService;
	@Mock
	private Converter<AddressModel, AddressData> addressConverter;
	@Mock
	private B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;
	@Mock
	private CartFacade mockCartFacade;
	@Mock
	private Converter<ZoneDeliveryModeModel, ZoneDeliveryModeData> zoneDeliveryModeConverter;
	@Mock
	private DeliveryService deliveryService;
	@Mock
	private Converter<CreditCardPaymentInfoModel, CCPaymentInfoData> creditCardPaymentInfoConverter;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private UserService userService;
	@Mock
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	@Mock
	private CommerceCheckoutService b2bCommerceCheckoutService;
	@Mock
	private EnumerationService enumerationService;
	@Mock
	private AddressReversePopulator addressReversePopulator;
	@Mock
	private CustomerAccountService customerAccountService;
	@Mock
	private Converter<OrderModel, OrderData> orderConverter;
	@Mock
	private Converter<CardType, CardTypeData> cardTypeConverter;
	@Mock
	private Converter<AbstractOrderEntryModel, OrderEntryData> orderEntryDataConverter;
	@Mock
	private B2BCommerceCartService commerceCartService;


	private DeliveryModeModel deliveryModeModel;
	private CartModel cartModel;
	private static final String COST_CENTER_CODE = "CC1";
	private static final String COST_CENTER_NAME = "Cost Center 1";
	private static final String ORDER_CODE = "0000000001";
	final CustomerModel userModel = new CustomerModel();

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		b2BCheckoutFacade = new DefaultB2BCheckoutFacade();
		b2BCheckoutFacade.setAbstractOrderGenericDao(abstractOrderGenericDao);
		b2BCheckoutFacade.setB2BOrderService(orderService);
		b2BCheckoutFacade.setCartService(cartService);
		b2BCheckoutFacade.setB2bCostCenterConverter(b2bCostCenterConverter);
		b2BCheckoutFacade.setB2bCostCenterService(b2bCostCenterService);
		b2BCheckoutFacade.setModelService(modelService);
		b2BCheckoutFacade.setAddressConverter(addressConverter);
		b2BCheckoutFacade.setPriceDataFactory(priceDataFactory);
		b2BCheckoutFacade.setUserService(userService);
		b2BCheckoutFacade.setEnumerationService(enumerationService);
		b2BCheckoutFacade.setAddressReversePopulator(addressReversePopulator);
		b2BCheckoutFacade.setCustomerAccountService(customerAccountService);
		b2BCheckoutFacade.setModelService(modelService);
		b2BCheckoutFacade.setOrderConverter(orderConverter);
		b2BCheckoutFacade.setCardTypeConverter(cardTypeConverter);
		b2BCheckoutFacade.setCartFacade(mockCartFacade);
		b2BCheckoutFacade.setCommerceCheckoutService(b2bCommerceCheckoutService);
		b2BCheckoutFacade.setCheckoutCustomerStrategy(checkoutCustomerStrategy);
		b2BCheckoutFacade.setCommerceCartService(commerceCartService);

		final CartData cartData = new CartData();
		cartModel = new CartModel();
		final AddressModel addressModel = new AddressModel();
		final CountryModel countryModel = new CountryModel();
		final CountryData countryData = new CountryData();
		countryData.setIsocode("PL");
		final B2BPaymentTypeData paymentTypeData = new B2BPaymentTypeData();
		paymentTypeData.setCode("CARD");

		addressModel.setCountry(countryModel);
		final AddressData addressData = new AddressData();
		addressData.setId("addressId");
		addressData.setTown("Koluszki");
		addressData.setCountry(countryData);
		final ZoneDeliveryModeModel zoneDeliveryModeModel = new ZoneDeliveryModeModel();
		final ZoneDeliveryModeData zoneDeliveryModeData = new ZoneDeliveryModeData();
		final CreditCardPaymentInfoModel paymentInfoModel = new CreditCardPaymentInfoModel();
		paymentInfoModel.setSubscriptionId("subsId");
		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		paymentInfoData.setId("paymentId");
		paymentInfoData.setBillingAddress(addressData);
		paymentInfoData.setExpiryMonth("5");
		paymentInfoData.setExpiryYear("2012");
		paymentInfoData.setIssueNumber("22");

		cartModel.setDeliveryAddress(addressModel);
		cartModel.setDeliveryMode(zoneDeliveryModeModel);
		cartModel.setPaymentInfo(paymentInfoModel);
		cartModel.setB2bcomments(Collections.EMPTY_LIST);
		cartModel.setEntries(List.of(new AbstractOrderEntryModel()));
		cartData.setDeliveryAddress(addressData);
		cartData.setDeliveryMode(zoneDeliveryModeData);
		cartData.setPaymentInfo(paymentInfoData);
		cartData.setPaymentType(paymentTypeData);

		final List<CountryModel> deliveryCountries = new ArrayList<CountryModel>();
		deliveryCountries.add(countryModel);

		userModel.setDefaultShipmentAddress(addressModel);
		cartModel.setUser(userModel);

		final List<HybrisEnumValue> cardTypes = new ArrayList<HybrisEnumValue>();
		cardTypes.add(CreditCardType.MASTER);

		final CommerceCheckoutParameter commerceCheckoutParameter = new CommerceCheckoutParameter();
		commerceCheckoutParameter.setEnableHooks(true);
		final CommerceOrderResult commerceOrderResult = new CommerceOrderResult();

		final OrderModel orderModel = new OrderModel();
		commerceOrderResult.setOrder(orderModel);

		given(Boolean.valueOf(cartService.hasSessionCart())).willReturn(Boolean.TRUE);
		given(addressConverter.convert(addressModel)).willReturn(addressData);
		given(zoneDeliveryModeConverter.convert(Mockito.any(ZoneDeliveryModeModel.class))).willReturn(zoneDeliveryModeData);
		//given(deliveryService.getZoneDeliveryModeValueForAbstractOrder(zoneDeliveryModeModel, cartModel)).willReturn(null);
		given(creditCardPaymentInfoConverter.convert(paymentInfoModel)).willReturn(paymentInfoData);
		//given(deliveryService.getDeliveryCountries(null)).willReturn(deliveryCountries);
		given(mockCartFacade.getSessionCart()).willReturn(cartData);
		given(Boolean.valueOf(mockCartFacade.hasSessionCart())).willReturn(Boolean.TRUE);
		given(mockCartFacade.getSessionCart()).willReturn(cartData);
		given(cartService.getSessionCart()).willReturn(cartModel);
		given(userService.getCurrentUser()).willReturn(userModel);
		//given(deliveryService.getDeliveryModeForCode(Mockito.anyString())).willReturn(deliveryModeModel);
		given(customerAccountService.getAddressForCode(Mockito.any(CustomerModel.class), Mockito.anyString()))
				.willReturn(addressModel);
		given(modelService.create(Mockito.any(Class.class))).willReturn(addressModel);
		given(enumerationService.getEnumerationValues(CreditCardType.class.getSimpleName())).willReturn(cardTypes);
		given(enumerationService.getEnumerationValues(CheckoutPaymentType._TYPECODE)).willReturn(List.of(CheckoutPaymentType.ACCOUNT, CheckoutPaymentType.CARD));
		given(checkoutCustomerStrategy.getCurrentUserForCheckout()).willReturn(userModel);
		doNothing().when(commerceCartService).calculateCartForPaymentTypeChange(any(CartModel.class));
		try
		{
			given(b2bCommerceCheckoutService.placeOrder(any(CommerceCheckoutParameter.class))).willReturn(commerceOrderResult);
		}
		catch (final InvalidCartException e)
		{
			LOG.error(e.getMessage(), e);
		}

	}

	@Test
	public void testGetCostCenters() throws Exception
	{
		final B2BCostCenterModel b2BCostCenterModel = mock(B2BCostCenterModel.class);
		given(b2BCostCenterModel.getCode()).willReturn(COST_CENTER_CODE);
		given(b2BCostCenterModel.getName()).willReturn(COST_CENTER_NAME);
		given(b2BCostCenterModel.getActive()).willReturn(Boolean.TRUE);
		final B2BCostCenterData b2bCostCenterData = mock(B2BCostCenterData.class);
		given(b2bCostCenterData.getCode()).willReturn(COST_CENTER_CODE);
		given(b2bCostCenterData.getName()).willReturn(COST_CENTER_NAME);
		given(Boolean.valueOf(b2bCostCenterData.isActive())).willReturn(Boolean.TRUE);
		given(b2bCostCenterService.getAllCostCenters()).willReturn(Collections.singletonList(b2BCostCenterModel));
		given(b2bCostCenterConverter.convert(b2BCostCenterModel)).willReturn(b2bCostCenterData);

		//	final List<B2BCostCenterData> visibleCostCenters = b2BCheckoutFacade.getVisibleCostCenters();
		//	Assert.assertNotNull(visibleCostCenters);
		//	Assert.assertEquals(visibleCostCenters.iterator().next().getCode(), b2BCostCenterModel.getCode());
	}

	@Test
	public void testSetCostCenterForEntry() throws Exception
	{
		final OrderData orderData = mock(OrderData.class);
		given(orderData.getCode()).willReturn(ORDER_CODE);

		final AbstractOrderModel orderModel = mock(OrderModel.class);
		given(orderModel.getCode()).willReturn(ORDER_CODE);

		final AbstractOrderEntryModel orderEntry = mock(OrderEntryModel.class);
		given(orderEntry.getEntryNumber()).willReturn(Integer.valueOf(1));

		final OrderEntryData orderEntryData = mock(OrderEntryData.class);
		given(orderEntryData.getEntryNumber()).willReturn(Integer.valueOf(1));
		given(orderEntryDataConverter.convert(orderEntry)).willReturn(orderEntryData);

		final B2BCostCenterModel b2BCostCenterModel = mock(B2BCostCenterModel.class);
		given(b2BCostCenterModel.getCode()).willReturn(COST_CENTER_CODE);
		given(b2BCostCenterModel.getName()).willReturn(COST_CENTER_NAME);
		given(b2BCostCenterModel.getActive()).willReturn(Boolean.TRUE);

		final B2BCostCenterData b2bCostCenterData = mock(B2BCostCenterData.class);
		given(b2bCostCenterData.getCode()).willReturn(COST_CENTER_CODE);
		given(b2bCostCenterData.getName()).willReturn(COST_CENTER_NAME);
		given(Boolean.valueOf(b2bCostCenterData.isActive())).willReturn(Boolean.TRUE);
		given(orderEntry.getCostCenter()).willReturn(b2BCostCenterModel);
		given(orderModel.getEntries()).willReturn(Collections.singletonList(orderEntry));
		given(abstractOrderGenericDao.find(Mockito.anyMap())).willReturn(Collections.singletonList(orderModel));
		given(orderService.getEntryForNumber((OrderModel) orderModel, 1)).willReturn((OrderEntryModel) orderEntry);
		given(b2bCostCenterConverter.convert(b2BCostCenterModel)).willReturn(b2bCostCenterData);
		given(b2bCostCenterService.getCostCenterForCode(COST_CENTER_CODE)).willReturn(b2BCostCenterModel);

	}

	@Test
	public void testPlaceOrder() throws InvalidCartException
	{

		b2BCheckoutFacade.placeOrder();
		verify(orderConverter).convert(Mockito.any(OrderModel.class));

	}



	@Ignore("has this ever worked ?? commented after commiting ACC-1885 ")
	@Test
	public void testScheduleOrder()
	{
		final TriggerData triggerData = new TriggerData();
		triggerData.setDay(Integer.valueOf(1));
		triggerData.setActivationTime(new Date());
		triggerData.setRelative(Boolean.TRUE);
		Assert.assertEquals(Boolean.TRUE, b2BCheckoutFacade.scheduleOrder(triggerData));

	}

	@Test
	public void testCreateCartFromOrder()
	{
		final AbstractOrderEntryModel orderEntry1 = mock(OrderEntryModel.class);
		given(orderEntry1.getQuantityStatus()).willReturn(OrderEntryStatus.DEAD);

		final AbstractOrderEntryModel orderEntry2 = mock(OrderEntryModel.class);
		given(orderEntry2.getQuantityStatus()).willReturn(OrderEntryStatus.LIVING);

		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(orderEntry1);
		entries.add(orderEntry2);

		final CustomerModel userModel = new CustomerModel();

		final OrderModel orderModel = mock(OrderModel.class);
		given(orderModel.getEntries()).willReturn(entries);
		given(orderModel.getUser()).willReturn(userModel);
		given(userService.getCurrentUser()).willReturn(userModel);

		given(orderService.getOrderForCode(ORDER_CODE)).willReturn(orderModel);
		b2BCheckoutFacade.createCartFromOrder(ORDER_CODE);

		verify(orderEntry1).setQuantityStatus(null);
		verify(orderEntry2).setQuantityStatus(null);
	}


	@Test
	public void testSetPaymentTypeSelectedForCheckout()
	{
		final String paymentType = "account";
		final AbstractOrderModel cartModel = mock(CartModel.class);
		given(cartModel.getCode()).willReturn(ORDER_CODE);
		cartModel.setPaymentType(CheckoutPaymentType.valueOf(paymentType));
		given(cartModel.getPaymentType()).willReturn(CheckoutPaymentType.ACCOUNT);

	}

	@Test
	public void shouldAuthorizeInvoicePayment()
	{
		final PaymentTransactionEntryModel entry = mock(PaymentTransactionEntryModel.class);
		cartModel.setPaymentInfo(mock(InvoicePaymentInfoModel.class));
		given(b2bCommerceCheckoutService.authorizePayment(any(CommerceCheckoutParameter.class))).willReturn(entry);
		given(entry.getTransactionStatus()).willReturn(TransactionStatus.ACCEPTED.name());
		Assert.assertTrue(b2BCheckoutFacade.authorizePayment(StringUtils.EMPTY));
	}

	@Test
	public void shouldNotAuthorizeInvoicePaymentReject()
	{
		final PaymentTransactionEntryModel entry = mock(PaymentTransactionEntryModel.class);
		cartModel.setPaymentInfo(mock(InvoicePaymentInfoModel.class));
		given(b2bCommerceCheckoutService.authorizePayment(any(CommerceCheckoutParameter.class))).willReturn(entry);
		given(entry.getTransactionStatus()).willReturn(TransactionStatus.REJECTED.name());
		Assert.assertFalse(b2BCheckoutFacade.authorizePayment(StringUtils.EMPTY));
	}

	@Test
	public void shouldNotAuthorizeInvoicePaymentNullEntry()
	{
		cartModel.setPaymentInfo(mock(InvoicePaymentInfoModel.class));
		given(b2bCommerceCheckoutService.authorizePayment(any(CommerceCheckoutParameter.class))).willReturn(null);
		Assert.assertFalse(b2BCheckoutFacade.authorizePayment(StringUtils.EMPTY));
	}

	@Test
	public void shouldUpdateCheckoutCartWithCard()
	{
		//Arrange
		final CartData cartData = new CartData();
		final B2BPaymentTypeData paymentTypeData = new B2BPaymentTypeData();
		paymentTypeData.setCode("CARD");
		cartData.setPaymentType(paymentTypeData);

		//Act
		final CartData ret = b2BCheckoutFacade.updateCheckoutCart(cartData);

		//Assert
		Assert.assertNull(ret.getCostCenter());
		Assert.assertEquals(cartData.getPaymentType().getCode(), ret.getPaymentType().getCode());
		for(AbstractOrderEntryModel order : cartModel.getEntries())
		{
			Assert.assertNull(order.getCostCenter());
		}
	}
}
