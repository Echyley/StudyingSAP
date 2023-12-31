/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.facades.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.CartModificationData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.order.CommerceCartModificationStatus;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.facades.ConfigurationData;
import de.hybris.platform.sap.productconfig.services.model.CPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.productconfig.services.model.ProductConfigurationModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;


/**
 * Integration test for Add to Cart Scenario
 */
@IntegrationTest
public class AddToCartFacadeIntegrationTest extends CPQFacadeLayerTest
{
	private static final String QUERY_ATTRIBUTE_CART_ENTRY_KEY = "cartEntryKey";

	private static final String QUERY_GET_ENTRY_BY_PK = "GET {cartentry} where {pk}=?cartEntryKey";

	private static Logger LOG = Logger.getLogger(AddToCartFacadeIntegrationTest.class);

	@Before
	public void setUp() throws Exception
	{
		prepareCPQData();
	}

	@Override
	public void importCPQTestData() throws ImpExException, Exception
	{
		super.importCPQTestData();
		importCPQUserData();
	}


	@Test
	public void testAddToCartCheckQty() throws Exception
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final long myQty = 5L;
		configData.setQuantity(myQty);
		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull(cartItemKey);
		final Map<String, String> params = Collections.singletonMap(QUERY_ATTRIBUTE_CART_ENTRY_KEY, cartItemKey);

		final CartEntryModel cartEntry = flexibleSearchService.searchUnique(new FlexibleSearchQuery(QUERY_GET_ENTRY_BY_PK, params));

