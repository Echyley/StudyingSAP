/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.populator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.OrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryAttributeData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryLineItemData;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Unit test for {@link ConfigurationInfoDataPopulator}
 */
@RunWith(MockitoJUnitRunner.class)
@UnitTest
public class ConfigurationInfoDataPopulatorTest
{
	private static final String FORMATTED_PRICE = "formattedPrice";
	private static final String QUANTITY_WITH_TRAILING_ZERO = "2.00";
	private static final String CONFIG_ID = "config id";
	private static final String CURRENCY_ISO_CODE = "USD";
	private static final String CONFIGURATION_SUMMARY = "{ \"configuration\":{ \"productSystemId\": \"CONF_LAPTOP\",  \"completed\": \"true\",  \"totalPrice\": \"2735.23\",  \"totalMRCPrice\": \"726.23\",  \"currencyISOCode\": \"USD\",  \"lineItems\": [    {      \"description\": \"Brewery unit (in this case value represents a product, and we need the product system ID)\",      \"productSystemId\": \"CONF_BREW\",      \"price\": \"23\",      \"recurringPrice\": \"1\",      \"quantity\": \"1\",  \"attribute\":{\"dataType\": \"Quantity\",    \"displayAs\": \"1\"}},    {      \"description\": \"Brewing mode (in this case value does not represent a product, thus product system ID is blank)\",      \"productSystemId\": \"CONF_BREW_MODE\",      \"price\": \"24\",      \"recurringPrice\": \"0\",      \"quantity\": \"1.00\",   \"attribute\":{ \"dataType\": \"String\",    \"displayAs\": \"1\"},  \"subLineItems\": [        {          \"description\": \"This is not relevant now but maybe comes later! Note that in a long term solution, we would not restict the nesting level\",          \"productSystemId\": \"CONF_MILK_FOAMER\",          \"price\": \"23\",          \"recurringPrice\": \"1\",          \"quantity\": \"1\" , \"attribute\": {\"dataType\": \"Quantity\",    \"displayAs\": \"1\"}       }      ]    }  ]}}";
	private static final String CONFIGURATION_SUMMARY_NO_LINEITEMS = "{\"configuration\":{  \"productSystemId\": \"CONF_LAPTOP\",  \"completed\": \"true\",  \"totalPrice\": \"2735.23\",  \"totalMRCPrice\": \"726.23\",  \"currencyISOCode\": \"USD\",  \"lineItems\": []}}";
	private static final String CONFIGURATION_SUMMARY_LINEITEM_WITHOUT_PRICE = "{\"configuration\":{  \"productSystemId\": \"CONF_LAPTOP\",  \"completed\": \"true\",  \"totalPrice\": \"2735.23\",  \"totalMRCPrice\": \"726.23\",  \"currencyISOCode\": \"USD\",  \"lineItems\": [{    \"description\": \"Brewery unit \",  \"productSystemId\": \"CONF_BREW\",     \"quantity\": \"1\",  \"attribute\":{\"dataType\": \"Quantity\",    \"displayAs\": \"1\"}} ]}}";

	private static final String CURRENCY = "USD";



	@Mock
	private ConfigurationService configurationService;
	@Mock
	private PriceDataFactory priceDataFactory;
	@Mock
	private CommonI18NService commonI18NService;

	@InjectMocks
	private ConfigurationInfoDataPopulator<AbstractOrderEntryProductInfoModel> classUnderTest;
	private final CloudCPQOrderEntryProductInfoModel source = new CloudCPQOrderEntryProductInfoModel();
	private final List<ConfigurationInfoData> target = new ArrayList<>();
	private ConfigurationSummaryData configurationSummary;

	@Mock
	private CurrencyModel currentCurrency;
	private final ConfigurationSummaryLineItemData lineItem = new ConfigurationSummaryLineItemData();
	private final ConfigurationSummaryAttributeData lineItemAttr = new ConfigurationSummaryAttributeData();
	private final OrderEntryModel entry = new OrderEntryModel();


	@Before
	public void setup() throws JsonProcessingException
	{
		configurationSummary = prepareConfigurationSummary(CONFIGURATION_SUMMARY);

		final PriceData priceData = new PriceData();
		priceData.setFormattedValue(FORMATTED_PRICE);

		when(priceDataFactory.create(any(), any(BigDecimal.class), eq(CURRENCY))).thenReturn(priceData);
		when(configurationService.getConfigurationSummary(CONFIG_ID, entry)).thenReturn(configurationSummary);

		source.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		source.setProductInfoStatus(ProductInfoStatus.SUCCESS);
		source.setConfigurationId(CONFIG_ID);
		source.setOrderEntry(entry);

		lineItem.setQuantity(new BigDecimal(2));
		lineItem.setAttribute(lineItemAttr);

		lineItemAttr.setDataType(ConfigurationInfoDataPopulator.DATA_TYPE_QTY_ATTRIBUTE_LEVEL);
		lineItemAttr.setDisplayAs(ConfigurationInfoDataPopulator.DISPLAY_AS_CHECK_BOX);
	}

