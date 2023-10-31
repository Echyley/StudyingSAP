/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapproductconfigsomservices.converters.populators;

import de.hybris.platform.catalog.enums.ConfiguratorType;
import de.hybris.platform.catalog.enums.ProductInfoStatus;
import de.hybris.platform.commercefacades.order.data.ConfigurationInfoData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.product.ProductService;
import de.hybris.platform.sap.productconfig.facades.VariantConfigurationInfoProvider;
import de.hybris.platform.sap.productconfig.runtime.interf.model.ConfigModel;
import de.hybris.platform.sap.productconfig.services.SessionAccessService;
import de.hybris.platform.sap.productconfig.services.data.CartEntryConfigurationAttributes;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationOrderIntegrationService;
import de.hybris.platform.sap.productconfig.services.intf.ProductConfigurationService;
import de.hybris.platform.sap.sapordermgmtbol.transaction.item.businessobject.interf.Item;
import de.hybris.platform.sap.sapproductconfigsombol.transaction.item.businessobject.impl.CPQItem;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;


/**
 *
 */
public class DefaultAbstractOrderEntryConfigurablePopulator implements Populator<Item, OrderEntryData>
{
	private static final Logger LOG = Logger.getLogger(DefaultAbstractOrderEntryConfigurablePopulator.class);
	public static final int ECC_HANDLE_LENGTH = 32;
	private ProductConfigurationService productConfigurationService;
	private ProductConfigurationOrderIntegrationService productConfigurationOrderIntegrationService;
	private SessionAccessService sessionAccessService;
	private Converter<ConfigModel, List<ConfigurationInfoData>> orderEntryConfigurationInfoConverter;
	private VariantConfigurationInfoProvider variantConfigurationInfoProvider;
	private ProductService productService;


	/**
	 * @return the orderEntryConfigurationInfoConverter
	 */
	public Converter<ConfigModel, List<ConfigurationInfoData>> getOrderEntryConfigurationInfoConverter()
	{
		return orderEntryConfigurationInfoConverter;
	}

	/**
	 * @param orderEntryConfigurationInfoConverter
	 *           the orderEntryConfigurationInfoConverter to set
	 */
	public void setOrderEntryConfigurationInfoConverter(
			final Converter<ConfigModel, List<ConfigurationInfoData>> orderEntryConfigurationInfoConverter)
	{
		this.orderEntryConfigurationInfoConverter = orderEntryConfigurationInfoConverter;
	}

	/**
	 * @return the productConfigurationService
	 */
	public ProductConfigurationService getProductConfigurationService()
	{
		return productConfigurationService;
	}

	protected ProductConfigurationOrderIntegrationService getProductConfigurationOrderIntegrationService()
	{
		return productConfigurationOrderIntegrationService;
	}

	/**
	 * @param productConfigurationOrderIntegrationService
	 *           The productConfigurationOrderIntegrationService to set
	 */
	@Required
	public void setProductConfigurationOrderIntegrationService(
			final ProductConfigurationOrderIntegrationService productConfigurationOrderIntegrationService)
	{
		this.productConfigurationOrderIntegrationService = productConfigurationOrderIntegrationService;
	}


	/**
	 * @param productConfigurationService
	 *           the productConfigurationService to set
	 */
	@Required
	public void setProductConfigurationService(final ProductConfigurationService productConfigurationService)
	{
		this.productConfigurationService = productConfigurationService;
	}

	/**
	 * @return the sessionAccessService
	 */
	public SessionAccessService getSessionAccessService()
	{
		return sessionAccessService;
	}

	/**
	 * @param sessionAccessService
	 *           the sessionAccessService to set
	 */
	@Required
	public void setSessionAccessService(final SessionAccessService sessionAccessService)
	{
		this.sessionAccessService = sessionAccessService;
	}

	@Override
	public void populate(final Item item, final OrderEntryData target)
	{
		final String itemHandle = item.getHandle();
		target.setItemPK(item.getHandle());
		if (itemHandle.length() != ECC_HANDLE_LENGTH)
		{
			LOG.debug("item has not been persisted yet, so configuration has to be assigned to the item " + itemHandle);
			handleConfiguration(item, target, target.getProduct());
		}
		else
		{
			LOG.debug("we deal with an existing ECC item:  " + itemHandle);
			handleConfigurationBackendLeads(item, target, target.getProduct());
		}
	}

