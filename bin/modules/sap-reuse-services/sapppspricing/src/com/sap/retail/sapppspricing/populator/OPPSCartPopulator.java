/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.populator;

import java.util.ArrayList;
import java.util.List;

import com.sap.retail.sapppspricing.PPSConfigService;

import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;

public class OPPSCartPopulator extends CartPopulator<CartData> {

	private PPSConfigService configService;
	
	@Override
	protected void addPromotions(final AbstractOrderModel source, final PromotionOrderResults promoOrderResults,
			final AbstractOrderData target) {

		
		if (getConfigService().isPpsActive(source) && promoOrderResults != null) {
			List<PromotionResultData> allPromotionResultData = getPromotionResultConverter()
					.convertAll(source.getAllPromotionResults());
			List<PromotionResultData> potentialPromotions = new ArrayList<>();
			List<PromotionResultData> appliedPromotions = new ArrayList<>();
			
			final double productsDiscountsAmount = getProductsDiscountsAmount(source);
			final double orderDiscountsAmount = getOrderDiscountsAmount(source);

			target.setProductDiscounts(createPrice(source, Double.valueOf(productsDiscountsAmount)));
			target.setOrderDiscounts(createPrice(source, Double.valueOf(orderDiscountsAmount)));
			target.setTotalDiscounts(createPrice(source, Double.valueOf(productsDiscountsAmount + orderDiscountsAmount)));

			allPromotionResultData.stream().forEach(promoResult -> {
				if (promoResult.getOppPromoResultType().equalsIgnoreCase("APPLIED")) {
					appliedPromotions.add(promoResult);
				} else if (promoResult.getOppPromoResultType().equalsIgnoreCase("POTENTIAL")) {
					potentialPromotions.add(promoResult);
				}
			});
			final CartData cartData = (CartData) target;
			cartData.setAppliedOrderPromotions(appliedPromotions);
			cartData.setPotentialOrderPromotions(potentialPromotions);
		}
		else {
			super.addPromotions(source, promoOrderResults, target);
		}
	}
	
	public PPSConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(final PPSConfigService configService) {
		this.configService = configService;
	}

}