		final Long qty = cartEntry.getQuantity();
		assertEquals(Long.valueOf(myQty), qty);
	}

	@Test
	public void testAddToCartAttachedConfiguration() throws Exception
	{
		assumeTrue(isPersistentLifecycle());
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final CartEntryModel cartEntry = addConfigAndGetCartEntry(configData);
		final ProductConfigurationModel productConfiguration = cartEntry.getProductConfiguration();
		assertNotNull(productConfiguration);
		final Collection<ProductModel> products = productConfiguration.getProduct();
		assertNotNull(products);
		assertEquals(0, products.size());
	}

	protected CartEntryModel addConfigAndGetCartEntry(final ConfigurationData configData) throws CommerceCartModificationException
	{
		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);

		assertNotNull(cartItemKey);
		final Map<String, String> params = Collections.singletonMap(QUERY_ATTRIBUTE_CART_ENTRY_KEY, cartItemKey);

		final CartEntryModel cartEntry = flexibleSearchService.searchUnique(new FlexibleSearchQuery(QUERY_GET_ENTRY_BY_PK, params));
		return cartEntry;
	}


	@Test
	public void testAddToCartXmlInDB() throws Exception
	{
		assumeFalse(isPersistentLifecycle());
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final CartEntryModel cartEntry = addConfigAndGetCartEntry(configData);
		final String xml = externalConfigurationAccess.getExternalConfiguration(cartEntry);
		LOG.debug("ExternalConfig from DB: " + xml);

		// check that there is some parseable XML in DB as external configuration
		final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		final InputStream source = new ByteArrayInputStream(xml.getBytes("UTF-8"));
		final Document doc = dBuilder.parse(source);
		assertNotNull(doc);
	}

	@Test
	public void testAddToCartConfigurationProductInfos() throws Exception
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);

		final CartEntryModel cartEntry = addConfigurationToCart(configData);
		final List<AbstractOrderEntryProductInfoModel> productInfos = cartEntry.getProductInfos();
		assertEquals(1, productInfos.size());
		assertTrue(productInfos.get(0) instanceof CPQOrderEntryProductInfoModel);

		final CPQOrderEntryProductInfoModel info = (CPQOrderEntryProductInfoModel) productInfos.get(0);
		if (LOG.isDebugEnabled())
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("Product Info: ").append(info.getCpqCharacteristicName()).append(", ")
					.append(info.getCpqCharacteristicAssignedValues());
			LOG.debug(sb.toString());
		}
		assertEquals(ConfiguratorType.CPQCONFIGURATOR, info.getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, info.getProductInfoStatus());
		assertEquals("Simple Flag: Hide options", info.getCpqCharacteristicName());
		assertEquals("Hide", info.getCpqCharacteristicAssignedValues());
	}

	protected CartEntryModel addConfigurationToCart(final ConfigurationData configData) throws CommerceCartModificationException
	{
		final String cartItemKey = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull(cartItemKey);
		final Map<String, String> params = Collections.singletonMap(QUERY_ATTRIBUTE_CART_ENTRY_KEY, cartItemKey);

		final CartEntryModel cartEntry = flexibleSearchService.searchUnique(new FlexibleSearchQuery(QUERY_GET_ENTRY_BY_PK, params));
		return cartEntry;
	}

	@Test
	public void testAddToCartSameProductAddedTwice() throws CommerceCartModificationException
	{
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull(cartItemKey1);
		configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey2 = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull(cartItemKey2);

		assertNotEquals("expected new cart item, not same one!", cartItemKey2, cartItemKey1);
		assertEquals("Adding same configurable product twice should lead two distinct entries in cart", 2,
				cartService.getSessionCart().getEntries().size());
	}

	@Test
	public void testAddToCartUpdate() throws CommerceCartModificationException
	{
		final ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull(cartItemKey1);
		final String cartItemKey2 = cpqCartFacade.addConfigurationToCart(configData);
		assertEquals("new cartItem created instead of updated of existing one", cartItemKey1, cartItemKey2);
		assertEquals("new cartItem created instead of updated of existing one", 1,
				cartService.getSessionCart().getEntries().size());
	}

	@Test
	public void testAddToCartUpdateRemovedProduct() throws CommerceCartModificationException
	{
		ConfigurationData configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(configData);
		assertNotNull(cartItemKey1);
		final Map<Integer, Long> quantities = new HashMap();
		final AbstractOrderEntryModel cartItem1 = cartService.getSessionCart().getEntries().get(0);
		quantities.put(cartItem1.getEntryNumber(), Long.valueOf(0));
		cartService.updateQuantities(cartService.getSessionCart(), quantities);
		configData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey2 = cpqCartFacade.addConfigurationToCart(configData);
		assertNotEquals("expected new cart item, not same one!", cartItemKey2, cartItemKey1);
		assertEquals("there should be only one item in the cart", 1, cartService.getSessionCart().getEntries().size());
	}

	@Test
	public void testAddToCartUpdateRemovedProductWithProductLinkPersisting() throws CommerceCartModificationException
	{
		final ConfigurationData firstConfigData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		final String cartItemKey1 = cpqCartFacade.addConfigurationToCart(firstConfigData);
		assertNotNull(cartItemKey1);
		final Map<Integer, Long> quantities = new HashMap();
		final AbstractOrderEntryModel cartItem1 = cartService.getSessionCart().getEntries().get(0);
		quantities.put(cartItem1.getEntryNumber(), Long.valueOf(0));
		cartService.updateQuantities(cartService.getSessionCart(), quantities);
		final ConfigurationData finalConfigData = cpqFacade.getConfiguration(KB_KEY_Y_SAP_SIMPLE_POC);
		assertNotEquals("ConfigId should'nt be equal: ", firstConfigData.getConfigId(), finalConfigData.getConfigId());
		final String cartItemKey2 = cpqCartFacade.addConfigurationToCart(finalConfigData);
		assertNotEquals("expected new cart item, not same one!", cartItemKey2, cartItemKey1);
		assertEquals("there should be only one item in the cart", 1, cartService.getSessionCart().getEntries().size());
	}

	@Test
	public void testAddToCartNoStock() throws Exception
	{
		disableSOMIfPresent(); // this test works only with default implementation of CartFacade

		final CartModificationData modificationData = cartFacade.addToCart(PRODUCT_CODE_YSAP_SIMPLE_POC, 9999L);
		assertEquals(CommerceCartModificationStatus.SUCCESS, modificationData.getStatusCode());
		assertEquals(9999L, modificationData.getQuantityAdded());

		final CartModificationData modificationData2 = cartFacade.addToCart(PRODUCT_CODE_YSAP_SIMPLE_POC, 9999L);
		assertEquals(CommerceCartModificationStatus.NO_STOCK, modificationData2.getStatusCode());
		assertEquals(0L, modificationData2.getQuantityAdded());
	}
}
