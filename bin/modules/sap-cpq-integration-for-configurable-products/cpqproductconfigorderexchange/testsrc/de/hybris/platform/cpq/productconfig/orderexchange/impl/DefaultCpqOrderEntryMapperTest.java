/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.orderexchange.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryLineItemData;
import de.hybris.platform.cpq.productconfig.services.impl.DefaultConfigurationServiceLayerHelper;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.sap.sapcpiadapter.model.SAPCpiOutboundOrderItemModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;


/**
 * Unit test for {@link DefaultCpqOrderEntryMapper}
 */
@UnitTest
public class DefaultCpqOrderEntryMapperTest
{
	private static final String LINE_ITEM_PRODUCT_ID_LOWERCASE = "lowercaseproductid";
	private static final String CONFIG_ID = "configId";
	private DefaultCpqOrderEntryMapper classUnderTest;
	private DefaultCpqOrderEntryMapper classUnderTestNoHelperMock;
	private static final String DESCRIPTION = "Brewery unit (in this case value represents a product, and we need the product system ID)";
	private static final String ENTRY_NUMBER = "1";
	private static final String LINE_ITEM_PRODUCT_ID_1 = "CONF_BREW";
	private static final String LINE_ITEM_PRODUCT_ID_2 = "CONF_FILTER";
	private static final String CONFIGURATION_SUMMARY = "{\"configuration\":{  \"productSystemId\": \"CONF_LAPTOP\",  \"completed\": \"true\",  \"totalPrice\": \"2735.23\",  \"totalRecurringPrice\": \"726.23\",  \"rootPrice\": \"2603.23\",  \"rootRecurringPrice\": \"723.23\",  \"lineItems\": [    {      \"description\": \"Brewery unit (in this case value represents a product, and we need the product system ID)\",      \"productSystemId\": \"CONF_BREW\",      \"price\": \"23\",      \"recurringPrice\": \"1\",      \"quantity\": \"1\"    },    {      \"description\": \"Brewing mode (in this case value does not represent a product, thus product system ID is blank)\",      \"productSystemId\": \"\",      \"price\": \"24\",      \"recurringPrice\": \"0\",      \"quantity\": \"1\",      \"subLineItems\": [        {          \"description\": \"This is not relevant for 19.05 but maybe comes later! Note that in a long term solution, we would not restict the nesting level\",          \"productSystemId\": \"CONF_MILK_FOAMER\",          \"price\": \"23\",          \"recurringPrice\": \"1\",          \"quantity\": \"1\"        }      ]    }, {      \"description\": \"Brewing Filter\",      \"productSystemId\": \"CONF_FILTER\",      \"price\": \"37\",      \"recurringPrice\": \"1\",      \"quantity\": \"2\"    }  ]}}";
	private static final String CONFIGURATION_SUMMARY_NO_LINEITEMS = "{\"configuration\":{   \"productSystemId\": \"CONF_LAPTOP\",  \"completed\": \"true\",  \"totalPrice\": \"2735.23\",  \"totalRecurringPrice\": \"726.23\",  \"rootPrice\": \"2603.23\",  \"rootRecurringPrice\": \"723.23\",  \"lineItems\": []}}";


	@Mock
	private AbstractOrderEntryModel orderEntryModel;
	@Mock
	private ProductModel product;
	private final SAPCpiOutboundOrderItemModel outboundItem = new SAPCpiOutboundOrderItemModel();

	@Mock
	private ConfigurationService cpqService;
	@Mock
	private DefaultConfigurationServiceLayerHelper serviceLayerHelperMock;
	private DefaultConfigurationServiceLayerHelper serviceLayerHelper;
	@Mock
	private BaseSiteService baseSiteService;
	@Mock
	private UserService userService;
	@Mock
	private CloudCPQOrderEntryProductInfoModel infoModel;
	private List<SAPCpiOutboundOrderItemModel> cpqLineItems;
	private ConfigurationSummaryData configurationSummary;

