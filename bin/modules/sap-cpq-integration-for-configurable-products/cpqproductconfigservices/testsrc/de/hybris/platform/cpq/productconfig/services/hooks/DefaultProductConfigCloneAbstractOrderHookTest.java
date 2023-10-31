/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.services.hooks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.order.QuoteModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.ConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


/**
 * Unit test for {@link DefaultProductConfigCloneAbstractOrderHook}
 */
@UnitTest
public class DefaultProductConfigCloneAbstractOrderHookTest
{
	private static final String CONFIG_ID = "c123";
	private static final String CONFIG_ID_2 = "c456";
	private static final int ITEMS_START = 10;
	private static final int ITEMS_INCREMENT = 10;
	private static final int ANOTHER_ITEMS_START = 100;
	private static final int ANOTHER_ITEMS_INCREMENT = 3;

	private DefaultProductConfigCloneAbstractOrderHook classUnderTest;

	@Mock
	private ConfigurationService cpqService;

	@Mock
	private BaseSiteService baseSiteService;

	@Mock
	private UserService userService;

	private ConfigurationServiceLayerHelper serviceLayerHelper;
	private final AbstractOrderModel source = new AbstractOrderModel();
	private final AbstractOrderModel target = new AbstractOrderModel();
	private final AbstractOrderEntryModel targetEntry = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel targetEntry2 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel sourceEntry = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel sourceEntry2 = new AbstractOrderEntryModel();
	private final List<AbstractOrderEntryModel> sourceEntries = new ArrayList<>();
	private final List<AbstractOrderEntryModel> targetEntries = new ArrayList<>();

	private final Map<AbstractOrderEntryModel, Integer> entryNumberMappings = new LinkedHashMap<>();
	private final AbstractOrderEntryModel orderEntry_1 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel orderEntry_2 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel orderEntry_3 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel orderEntry_4 = new AbstractOrderEntryModel();
	private final AbstractOrderEntryModel orderEntry_5 = new AbstractOrderEntryModel();



	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		serviceLayerHelper = new DefaultConfigurationServiceLayerHelper(baseSiteService, userService);
		classUnderTest = new DefaultProductConfigCloneAbstractOrderHook(cpqService, serviceLayerHelper, ITEMS_START,
				ITEMS_INCREMENT);
		final AbstractOrderEntryProductInfoModel otherInfo = new AbstractOrderEntryProductInfoModel();
		final CloudCPQOrderEntryProductInfoModel cpqInfo = new CloudCPQOrderEntryProductInfoModel();
		//target cpq info must never contain a configuration id as it is excluded from clone
		cpqInfo.setConfigurationId(null);
		cpqInfo.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		final List<AbstractOrderEntryProductInfoModel> productInfos = new ArrayList<>();
		productInfos.add(otherInfo);
		productInfos.add(cpqInfo);
		targetEntry.setProductInfos(productInfos);
		targetEntry.setEntryNumber(Integer.valueOf(0));
		targetEntry2.setEntryNumber(Integer.valueOf(1));
		targetEntries.add(targetEntry2);
		targetEntries.add(targetEntry);
		target.setEntries(targetEntries);

		final AbstractOrderEntryProductInfoModel sourceOtherInfo = new AbstractOrderEntryProductInfoModel();
		final CloudCPQOrderEntryProductInfoModel sourceCpqInfo = new CloudCPQOrderEntryProductInfoModel();
		sourceCpqInfo.setConfigurationId(CONFIG_ID);
		sourceCpqInfo.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		final List<AbstractOrderEntryProductInfoModel> sourceProductInfos = new ArrayList<>();
		sourceProductInfos.add(sourceOtherInfo);
		sourceProductInfos.add(sourceCpqInfo);
		sourceEntry.setProductInfos(sourceProductInfos);
		sourceEntry.setEntryNumber(Integer.valueOf(0));
		sourceEntry2.setEntryNumber(Integer.valueOf(1));
		sourceEntries.add(sourceEntry2);
		sourceEntries.add(sourceEntry);
		source.setEntries(sourceEntries);

