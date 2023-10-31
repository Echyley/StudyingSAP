/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.populator;

import de.hybris.platform.commercefacades.order.converters.populator.PromotionResultPopulator;
import de.hybris.platform.commercefacades.product.data.PromotionResultData;
import de.hybris.platform.promotions.model.PromotionResultModel;

public class OPPSPromotionResultPopulator extends PromotionResultPopulator {

	@Override
	public void populate(final PromotionResultModel source, final PromotionResultData target) {
		super.populate(source, target);

		if (null != source.getPromotion() && null != source.getOppsPromoResultType()) {
			target.setDescription(source.getPromotion().getDescription());
			target.setOppPromoResultType(source.getOppsPromoResultType().toString());
		}

	}
}
