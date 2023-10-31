/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.facades.populator;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.product.PriceDataFactory;
import de.hybris.platform.commercefacades.product.data.PriceData;
import de.hybris.platform.commercefacades.product.data.PriceDataType;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.cpq.productconfig.services.ConfigurationService;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryAttributeData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryData;
import de.hybris.platform.cpq.productconfig.services.data.ConfigurationSummaryLineItemData;
import de.hybris.platform.cpq.productconfig.services.model.CloudCPQOrderEntryProductInfoModel;
import de.hybris.platform.order.model.AbstractOrderEntryProductInfoModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;


/**
 * Populates the {@link ConfigurationInfoData} for the CPQCloudConfigurator.
 *
 * @param <T>
 *           concreate AbstractOrderEntryProductInfoModel type
 */
public class ConfigurationInfoDataPopulator<T extends AbstractOrderEntryProductInfoModel>
		implements Populator<T, List<ConfigurationInfoData>>
{

	private static final Logger LOG = Logger.getLogger(ConfigurationInfoDataPopulator.class);
	private static final String CURRENCY_ISO_CODE = "CI#@#CURRENCY_ISO_CODE";
	private static final String LINE_ITEM_FORMAT_TEMPLATE = "LI#%d#%s";
	private static final String VERSION_LABEL = "CI#@#VERSION";
	private static final String VERSION_VALUE = "2";
	protected static final String DATA_TYPE_QTY_ATTRIBUTE_LEVEL = "Quantity";
	protected static final String DATA_TYPE_INPUT_STRING = "String";
	protected static final int DISPLAY_AS_RADIO_BUTTON = 1;
	protected static final String DATA_TYPE_QTY_VALUE_LEVEL = "Attr.Quantity";
	protected static final int DISPLAY_AS_CHECK_BOX = 2;
	protected static final int DISPLAY_AS_DROPDOWN = 3;

	private final ConfigurationService configurationService;
	private final PriceDataFactory priceDataFactory;
	private final CommonI18NService commonI18NService;

	/**
	 * Default constructor
	 *
	 * @param configurationService
	 *           configuration service
	 * @param priceDataFactory
	 *           price data factory
	 * @param commonI18NService
	 *           common I18N service
	 */
	public ConfigurationInfoDataPopulator(final ConfigurationService configurationService, final PriceDataFactory priceDataFactory,
			final CommonI18NService commonI18NService)
	{
		this.configurationService = configurationService;
		this.priceDataFactory = priceDataFactory;
		this.commonI18NService = commonI18NService;
	}

	@Override
	public void populate(final T source, final List<ConfigurationInfoData> target)
	{
		if (ConfiguratorType.CLOUDCPQCONFIGURATOR.equals(source.getConfiguratorType()))
		{
			if (!(source instanceof CloudCPQOrderEntryProductInfoModel))
			{
				throw new ConversionException(
						"Instance with type " + source.getConfiguratorType() + " is of class " + source.getClass().getName()
								+ " which is not convertible to " + CloudCPQOrderEntryProductInfoModel.class.getName());
			}

			final CloudCPQOrderEntryProductInfoModel model = (CloudCPQOrderEntryProductInfoModel) source;
			List<ConfigurationSummaryLineItemData> lineItems = null;
			String currency = null;
			final String configurationId = model.getConfigurationId();
			final ProductInfoStatus productStatus = model.getProductInfoStatus();
			ProductInfoStatus configStatus = null;

			ConfigurationInfoData itemHeader = prepareItemData(target, productStatus);
			itemHeader.setConfigurationLabel(VERSION_LABEL);
			itemHeader.setConfigurationValue(VERSION_VALUE);


			if (StringUtils.isNotEmpty(configurationId))
			{
				final ConfigurationSummaryData configurationSummary = getConfigurationService()
						.getConfigurationSummary(configurationId, source.getOrderEntry());
				Preconditions.checkNotNull(configurationSummary, "We expect a summary");
				lineItems = configurationSummary.getConfiguration().getLineItems();
				currency = configurationSummary.getConfiguration().getCurrencyISOCode();
				configStatus = getStatus(configurationId, source.getOrderEntry());
				itemHeader.setStatus(configStatus);
			}

			if (CollectionUtils.isNotEmpty(lineItems))
			{
				itemHeader = prepareItemData(target, configStatus);
				itemHeader.setConfigurationLabel(CURRENCY_ISO_CODE);
				itemHeader.setConfigurationValue(currency);

				populateLineItems(target, lineItems, currency, configStatus);
			}
		}
	}

	protected void populateLineItems(final List<ConfigurationInfoData> target,
			final List<ConfigurationSummaryLineItemData> lineItems, final String currency, final ProductInfoStatus configStatus)
	{
		int index = 0;
		ConfigurationInfoData item;
		for (final ConfigurationSummaryLineItemData lineItem : lineItems)
		{
			item = prepareItemData(target, configStatus);
			item.setConfigurationLabel(String.format(LINE_ITEM_FORMAT_TEMPLATE, index, "KEY"));
			item.setConfigurationValue(lineItem.getProductSystemId());

			item = prepareItemData(target, configStatus);
			item.setConfigurationLabel(String.format(LINE_ITEM_FORMAT_TEMPLATE, index, "NAME"));
			item.setConfigurationValue(lineItem.getDescription());

			item = prepareItemData(target, configStatus);
			item.setConfigurationLabel(String.format(LINE_ITEM_FORMAT_TEMPLATE, index, "QTY"));
			item.setConfigurationValue(getLineItemQuantityString(lineItem));


			if (null != lineItem.getPrice() && BigDecimal.ZERO.compareTo(lineItem.getPrice()) != 0)
			{
				item = prepareItemData(target, configStatus);
				item.setConfigurationLabel(String.format(LINE_ITEM_FORMAT_TEMPLATE, index, "FORMATTED_PRICE"));
				item.setConfigurationValue(formatPrice(lineItem.getPrice(), currency));

				item = prepareItemData(target, configStatus);
				item.setConfigurationLabel(String.format(LINE_ITEM_FORMAT_TEMPLATE, index, "PRICE_VALUE"));
				item.setConfigurationValue(lineItem.getPrice().toPlainString());
			}
			index++;
		}
	}



	protected String getLineItemQuantityString(final ConfigurationSummaryLineItemData lineItem)
	{
		final ConfigurationSummaryAttributeData attribute = lineItem.getAttribute();
		final boolean displayAsIsSingleSelection = DISPLAY_AS_DROPDOWN == attribute.getDisplayAs()
				|| DISPLAY_AS_RADIO_BUTTON == attribute.getDisplayAs();
		if (DATA_TYPE_QTY_ATTRIBUTE_LEVEL.equals(attribute.getDataType())
				|| (DATA_TYPE_QTY_VALUE_LEVEL.equals(attribute.getDataType()) && !displayAsIsSingleSelection))
		{
			return lineItem.getQuantity().stripTrailingZeros().toPlainString();
		}
		else
		{
			LOG.debug(String.format("Discarding quantity for '%s' because of dataType='%s' and displayAs='%s'",
					lineItem.getDescription(), attribute.getDataType(), attribute.getDisplayAs()));
			return "";
		}
	}


	private ConfigurationInfoData prepareItemData(final List<ConfigurationInfoData> target, final ProductInfoStatus status)
	{
		ConfigurationInfoData item;
		item = new ConfigurationInfoData();
		item.setConfiguratorType(ConfiguratorType.CLOUDCPQCONFIGURATOR);
		item.setStatus(status);
		target.add(item);
		return item;
	}


	protected ProductInfoStatus getStatus(final String configurationId)
	{
		return getConfigurationService().hasConfigurationIssues(configurationId) ? ProductInfoStatus.ERROR
				: ProductInfoStatus.SUCCESS;
	}

	protected ProductInfoStatus getStatus(final String configurationId, final AbstractOrderEntryModel entry)
	{
		return getConfigurationService().hasConfigurationIssues(configurationId, entry) ? ProductInfoStatus.ERROR
				: ProductInfoStatus.SUCCESS;
	}

	protected String formatPrice(final BigDecimal price, final String currency)
	{
		//formatting of price is not influenced by choice of PriceDataType; just required from method signature
		final PriceData priceData = getPriceDataFactory().create(PriceDataType.BUY, price, currency);
		return priceData.getFormattedValue();
	}


	protected ConfigurationService getConfigurationService()
	{
		return configurationService;
	}

	protected PriceDataFactory getPriceDataFactory()
	{
		return priceDataFactory;
	}

	protected CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}

}
