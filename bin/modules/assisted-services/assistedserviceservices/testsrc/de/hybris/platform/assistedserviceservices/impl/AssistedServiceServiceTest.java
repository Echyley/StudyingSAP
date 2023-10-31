/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedserviceservices.impl;

import static de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService.ACTING_USER_UID;
import static de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService.NOT_ANONYMOUS_CART_ERROR;
import static de.hybris.platform.assistedserviceservices.impl.DefaultAssistedServiceService.UNKNOWN_CART_ERROR;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.assistedserviceservices.exception.AssistedServiceException;
import de.hybris.platform.assistedserviceservices.utils.AssistedServiceSession;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.customer.DuplicateUidException;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.storelocator.model.PointOfServiceModel;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.validation.services.ValidationService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.access.AccessDeniedException;


/**
 * Test class for {@link DefaultAssistedServiceService}
 */
@IntegrationTest
public class AssistedServiceServiceTest extends ServicelayerTransactionalTest
{
	private final String customerUID = "ascustomer";
	private final String firstCart = "as00000001";
	private final String secondCart = "as00000002";
	private final String anonymousCart = "an00000010";

	private final String AGENT_NON_ISOLATED = "asagent";

	private final String AGENT_ISOLATED_SITE_A_C = "customer.support.standalone_site_ac@nakano.com";

	private final String AGENT_ISOLATED_SITE_B_C = "customer.support.standalone_site_bc@nakano.com";

	private final String AGENT_ISOLATED_SITE_ALL = "customer.support.standalone_site_all@nakano.com";


	@Resource
	private DefaultAssistedServiceService assistedServiceService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private UserService userService;
	@Resource
	private ModelService modelService;
	@Resource
	private CartService cartService;
	@Resource
	private ValidationService validationService;

	@Resource
	private SessionService sessionService;

	@Before
	public void setup() throws Exception
	{
		importCsv("/assistedserviceservices/test/cart_data.impex", "UTF-8");
		baseSiteService.setCurrentBaseSite("testSite", true);
		baseSiteService.getCurrentBaseSite().setDataIsolationEnabled(Boolean.valueOf(false));
		userService.setCurrentUser(userService.getUserForUID(customerUID));
		((CustomerModel) userService.getCurrentUser()).setSite(null);
		sessionService.setAttribute("ASM", new AssistedServiceSession());
		importCsv("/assistedserviceservices/test/multisite_data.impex", "UTF-8");
		importCsv("/assistedserviceservices/test/agents.impex", "UTF-8");
		importCsv("/assistedserviceservices/test/constraints.impex", "UTF-8");
		validationService.reloadValidationEngine();
	}

	@Test
	public void latestModifiedCartTest()
	{
		assertEquals(secondCart, assistedServiceService.getLatestModifiedCart(userService.getUserForUID("ascustomer")).getCode());
		assertEquals("as00000003",
				assistedServiceService.getLatestModifiedCart(userService.getUserForUID("ascustomer2")).getCode());
		assertNull(assistedServiceService.getLatestModifiedCart(userService.getUserForUID("ascustomer3")));
	}

	@Test
	public void getAllStoresForNonIsolatedAgentAccessWithNonIsolatedAgent()
	{
		sessionService.setAttribute(ACTING_USER_UID, AGENT_NON_ISOLATED);
		final List<PointOfServiceModel> pointOfServiceModels = assistedServiceService.getAllStoresForAgent(AGENT_NON_ISOLATED);
		assertThat(pointOfServiceModels).hasSize(2).extracting("name", "displayName").contains(tuple("Nakano", "Nakano"), tuple("ws_warehouse_n", "Warehouse North"));
	}

	@Test
	public void getAllStoresForNonIsolatedAgentAccessWithIsolatedAgent()
	{
		sessionService.setAttribute(ACTING_USER_UID, AGENT_ISOLATED_SITE_A_C);
		assertThrows(AccessDeniedException.class, () -> assistedServiceService.getAllStoresForAgent(AGENT_NON_ISOLATED));
	}
	@Test
	public void getAllStoresForIsolatedAgentAccessWithNonIsolatedAgent()
	{
		sessionService.setAttribute(ACTING_USER_UID, AGENT_NON_ISOLATED);
		final List<PointOfServiceModel> pointOfServiceModels = assistedServiceService.getAllStoresForAgent(AGENT_ISOLATED_SITE_A_C);
		assertThat(pointOfServiceModels).hasSize(2).extracting("name", "displayName").contains(tuple("Nakano", "Nakano"), tuple("ws_warehouse_n", "Warehouse North"));
	}