	@Test
	public void testPopulateCPQVersion()
	{
		classUnderTest.populate(source, target);
		assertEquals(ConfiguratorType.CLOUDCPQCONFIGURATOR, target.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, target.get(0).getStatus());
		assertEquals("CI#@#VERSION", target.get(0).getConfigurationLabel());
		assertEquals("2", target.get(0).getConfigurationValue());
	}

	@Test
	public void testPopulateCPQCloud()
	{
		classUnderTest.populate(source, target);
		assertEquals(ConfiguratorType.CLOUDCPQCONFIGURATOR, target.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, target.get(0).getStatus());
		assertEquals(12, target.size()); // expect one line for version, one for currency and 2 x 5 lines for the line items
		assertEquals("CI#@#CURRENCY_ISO_CODE", target.get(1).getConfigurationLabel());
		assertEquals(CURRENCY_ISO_CODE, target.get(1).getConfigurationValue());

		assertEquals("LI#0#KEY", target.get(2).getConfigurationLabel());
		assertEquals("CONF_BREW", target.get(2).getConfigurationValue());
		assertEquals("LI#0#NAME", target.get(3).getConfigurationLabel());
		assertEquals("Brewery unit (in this case value represents a product, and we need the product system ID)",
				target.get(3).getConfigurationValue());
		assertEquals("LI#0#QTY", target.get(4).getConfigurationLabel());
		assertEquals("1", target.get(4).getConfigurationValue());
		assertEquals("LI#0#FORMATTED_PRICE", target.get(5).getConfigurationLabel());
		assertEquals(FORMATTED_PRICE, target.get(5).getConfigurationValue());
		assertEquals("LI#0#PRICE_VALUE", target.get(6).getConfigurationLabel());
		assertEquals("23", target.get(6).getConfigurationValue());

		assertEquals("LI#1#KEY", target.get(7).getConfigurationLabel());
		assertEquals("CONF_BREW_MODE", target.get(7).getConfigurationValue());
		assertEquals("LI#1#NAME", target.get(8).getConfigurationLabel());
		assertEquals("Brewing mode (in this case value does not represent a product, thus product system ID is blank)",
				target.get(8).getConfigurationValue());
		assertEquals("LI#1#QTY", target.get(9).getConfigurationLabel());
		assertEquals("", target.get(9).getConfigurationValue());
		assertEquals("LI#1#FORMATTED_PRICE", target.get(10).getConfigurationLabel());
		assertEquals(FORMATTED_PRICE, target.get(10).getConfigurationValue());
		assertEquals("LI#1#PRICE_VALUE", target.get(11).getConfigurationLabel());
		assertEquals("24", target.get(11).getConfigurationValue());

	}

	@Test
	public void testPopulateCPQCloudNoLineItems() throws JsonProcessingException
	{
		configurationSummary = prepareConfigurationSummary(CONFIGURATION_SUMMARY_NO_LINEITEMS);
		when(configurationService.getConfigurationSummary(CONFIG_ID, entry)).thenReturn(configurationSummary);
		classUnderTest.populate(source, target);
		assertEquals(ConfiguratorType.CLOUDCPQCONFIGURATOR, target.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, target.get(0).getStatus());
		assertEquals(1, target.size());
	}

	@Test
	public void testPopulateCPQCloudLineItemWithoutPrice() throws JsonProcessingException
	{
		configurationSummary = prepareConfigurationSummary(CONFIGURATION_SUMMARY_LINEITEM_WITHOUT_PRICE);
		when(configurationService.getConfigurationSummary(CONFIG_ID, entry)).thenReturn(configurationSummary);
		classUnderTest.populate(source, target);
		assertEquals(5, target.size());
		assertEquals(ConfiguratorType.CLOUDCPQCONFIGURATOR, target.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, target.get(0).getStatus());
		assertEquals("CI#@#CURRENCY_ISO_CODE", target.get(1).getConfigurationLabel());
		assertEquals(CURRENCY_ISO_CODE, target.get(1).getConfigurationValue());

		assertEquals("LI#0#KEY", target.get(2).getConfigurationLabel());
		assertEquals("CONF_BREW", target.get(2).getConfigurationValue());
		assertEquals("LI#0#NAME", target.get(3).getConfigurationLabel());
		assertEquals("Brewery unit ",
				target.get(3).getConfigurationValue());
		assertEquals("LI#0#QTY", target.get(4).getConfigurationLabel());
		assertEquals("1", target.get(4).getConfigurationValue());
	}