	@Before
	public void setup() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		serviceLayerHelper = new DefaultConfigurationServiceLayerHelperForTest(baseSiteService, userService);
		configurationSummary = prepareConfigurationSummary(CONFIGURATION_SUMMARY);
		when(orderEntryModel.getProduct()).thenReturn(product);
		when(infoModel.getConfigurationId()).thenReturn(CONFIG_ID);
		when(serviceLayerHelperMock.getCPQInfo(orderEntryModel)).thenReturn(infoModel);
		when(cpqService.getConfigurationSummary(CONFIG_ID, orderEntryModel))
				.thenReturn(configurationSummary);
		cpqLineItems = new ArrayList<>();
		classUnderTest = new DefaultCpqOrderEntryMapper(cpqService, serviceLayerHelperMock, 1);
		classUnderTestNoHelperMock = new DefaultCpqOrderEntryMapper(cpqService, serviceLayerHelper, 1);
		outboundItem.setEntryNumber(ENTRY_NUMBER);
		outboundItem.setQuantity("1");
	}

	@Test
	public void testIsMapperApplicable()
	{
		final boolean result = classUnderTest.isMapperApplicable(orderEntryModel);
		assertTrue(result);
	}

	@Test
	public void testIsMapperApplicableFalse()
	{
		when(serviceLayerHelperMock.getCPQInfo(orderEntryModel)).thenReturn(null);
		final boolean result = classUnderTest.isMapperApplicable(orderEntryModel);
		assertFalse(result);
	}

	@Test
	public void testMapCPQLineItems()
	{
		final List<SAPCpiOutboundOrderItemModel> result = classUnderTestNoHelperMock.mapCPQLineItems(orderEntryModel, outboundItem);
		verify(cpqService).getConfigurationSummary(any(), any());
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(LINE_ITEM_PRODUCT_ID_1, result.get(0).getProductCode());
		assertEquals("2", result.get(0).getEntryNumber());
		assertEquals(LINE_ITEM_PRODUCT_ID_2, result.get(1).getProductCode());
		assertEquals("3", result.get(1).getEntryNumber());
	}

	@Test
	public void testMapSummaryLineItem()
	{
		final SAPCpiOutboundOrderItemModel result = classUnderTest
				.mapSummaryLineItem(configurationSummary.getConfiguration().getLineItems().get(0), outboundItem, 1);
		assertNotNull(result);
		assertEquals(LINE_ITEM_PRODUCT_ID_1, result.getProductCode());
		assertEquals(DESCRIPTION, result.getProductName());
		assertEquals(ENTRY_NUMBER, result.getHigherLevelEntryNumber());
		assertEquals("2", result.getEntryNumber());
	}

	@Test
	public void testMapSummaryLineItemUpperCase() throws Exception
	{
		final String CONFIGURATION_SUMMARY_WITH_LOWERCASE_ID = CONFIGURATION_SUMMARY.replace(LINE_ITEM_PRODUCT_ID_1,
				LINE_ITEM_PRODUCT_ID_LOWERCASE);
		configurationSummary = prepareConfigurationSummary(CONFIGURATION_SUMMARY_WITH_LOWERCASE_ID);
		final SAPCpiOutboundOrderItemModel result = classUnderTest
				.mapSummaryLineItem(configurationSummary.getConfiguration().getLineItems().get(0), outboundItem, 1);
		assertNotNull(result);
		assertEquals(LINE_ITEM_PRODUCT_ID_LOWERCASE.toUpperCase(), result.getProductCode());
		assertEquals(DESCRIPTION, result.getProductName());
		assertEquals(ENTRY_NUMBER, result.getHigherLevelEntryNumber());
		assertEquals("2", result.getEntryNumber());
	}

	@Test
	public void testMapSummaryLineQuantity()
	{
		final SAPCpiOutboundOrderItemModel result = classUnderTest
				.mapSummaryLineItem(configurationSummary.getConfiguration().getLineItems().get(2), outboundItem, 1);
		assertEquals(LINE_ITEM_PRODUCT_ID_2, result.getProductCode());
		assertEquals("2", result.getQuantity());
	}


	@Test
	public void testCalculateLineItemQuantity()
	{
		outboundItem.setQuantity("3");
		final String calculatedLineItemQuantity = classUnderTest.calculateLineItemQuantity(outboundItem,
				configurationSummary.getConfiguration().getLineItems().get(2));
		assertEquals("6", calculatedLineItemQuantity);
	}

	@Test
	public void testMapSummaryLineItemNoMappingNeeded()
	{
		final SAPCpiOutboundOrderItemModel result = classUnderTest
				.mapSummaryLineItem(configurationSummary.getConfiguration().getLineItems().get(1), outboundItem, 1);
		assertNull(result);
	}

	@Test
	public void testMapSummaryLineItems()
	{
		classUnderTest.mapSummaryLineItems(cpqLineItems, configurationSummary, outboundItem);
		assertEquals(2, cpqLineItems.size());
	}

	@Test
	public void testMapSummaryLineItemsSpacing2()
	{
		final DefaultCpqOrderEntryMapper anotherClassUnderTest = new DefaultCpqOrderEntryMapper(cpqService, serviceLayerHelperMock,
				2);
		anotherClassUnderTest.mapSummaryLineItems(cpqLineItems, configurationSummary, outboundItem);
		assertEquals(2, cpqLineItems.size());
		assertEquals("3", cpqLineItems.get(0).getEntryNumber());
		assertEquals("5", cpqLineItems.get(1).getEntryNumber());
	}

	@Test
	public void testIncrementEntryNumber()
	{
		final String result = classUnderTest.incrementEntryNumber("5", 3);
		assertEquals("8", result);
	}

	@Test
	public void testRetrieveCPQLineItems()
	{
		classUnderTest.retrieveCPQLineItems(orderEntryModel, outboundItem, cpqLineItems);
		assertEquals(2, cpqLineItems.size());
		assertEquals(LINE_ITEM_PRODUCT_ID_1, cpqLineItems.get(0).getProductCode());
		assertEquals(LINE_ITEM_PRODUCT_ID_2, cpqLineItems.get(1).getProductCode());
	}

	@Test
	public void testRetrieveCPQLineItemsNoLineItemsPresent() throws Exception
	{
		when(cpqService.getConfigurationSummary(CONFIG_ID, orderEntryModel))
				.thenReturn(prepareConfigurationSummary(CONFIGURATION_SUMMARY_NO_LINEITEMS));
		classUnderTest.retrieveCPQLineItems(orderEntryModel, outboundItem, cpqLineItems);
		assertNotNull(cpqLineItems);
		assertEquals(0, cpqLineItems.size());
	}

	@Test
	public void testIsMappingNeeded()
	{
		final ConfigurationSummaryLineItemData summaryLineItem = new ConfigurationSummaryLineItemData();
		summaryLineItem.setProductSystemId(LINE_ITEM_PRODUCT_ID_1);
		assertTrue(classUnderTest.isMappingNeeded(summaryLineItem));
	}

	@Test
	public void testIsMappingNeededFalse()
	{
		final ConfigurationSummaryLineItemData summaryLineItem = new ConfigurationSummaryLineItemData();
		assertFalse(classUnderTest.isMappingNeeded(summaryLineItem));
	}

	protected ConfigurationSummaryData prepareConfigurationSummary(final String configurationSummaryJson) throws Exception
	{
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(configurationSummaryJson, ConfigurationSummaryData.class);
	}


	private class DefaultConfigurationServiceLayerHelperForTest extends DefaultConfigurationServiceLayerHelper
	{

		public DefaultConfigurationServiceLayerHelperForTest(final BaseSiteService baseSiteService, final UserService userService)
		{
			super(baseSiteService, userService);
		}

		@Override
		public CloudCPQOrderEntryProductInfoModel getCPQInfo(final AbstractOrderEntryModel targetEntry)
		{
			return infoModel;
		}
	}
}