	@Test
	public void getAllStoresForIsolatedAgentAccessWithCorrectIsolatedAgent()
	{
		sessionService.setAttribute(ACTING_USER_UID, AGENT_ISOLATED_SITE_A_C);
		final List<PointOfServiceModel> pointOfServiceModels = assistedServiceService.getAllStoresForAgent(AGENT_ISOLATED_SITE_A_C);
		assertThat(pointOfServiceModels).hasSize(2).extracting("name", "displayName").contains(tuple("Nakano", "Nakano"), tuple("ws_warehouse_n", "Warehouse North"));
	}

	@Test
	public void getAllStoresForIsolatedAgentAccessIncludedIsolatedAgent()
	{
		sessionService.setAttribute(ACTING_USER_UID, AGENT_ISOLATED_SITE_ALL );
		final List<PointOfServiceModel> pointOfServiceModels = assistedServiceService.getAllStoresForAgent(AGENT_ISOLATED_SITE_A_C);
		assertThat(pointOfServiceModels).hasSize(2).extracting("name", "displayName").contains(tuple("Nakano", "Nakano"), tuple("ws_warehouse_n", "Warehouse North"));
	}

	@Test
	public void getAllStoresForIsolatedAgentAccessIncorrectIsolatedAgent()
	{
		sessionService.setAttribute(ACTING_USER_UID, AGENT_ISOLATED_SITE_B_C);
		assertThrows(AccessDeniedException.class, () -> assistedServiceService.getAllStoresForAgent(AGENT_ISOLATED_SITE_A_C));
	}

	@Test
	public void testGetCartByCode()
	{
		final CartModel firstCartModel = assistedServiceService.getCartByCode(firstCart, userService.getUserForUID(customerUID));
		assertEquals(firstCart, firstCartModel.getCode());

		final CartModel secondCartModel = assistedServiceService.getCartByCode(secondCart, userService.getUserForUID(customerUID));
		assertEquals(secondCart, secondCartModel.getCode());
	}

	@Test
	public void testGetCarts()
	{
		final Collection<CartModel> cartsForCustomer = assistedServiceService.getCartsForCustomer(
				(CustomerModel) userService.getUserForUID(customerUID));
		assertTrue(cartsForCustomer.stream().anyMatch(cartModel -> cartModel.getCode().equals(firstCart)));
		assertTrue(cartsForCustomer.stream().anyMatch(cartModel -> cartModel.getCode().equals(secondCart)));
	}

	@Test
	public void restoreCartToUserTest()
	{
		final CartService spyService = spy(cartService);
		final ModelService modelServiceSpy = spy(modelService);
		final UserModel user = userService.getUserForUID(customerUID);
		final CartModel cart = cartService.getSessionCart();
		cart.setEntries(new ArrayList<>());
		assistedServiceService.setCartService(spyService);
		assistedServiceService.setModelService(modelServiceSpy);
		assistedServiceService.restoreCartToUser(null, null);
		assistedServiceService.restoreCartToUser(cart, null);
		assistedServiceService.restoreCartToUser(cart, user);
		verify(spyService, never()).changeCurrentCartUser(nullable(UserModel.class));
		final List<AbstractOrderEntryModel> entries = new ArrayList<>();
		entries.add(new AbstractOrderEntryModel());
		cart.setEntries(entries);
		doNothing().when(spyService).changeCurrentCartUser(nullable(UserModel.class));
		doNothing().when(modelServiceSpy).refresh(nullable(Object.class));
		assistedServiceService.restoreCartToUser(cart, user);
		verify(spyService).changeCurrentCartUser(nullable(UserModel.class));
		verify(modelServiceSpy).refresh(nullable(Object.class));
	}

	@Test
	public void bindCustomerToCartTest()
	{
		final UserModel user = userService.getUserForUID(customerUID);
		final CartModel cart = assistedServiceService.getCartByCode(firstCart, user);
		cartService.setSessionCart(cart);

		assertThrows(Localization.getLocalizedString(NOT_ANONYMOUS_CART_ERROR), AssistedServiceException.class,
				() -> assistedServiceService.bindCustomerToCart(null, firstCart));
	}

	@Test
	public void bindNotExistingToCartTest()
	{
		final UserModel user = userService.getUserForUID(customerUID);
		final CartModel cart = assistedServiceService.getCartByCode(firstCart, user);
		cartService.setSessionCart(cart);

		assertThrows(Localization.getLocalizedString(UNKNOWN_CART_ERROR), AssistedServiceException.class,
				() -> assistedServiceService.bindCustomerToCart(null, "non-existing"));
	}

