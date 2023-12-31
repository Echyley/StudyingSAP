/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.commercefacades.order.SaveCartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartParameterData;
import de.hybris.platform.commercefacades.order.data.CommerceSaveCartResultData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceSaveCartException;
import de.hybris.platform.converters.impl.AbstractPopulatingConverter;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.facades.ConfigurationSavedCartIntegrationFacade;
import de.hybris.platform.sap.productconfig.facades.CsticData;
import de.hybris.platform.sap.productconfig.facades.KBKeyData;
import de.hybris.platform.sap.productconfig.facades.overview.ConfigurationOverviewData;
import de.hybris.platform.sap.productconfig.services.strategies.lifecycle.intf.ConfigurationCopyStrategy;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;


/**
 * Integration test for Configuration cart integration
 */
@IntegrationTest
public class ConfigCartIntegrationTest extends CPQFacadeLayerTest
{
	private static Logger LOG = Logger.getLogger(ConfigCartIntegrationTest.class);
	private static final BigDecimal CART_ENTRY_BASE_PRICE = BigDecimal.valueOf(22000);

	@Resource(name = "saveCartFacade")
	private SaveCartFacade saveCartFacade;
	@Resource(name = "sapProductConfigSavedCartIntegrationFacade")
	protected ConfigurationSavedCartIntegrationFacade configurationSavedCartIntegrationFacade;
	@Resource(name = "cartConverter")
	private AbstractPopulatingConverter<CartModel, CartData> cartConverter;
	@Resource(name = "sapProductConfigConfigurationCopyStrategy")
	private ConfigurationCopyStrategy configCopyStrategy;
	private String cartItemHandle;

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();