	/**
	 * @param item
	 * @param target
	 * @param productData
	 */
	protected void handleConfiguration(final Item item, final OrderEntryData target, final ProductData productData)
	{
		target.setConfigurationAttached(item.isConfigurable());


		if (item.isConfigurable())
		{
			productData.setConfigurable(Boolean.TRUE);

			// We will still call the deprecated method as we cannot use the new method, because SOM does bypass the SAP Commerce model layer,
			// AbstractOrderEntryModel is not present and cannot be used to call the new method. 
			// The deprecated method must therefore not be deleted.
			final CartEntryConfigurationAttributes configAttributes = getProductConfigurationOrderIntegrationService()
					.calculateCartEntryConfigurationAttributes(item.getHandle(), productData.getCode(), null);
			addNumberOfIssuesForCartDisplay(configAttributes, target);

			final ConfigModel configModel = getProductConfigurationService()
					.retrieveConfigurationOverview(getSessionAccessService().getConfigIdForCartEntry(item.getHandle()));

			final List<ConfigurationInfoData> configInfoData = getOrderEntryConfigurationInfoConverter().convert(configModel);
			target.setConfigurationInfos(configInfoData);
		}
		handleVariant(item, target, productData);

	}

	protected void handleVariant(final Item item, final OrderEntryData target, final ProductData productData)
	{
		if (item.isVariant())
		{
			productData.setConfigurable(Boolean.TRUE);
			final List<ConfigurationInfoData> variantConfigurationInfos = getVariantConfigurationInfoProvider()
					.retrieveVariantConfigurationInfo(getProductService().getProductForCode(productData.getCode()));
			target.setConfigurationInfos(variantConfigurationInfos);
		}
	}

	protected void addNumberOfIssuesForCartDisplay(final CartEntryConfigurationAttributes configurationAttributes,
			final OrderEntryData targetEntry)
	{
		if (!configurationAttributes.getConfigurationConsistent().booleanValue())
		{
			final Map<ProductInfoStatus, Integer> statusSummaryMap = new HashMap<>();
			statusSummaryMap.put(ProductInfoStatus.ERROR, configurationAttributes.getNumberOfErrors());
			targetEntry.setStatusSummaryMap(statusSummaryMap);
		}
	}

	protected void handleConfigurationBackendLeads(final Item item, final OrderEntryData target, final ProductData product)
	{

		final boolean isConfigurableAndValid = item.isConfigurable() && isKbPresent(item);
		if (isConfigurableAndValid)
		{
			target.setConfigurationAttached(true);
			product.setConfigurable(Boolean.TRUE);
			final List<ConfigurationInfoData> configInfoData = new ArrayList<>();
			final ConfigurationInfoData configInfo = new ConfigurationInfoData();
			configInfo.setConfiguratorType(ConfiguratorType.CPQCONFIGURATOR);
			configInfoData.add(configInfo);
			target.setConfigurationInfos(configInfoData);
		}
	}

	protected final boolean isKbPresent(final Item item)
	{
		final CPQItem cpqItem = (CPQItem) item;
		final Date kbDate = cpqItem.getKbDate();
		if (kbDate != null)
		{

			return productConfigurationService.hasKbForDate(cpqItem.getProductId(), kbDate);
		}
		else
		{
			return false;
		}
	}

	protected VariantConfigurationInfoProvider getVariantConfigurationInfoProvider()
	{
		return variantConfigurationInfoProvider;
	}

	@Required
	public void setVariantConfigurationInfoProvider(final VariantConfigurationInfoProvider variantConfigurationInfoProvider)
	{
		this.variantConfigurationInfoProvider = variantConfigurationInfoProvider;
	}

	protected ProductService getProductService()
	{
		return productService;
	}

	@Required
	public void setProductService(final ProductService productService)
	{
		this.productService = productService;
	}



}
