/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.assistedservicepromotionfacades.populator;

import de.hybris.platform.assistedservicepromotionfacades.constants.AssistedservicepromotionfacadesConstants;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.couponservices.model.AbstractCouponModel;
import de.hybris.platform.util.Config;
import org.apache.commons.lang.StringUtils;


/**
 * @author CSACouponDataPopulator
 *
 */
public class CSACouponDataPopulator implements Populator<AbstractCouponModel, CSACouponData>
{
	private final String csaCouponPrefix = Config.getString(AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX_KEY,
			AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX);
	/*
	 * (non-Javadoc)
	 *
	 * @see de.hybris.platform.converters.Populator#populate(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void populate(final AbstractCouponModel source, final CSACouponData target)
	{
		target.setCode(source.getCouponId().replaceFirst(csaCouponPrefix, StringUtils.EMPTY));
		target.setName(source.getName());
	}
}
