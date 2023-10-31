package com.sap.retail.sapppspricing.populator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.sap.retail.sapppspricing.PPSConfigService;

import de.hybris.platform.commercefacades.order.converters.populator.CartPopulator;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.core.model.order.CartModel;
/**
 * Populator to populate OPPS promotions
 *
 */
public class OPPSExtendedCartPopulator extends CartPopulator<CartData> {

	private static final String APPLIED = "APPLIED";
	private static final String POTENTIAL = "POTENTIAL";
	private PPSConfigService configService;
	
    /**
     * Populate method to populate OPPS promotions to Cart data
     */
	@Override
	public void populate(final CartModel source, final CartData target) {

		if (!getConfigService().isPpsActive(source)) {
			return;
		}

		List<PromotionResultData> allPromotionResultData = getPromotionResultConverter()
				.convertAll(source.getAllPromotionResults());
		if (CollectionUtils.isEmpty(allPromotionResultData)) {
			return;
		}
		List<PromotionResultData> potentialProductPromotions = new ArrayList<>();
		List<PromotionResultData> potentialOrderPromotions = new ArrayList<>();
		List<PromotionResultData> appliedOrderPromotions = new ArrayList<>();
		List<PromotionResultData> appliedProductPromotions = new ArrayList<>();

		final double productsDiscountsAmount = getProductsDiscountsAmount(source);
		final double orderDiscountsAmount = getOrderDiscountsAmount(source);

		target.setProductDiscounts(createPrice(source, Double.valueOf(productsDiscountsAmount)));
		target.setOrderDiscounts(createPrice(source, Double.valueOf(orderDiscountsAmount)));
		target.setTotalDiscounts(createPrice(source, Double.valueOf(productsDiscountsAmount + orderDiscountsAmount)));

		allPromotionResultData.stream().forEach(promoResult -> {
			if (APPLIED.equals(promoResult.getOppPromoResultType())) {
				addPromotionResult(appliedOrderPromotions, appliedProductPromotions, promoResult);
			} else if (POTENTIAL.equals(promoResult.getOppPromoResultType())) {
				addPromotionResult(potentialOrderPromotions, potentialProductPromotions, promoResult);
			}
		});
		target.setAppliedOrderPromotions(appliedOrderPromotions);
		target.setAppliedProductPromotions(appliedProductPromotions);
		target.setPotentialProductPromotions(potentialProductPromotions);
		target.setPotentialOrderPromotions(potentialOrderPromotions);

	}
    /**
     * Method to add promotion result to order level or product level promotion result based on consumed entries
     * @param orderPromotions : Order level potential/applied promotion results
     * @param productPromotions : Product level potential/applied promotion results
     * @param promoResult : Prom0tion result
     */
	private void addPromotionResult(List<PromotionResultData> orderPromotions,
			List<PromotionResultData> productPromotions, PromotionResultData promoResult) {
		if (CollectionUtils.isEmpty(promoResult.getConsumedEntries())) {
			orderPromotions.add(promoResult);
		} else {
			productPromotions.add(promoResult);
		}

	}

	public PPSConfigService getConfigService() {
		return configService;
	}

	public void setConfigService(final PPSConfigService configService) {
		this.configService = configService;
	}
}