	@Test
	public void testPopulateCPQCloudNoConfigId()
	{
		source.setConfigurationId("");
		classUnderTest.populate(source, target);
		assertEquals(ConfiguratorType.CLOUDCPQCONFIGURATOR, target.get(0).getConfiguratorType());
		assertEquals(ProductInfoStatus.SUCCESS, target.get(0).getStatus());
	}

	@Test(expected = ConversionException.class)
	public void testPopulateOtherSource()
	{
		final AbstractOrderEntryProductInfoModel otherSource = new AbstractOrderEntryProductInfoModel();
		otherSource.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		classUnderTest.populate(otherSource, target);
	}

	@Test
	public void testPopulateOtherType()
	{
		source.setConfiguratorType(null);
		classUnderTest.populate(source, target);
		assertEquals(0, target.size());
	}

	@Test
	public void testGetStatusOk()
	{
		when(configurationService.hasConfigurationIssues(CONFIG_ID)).thenReturn(false);
		assertEquals(ProductInfoStatus.SUCCESS, classUnderTest.getStatus(CONFIG_ID));
	}

	@Test
	public void testStatusError()
	{
		when(configurationService.hasConfigurationIssues(CONFIG_ID)).thenReturn(true);
		assertEquals(ProductInfoStatus.ERROR, classUnderTest.getStatus(CONFIG_ID));
	}

	@Test
	public void testGetStatusWithImpersonationOk()
	{
		when(configurationService.hasConfigurationIssues(CONFIG_ID, entry)).thenReturn(false);
		assertEquals(ProductInfoStatus.SUCCESS, classUnderTest.getStatus(CONFIG_ID, entry));
	}

	@Test
	public void testStatusWithImpersonationError()
	{
		when(configurationService.hasConfigurationIssues(CONFIG_ID, entry)).thenReturn(true);
		assertEquals(ProductInfoStatus.ERROR, classUnderTest.getStatus(CONFIG_ID, entry));
	}

	@Test
	public void testFormatPrice()
	{
		final BigDecimal price = new BigDecimal("4.99");
		final String result = classUnderTest.formatPrice(price, CURRENCY);
		assertNotNull(result);
		assertEquals(FORMATTED_PRICE, result);
		verify(priceDataFactory).create(PriceDataType.BUY, price, CURRENCY);
	}

	@Test
	public void testGetLineItemQuantityString()
	{
		assertEquals("2", classUnderTest.getLineItemQuantityString(lineItem));
	}



	@Test
	public void testGetLineItemQuantityStringQTY_VALUE_LEVEL_notForRB()
	{
		lineItemAttr.setDataType(ConfigurationInfoDataPopulator.DATA_TYPE_QTY_VALUE_LEVEL);
		lineItemAttr.setDisplayAs(ConfigurationInfoDataPopulator.DISPLAY_AS_RADIO_BUTTON);
		assertEquals("", classUnderTest.getLineItemQuantityString(lineItem));
	}

	@Test
	public void testGetLineItemQuantityStringQTY_VALUE_LEVEL_notForDROPDOWN()
	{
		lineItemAttr.setDataType(ConfigurationInfoDataPopulator.DATA_TYPE_QTY_VALUE_LEVEL);
		lineItemAttr.setDisplayAs(ConfigurationInfoDataPopulator.DISPLAY_AS_DROPDOWN);
		assertEquals("", classUnderTest.getLineItemQuantityString(lineItem));
	}

	@Test
	public void testGetLineItemQuantityStringQTY_VALUE_LEVEL()
	{
		lineItemAttr.setDataType(ConfigurationInfoDataPopulator.DATA_TYPE_QTY_VALUE_LEVEL);
		assertEquals("2", classUnderTest.getLineItemQuantityString(lineItem));
	}


	@Test
	public void testGetLineItemQuantityStringNoQuantityType()
	{
		lineItemAttr.setDataType(ConfigurationInfoDataPopulator.DATA_TYPE_INPUT_STRING);
		assertEquals("", classUnderTest.getLineItemQuantityString(lineItem));
	}

	protected ConfigurationSummaryData prepareConfigurationSummary(final String configurationSummaryJson)
			throws JsonProcessingException
	{
		final ObjectMapper mapper = new ObjectMapper();
		mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper.readValue(configurationSummaryJson, ConfigurationSummaryData.class);
	}


}
