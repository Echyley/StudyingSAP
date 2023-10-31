/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import com.sap.retail.sapppspricing.swagger.businessobject.dto.ExtendedAmountType;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.LineItemDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateResponse;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.SaleBase;
import com.sap.retail.sapppspricing.PPSClient;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.PPSRequestCreator;
import com.sap.retail.sapppspricing.PriceCalculateToOrderMapper;
import com.sap.retail.sapppspricing.PricingBackend;
import com.sap.retail.sapppspricing.SapPPSPricingRuntimeException;
import com.sap.retail.sapppspricing.opps.PPSClientBeanAccessor;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.jalo.order.price.PriceInformation;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.util.PriceValue;

/**
 * Implementation of {@link PricingBackend} performing the calls for catalog or
 * basket pricing against an embedded or remote PPS. Takes care for request
 * creation as well as mapping the response to the corresponding hybris objects
 */
public class PricingBackendPPS implements PricingBackend {
	private static final Logger LOG = LoggerFactory.getLogger(PricingBackendPPS.class);
	private double highPrice;
	private boolean useHighPrice = true;
	private PPSClient ppsClient;

	private PPSClientBeanAccessor accessor;
	private List<PriceCalculateToOrderMapper> resultToOrderMappers;
	private PPSRequestCreator requestCreator;
	private CommonI18NService commonI18NService;
	private PPSConfigService configService;

	private AtomicReference<CommonI18NService> atomicRefCommonI18NService = new AtomicReference<>(commonI18NService);
	private AtomicReference<List<PriceCalculateToOrderMapper>> atomicRefResultToOrderMappers = new AtomicReference<>(resultToOrderMappers);
	private AtomicReference<PPSClientBeanAccessor> atomicRefAccessor = new AtomicReference<>(accessor);
	private AtomicReference<PPSClient> atomicRefPpsClient = new AtomicReference<>(ppsClient);

	@Override
	public void readPricesForCart(final AbstractOrderModel order) {
		LOG.debug("entering readPricesForCart()");
		if (order.getEntries().isEmpty()) {
			return;
		}
		final PriceCalculate priceCalculate = getRequestCreator().createRequestForCart(order);

		try {
			final PriceCalculateResponse response = getPpsClient().callPPS(priceCalculate,
					order.getStore().getSAPConfiguration());
			for (final PriceCalculateToOrderMapper mapper : getResultToOrderMappers()) {
				mapper.map(response, order);
			}
		} catch (final SapPPSPricingRuntimeException e) {
			throw new SapPPSPricingRuntimeException("Calculation for AbstractOrder " + order.getCode() + " failed" + e.getMessage());
		}
		LOG.debug("exiting readPricesForCart method ");
	}

	@Override
	public List<PriceInformation> readPriceInformationForProducts(final List<ProductModel> productModels,
			final boolean isNet) {
		LOG.debug("entering readPriceInformationForProducts()");
		final String expectedCurrencyCode = getCommonI18NService().getCurrentCurrency().getIsocode();

		final List<PriceInformation> result = new ArrayList<>(productModels.size());
		final Map<ProductModel, PriceInformation> ppsPinfos = readPriceInfosFromPps(productModels, isNet);
		if (MapUtils.isEmpty(ppsPinfos)) {
			return Collections.emptyList();
		}
		for (final Entry<ProductModel, PriceInformation> ppsPinfo : ppsPinfos.entrySet()) {
			PriceInformation pinfo = ppsPinfo.getValue();
			final ProductModel prod = ppsPinfo.getKey();
			final String actualCurrencyCode = pinfo.getPriceValue().getCurrencyIso();
			if (!expectedCurrencyCode.equals(actualCurrencyCode)) {
				LOG.warn("Unexpected currency code {} for item {}, setting to {}", actualCurrencyCode, prod.getCode(),
						expectedCurrencyCode);
				pinfo = new PriceInformation(
						new PriceValue(expectedCurrencyCode, pinfo.getPriceValue().getValue(), true));
			}
			result.add(pinfo);

		}
		return result;

	}

