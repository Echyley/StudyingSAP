/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import com.sap.retail.sapppspricing.swagger.businessobject.dto.LineItemDomainSpecific;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculate;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateBase;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.PriceCalculateBase.TransactionTypeEnum;
import com.sap.retail.sapppspricing.swagger.businessobject.dto.SaleBase;
import com.sap.retail.sapppspricing.LineItemPopulator;
import com.sap.retail.sapppspricing.PPSConfigService;
import com.sap.retail.sapppspricing.PPSRequestCreator;
import com.sap.retail.sapppspricing.enums.InterfaceVersion;
import com.sap.retail.sapppspricing.opps.PPSClientBeanAccessor;
import com.sap.retail.sapppspricing.opps.RetailTransactionItemTypeEnumeration;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.user.UserService;

/**
 * Helper class for creating price calculation requests for usage from catalog
 * or cart. Supports injection of an arbitrary number of line item populators.
 * Set requested language based on current language provided by
 * {@link CommonI18NService}
 */
public class DefaultPPSRequestCreator implements PPSRequestCreator {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultPPSRequestCreator.class);
	private static final String VERSION = "VERSION";
	private PPSClientBeanAccessor accessor;
	private List<LineItemPopulator<ProductModel>> lineItemPopulators;
	private PPSConfigService configService;
	private CommonI18NService commonI18NService;
	private UserService userService;
	private boolean discountableFlag;	
	private final PPSAccessorHelper helper = new PPSAccessorHelper();
	private int minorVersion=0;
	private AtomicReference<CommonI18NService> atmocRefCommonI18NService = new AtomicReference<>(commonI18NService);
	private AtomicReference<PPSConfigService> atomicRefConfigService = new AtomicReference<>(configService);
	private AtomicReference<PPSClientBeanAccessor> atomicRefAccessor = new AtomicReference<>(accessor);
	private AtomicReference<List<LineItemPopulator<ProductModel>>> atomicRefLineItemPopulators = new AtomicReference<>(lineItemPopulators);
	
	protected PriceCalculate createRequest(final String businessUnitId, String sourceSystemId,
			final String transactionId, final boolean isNet) {

		GregorianCalendar gcTesting = new GregorianCalendar();
		final PriceCalculate priceCalculate = getAccessor().getHelper().createCalculateRequestSkeleton(businessUnitId,
				gcTesting);
		priceCalculate.setInternalMinorVersion(minorVersion);
		priceCalculate.getArTSHeader().setMasterDataSourceSystemID(sourceSystemId);

		final String languageCode = getCommonI18NService().getCurrentLanguage().getIsocode().toUpperCase(); //NOSONAR
		priceCalculate.getArTSHeader().setRequestedLanguage(languageCode);
		final PriceCalculateBase priceCalculateBase = priceCalculate.getPriceCalculateBody().get(0);
		priceCalculateBase.setTransactionType(TransactionTypeEnum.SALETRANSACTION);
		priceCalculateBase.setNetPriceFlag(isNet);
		priceCalculateBase.getTransactionID().setValue(transactionId);
		return priceCalculate;
	}

	@Override
	public PriceCalculate createRequestForCatalog(final ProductModel productModel, final boolean isNet) {
		final String businessUnit = getConfigService().getBusinessUnitId(productModel);
		final String mastreSourceSystemId = getConfigService().getSourceSystemId(productModel);
		final PriceCalculate priceCalculate = createRequest(businessUnit, mastreSourceSystemId,
				"commerce-product_" + productModel.getCode() + "@" + productModel.getCatalogVersion().getVersion(),
				isNet);
		InterfaceVersion clientInterfaceVersion = getConfigService().getClientInterfaceVersion(productModel);
		setVersionForRequest(clientInterfaceVersion, priceCalculate);

		final List<LineItemDomainSpecific> lineItems = priceCalculate.getPriceCalculateBody().get(0).getShoppingBasket()
				.getLineItem();
		final String uomCode = productModel.getUnit().getCode();
		lineItems.add(createLineItem(0, productModel, uomCode, BigDecimal.valueOf(1L)));
		return priceCalculate;
	}

	protected void setVersionForRequest(InterfaceVersion version, final PriceCalculate priceCalculate) {
		if (version == null) {
			LOG.warn("No client interface version could be determined, using default version 2.0");
			return;
		}
		String[] splitResult = version.getCode().split(Pattern.quote(VERSION));
		final int versionNumber = Integer.parseInt(splitResult[1]);
		final BigInteger majorVersion = BigInteger.valueOf(((long) versionNumber) / 10);
		// Setting Major and Minor Version
		priceCalculate.setInternalMajorVersion(majorVersion.intValue());
		priceCalculate.setInternalMinorVersion(version == InterfaceVersion.VERSION100 ? 0 : 1);
		LOG.debug("Interface major / minor version {} / {} is used", majorVersion, minorVersion);
	}

	@Override
	public PriceCalculate createRequestForCart(final AbstractOrderModel order) {
		final PriceCalculate priceCalculate = createRequest(getConfigService().getBusinessUnitId(order),
				getConfigService().getSourceSystemId(order), "commerce_order_" + order.getCode(), order.getNet());
		fillRequestBodyForCart(order, priceCalculate);
		InterfaceVersion clientInterfaceVersion = getConfigService().getClientInterfaceVersion(order);
		setVersionForRequest(clientInterfaceVersion, priceCalculate);
		return priceCalculate;
	}

	protected void fillRequestBodyForCart(final AbstractOrderModel order, final PriceCalculate priceCalculate) {
		int saleItemIndex = 0;
		int entryIndex = 0;

		final List<LineItemDomainSpecific> lineItems =priceCalculate.getPriceCalculateBody().get(0).getShoppingBasket()
				.getLineItem();
		for (int i = 0; i < order.getEntries().size(); i++) {
			final Pair<Integer, SaleBase> next = helper.nextNonDiscountItem(lineItems, saleItemIndex);
			if (next == null) {
				break;
			}
			saleItemIndex = next.getLeft().intValue() + 1;
			entryIndex++;
		}
		// We assume that sequence numbers do not have gaps
		for (int i = entryIndex; i < order.getEntries().size(); i++) {
			final AbstractOrderEntryModel entry = order.getEntries().get(i);
			final String uomCode = entry.getUnit().getCode();
			final int sequenceNumber = saleItemIndex++;
			lineItems.add(createLineItem(sequenceNumber, entry.getProduct(), uomCode,
					BigDecimal.valueOf(entry.getQuantity().longValue())));
		}
	}

	protected LineItemDomainSpecific createLineItem(final int sequenceNumber, final ProductModel product,
			final String uom, final BigDecimal qty) {
		final LineItemDomainSpecific lineItem =getAccessor().getHelper().createSaleLineItem(sequenceNumber,product.getCode(), uom, qty);
		SaleBase saleBase = lineItem.getSale();
		saleBase.setItemType(RetailTransactionItemTypeEnumeration.STOCK.value());
		// consider discount flag per line item
		if (isDiscountableFlag() && Boolean.FALSE.equals(product.getDiscountable())) {
			saleBase.setNonDiscountableFlag(Boolean.TRUE);
		}
		for (final LineItemPopulator<ProductModel> populator : getLineItemPopulators()) {
			populator.populate(lineItem, product);
		}
		return lineItem;
	}

	public PPSClientBeanAccessor getAccessor() {
		return this.atomicRefAccessor.get();
	}

	public void setAccessor(final PPSClientBeanAccessor accessor) {
		this.atomicRefAccessor.set(accessor);
	}

	public List<LineItemPopulator<ProductModel>> getLineItemPopulators() {
		return this.atomicRefLineItemPopulators.get();
	}

	public void setLineItemPopulators(final List<LineItemPopulator<ProductModel>> lineItemPopulators) {
		this.atomicRefLineItemPopulators.set(lineItemPopulators);
		Collections.sort(getLineItemPopulators(), new OrderedComparator());
		if (LOG.isDebugEnabled()) {
			LOG.debug(String.format("List of line item populators after sorting: [%s]", getLineItemPopulators()));
		}
	}

	public CommonI18NService getCommonI18NService() {
		return this.atmocRefCommonI18NService.get();
	}

	public void setCommonI18NService(final CommonI18NService commonI18NService) {
		this.atmocRefCommonI18NService.set(commonI18NService);
	}

	public PPSConfigService getConfigService() {
		return this.atomicRefConfigService.get();
	}

	public void setConfigService(final PPSConfigService configService) {
		this.atomicRefConfigService.set(configService);
	}

	private static class OrderedComparator implements Comparator<Ordered>, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public int compare(final Ordered arg0, final Ordered arg1) {
			return Integer.compare(arg0.getOrder(), arg1.getOrder());
		}
	}

	public boolean isDiscountableFlag() {
		return discountableFlag;
	}

	public void setDiscountableFlag(boolean discountableFlag) {
		this.discountableFlag = discountableFlag;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

}
