/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.populators;

import de.hybris.platform.cmsfacades.data.CMSItemSearchData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;

/**
 * CMSItemSearchData populator for cmsfacades to integrate with cms2's version of CMSItemSearchData.
 */
public class CMSItemSearchDataPopulator implements Populator<CMSItemSearchData, de.hybris.platform.cms2.data.CMSItemSearchData>
{
	private static final String COMMA = ",";
	private static final String COLON = ":";
	public static final int PARAMETER_BLOCK_LENGTH = 2;

	@Override
	public void populate(final CMSItemSearchData source, final de.hybris.platform.cms2.data.CMSItemSearchData target) throws ConversionException
	{
		target.setMask(source.getMask());
		target.setTypeCode(source.getTypeCode());
		target.setTypeCodes(buildTypeCodesList(source.getTypeCodes()));
		target.setCatalogId(source.getCatalogId());
		target.setCatalogVersion(source.getCatalogVersion());
		target.setItemSearchParams(buildParameterStringMap(source.getItemSearchParams()));
	}

	/**
	 * Using the parameter String, convert it to a Map<String, String> for future processing.
	 *
	 * @param params
	 *           - the String parameter received from the request
	 * @return a Map<String, String> representing the requested parameter String
	 */
	protected Map<String, String> buildParameterStringMap(final String params)
	{
		if (StringUtils.isNotEmpty(params))
		{
			final String[] paramBlocks = params.split(COMMA);
			try (final Stream<String> stream = Arrays.stream(paramBlocks))
			{
				return stream.map(paramBlock -> paramBlock.trim().split(COLON)) //
						.filter(paramBlock -> paramBlock.length == PARAMETER_BLOCK_LENGTH)
						.collect(Collectors.toMap(paramPair -> paramPair[0].trim(), paramPair -> paramPair[1].trim()));
			}
			catch (final IllegalStateException e)
			{
				throw new ConversionException("Error while parsing parameter map.", e);
			}
		}

		return Collections.emptyMap();
	}

	protected List<String> buildTypeCodesList(final String typeCodes)
	{
		if (StringUtils.isNotEmpty(typeCodes))
		{
			return Arrays.stream(typeCodes.split(COMMA)).map(String::trim).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}
}
