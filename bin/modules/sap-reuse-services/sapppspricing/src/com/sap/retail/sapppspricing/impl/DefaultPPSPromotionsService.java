/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.collect.Lists;
import com.sap.retail.sapppspricing.PPSConfigService;

import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.promotionengineservices.promotionengine.impl.DefaultPromotionEngineService;
import de.hybris.platform.promotions.jalo.PromotionResult;
import de.hybris.platform.promotions.jalo.PromotionsManager;
import de.hybris.platform.promotions.model.ProductPromotionModel;
import de.hybris.platform.promotions.model.PromotionGroupModel;
import de.hybris.platform.promotions.model.PromotionOrderEntryConsumedModel;
import de.hybris.platform.promotions.model.PromotionResultModel;
import de.hybris.platform.promotions.result.PromotionOrderResults;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Promotions service simply doing nothing in case PPS is set to active
 */
public class DefaultPPSPromotionsService extends DefaultPromotionEngineService{
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPPSPromotionsService.class);
	private PPSConfigService configService;

	private AtomicReference<PPSConfigService> atomicRefConfigService = new AtomicReference<>(configService);

	/**
	 * SAP promotions are already calculated from backend
	 */
	@Override
	public PromotionOrderResults updatePromotions(final Collection<PromotionGroupModel> promotionGroups,
			final AbstractOrderModel order) {

		if (getConfigService().isPpsActive(order)) {
			return createEmptyPromotionOrderResult();

		} else {
			return super.updatePromotions(promotionGroups, order);
		}
	}

	private PromotionOrderResults createEmptyPromotionOrderResult() {
		return new PromotionOrderResults(null, null, Collections.<PromotionResult>emptyList(), 0.0);
	}

	/**
	 * SAP promotions are already calculated from backend
	 */
	@Override
	public PromotionOrderResults updatePromotions(final Collection<PromotionGroupModel> promotionGroups,
			final AbstractOrderModel order, final boolean evaluateRestrictions,
			final PromotionsManager.AutoApplyMode productPromotionMode,
			final PromotionsManager.AutoApplyMode orderPromotionMode, final Date date) {
		if (getConfigService().isPpsActive(order)) {
			return createEmptyPromotionOrderResult();
		} else {
			return super.updatePromotions(promotionGroups, order, evaluateRestrictions, productPromotionMode,
					orderPromotionMode, date);
		}
	}

	/**
	 * Remove Hybris promotions from product detail page as SAP promotions are
	 * calculated in the PPS
	 */
	@Override
	public List<ProductPromotionModel> getProductPromotions(final Collection<PromotionGroupModel> promotionGroups,
			final ProductModel product, final boolean evaluateRestrictions, final Date date) {
		if (getConfigService().isPpsActive(product)) {
			return Collections.emptyList();
		} else {
			return super.getProductPromotions(promotionGroups, product, evaluateRestrictions, date);
		}
	}


	@SuppressWarnings("javadoc")
	public PPSConfigService getConfigService() {
		return this.atomicRefConfigService.get();
	}

	@SuppressWarnings("javadoc")
	public void setConfigService(final PPSConfigService configService) {
		this.atomicRefConfigService.set(configService);
	}

	/**
	 * Overridden transferPromotionsToOrder to add null check for targetPromoResult.getAllPromotionActions()
	 */
	@Override
	public void transferPromotionsToOrder(AbstractOrderModel source, OrderModel target, boolean onlyTransferAppliedPromotions) {
		Set<PromotionResultModel> sourcePromotionResults = source.getAllPromotionResults();
		if (CollectionUtils.isNotEmpty(sourcePromotionResults)) {
			List<Object> toSave = Lists.newArrayList();
			Iterator<PromotionResultModel> promotionResultModel = sourcePromotionResults.iterator();

			while (promotionResultModel.hasNext()) {
				PromotionResultModel sourcePromoResult = promotionResultModel.next();
				PromotionResultModel targetPromoResult = this.getModelService().clone(sourcePromoResult);
				toSave.add(targetPromoResult);
				targetPromoResult.setOrder(target);
				targetPromoResult.setOrderCode(target.getCode());
				if (targetPromoResult.getAllPromotionActions() != null) {
					targetPromoResult.setActions(Lists.newArrayList(targetPromoResult.getAllPromotionActions()));
				} else {
					LOGGER.info("allPromotionActions is null for OrderModel {}", source.getCode());
				}
				if (Objects.isNull(targetPromoResult.getConsumedEntries())) {
					targetPromoResult.setConsumedEntries(Lists.newArrayList());
				}

				Iterator<PromotionOrderEntryConsumedModel> promotionOrderEntryConsumedModel = targetPromoResult.getConsumedEntries().iterator();

				while (promotionOrderEntryConsumedModel.hasNext()) {
					PromotionOrderEntryConsumedModel consumedEntry = promotionOrderEntryConsumedModel.next();
					consumedEntry.setOrderEntry(target.getEntries().stream().filter(entry -> Objects.nonNull(entry.getEntryNumber()) && Objects.equals(entry.getEntryNumber(), consumedEntry.getOrderEntryNumberWithFallback())).findFirst().orElse((AbstractOrderEntryModel) null));
					consumedEntry.setPromotionResult(targetPromoResult);
				}

				targetPromoResult.setPromotion(sourcePromoResult.getPromotion());
			}

			this.getModelService().saveAll(toSave);
			toSave.forEach(o -> this.getModelService().refresh(o));
		}

	}

}
