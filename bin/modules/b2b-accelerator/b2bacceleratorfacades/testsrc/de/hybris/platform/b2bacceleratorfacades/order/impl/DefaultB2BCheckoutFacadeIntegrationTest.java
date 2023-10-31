/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bacceleratorfacades.order.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BOrderService;
import de.hybris.platform.b2bacceleratorfacades.checkout.data.PlaceOrderData;
import de.hybris.platform.b2bacceleratorfacades.exception.EntityValidationException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BPaymentTypeData;
import de.hybris.platform.b2bacceleratorfacades.order.data.ScheduledCartData;
import de.hybris.platform.b2bacceleratorfacades.order.data.TriggerData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.basecommerce.util.BaseCommerceBaseTest;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.OrderQuoteDiscountValuesAccessor;
import de.hybris.platform.commerceservices.order.impl.DefaultCommerceCheckoutService;
import de.hybris.platform.core.enums.CreditCardType;
import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.payment.CreditCardPaymentInfoModel;
import de.hybris.platform.core.model.order.payment.InvoicePaymentInfoModel;
import de.hybris.platform.core.model.order.payment.PaymentInfoModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import de.hybris.platform.deliveryzone.model.ZoneDeliveryModeModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.payment.commands.factory.CommandFactoryRegistry;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.impl.DefaultPaymentServiceImpl;
import de.hybris.platform.payment.methods.impl.DefaultCardPaymentServiceImpl;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.enums.ProcessState;
import de.hybris.platform.processengine.model.BusinessProcessModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.model.ServicelayerJobModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.Utilities;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;


import static de.hybris.platform.b2b.enums.CheckoutPaymentType.CARD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@ContextConfiguration(locations =
{ "classpath:/payment-spring-test.xml" })
@IntegrationTest
public class DefaultB2BCheckoutFacadeIntegrationTest extends BaseCommerceBaseTest
{
	private static final Logger LOG = Logger.getLogger(DefaultB2BCheckoutFacadeIntegrationTest.class);

	@Resource
	private ModelService modelService;
	@Resource
	private CartService cartService;
	@Resource
	private UserService userService;
	@Resource
	private CommandFactoryRegistry mockupCommandFactoryRegistry;
	@Resource
	private CommandFactoryRegistry commandFactoryRegistry;
	@Resource
	private DefaultCardPaymentServiceImpl cardPaymentService;
	@Resource
	private DefaultPaymentServiceImpl paymentService;
	@Resource
	private DefaultCommerceCheckoutService commerceCheckoutService;
	@Resource
	private DefaultB2BCheckoutFacade b2bCheckoutFacade;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private B2BOrderService b2bOrderService;
	@Resource
	private OrderQuoteDiscountValuesAccessor orderQuoteDiscountValuesAccessor;

	@Resource
	private CommonI18NService commonI18NService;

