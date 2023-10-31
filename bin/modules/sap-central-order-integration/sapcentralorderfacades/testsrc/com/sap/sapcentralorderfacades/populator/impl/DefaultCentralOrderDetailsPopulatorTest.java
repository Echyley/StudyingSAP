/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.sapcentralorderfacades.populator.impl;

import static com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants.CATEGORY_ONETIME;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CCPaymentInfoData;
import de.hybris.platform.commercefacades.order.data.ConsignmentData;
import de.hybris.platform.commercefacades.order.data.ConsignmentEntryData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Address;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.CentralOrderDetailsResponse;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Customer;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.ExternalSystemReference;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.ItemPrice;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.ItemPriceAspectData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Market;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Metadata;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.OrderItem;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PaymentData;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Person;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PhysicalItemPrice;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PrecedingDocument;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.PriceTotals;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Product;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Quantity;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Tax;
import de.hybris.platform.sap.sapcentralorderservices.pojo.v1.Total;
import de.hybris.platform.sap.sapmodel.enums.SAPOrderStatus;
import de.hybris.platform.sap.sapmodel.model.SAPOrderModel;
import de.hybris.platform.servicelayer.user.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.sap.sapcentralorderfacades.constants.SapcentralorderfacadesConstants;
import com.sap.sapcentralorderservices.services.CentralOrderService;
import com.sap.sapcentralorderservices.services.config.CoConfigurationService;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultCentralOrderDetailsPopulatorTest
{


	DefaultCentralOrderDetailsPopulator defaultCentralOrderDetailsPopulator = new DefaultCentralOrderDetailsPopulator();


	CoConfigurationService configurationService = mock(CoConfigurationService.class);


	UserService userService = mock(UserService.class);


	CheckoutCustomerStrategy checkoutCustomerStrategy = mock(CheckoutCustomerStrategy.class);


	CentralOrderService centralOrderService = mock(CentralOrderService.class);


	PriceDataFactory priceDataFactory = mock(PriceDataFactory.class);


	I18NFacade i18NFacade = mock(I18NFacade.class);

	CountryData countryData;

	final String status = OrderStatus.COMPLETED.toString();

	String id = "Id";

	String precedingDocumentNumber = "precedingDocumentNumber";

	String createdAt = "1970-01-01";

	double finalAmount = 53600.0;
	double effectiveAmount = 50000.0;
	double originalAmount = 55000.0;
	double totalAmount = 53500.0;

	String currencyISO = "currencyISO";

	//Person details
	String academicTitle = "academicTitle";
	String firstName = "firstName";
	String lastName = "lastName";

	//address details
	String houseNumber = "dummyHouseNumber";
	String city = "dummyCity";
	String postalCode = "dummyPostalCode";
	String phone = "9999999999";

	@Test
	public void test()
	{



		final OrderModel source = new OrderModel();
		source.setCode("code");

		final OrderData target = new OrderData();
		final CCPaymentInfoData paymentInfoData = new CCPaymentInfoData();
		target.setPaymentInfo(paymentInfoData);

		final Map orderStatusDisplayMap = new HashMap();
		orderStatusDisplayMap.put(status, status);

		when(configurationService.isCoActive()).thenReturn(true);
		when(configurationService.getCoSourceSystemId()).thenReturn("coSourceSystemId");

		final CustomerModel userModel = new CustomerModel();
		when(userService.getCurrentUser()).thenReturn(userModel);

		when(checkoutCustomerStrategy.isAnonymousCheckout()).thenReturn(true);

		final SAPOrderModel sapOrderModel = new SAPOrderModel();
		sapOrderModel.setSapOrderStatus(SAPOrderStatus.SENT_TO_ERP);

		final Set<SAPOrderModel> sapOrderModels = new HashSet<>();
		sapOrderModels.add(sapOrderModel);

		source.setSapOrders(sapOrderModels);

		final Tax tax = new Tax();
		tax.setTotalAmount(totalAmount);

		//country data
		countryData = new CountryData();
		countryData.setIsocode("isocode");
		when(i18NFacade.getCountryForIsocode("country")).thenReturn(countryData);

		//Shipping Address
		final Address addressShipTo = new Address();
		addressShipTo.setAddressType(SapcentralorderfacadesConstants.ADDRESS_TYPE_SHIPTO);
		addressShipTo.setHouseNumber(houseNumber);
		addressShipTo.setCity(city);
		addressShipTo.setPostalCode(postalCode);
		addressShipTo.setPhone(phone);
		addressShipTo.setCountry("country");

		//Payment data for bill address
		final PaymentData paymentData = new PaymentData();
		paymentData.setMethod(SapcentralorderfacadesConstants.PAYMENT_TYPE_CARD);
		final List<PaymentData> paymentDataList = new ArrayList();
		paymentDataList.add(paymentData);

		//Billing Address
		final Address addressBillTo = new Address();
		addressBillTo.setAddressType(SapcentralorderfacadesConstants.ADDRESS_TYPE_BILLTO);
		addressBillTo.setHouseNumber(houseNumber);
		addressBillTo.setCity(city);
		addressBillTo.setPostalCode(postalCode);
		addressBillTo.setPhone(phone);
		addressBillTo.setCountry("country");

		//Setting Addresses
		final List<Address> addresses = new ArrayList<Address>();
		addresses.add(addressShipTo);
		addresses.add(addressBillTo);

		//Person
		final Person person = new Person();
		person.setAcademicTitle(academicTitle);
		person.setFirstName(firstName);
		person.setLastName(lastName);

		//Customer
		final Customer customer = new Customer();
		customer.setAddresses(addresses);
		customer.setPerson(person);

		//setting consignments to target
		final ProductData productData = new ProductData();
		productData.setCode(id);

		final OrderEntryData orderEntryData = new OrderEntryData();
		orderEntryData.setProduct(productData);

		final ConsignmentEntryData consignmentEntryData = new ConsignmentEntryData();
		consignmentEntryData.setOrderEntry(orderEntryData);

		final List<ConsignmentEntryData> entries = new ArrayList<>();
		entries.add(consignmentEntryData);

		final ConsignmentData consignmentData = new ConsignmentData();
		consignmentData.setEntries(entries);

		final List<ConsignmentData> consignments = new ArrayList<>();
		consignments.add(consignmentData);
		target.setConsignments(consignments);

		final Total total = new Total();
		total.setTax(tax);
		total.setEffectiveAmount(effectiveAmount);
		total.setFinalAmount(finalAmount);
		total.setEffectiveAmount(effectiveAmount);
		total.setOriginalAmount(originalAmount);

		final PriceTotals priceTotal = new PriceTotals();
		priceTotal.setCategory(CATEGORY_ONETIME);
		priceTotal.setFinalAmount(finalAmount);
		priceTotal.setEffectiveAmount(effectiveAmount);
		priceTotal.setOriginalAmount(originalAmount);
		priceTotal.setTotal(total);


		final List<PriceTotals> priceTotalsList = new ArrayList<>();
		priceTotalsList.add(priceTotal);

		final PhysicalItemPrice physicalItemPrice = new PhysicalItemPrice();
		physicalItemPrice.setPriceTotals(priceTotalsList);

		final ItemPriceAspectData itemPriceAspectData = new ItemPriceAspectData();
		itemPriceAspectData.setPhysicalItemPrice(physicalItemPrice);

		final ItemPrice itemPrice = new ItemPrice();
		itemPrice.setItemPriceAspectData(itemPriceAspectData);

		final ExternalSystemReference ref = new ExternalSystemReference();
		ref.setExternalId(id);
		ref.setExternalNumber(precedingDocumentNumber);
		final List<ExternalSystemReference> refList = List.of(ref);
		final Product product = new Product();
		//product.setId(id);
		product.setExternalSystemReferences(refList);

		final Quantity quantity = new Quantity();
		quantity.setValue(2);

		final OrderItem orderItem = new OrderItem();
		orderItem.setItemPrice(itemPrice);
		orderItem.setProduct(product);
		orderItem.setQuantity(quantity);

		final List<OrderItem> orderItems = new ArrayList<>();
		orderItems.add(orderItem);

		final Metadata metaData = new Metadata();
		metaData.setCreatedAt(createdAt);

		final PrecedingDocument precedingDocument = new PrecedingDocument();
		precedingDocument.setExternalSystemReference(ref);
		//		precedingDocument.setPrecedingDocumentNumber(precedingDocumentNumber);

		final Market market = new Market();
		market.setCurrency("currency");

		//price data for original amount
		final PriceData originalAmountPriceData = new PriceData();
		originalAmountPriceData.setPriceType(PriceDataType.BUY);
		originalAmountPriceData.setValue(BigDecimal.valueOf(originalAmount));
		originalAmountPriceData.setCurrencyIso(currencyISO);
		when(priceDataFactory.create(eq(PriceDataType.BUY), eq(BigDecimal.valueOf(originalAmount)), anyString()))
				.thenReturn(originalAmountPriceData);

		//price data for effective amount
		final PriceData effectiveAmountPriceData = new PriceData();
		effectiveAmountPriceData.setPriceType(PriceDataType.BUY);
		effectiveAmountPriceData.setValue(BigDecimal.valueOf(effectiveAmount));
		effectiveAmountPriceData.setCurrencyIso(currencyISO);
		when(priceDataFactory.create(eq(PriceDataType.BUY), eq(BigDecimal.valueOf(effectiveAmount)), anyString()))
				.thenReturn(effectiveAmountPriceData);

		//price data for discount
		final PriceData discountPriceData = new PriceData();
		discountPriceData.setPriceType(PriceDataType.BUY);
		discountPriceData.setValue(BigDecimal.valueOf(originalAmount - effectiveAmount));
		discountPriceData.setCurrencyIso(currencyISO);
		when(priceDataFactory.create(eq(PriceDataType.BUY), eq(BigDecimal.valueOf(originalAmount - effectiveAmount)), anyString()))
				.thenReturn(discountPriceData);

		//price data for total tax price
		final PriceData totalTaxPriceData = new PriceData();
		totalTaxPriceData.setPriceType(PriceDataType.BUY);
		totalTaxPriceData.setValue(BigDecimal.valueOf(totalAmount));
		totalTaxPriceData.setCurrencyIso(currencyISO);
		when(priceDataFactory.create(eq(PriceDataType.BUY), eq(BigDecimal.valueOf(totalAmount)), anyString()))
				.thenReturn(totalTaxPriceData);

		//price data for total price with tax
		final PriceData totalPriceWithTaxPriceData = new PriceData();
		totalPriceWithTaxPriceData.setPriceType(PriceDataType.BUY);
		totalPriceWithTaxPriceData.setValue(BigDecimal.valueOf(finalAmount));
		totalPriceWithTaxPriceData.setCurrencyIso(currencyISO);
		when(priceDataFactory.create(eq(PriceDataType.BUY), eq(BigDecimal.valueOf(finalAmount)), anyString()))
				.thenReturn(totalPriceWithTaxPriceData);

		final CentralOrderDetailsResponse centralOrderDetails = new CentralOrderDetailsResponse();
		centralOrderDetails.setStatus(status);
		centralOrderDetails.setId(id);
		centralOrderDetails.setPrecedingDocument(precedingDocument);
		centralOrderDetails.setMetadata(metaData);
		centralOrderDetails.setPrices(priceTotalsList);
		centralOrderDetails.setMarket(market);
		centralOrderDetails.setCustomer(customer);
		centralOrderDetails.setPayment(paymentDataList);
		centralOrderDetails.setOrderItems(orderItems);

		final ResponseEntity<CentralOrderDetailsResponse> centralOrderDetailsResponse = new ResponseEntity(
				centralOrderDetails, HttpStatus.OK);

		when(centralOrderService.getCentalOrderDetailsForGuid(any(CustomerModel.class), anyString(), anyString()))
				.thenReturn(centralOrderDetailsResponse);

		defaultCentralOrderDetailsPopulator.setOrderStatusDisplayMap(orderStatusDisplayMap);

		//execute
		defaultCentralOrderDetailsPopulator.setConfigurationService(configurationService);
		defaultCentralOrderDetailsPopulator.setUserService(userService);
		defaultCentralOrderDetailsPopulator.setCheckoutCustomerStrategy(checkoutCustomerStrategy);
		defaultCentralOrderDetailsPopulator.setCentralOrderService(centralOrderService);
		defaultCentralOrderDetailsPopulator.setPriceDataFactory(priceDataFactory);
		defaultCentralOrderDetailsPopulator.setI18NFacade(i18NFacade);
		defaultCentralOrderDetailsPopulator.populate(source, target);

		//verifying order details
		Assert.assertEquals(id, target.getGuid());
		Assert.assertEquals(OrderStatus.valueOf(status), target.getStatus());
		Assert.assertEquals(status, target.getStatusDisplay());
		Assert.assertEquals(precedingDocumentNumber, target.getCode());
		Assert.assertNotNull(target.getCreated());

		//Verifying Order total
		Assert.assertEquals(effectiveAmountPriceData, target.getSubTotal());
		Assert.assertEquals(discountPriceData, target.getTotalDiscounts());
		Assert.assertEquals(totalTaxPriceData, target.getTotalTax());
		Assert.assertEquals(totalPriceWithTaxPriceData, target.getTotalPriceWithTax());
		Assert.assertEquals(totalPriceWithTaxPriceData, target.getTotalPrice());
		Assert.assertNull(target.getDeliveryCost());

		//Verifying shipping address
		Assert.assertEquals(firstName, target.getDeliveryAddress().getFirstName());
		Assert.assertEquals(lastName, target.getDeliveryAddress().getLastName());
		Assert.assertEquals(academicTitle, target.getDeliveryAddress().getTitleCode());
		Assert.assertEquals(houseNumber, target.getDeliveryAddress().getLine1());
		Assert.assertNull(target.getDeliveryAddress().getLine2());
		Assert.assertEquals(city, target.getDeliveryAddress().getTown());
		Assert.assertEquals(postalCode, target.getDeliveryAddress().getPostalCode());
		Assert.assertEquals(phone, target.getDeliveryAddress().getPhone());
		Assert.assertEquals(countryData, target.getDeliveryAddress().getCountry());
		Assert.assertTrue(target.getDeliveryAddress().isShippingAddress());
		Assert.assertFalse(target.getDeliveryAddress().isBillingAddress());
		Assert.assertNull(target.getDeliveryMode());

		//Verifying billing Address
		Assert.assertEquals(firstName, target.getPaymentInfo().getBillingAddress().getFirstName());
		Assert.assertEquals(lastName, target.getPaymentInfo().getBillingAddress().getLastName());
		Assert.assertEquals(academicTitle, target.getPaymentInfo().getBillingAddress().getTitleCode());
		Assert.assertEquals(houseNumber, target.getPaymentInfo().getBillingAddress().getLine1());
		Assert.assertNull(target.getPaymentInfo().getBillingAddress().getLine2());
		Assert.assertEquals(city, target.getPaymentInfo().getBillingAddress().getTown());
		Assert.assertEquals(postalCode, target.getPaymentInfo().getBillingAddress().getPostalCode());
		Assert.assertEquals(phone, target.getPaymentInfo().getBillingAddress().getPhone());
		Assert.assertTrue(target.getPaymentInfo().getBillingAddress().isBillingAddress());
		Assert.assertFalse(target.getPaymentInfo().getBillingAddress().isShippingAddress());
		Assert.assertEquals(countryData, target.getPaymentInfo().getBillingAddress().getCountry());

		//Verifying target consignments
		Assert.assertEquals(Long.valueOf(2), target.getConsignments().get(0).getEntries().get(0).getOrderEntry().getQuantity());
		Assert.assertEquals(BigDecimal.valueOf(finalAmount),
				target.getConsignments().get(0).getEntries().get(0).getOrderEntry().getTotalPrice().getValue());
		Assert.assertEquals(originalAmountPriceData,
				target.getConsignments().get(0).getEntries().get(0).getOrderEntry().getBasePrice());

	}

}
