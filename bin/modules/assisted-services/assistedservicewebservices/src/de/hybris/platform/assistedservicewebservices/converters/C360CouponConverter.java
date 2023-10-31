/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.assistedservicewebservices.converters;

import de.hybris.platform.assistedservicepromotionfacades.constants.AssistedservicepromotionfacadesConstants;
import de.hybris.platform.assistedservicepromotionfacades.customer360.CSACouponData;
import de.hybris.platform.assistedservicewebservices.dto.C360CouponWsDTO;
import de.hybris.platform.util.Config;
import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.apache.commons.lang.StringUtils;


@WsDTOMapping
public class C360CouponConverter extends BidirectionalConverter<CSACouponData, C360CouponWsDTO>
{
	private final String csaCouponPrefix = Config.getString(AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX_KEY,
			AssistedservicepromotionfacadesConstants.COUPON_SEARCH_PREFIX);

	@Override
	public C360CouponWsDTO convertTo(final CSACouponData csaCouponData, final Type<C360CouponWsDTO> type,
			final MappingContext mappingContext)
	{
		final C360CouponWsDTO c360CouponWsDTO = new C360CouponWsDTO();
		c360CouponWsDTO.setName(csaCouponData.getName());
		c360CouponWsDTO.setCode(csaCouponPrefix + csaCouponData.getCode());
		c360CouponWsDTO.setApplied(csaCouponData.getCouponApplied() == null ? false : csaCouponData.getCouponApplied());
		return c360CouponWsDTO;
	}

	@Override
	public CSACouponData convertFrom(final C360CouponWsDTO c360CouponWsDTO, final Type<CSACouponData> type,
			final MappingContext mappingContext)
	{
		final CSACouponData csaCouponData = new CSACouponData();
		csaCouponData.setName(c360CouponWsDTO.getName());
		csaCouponData.setCode(c360CouponWsDTO.getCode().replaceFirst(csaCouponPrefix, StringUtils.EMPTY));
		csaCouponData.setCouponApplied(c360CouponWsDTO.isApplied());
		return csaCouponData;
	}
}