	@Test
	public void bindCustomerToCartTestWithAnonymousCart() throws AssistedServiceException
	{
		final UserModel user = userService.getUserForUID(customerUID);
		final CartModel cart = assistedServiceService.getCartByCode(anonymousCart, user);
		cartService.setSessionCart(cart);

		assistedServiceService.bindCustomerToCart(null, anonymousCart);
	}

	@Test
	public void bindCustomerToCartWithIsolatedSiteAndCommonCustomerTest()
	{
		final UserModel user = userService.getUserForUID(customerUID);
		final CartModel cart = assistedServiceService.getCartByCode(firstCart, user);
		cartService.setSessionCart(cart);
		baseSiteService.getCurrentBaseSite().setDataIsolationEnabled(Boolean.valueOf(true));

		assertThrows(Localization.getLocalizedString(DefaultAssistedServiceService.CUSTOMER_NOT_FOUND),
				AssistedServiceException.class, () -> assistedServiceService.bindCustomerToCart(null, firstCart));
	}

	@Test
	public void bindCustomerToCartWithCommonSiteAndIsolatedCustomerTest() throws AssistedServiceException
	{
		final UserModel user = userService.getUserForUID(customerUID);
		final CartModel cart = assistedServiceService.getCartByCode(firstCart, user);
		cartService.setSessionCart(cart);
		((CustomerModel) user).setSite(baseSiteService.getCurrentBaseSite());

		assertThrows(Localization.getLocalizedString(DefaultAssistedServiceService.CUSTOMER_NOT_FOUND),
				AssistedServiceException.class, () -> assistedServiceService.bindCustomerToCart(null, firstCart));
	}

	@Test
	public void bindCustomerToCartWithIsolatedSiteAndIsolatedCustomerTestInCorrectSite()
	{
		final UserModel user = userService.getUserForUID(customerUID);
		final CartModel cart = assistedServiceService.getCartByCode(firstCart, user);
		cartService.setSessionCart(cart);
		final BaseSiteModel baseSite = new BaseSiteModel();
		baseSite.setUid("differentSite");
		((CustomerModel) user).setSite(baseSite);
		baseSiteService.getCurrentBaseSite().setDataIsolationEnabled(Boolean.valueOf(true));

		assertThrows(Localization.getLocalizedString(DefaultAssistedServiceService.CUSTOMER_NOT_FOUND),
				AssistedServiceException.class, () -> assistedServiceService.bindCustomerToCart(null, firstCart));

	}

	@Test
	public void bindCustomerToCartWithIsolatedSiteAndIsolatedCustomerWithCommonCartTest() throws AssistedServiceException
	{
		final UserModel user = userService.getUserForUID(customerUID);
		((CustomerModel) user).setSite(baseSiteService.getCurrentBaseSite());
		baseSiteService.getCurrentBaseSite().setDataIsolationEnabled(Boolean.valueOf(true));

		assertThrows(Localization.getLocalizedString(NOT_ANONYMOUS_CART_ERROR), AssistedServiceException.class,
				() -> assistedServiceService.bindCustomerToCart(null, firstCart));
	}

	@Test
	public void bindCustomerToCartWithIsolatedSiteAndIsolatedCustomerWithAnonymousCartTest() throws AssistedServiceException
	{
		final UserModel user = userService.getUserForUID(customerUID);
		((CustomerModel) user).setSite(baseSiteService.getCurrentBaseSite());
		baseSiteService.getCurrentBaseSite().setDataIsolationEnabled(Boolean.valueOf(true));

		assistedServiceService.bindCustomerToCart(null, anonymousCart);

		final CartModel cart = cartService.getSessionCart();
		assertEquals(customerUID, cart.getUser().getUid());
	}

	@Test
	public void noneIsolatedAgentLoginIsolatedSiteTest()
	{
		final UserModel user = userService.getUserForUID("customer.support.none_isolated@nakano.com");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("test-site-a"), false);

