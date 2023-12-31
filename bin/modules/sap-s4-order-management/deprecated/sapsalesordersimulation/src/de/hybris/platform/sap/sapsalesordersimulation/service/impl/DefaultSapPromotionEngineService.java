/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsalesordersimulation.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotionengineservices.promotionengine.impl.DefaultPromotionEngineService;
import de.hybris.platform.promotions.jalo.PromotionsManager.AutoApplyMode;
import de.hybris.platform.promotions.model.AbstractPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import de.hybris.platform.sap.sapmodel.enums.SAPProductType;
import de.hybris.platform.sap.sapsalesordersimulation.service.SapSimulateSalesOrderEnablementService;


/**
 * Default implementation of Sap Promotion Engine Service.
 */
public class DefaultSapPromotionEngineService extends DefaultPromotionEngineService {

	private SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService;

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultSapPromotionEngineService.class);

	@Override
	public List<? extends AbstractPromotionModel> getAbstractProductPromotions(
			Collection<PromotionGroupModel> promotionGroups, ProductModel product, boolean evaluateRestrictions,
			Date date) {

		if (getSapSimulateSalesOrderEnablementService().isCatalogPricingEnabled() && product.getSapProductTypes().contains(SAPProductType.PHYSICAL)) {
			String msg = String.format(
					"The product [%s]  promotions are not supported in the SAP synchronous pricing scenario!",
					product.getCode());
			LOGGER.warn(msg);
			return Collections.emptyList();

		} else {

			return super.getAbstractProductPromotions(promotionGroups, product, evaluateRestrictions, date);

		}

	}

	@Override
	public PromotionOrderResults updatePromotions(Collection<PromotionGroupModel> promotionGroups,
			AbstractOrderModel order) {

		if (getSapSimulateSalesOrderEnablementService().isCartPricingEnabled()) {
			String msg = String.format(
					"The order [%s] promotions update is not supported in the SAP synchronous pricing scenario!",
					order.getCode());
			LOGGER.warn(msg);
			return new PromotionOrderResults(null, null, Collections.emptyList(), 0d);

		} else {

			return super.updatePromotions(promotionGroups, order);

		}

	}

	@Override
	public PromotionOrderResults updatePromotions(Collection<PromotionGroupModel> promotionGroups,
			AbstractOrderModel order, boolean evaluateRestrictions, AutoApplyMode productPromotionMode,
			AutoApplyMode orderPromotionMode, Date date) {

		if (getSapSimulateSalesOrderEnablementService().isCartPricingEnabled()) {
			String s= String.format(
			"The order [%s] promotions update is not supported in the SAP synchronous pricing scenario!",
			order.getCode());
			LOGGER.warn(s);
			return new PromotionOrderResults(null, null, Collections.emptyList(), 0d);

		} else {

			return super.updatePromotions(promotionGroups, order, evaluateRestrictions, productPromotionMode,
					orderPromotionMode, date);
		}

	}

	@Override
	public PromotionOrderResults getPromotionResults(AbstractOrderModel order) {

		if (getSapSimulateSalesOrderEnablementService().isCartPricingEnabled()) {
			String s = String.format(
					"The order [%s] promotions are not supported in the SAP synchronous pricing scenario!",
					order.getCode());
			LOGGER.warn(s);
			return new PromotionOrderResults(null, null, Collections.emptyList(), 0d);

		} else {

			return super.getPromotionResults(order);
		}

	}

	@Override
	public PromotionOrderResults getPromotionResults(Collection<PromotionGroupModel> promotionGroups,
			AbstractOrderModel order, boolean evaluateRestrictions, AutoApplyMode productPromotionMode,
			AutoApplyMode orderPromotionMode, Date date) {

		if (getSapSimulateSalesOrderEnablementService().isCartPricingEnabled()) {
			String s = String.format(
					"The order [%s] promotions are not supported in the SAP synchronous pricing scenario!",
					order.getCode());
			LOGGER.warn(s);
			return new PromotionOrderResults(null, null, Collections.emptyList(), 0d);

		} else {

			return super.getPromotionResults(promotionGroups, order, evaluateRestrictions, productPromotionMode,
					orderPromotionMode, date);
		}

	}

	@Override
	public void cleanupCart(CartModel cart) {

		if (getSapSimulateSalesOrderEnablementService().isCartPricingEnabled()) {
			String s = String.format(
					"The cart [%s] promotions cleanup is not supported in the SAP synchronous pricing scenario!",
					cart.getCode());
			LOGGER.warn(s);

		} else {

			super.cleanupCart(cart);

		}

	}

	@Override
	public void transferPromotionsToOrder(AbstractOrderModel source, OrderModel target,
			boolean onlyTransferAppliedPromotions) {

		if (getSapSimulateSalesOrderEnablementService().isCartPricingEnabled()) {

			LOGGER.warn("Promotions transfer is not supported in the SAP synchronous pricing scenario!");

		} else {

			super.transferPromotionsToOrder(source, target, onlyTransferAppliedPromotions);

		}

	}

	public SapSimulateSalesOrderEnablementService getSapSimulateSalesOrderEnablementService() {
		return sapSimulateSalesOrderEnablementService;
	}

	
	public void setSapSimulateSalesOrderEnablementService(
			SapSimulateSalesOrderEnablementService sapSimulateSalesOrderEnablementService) {
		this.sapSimulateSalesOrderEnablementService = sapSimulateSalesOrderEnablementService;
	}


}