	@Before
	public void beforeTest() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		importCsv("/b2bacceleratorfacades/test/testOrganizations.csv", "utf-8");
		importCsv("/b2bacceleratorfacades/test/testB2BCommerceCart.csv", "utf-8");
		importCsv("/b2bacceleratorfacades/test/testCommerceComments.impex", "utf-8");
		importCsv("/b2bacceleratorfacades/test/user-orders.impex", "utf-8");

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("b2bstoretemplate"), false);

		final CartModel modelByExample = new CartModel();
		modelByExample.setCode("dc_shhCart_b2baf");
		final CartModel cart = flexibleSearchService.getModelByExample(modelByExample);
		Assert.assertNotNull("dc_shhCart_b2baf cart.code should have been found", cart);
		cartService.setSessionCart(cart);
		userService.setCurrentUser(cart.getUser());

		if (flexibleSearchService.search(
				"SELECT {" + ServicelayerJobModel.PK + "} FROM {" + ServicelayerJobModel._TYPECODE + "} WHERE " + "{"
						+ ServicelayerJobModel.SPRINGID + "}=?springid",
				Collections.singletonMap("springid", "b2bAcceleratorCartToOrderJob")).getResult().isEmpty())
		{
			final ServicelayerJobModel servicelayerJobModel = modelService.create(ServicelayerJobModel.class);
			servicelayerJobModel.setCode("b2bAcceleratorCartToOrderJob");
			servicelayerJobModel.setSpringId("b2bAcceleratorCartToOrderJob");
			modelService.save(servicelayerJobModel);
		}

		// inject a mock payment provider
		cardPaymentService.setCommandFactoryRegistry(mockupCommandFactoryRegistry);
		paymentService.setCardPaymentService(cardPaymentService);
		commerceCheckoutService.setPaymentService(paymentService);
		commerceCheckoutService.setBaseSiteService(baseSiteService);
	}

	@After
	public void tearDown() throws Exception
	{
		cardPaymentService.setCommandFactoryRegistry(commandFactoryRegistry);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreateCartFromNullOrder()
	{
		b2bCheckoutFacade.createCartFromOrder("123");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotCreateCartFromOrderNotBelongToCurrentUser()
	{
		b2bCheckoutFacade.createCartFromOrder("testOrder1");
	}

	@Test
	public void shouldCreateCartFromOrder()
	{
		final String orderCode = "testOrder1";
		final OrderModel order = b2bOrderService.getOrderForCode(orderCode);
		userService.setCurrentUser(order.getUser());

		final AddressModel originalPaymentAddress = order.getPaymentAddress();
		final AddressModel originalDeliveryAddress = order.getDeliveryAddress();
		final PaymentInfoModel paymentInfoModel = getPaymentInfoModelForClonedCart(order);
		Assert.assertNotNull("B2bcomments", order.getB2bcomments());
		Assert.assertNotNull("Workflow", order.getWorkflow());
		Assert.assertNotNull("PermissionResults", order.getPermissionResults());
		Assert.assertNotNull("Name", order.getName());
		Assert.assertNotNull("Description", order.getDescription());
		Assert.assertNotNull("ExpirationTime", order.getExpirationTime());
		Assert.assertNotNull("quoteReference", order.getQuoteReference());
		Assert.assertNotNull("Comments", order.getComments());
		for (final AbstractOrderEntryModel orderEntry : order.getEntries())
		{
			Assert.assertNotNull("Order entry comment. Entry#:" + orderEntry.getEntryNumber(), orderEntry.getComments());
			Assert.assertEquals("Order entry comment. Entry#:" + orderEntry.getEntryNumber(), 1, orderEntry.getComments().size());
		}
		List<DiscountValue> quoteDiscountValues = orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(order);
		Assert.assertNotNull("QuoteDiscountValues", quoteDiscountValues);
		Assert.assertEquals("QuoteDiscountValues size", 1, quoteDiscountValues.size());
		Assert.assertEquals("QuoteDiscountValues quote discount code", CommerceServicesConstants.QUOTE_DISCOUNT_CODE,
				quoteDiscountValues.get(0).getCode());

		b2bCheckoutFacade.createCartFromOrder(orderCode);
		final CartModel cart = cartService.getSessionCart();
		Assert.assertNotNull("cart is null", cart);
		Assert.assertEquals("Cart status", OrderStatus.CREATED, cart.getStatus());
		Assert.assertEquals("PaymentAddress", originalPaymentAddress.getOriginal(), cart.getPaymentAddress());
		Assert.assertEquals("DeliveryAddress", originalDeliveryAddress.getOriginal(), cart.getDeliveryAddress());
		Assert.assertEquals("PaymentInfo", paymentInfoModel, cart.getPaymentInfo());
		Assert.assertNotNull("B2bcomments", cart.getB2bcomments());
		Assert.assertEquals("B2bcomments size", 0, cart.getB2bcomments().size());
		Assert.assertNull("Workflow", cart.getWorkflow());
		Assert.assertNotNull("PermissionResults", cart.getPermissionResults());
		Assert.assertEquals("PermissionResults size", 0, cart.getPermissionResults().size());
		Assert.assertNull("Name", cart.getName());
		Assert.assertNull("Description", cart.getDescription());
		Assert.assertNull("ExpirationTime", cart.getExpirationTime());
		Assert.assertNull("quoteReference", cart.getQuoteReference());
		Assert.assertNotNull("Comments", cart.getComments());
		Assert.assertEquals("Comments size", 0, cart.getComments().size());
		for (final AbstractOrderEntryModel cartEntry : cart.getEntries())
		{
			Assert.assertNotNull("Cart entry comment. Entry#:" + cartEntry.getEntryNumber(), cartEntry.getComments());
			Assert.assertEquals("Cart entry comments size. Entry#:" + cartEntry.getEntryNumber(), 0, cartEntry.getComments().size());
		}
		quoteDiscountValues = orderQuoteDiscountValuesAccessor.getQuoteDiscountValues(cart);
		Assert.assertNotNull("QuoteDiscountValues", quoteDiscountValues);
		Assert.assertEquals("QuoteDiscountValues size", 0, quoteDiscountValues.size());
	}

	protected PaymentInfoModel getPaymentInfoModelForClonedCart(final OrderModel order)
	{
		final PaymentInfoModel paymentInfoModel = order.getPaymentInfo();
		return (paymentInfoModel != null && paymentInfoModel instanceof InvoicePaymentInfoModel)
				? (PaymentInfoModel) paymentInfoModel.getOriginal() : null;
	}


	@Test
	public void testScheduleOrder() throws Exception
	{
		Assert.assertNotNull("cart not null", cartService.getSessionCart());
		Assert.assertNotNull("user not null", cartService.getSessionCart().getUser());
		Assert.assertEquals("DC S HH", cartService.getSessionCart().getUser().getUid());
		final TriggerData triggerData = new TriggerData();
		triggerData.setDay(Integer.valueOf(1));
		triggerData.setActivationTime(DateUtils.addDays(new Date(), 1));
		triggerData.setRelative(Boolean.TRUE);
		triggerData.setDaysOfWeek(Collections.singletonList(DayOfWeek.FRIDAY));
		triggerData.setMonth(Integer.valueOf(1));
		triggerData.setWeekInterval(Integer.valueOf(1));
		final ScheduledCartData scheduledCartData = b2bCheckoutFacade.scheduleOrder(triggerData);
		Assert.assertNotNull(scheduledCartData);

		// scheduleOrder method triggers a replenishmentOrderPlacedEmailProcess we want to try and wait for it to complete otherwise
		// the hybris test framwork will start removing items created during the test and the Process will encounter de.hybris.platform.servicelayer.exceptions.ModelSavingException: Entity not found
		TestUtils.disableFileAnalyzer("GenerateEmailAction logs ERROR when content catalog can't be found");
		waitForProcessToEnd("replenishmentOrderPlacedEmailProcess", 2000);
		TestUtils.enableFileAnalyzer();
	}

	@Override
	protected List<BusinessProcessModel> getProcesses(final String processDefinitionName, final List<ProcessState> processStates)
	{
		final FlexibleSearchQuery query = new FlexibleSearchQuery("select {" + BusinessProcessModel.PK + "} from {"
				+ BusinessProcessModel._TYPECODE + "} where {" + BusinessProcessModel.STATE + "} in (?state) AND {"
				+ BusinessProcessModel.PROCESSDEFINITIONNAME + "} = ?processDefinitionName");
		query.addQueryParameter(BusinessProcessModel.PROCESSDEFINITIONNAME, processDefinitionName);
		query.addQueryParameter(BusinessProcessModel.STATE, processStates);
		final SearchResult<BusinessProcessModel> result = flexibleSearchService.search(query);
		return result.getResult();
	}


	@Override
	protected boolean waitForProcessToEnd(final String processDefinitionName, final long maxWait) throws InterruptedException
	{
		final long start = System.currentTimeMillis();
		while (true)
		{
			final List<BusinessProcessModel> processes = getProcesses(processDefinitionName, Arrays.asList(new ProcessState[]
			{ ProcessState.RUNNING, ProcessState.CREATED, ProcessState.WAITING }));

			if (CollectionUtils.isEmpty(processes))
			{
				return true;
			}
			if (System.currentTimeMillis() - start > maxWait)
			{
				LOG.warn(String.format("BusinessProcesses with processDefinitionName %s are still in running! Waited for %s",
						processDefinitionName, Utilities.formatTime(System.currentTimeMillis() - start)));
				return false;
			}
			else
			{
				Thread.sleep(1000);
			}
		}
	}

	@Test
	public void shouldAuthorizeInvoicePayment() throws Exception
	{
		final CartModel modelByExample = new CartModel();
		modelByExample.setCode("invoicePaymentCart_b2b");
		final CartModel cart = flexibleSearchService.getModelByExample(modelByExample);
		Assert.assertNotNull("invoicePaymentCart_b2b cart.code should have been found", cart);
		cartService.setSessionCart(cart);

		final boolean authResult = b2bCheckoutFacade.authorizePayment(StringUtils.EMPTY);
		Assert.assertTrue(authResult);
		Assert.assertNotNull("payment transactions are null", cart.getPaymentTransactions());
		Assert.assertEquals(1, cart.getPaymentTransactions().size());
		final PaymentTransactionModel paymentTran = cart.getPaymentTransactions().get(0);
		Assert.assertNotNull(paymentTran.getCode());
		final BigDecimal totalAmount = new BigDecimal(526.88).setScale(2, RoundingMode.HALF_EVEN);
		Assert.assertEquals(totalAmount, paymentTran.getPlannedAmount().setScale(2, RoundingMode.HALF_EVEN));
		Assert.assertEquals(cart, paymentTran.getOrder());
		Assert.assertNotNull(paymentTran.getInfo());
		Assert.assertTrue(paymentTran.getInfo() instanceof InvoicePaymentInfoModel);
		Assert.assertNotNull("payment transaction entries are null", paymentTran.getEntries());
		Assert.assertEquals(1, paymentTran.getEntries().size());
		final PaymentTransactionEntryModel paymentTranEntry = paymentTran.getEntries().get(0);
		Assert.assertEquals(totalAmount, paymentTranEntry.getAmount().setScale(2, RoundingMode.HALF_EVEN));
		Assert.assertEquals(cart.getCurrency(), paymentTranEntry.getCurrency());
		Assert.assertEquals(PaymentTransactionType.AUTHORIZATION, paymentTranEntry.getType());
		Assert.assertNotNull(paymentTranEntry.getTime());
		Assert.assertEquals(TransactionStatus.ACCEPTED.name(), paymentTranEntry.getTransactionStatus());
		Assert.assertEquals(TransactionStatusDetails.SUCCESFULL.toString(), paymentTranEntry.getTransactionStatusDetails());
	}

	@Test(expected = EntityValidationException.class)
	public void shouldNotUpdateCheckoutCartWithCardAndCostCenter()
	{
		//Arrange
		final CartData cartData = new CartData();
		final B2BCostCenterData costCenterData = new B2BCostCenterData();
		final B2BPaymentTypeData paymentTypeData = new B2BPaymentTypeData();
		paymentTypeData.setCode("CARD");
		costCenterData.setCode("someCostCenter");
		cartData.setPaymentType(paymentTypeData);
		cartData.setCostCenter(costCenterData);

		//Act ~ Should throw EntityValidationException
		final CartData _ret = b2bCheckoutFacade.updateCheckoutCart(cartData);
	}

	@Test
	public void shouldReturnAllPaymentTypes()
	{
		final B2BPaymentTypeData cardPaymentTypeData = new B2BPaymentTypeData();
		cardPaymentTypeData.setCode("CARD");
		final B2BPaymentTypeData accountPaymentTypeData = new B2BPaymentTypeData();
		accountPaymentTypeData.setCode("ACCOUNT");

		List<B2BPaymentTypeData> b2BPaymentTypeDataList1 = b2bCheckoutFacade.getPaymentTypes();
		assertThat(b2BPaymentTypeDataList1).hasSize(2)
				.usingElementComparatorOnFields("code")
				.containsExactlyInAnyOrder(accountPaymentTypeData, cardPaymentTypeData);
	}

	@Test
	public void shouldNotReturnCardPaymentType()
	{
		final B2BPaymentTypeData accountPaymentTypeData = new B2BPaymentTypeData();
		accountPaymentTypeData.setCode("ACCOUNT");

		final B2BUnitModel b2BUnitModel = b2bCheckoutFacade.getB2bCommerceUnitService().getRootUnit();
		b2BUnitModel.setB2bExcludedPaymentTypes(Set.of(CARD));

		List<B2BPaymentTypeData> b2BPaymentTypeDataList = b2bCheckoutFacade.getPaymentTypes();

		assertThat(b2BPaymentTypeDataList).hasSize(1)
				.usingElementComparatorOnFields("code")
				.containsExactly(accountPaymentTypeData);
	}

	@Test
	public void shouldNotValidateCheckoutCartWhenNotAllowedPaymentTypeException()
	{
		final PlaceOrderData placeOrderData = new PlaceOrderData();
		final B2BUnitModel b2BUnitModel = b2bCheckoutFacade.getB2bCommerceUnitService().getRootUnit();
		b2BUnitModel.setB2bExcludedPaymentTypes(Set.of(CARD));
		setUpIsValidCheckoutCart();
		Exception exception = assertThrows(EntityValidationException.class, () -> b2bCheckoutFacade.isValidCheckoutCart(placeOrderData));
		assertThat(exception).hasMessageMatching("Payment type is not a valid value.");
	}

	@Test
	public void shouldValidateCheckoutCartWhenAllowedPaymentType()
	{
		final PlaceOrderData placeOrderData = new PlaceOrderData();
		setUpIsValidCheckoutCart();
		assertTrue(b2bCheckoutFacade.isValidCheckoutCart(placeOrderData));
	}

	private void setUpIsValidCheckoutCart() {

		cartService.removeSessionCart();

		final CartModel modelByExample = new CartModel();
		modelByExample.setCode("invoicePaymentCart_b2b");
		final CartModel cart = flexibleSearchService.getModelByExample(modelByExample);
		Assert.assertNotNull("invoicePaymentCart_b2b cart.code should have been found", cart);

		final PlaceOrderData placeOrderData = new PlaceOrderData();

		final UserModel userModel = cart.getUser();
		final LanguageModel languageModel = commonI18NService.getLanguage("en");
		languageModel.setIsocode("en");
		languageModel.setActive(Boolean.TRUE);
		commonI18NService.setCurrentLanguage(languageModel);
		userModel.setSessionLanguage(languageModel);
		userService.setCurrentUser(userModel);

		List<AddressModel> addresses = new ArrayList<>();
		final AddressModel addressModel = this.modelService.create(AddressModel.class);
		addressModel.setBillingAddress(Boolean.TRUE);
		addressModel.setShippingAddress(Boolean.TRUE);
		addressModel.setCountry(commonI18NService.getCountry("US"));
		addressModel.setStreetname("streetName");
		addressModel.setStreetnumber("streetNumber");
		addressModel.setPostalcode("postalCode");
		addressModel.setTown("town");
		addressModel.setFirstname("firstName");
		addressModel.setLastname("lastName");
		addressModel.setOwner(userModel);
		addresses.add(addressModel);

		userModel.setAddresses(addresses);
		userService.setCurrentUser(userModel);

		final CountryData countryData = new CountryData();
		countryData.setIsocode("USA");

		final ZoneDeliveryModeModel deliveryModeModel = this.modelService.create(ZoneDeliveryModeModel.class);
		deliveryModeModel.setCode("zoneDM1");
		deliveryModeModel.setPropertyName(ProductModel.NUMBERCONTENTUNITS);
		deliveryModeModel.setNet(Boolean.TRUE);

		CreditCardPaymentInfoModel paymentInfoModel = this.modelService.create(CreditCardPaymentInfoModel.class);
		paymentInfoModel.setUser(userModel);
		paymentInfoModel.setBillingAddress(addressModel);
		paymentInfoModel.setDuplicate(Boolean.FALSE);
		paymentInfoModel.setCode("test");
		paymentInfoModel.setUser(userModel);
		paymentInfoModel.setNumber("4111111111111111");
		paymentInfoModel.setCcOwner("Test Owner");
		paymentInfoModel.setType(CreditCardType.VISA);
		paymentInfoModel.setValidToMonth("10");
		paymentInfoModel.setValidToYear("2099");

		cart.setDeliveryAddress(addressModel);
		cart.setDeliveryMode(deliveryModeModel);
		cart.setPaymentType(CARD);
		cart.setPaymentInfo(paymentInfoModel);
		cart.setUser(userService.getCurrentUser());
		cart.setCalculated(Boolean.TRUE);

		cartService.setSessionCart(cart);

		modelService.save(userModel);
		modelService.save(addressModel);
		modelService.save(deliveryModeModel);
		modelService.save(paymentInfoModel);
		modelService.save(languageModel);
		modelService.save(cart);
	}

}