		final boolean isLogin = assistedServiceService.isAgentCanLogin(user);
		assertTrue(isLogin);
	}

	@Test
	public void noneIsolatedAgentLoginNoneIsolatedSiteTest()
	{
		final UserModel user = userService.getUserForUID("customer.support.none_isolated@nakano.com");

		final boolean isLogin = assistedServiceService.isAgentCanLogin(user);
		assertTrue(isLogin);
	}

	@Test
	public void isolatedAgentLoginNoneIsolatedSiteTest()
	{
		final UserModel user = userService.getUserForUID("customer.support.standalone_site_a@nakano.com");

		final boolean isLogin = assistedServiceService.isAgentCanLogin(user);
		assertFalse(isLogin);
	}

	@Test
	public void isolatedAgentLoginAssignedIsolatedSiteTest()
	{
		final UserModel user = userService.getUserForUID("customer.support.standalone_site_a@nakano.com");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("test-site-a"), false);

		final boolean isLogin = assistedServiceService.isAgentCanLogin(user);
		assertTrue(isLogin);
	}

	@Test
	public void isolatedAgentLoginNotAssignedIsolatedSiteTest()
	{
		final UserModel user = userService.getUserForUID("customer.support.standalone_site_a@nakano.com");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("test-site-b"), false);

		final boolean isLogin = assistedServiceService.isAgentCanLogin(user);
		assertFalse(isLogin);
	}

	@Test
	public void isolatedAgentWithMultiGroupLoginAssignedIsolatedSiteTest()
	{
		final UserModel user = userService.getUserForUID("customer.support.standalone_site_all@nakano.com");
		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("test-site-a"), false);

		boolean isLogin = assistedServiceService.isAgentCanLogin(user);
		assertTrue(isLogin);

		baseSiteService.setCurrentBaseSite(baseSiteService.getBaseSiteForUID("test-site-b"), false);
		isLogin = assistedServiceService.isAgentCanLogin(user);
		assertTrue(isLogin);
	}

	@Test
	public void nonIsolatedAgentCreateNonIsolatedCustomerTest()throws DuplicateUidException, AssistedServiceException
	{
		final UserModel user = userService.getUserForUID("customer.support.none_isolated@nakano.com");
		userService.setCurrentUser(user);
		baseSiteService.setCurrentBaseSite("test-site", false);
		CustomerModel customer = assistedServiceService.createNewCustomer("test_none_isolated_1@test.com", "testNonIsolatedAgentCreateNonIsolatedCustomer");
		assertTrue(customer != null && customer.getUid().equals("test_none_isolated_1@test.com"));
	}

	@Test
	public void isolatedAgentCreateIsolatedCustomerTest()throws DuplicateUidException, AssistedServiceException
	{
		final UserModel user = userService.getUserForUID("customer.support.standalone_site_a@nakano.com");
		userService.setCurrentUser(user);
		baseSiteService.setCurrentBaseSite("test-site-a", false);
		CustomerModel customer = assistedServiceService.createNewCustomer("test_isolated_a_1@test.com", "testIsolatedAgentCreateIsolatedCustomer");
		assertTrue(customer != null && customer.getUid().equals("test_isolated_a_1@test.com|test-site-a"));
	}

	@Test
	public void nonIsolatedAgentCreateIsolatedCustomerTest()throws DuplicateUidException, AssistedServiceException
	{
		final UserModel user = userService.getUserForUID("customer.support.none_isolated@nakano.com");
		userService.setCurrentUser(user);
		baseSiteService.setCurrentBaseSite("test-site-a", false);
		CustomerModel customer = assistedServiceService.createNewCustomer("test_isolated_a_2@test.com", "testNonIsolatedAgentCreateIsolatedCustomer");
		assertTrue(customer != null && customer.getUid().equals("test_isolated_a_2@test.com|test-site-a"));
	}

	@Test
	public void isolatedAgentCreateNonIsolatedCustomerTest()
	{
		final UserModel user = userService.getUserForUID("customer.support.standalone_site_a@nakano.com");
		userService.setCurrentUser(user);
		baseSiteService.setCurrentBaseSite("test-site", false);
		assertThrows("Can not create customer with the site: test-site", AssistedServiceException.class,
				() -> assistedServiceService.createNewCustomer("test_none_isolated_2@test.com", "testIsolatedAgentCreateNonIsolatedCustomer"));

	}

	@Test
	public void isolatedAgentCreateOtherIsolatedCustomerTest()
	{
		final UserModel user = userService.getUserForUID("customer.support.standalone_site_a@nakano.com");
		userService.setCurrentUser(user);
		baseSiteService.setCurrentBaseSite("test-site-b", false);
		assertThrows("Can not create customer with the site: test-site-b", AssistedServiceException.class,
				() -> assistedServiceService.createNewCustomer("test_isolated_b_1@test.com", "testIsolatedAgentCreateOtherIsolatedCustomer"));

	}
}