		given(cpqService.cloneConfiguration(CONFIG_ID, true)).willReturn(CONFIG_ID_2);
	}

	@Test
	public void testBeforCloneDoesNothing()
	{
		classUnderTest.beforeClone(null, OrderModel.class);
		verifyNoInteractions(cpqService);
	}

	@Test
	public void testBeforCloneEntriesDoesNothing()
	{
		classUnderTest.beforeCloneEntries(null);
		verifyNoInteractions(cpqService);
	}

	@Test
	public void testAfterCloneEntriesDoesNothing()
	{
		classUnderTest.afterCloneEntries(null, null);
		verifyNoInteractions(cpqService);
	}


	@Test
	public void testAfterCloneShouldCloneConfigWhenNeeded()
	{
		final OrderModel order = new OrderModel();
		order.setEntries(sourceEntries);
		final CartModel cart = new CartModel();
		cart.setEntries(targetEntries);

		classUnderTest.afterClone(order, cart, CartModel.class);
		final List<AbstractOrderEntryModel> entries = cart.getEntries();
		assertEquals(2, entries.size());
		assertNull(serviceLayerHelper.getCPQInfo(entries.get(0)));
		assertEquals(CONFIG_ID_2, serviceLayerHelper.getCPQInfo(entries.get(1)).getConfigurationId());
		verify(cpqService).cloneConfiguration(CONFIG_ID, true);
	}

	@Test
	public void testAfterCloneShouldNotCloneConfigWhenNotNeeded()
	{
		final OrderModel order = new OrderModel();
		order.setEntries(targetEntries);
		final CartModel cart = new CartModel();
		cart.setEntries(sourceEntries);

		classUnderTest.afterClone(cart, order, OrderModel.class);
		final List<AbstractOrderEntryModel> entries = order.getEntries();
		assertEquals(2, entries.size());
		assertNull(serviceLayerHelper.getCPQInfo(entries.get(0)));
		assertEquals(CONFIG_ID, serviceLayerHelper.getCPQInfo(entries.get(1)).getConfigurationId());
		verifyNoInteractions(cpqService);
	}

	@Test
	public void testAfterCloneWithNullEntries()
	{
		final OrderModel order = new OrderModel();
		classUnderTest.afterClone(new CartModel(), order, OrderModel.class);
		assertNull(order.getEntries());
	}

	@Test(expected = IllegalStateException.class)
	public void testAfterCloneWithDifferntListSizes()
	{
		final OrderModel order = new OrderModel();
		order.setEntries(targetEntries);
		classUnderTest.afterClone(new CartModel(), order, OrderModel.class);
	}


	@Test
	public void testCloneConfig()
	{
		classUnderTest.cloneConfig(source, sourceEntry, targetEntry);
		final CloudCPQOrderEntryProductInfoModel productInfo = serviceLayerHelper.getCPQInfo(targetEntry);
		assertEquals(CONFIG_ID_2, productInfo.getConfigurationId());
	}

	@Test
	public void testIsCloneConfigurationNeededForQuoteToQuoteTrue()
	{
		assertTrue(classUnderTest.isCloneConfigNeeded(new QuoteModel(), QuoteModel.class));
	}

	@Test
	public void testIsCloneConfigurationNeededForCartToCartTrue()
	{
		assertTrue(classUnderTest.isCloneConfigNeeded(new CartModel(), CartModel.class));
	}

	@Test
	public void testIsCloneConfigurationNeededForQuoteToCartTrue()
	{
		assertTrue(classUnderTest.isCloneConfigNeeded(new QuoteModel(), CartModel.class));
	}

	@Test
	public void testIsCloneConfigurationNeededForOrderToCartTrue()
	{
		assertTrue(classUnderTest.isCloneConfigNeeded(new OrderModel(), CartModel.class));
	}

	@Test
	public void testIsCloneConfigurationNeededForCartToQuoteFalse()
	{
		assertFalse(classUnderTest.isCloneConfigNeeded(new CartModel(), QuoteModel.class));
	}

	@Test
	public void testIsCloneConfigurationNeededForCartToOrderFalse()
	{
		assertFalse(classUnderTest.isCloneConfigNeeded(new CartModel(), OrderModel.class));
	}

	@Test
	public void testIsOrderToCartFalse()
	{
		assertFalse(classUnderTest.isOrderToCart(new CartModel(), CartModel.class));
	}

	@Test
	public void testIsSubClassTrue()
	{
		assertTrue(classUnderTest.isSubClass(QuoteModel.class, QuoteModelSubClassForTest.class));
	}

	@Test
	public void testIsSubClassFalse()
	{
		assertFalse(classUnderTest.isSubClass(QuoteModel.class, CartModel.class));
	}

	@Test
	public void testCloneConfigHandlesNullSource()
	{
		classUnderTest.cloneConfig(null, null, targetEntry);
		verifyNoInteractions(cpqService);
	}

	@Test
	public void testCloneConfigHandlesNullTarget()
	{
		classUnderTest.cloneConfig(source, null, null);
		verifyNoInteractions(cpqService);
	}

	@Test
	public void testClopyConfigHandlesNullSource()
	{
		classUnderTest.copyConfig(null, targetEntry);
		verifyNoInteractions(cpqService);
	}

	@Test
	public void testCopyConfigHandlesNullTarget()
	{
		classUnderTest.copyConfig(sourceEntry, null);
		verifyNoInteractions(cpqService);
	}


	@Test
	public void testCheckIfEntryNumbersIncrementedByOneWithEmptyOrNullValues()
	{
		assertFalse("Entry numbers should not be incremeted by 1",
				classUnderTest.checkIfEntryNumbersIncrementedByOne(entryNumberMappings.values()));
	}

	@Test
	public void testCheckIfEntryNumbersIncrementedByOneWithOnlyOneValueInValuesList()
	{
		orderEntry_1.setEntryNumber(0);
		entryNumberMappings.put(orderEntry_1, 0);

		assertTrue("Entry numbers should be incremeted by 1",
				classUnderTest.checkIfEntryNumbersIncrementedByOne(entryNumberMappings.values()));
	}

	@Test
	public void testCheckIfEntryNumbersIncrementedByOneWithTwoValuesInValuesList()
	{
		orderEntry_1.setEntryNumber(0);
		orderEntry_2.setEntryNumber(10);
		entryNumberMappings.put(orderEntry_1, 0);
		entryNumberMappings.put(orderEntry_2, 10);

		assertFalse("Entry numbers should not be incremeted by 1",
				classUnderTest.checkIfEntryNumbersIncrementedByOne(entryNumberMappings.values()));
	}

	@Test
	public void testCheckIfEntryNumbersIncrementedByOneTrue()
	{
		setUp(ITEMS_START, ITEMS_INCREMENT);
		assertTrue("Entry numbers should be incremeted by 1",
				classUnderTest.checkIfEntryNumbersIncrementedByOne(entryNumberMappings.values()));
	}

	@Test
	public void testCheckIfEntryNumbersIncrementedByOneFalse()
	{
		setUpWithWrongEntryNumbersIncrementation(ITEMS_START, ITEMS_INCREMENT);
		assertFalse("Entry numbers should not be incremeted by 1",
				classUnderTest.checkIfEntryNumbersIncrementedByOne(entryNumberMappings.values()));
	}

	@Test
	public void testUpdateEntryNumbers()
	{
		setUp(ITEMS_START, ITEMS_INCREMENT);
		classUnderTest.updateEntryNumbers(entryNumberMappings);

		assertEquals("10", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("20", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("30", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("40", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("50", entryNumberMappings.get(orderEntry_5).toString());
	}

	@Test
	public void testUpdateEntryNumbersWithAnotherItemsIncrement()
	{
		setUp(ANOTHER_ITEMS_START, ANOTHER_ITEMS_INCREMENT);
		classUnderTest.updateEntryNumbers(entryNumberMappings);

		assertEquals("100", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("103", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("106", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("109", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("112", entryNumberMappings.get(orderEntry_5).toString());
	}


	@Test
	public void testAdjustEntryNumbers()
	{
		setUp(ITEMS_START, ITEMS_INCREMENT);
		classUnderTest.adjustEntryNumbers(entryNumberMappings);

		assertEquals("10", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("20", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("30", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("40", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("50", entryNumberMappings.get(orderEntry_5).toString());
	}

	@Test
	public void testAdjustEntryNumbersWithAnotherItemsIncrement()
	{
		setUp(ANOTHER_ITEMS_START, ANOTHER_ITEMS_INCREMENT);
		classUnderTest.adjustEntryNumbers(entryNumberMappings);

		assertEquals("100", entryNumberMappings.get(orderEntry_1).toString());
		assertEquals("103", entryNumberMappings.get(orderEntry_2).toString());
		assertEquals("106", entryNumberMappings.get(orderEntry_3).toString());
		assertEquals("109", entryNumberMappings.get(orderEntry_4).toString());
		assertEquals("112", entryNumberMappings.get(orderEntry_5).toString());
	}

	protected void setUp(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigCloneAbstractOrderHook(cpqService, serviceLayerHelper, itemsStart, itemsIncrement);

		orderEntry_1.setEntryNumber(0);
		orderEntry_2.setEntryNumber(1);
		orderEntry_3.setEntryNumber(2);
		orderEntry_4.setEntryNumber(3);
		orderEntry_5.setEntryNumber(4);

		entryNumberMappings.put(orderEntry_1, 0);
		entryNumberMappings.put(orderEntry_2, 1);
		entryNumberMappings.put(orderEntry_3, 2);
		entryNumberMappings.put(orderEntry_4, 3);
		entryNumberMappings.put(orderEntry_5, 4);
	}

	protected void setUpWithWrongEntryNumbersIncrementation(final int itemsStart, final int itemsIncrement)
	{
		classUnderTest = new DefaultProductConfigCloneAbstractOrderHook(cpqService, serviceLayerHelper, itemsStart, itemsIncrement);

		orderEntry_1.setEntryNumber(0);
		orderEntry_2.setEntryNumber(2);
		orderEntry_3.setEntryNumber(4);
		orderEntry_4.setEntryNumber(6);
		orderEntry_5.setEntryNumber(8);

		entryNumberMappings.put(orderEntry_1, 0);
		entryNumberMappings.put(orderEntry_2, 2);
		entryNumberMappings.put(orderEntry_3, 4);
		entryNumberMappings.put(orderEntry_4, 6);
		entryNumberMappings.put(orderEntry_5, 8);
	}

	private abstract class QuoteModelSubClassForTest extends QuoteModel
	{
		//empty
	}

}