	protected Map<ProductModel, PriceInformation> readPriceInfosFromPps(final List<ProductModel> prods,
			final boolean isNet) {
		final Map<ProductModel, PriceInformation> result = new HashMap<>();
		final SAPConfigurationModel sapConfig = CollectionUtils.isEmpty(prods) ? null
				: getConfigService().getSapConfig(prods.get(0));
		for (final ProductModel prod : prods) {
			PriceInformation pinfo = null;
			final PriceCalculate priceCalculate = getRequestCreator().createRequestForCatalog(prod, isNet);
			double calculatedPrice = 0.0;
			String currencyCode = "";
			try {
				final PriceCalculateResponse response = getPpsClient().callPPS(priceCalculate, sapConfig);
				if (isInValidResponse(response)) {
					continue;
				}
				LineItemDomainSpecific lineItems = response.getPriceCalculateBody().get(0).getShoppingBasket()
						.getLineItem().get(0);
				final SaleBase saleBase = getAccessor().getHelper().getLineItemContent(lineItems);
				if (saleBase != null) {
					final ExtendedAmountType extendedAmount = saleBase.getExtendedAmount();
					calculatedPrice += extendedAmount.getValue().doubleValue();
					currencyCode = extendedAmount.getCurrency();
				}
			} catch (final SapPPSPricingRuntimeException e) {
				LOG.error("Could not determine catalog price for product {}", prod.getCode(), e);
				return Collections.emptyMap();
			}
			// Special handling as long as currency code is not returned by PPS - null is
			// not allowed for a PriceValue
			LOG.info("Calculated price {}", calculatedPrice);
			pinfo = new PriceInformation(
					new PriceValue(currencyCode == null ? "" : currencyCode, calculatedPrice, true));

			result.put(prod, pinfo);
		}
		return result;
	}

	/**
	 * Validate the OPPS response items
	 * 
	 * @param response : Response from OPPS
	 * @return Whether the response is valid or not
	 */
	private boolean isInValidResponse(PriceCalculateResponse response) {
		return null == response || CollectionUtils.isEmpty(response.getPriceCalculateBody())
				|| null == response.getPriceCalculateBody().get(0).getShoppingBasket()
				|| CollectionUtils.isEmpty(response.getPriceCalculateBody().get(0).getShoppingBasket().getLineItem());
	}

	public PPSClient getPpsClient() {
		return this.atomicRefPpsClient.get();
	}

	public void setPpsClient(final PPSClient ppsClient) {
		this.atomicRefPpsClient.set(ppsClient);
	}

	public PPSClientBeanAccessor getAccessor() {
		return this.atomicRefAccessor.get();
	}

	public void setAccessor(final PPSClientBeanAccessor accessor) {
		this.atomicRefAccessor.set(accessor);
	}

	public List<PriceCalculateToOrderMapper> getResultToOrderMappers() {
		return this.atomicRefResultToOrderMappers.get();
	}

	public void setResultToOrderMappers(final List<PriceCalculateToOrderMapper> resultToOrderMappers) {
		this.atomicRefResultToOrderMappers.set(resultToOrderMappers);
		Collections.sort(getResultToOrderMappers(), new OrderedComparator());
		if (LOG.isDebugEnabled()) {
			LOG.debug("List of PriceCalculate result mappers after sorting {}", getResultToOrderMappers());
		}
	}

	public PPSRequestCreator getRequestCreator() {
		return requestCreator;
	}

	public void setRequestCreator(final PPSRequestCreator creator) {
		this.requestCreator = creator;
	}

	private static class OrderedComparator implements Comparator<Ordered>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(final Ordered arg0, final Ordered arg1) {
			return Integer.compare(arg0.getOrder(), arg1.getOrder());
		}
	}

	public CommonI18NService getCommonI18NService() {
		return this.atomicRefCommonI18NService.get();
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService) {
		this.atomicRefCommonI18NService.set(commonI18NService);
	}

	public double getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(final double highPrice) {
		this.highPrice = highPrice;
	}

	public PPSConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(final PPSConfigService configService) {
		this.configService = configService;
	}

	public boolean isUseHighPrice() {
		return useHighPrice;
	}

	public void setUseHighPrice(final boolean useHighPrice) {
		this.useHighPrice = useHighPrice;
	}

}