		//Enforce remove of B2BCartPopulator
		removeB2BCartPopulator(cartConverter);
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}



	@Test
	public void testAddToCartInConsistent() throws Exception
	{
		final ConfigurationData configData = createAndUpdateConfiguration();
		cpqCartFacade.addConfigurationToCart(configData);

		final OrderEntryData orderEntryData = cartFacade.getSessionCart().getEntries().get(0);
		assertTrue("Configuration is not attached to cart entry", orderEntryData.isConfigurationAttached());
		assertFalse("Configuration consistent", orderEntryData.isConfigurationConsistent());
		assertEquals("wrong number of config errors", 2, orderEntryData.getConfigurationErrorCount());
	}

	@Test
	public void testAddToCartConsistent() throws Exception
	{
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		for (final CsticData cstic : configData.getGroups().get(0).getCstics())
		{
			if ("YSAP_POC_SIMPLE_FLAG".equals(cstic.getName()))
			{
				cstic.getDomainvalues().get(0).setSelected(false);
			}
		}
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);

		for (final CsticData cstic : configData.getGroups().get(0).getCstics())
		{
			if ("WCEM_NUMBER_SIMPLE".equals(cstic.getName()))
			{
				cstic.setValue("123");
				cstic.setFormattedValue("123");
			}
		}
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);

		for (final CsticData cstic : configData.getGroups().get(0).getCstics())
		{
			if ("EXP_NO_USERS".equals(cstic.getName()))
			{
				cstic.setValue("300");
				cstic.setFormattedValue("300");
			}
		}
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);

		cpqCartFacade.addConfigurationToCart(configData);

		final OrderEntryData orderEntryData = cartFacade.getSessionCart().getEntries().get(0);
		assertTrue("Configuration is not attached to cart entry", orderEntryData.isConfigurationAttached());
		assertTrue("Configuration not consistent", orderEntryData.isConfigurationConsistent());
		assertEquals("wrong number of config errors", 0, orderEntryData.getConfigurationErrorCount());
	}

	@Test
	public void testCopyConfiguration() throws Exception
	{
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		for (final CsticData cstic : configData.getGroups().get(0).getCstics())
		{
			if ("YSAP_POC_SIMPLE_FLAG".equals(cstic.getName()))
			{
				cstic.getDomainvalues().get(0).setSelected(false);
			}
		}

		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);

		for (final CsticData cstic : configData.getGroups().get(0).getCstics())
		{
			if ("WCEM_NUMBER_SIMPLE".equals(cstic.getName()))
			{
				cstic.setValue("123");
				cstic.setFormattedValue("123");
			}
		}
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);
		final String oldConfigId = configData.getConfigId();
		final String newConfigId = configCopyStrategy.deepCopyConfiguration(oldConfigId, KB_KEY_Y_SAP_SIMPLE_POC.getProductCode(),
				null, true);
		assertNotEquals(oldConfigId, newConfigId);
		final ConfigurationData configDataNew = cpqFacade.getConfiguration(configData);
		for (final CsticData cstic : configDataNew.getGroups().get(0).getCstics())
		{
			if ("WCEM_NUMBER_SIMPLE".equals(cstic.getName()))
			{
				assertEquals("123.0", cstic.getValue());
			}
		}
	}

	@Test
	public void testCopyConfigurationNumericProductId() throws Exception
	{
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_NUMERIC_PRODUCT);
		for (final CsticData cstic : configData.getGroups().get(0).getCstics())
		{
			if ("GH_LENGTH".equals(cstic.getName()))
			{
				cstic.setValue("5");
				cstic.setFormattedValue("5");
			}
		}

		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);

		final String oldConfigId = configData.getConfigId();
		final String newConfigId = configCopyStrategy.deepCopyConfiguration(oldConfigId, KB_KEY_NUMERIC_PRODUCT.getProductCode(),
				null, true);
		assertNotEquals(oldConfigId, newConfigId);
		final ConfigurationData configDataNew = cpqFacade.getConfiguration(configData);
		for (final CsticData cstic : configDataNew.getGroups().get(0).getCstics())
		{
			if ("GH_LENGTH".equals(cstic.getName()))
			{
				assertEquals("5.0", cstic.getValue());
			}
		}
	}

	@Test
	public void testAddToCartNoConfig() throws Exception
	{
		cartFacade.addToCart(PRODUCT_CODE_YSAP_NOCFG, 1);

		final OrderEntryData orderEntryData = cartFacade.getSessionCart().getEntries().get(0);
		assertFalse("Configuration is attached to cart entry for NON-Configurable Product!",
				orderEntryData.isConfigurationAttached());
		assertFalse("Configuration not consistent", orderEntryData.isConfigurationConsistent());
		assertEquals("wrong number of config errors", 0, orderEntryData.getConfigurationErrorCount());
	}

	@Test
	public void testSavedCart() throws CommerceCartModificationException, CommerceSaveCartException
	{
		//PREPARE Create session cart and save it
		final String code = createAndSaveCart(KB_KEY_Y_SAP_SIMPLE_POC);

		//TEST Read saved cart as in B2C accelerator
		final OrderEntryData cartEntry = getEntryFromSavedCart(code);

		//TEST Access configuration overview
		final ConfigurationOverviewData overviewData = configurationSavedCartIntegrationFacade.getConfiguration(code,
				cartEntry.getEntryNumber().intValue());

		//VERIFY Overview data exists and is connected to a valid configuration
		assertNotNull(overviewData);
		assertNotNull(overviewData.getId());
	}

	@Test
	public void testSavedCartRecover() throws CommerceCartModificationException, CommerceSaveCartException
	{
		//PREPARE Create session cart and save it
		final String code = createAndSaveCart(KB_KEY_CPQ_HOME_THEATER);

		//VERIFY Session artifacts are available in saved cart context
		final String configIdForCartEntry = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartItemHandle);
		assertNotNull(configIdForCartEntry);

		//TEST Restore the cart to a session cart again
		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(code);
		saveCartFacade.restoreSavedCart(parameters);

		//TEST read the cart so all is fetched into the session.
		final OrderEntryData cartEntryData = cartFacade.getSessionCart().getEntries().get(0);

		assertEquals("Wrong price, expected " + CART_ENTRY_BASE_PRICE + " but was: " + cartEntryData.getBasePrice().getValue(), 0,
				cartEntryData.getBasePrice().getValue().compareTo(CART_ENTRY_BASE_PRICE));

		//VERIFY (new) Session artifacts are available in the restored cart context
		final String configIdForCartEntryAfterRestore = cpqAbstractOrderEntryLinkStrategy.getConfigIdForCartEntry(cartItemHandle);
		assertNotNull(configIdForCartEntryAfterRestore);
	}

	protected OrderEntryData getEntryFromSavedCart(final String code) throws CommerceSaveCartException
	{
		final CommerceSaveCartParameterData parameters = new CommerceSaveCartParameterData();
		parameters.setCartId(code);
		final CommerceSaveCartResultData cartForCodeAndCurrentUser = saveCartFacade.getCartForCodeAndCurrentUser(parameters);
		final CartData savedCartData = cartForCodeAndCurrentUser.getSavedCartData();
		assertNotNull(savedCartData);
		final List<OrderEntryData> entries = savedCartData.getEntries();
		assertEquals(1, entries.size());
		final OrderEntryData cartEntry = entries.get(0);
		return cartEntry;
	}

	protected String createAndSaveCart(final KBKeyData kbKey) throws CommerceCartModificationException
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(kbKey);
		cartItemHandle = cpqCartFacade.addConfigurationToCart(configData);
		final CartData sessionCart = cartFacade.getSessionCart();
		final String code = sessionCart.getCode();
		modelService.save(cartService.getSessionCart());
		return code;
	}

	@Test
	public void testRestoreCart() throws Exception
	{
		try
		{
			// PREPARE
			final CartModificationData modificationData = cartFacade.addToCart(PRODUCT_CODE_YSAP_NOCFG, 1);
			//ConfigurationData configData = cpqFacade.getConfiguration(Y_SAP_SIMPLE_POC_KB_KEY);
			//String itemKey = cpqCartFacade.addConfigurationToCart(configData);
			final String toCart = cartFacade.getSessionCartGuid();
			LOG.debug("YSAP_NOCFG=" + modificationData.getEntry().getItemPK() + " created in cart=" + toCart);

			login(USER_NAME, PASSWORD);
			authenticationService.logout();
			makeProductCatalogVersionAvailableInSession();
			baseSiteService.setCurrentBaseSite(TEST_CONFIGURE_SITE, false);

			final ConfigurationData configData = createAndUpdateConfiguration();
			final String itemKey = cpqCartFacade.addConfigurationToCart(configData);
			//cartFacade.addToCart("YSAP_NOCFG", 1);
			final String fromCart = cartFacade.getSessionCartGuid();
			LOG.debug("YSAP_SIMPLE_POC=" + itemKey + " created in cart=" + fromCart);

			authenticationService.login("cpq01", "welcome");
			makeProductCatalogVersionAvailableInSession();
			cartService.getSessionCart().setUser(realUserService.getAnonymousUser());
			modelService.save(cartService.getSessionCart());

			// TEST
			LOG.debug("merging carts, fromCart=" + fromCart + "; toCart=" + toCart);
			cartFacade.restoreAnonymousCartAndMerge(fromCart, toCart);

			// CHECK
			final CartData sessionCart = cartFacade.getSessionCart();
			for (final OrderEntryData entry : sessionCart.getEntries())
			{
				LOG.debug("CartItem " + entry.getItemPK() + ": configAttached=" + entry.isConfigurationAttached());
				final boolean hasCFG = KB_KEY_Y_SAP_SIMPLE_POC.getProductCode().equals(entry.getProduct().getCode());
				assertEquals("CartItem hasCFG=" + hashCode() + "; but was not", Boolean.valueOf(hasCFG),
						Boolean.valueOf(entry.isConfigurationAttached()));
			}

			assertEquals("merged cart should have 2 entries, one from each source cart", 2, sessionCart.getEntries().size());
		}
		finally
		{
			authenticationService.logout();
			jaloSession = JaloSession.getCurrentSession();
		}
	}

	protected ConfigurationData createAndUpdateConfiguration()
	{
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		facadeConfigValueHelper.setCsticValue(configData, "YSAP_POC_SIMPLE_FLAG", "X", false);
		cpqFacade.updateConfiguration(configData);
		configData = cpqFacade.getConfiguration(configData);
		return configData;
	}

	@Test
	public void testConfigureFromCart() throws Exception
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		assertNotNull("We expect a default configuration", configData.getConfigId());

		final String entryKey = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull("We expect an entry key at this point", entryKey);

		final ConfigurationData draftConfiguration = cpqCartFacade.configureCartItem(entryKey);
		assertNotNull("We expect a configuration attached to the cart entry", draftConfiguration.getConfigId());
		assertNotEquals("Configurations must differ", configData.getConfigId(), draftConfiguration.getConfigId());

		final ConfigurationData secondDraftConfiguration = cpqCartFacade.configureCartItemOnExistingDraft(entryKey);
		assertNotNull("We expect a configuration attached to the cart entry", secondDraftConfiguration.getConfigId());
		assertEquals("Both drafts must match", draftConfiguration.getConfigId(), secondDraftConfiguration.getConfigId());
	}

	@Test
	public void testConfigureFromCartForVariants() throws Exception
	{
		importCsv("/sapproductconfigservices/test/sapProductConfig_variants_testData.impex", "utf-8");
		// in case sapproductconfigintegration is present we need an additional impex file
		importCsvIfExist("/sapproductconfigintegration/test/sapProductConfig_SAPVariants_testData.impex", "utf-8");


		// click directly add to cart for a product variant
		final CartModificationData addToCart = cartFacade.addToCart(PRODUCT_CODE_CPQ_LAPTOP_MUZAC, 1);
		modelService.save(cartService.getSessionCart());
		List<OrderEntryData> cartEntries = cartFacade.getSessionCart().getEntries();
		assertEquals("One cart entry expected", 1, cartEntries.size());
		final String entryKey = cartEntries.get(0).getItemPK();
		assertNotNull("We expect an entry key at this point", entryKey);
		assertEquals("Entry should have variant code", PRODUCT_CODE_CPQ_LAPTOP_MUZAC, cartEntries.get(0).getProduct().getCode());

		// configure the cart item
		final ConfigurationData draftConfiguration = cpqCartFacade.configureCartItem(entryKey);
		assertNotNull("We expect a configuration attached to the cart entry", draftConfiguration.getConfigId());
		assertEquals("Product Code shall be switched to KMAT", PRODUCT_CODE_CPQ_LAPTOP,
				draftConfiguration.getKbKey().getProductCode());

		// controller triggers a re-direct
		final ConfigurationData drafrtConfigurationAfterRedirect = cpqCartFacade.configureCartItemOnExistingDraft(entryKey);
		assertNotNull("We expect a configuration attached to the cart entry", drafrtConfigurationAfterRedirect.getConfigId());
		assertEquals("Both drafts must match", draftConfiguration.getConfigId(), drafrtConfigurationAfterRedirect.getConfigId());
		assertEquals("Product Code shall be switched to KMAT also after redirect", PRODUCT_CODE_CPQ_LAPTOP,
				drafrtConfigurationAfterRedirect.getKbKey().getProductCode());

		// update cart
		final String updatedEntryKey = cpqCartFacade.addConfigurationToCart(drafrtConfigurationAfterRedirect);
		assertEquals("entry should remain the same", entryKey, updatedEntryKey);
		cartEntries = cartFacade.getSessionCart().getEntries();
		assertEquals("One cart entry expected", 1, cartEntries.size());
		assertEquals("entry should have KMAT code", PRODUCT_CODE_CPQ_LAPTOP, cartEntries.get(0).getProduct().getCode());
	}

	@Test
	public void testConfigureFromCartAttemptToWorkOnNonExistingDraft() throws Exception
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		assertNotNull("We expect a default configuration", configData.getConfigId());


		final String entryKey = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull("We expect an entry key at this point", entryKey);

		assertNull(cpqCartFacade.configureCartItemOnExistingDraft(entryKey));
	}

	@Test
	public void testAddBaseProductToCart() throws Exception
	{
		cartFacade.addToCart(PRODUCT_CODE_CPQ_LAPTOP, 1);
		final int numberOfCartEntry = cartFacade.getSessionCart().getEntries().size();
		final String productCode = cartFacade.getSessionCart().getEntries().get(0).getProduct().getCode();
		assertEquals(1, numberOfCartEntry);
		assertEquals(PRODUCT_CODE_CPQ_LAPTOP, productCode);

	}

}
