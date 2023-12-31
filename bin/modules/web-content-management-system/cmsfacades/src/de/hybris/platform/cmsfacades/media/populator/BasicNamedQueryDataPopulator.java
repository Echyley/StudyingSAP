/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.media.populator;

import de.hybris.platform.cms2.enums.SortDirection;
import de.hybris.platform.cms2.namedquery.NamedQuery;
import de.hybris.platform.cms2.namedquery.Sort;
import de.hybris.platform.cmsfacades.data.NamedQueryData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;


/**
 * This populator will convert the the basic attributes of the {@link NamedQuery} bean: - queryName - currentPage -
 * pageSize - sort params
 */
public class BasicNamedQueryDataPopulator implements Populator<NamedQueryData, NamedQuery>
{

	private static final String COMMA = ",";
	private static final String COLON = ":";
	public static final int PAIR_LENGTH = 2;

	@Override
	public void populate(final NamedQueryData namedQueryData, final NamedQuery namedQuery) throws ConversionException
	{
		namedQuery.withPageSize(toOptionalInteger(namedQueryData.getPageSize()))
		.withCurrentPage(toOptionalInteger(namedQueryData.getCurrentPage())).withQueryName(namedQueryData.getNamedQuery())
		.withSort(convertSort(namedQueryData.getSort()));
	}

	/**
	 * Convert the string to an integer. If the value is null, then return null. This method assumes that a validation
	 * has already taken place, and a NumberFormatException is not expected at this point.
	 *
	 * @param value
	 *           - the value to be converted
	 * @return the Integer value
	 *
	 */
	protected Integer toOptionalInteger(final String value)
	{
		if (value == null)
		{
			return null;
		}
		return Integer.valueOf(value);
	}

	/**
	 * Converts the String sort param into a list of {@Sort } objects. The input, if not empty, should be already
	 * validated with the following format: {param}:{direction} Multiple sort parameters are allowed if the blocks are
	 * separated by comma.
	 *
	 * @param sort
	 *           - the sort parameter received from the request
	 * @return a list of Sort objects
	 */
	protected List<Sort> convertSort(final String sort)
	{
		if (StringUtils.isEmpty(sort))
		{
			return Collections.emptyList();
		}
		final String[] sortBlocks = sort.trim().split(COMMA);
		try (Stream<String> stream = Arrays.stream(sortBlocks))
		{
			return stream.map(this::convertSortBlock).collect(Collectors.toList());
		}
	}

	/**
	 * Convert A sort block, i.e. {param}:{direction} into a Sort object.
	 *
	 * @param sortBlock
	 * @return
	 */
	protected Sort convertSortBlock(final String sortBlock)
	{
		final String[] sortPair = sortBlock.trim().split(COLON);
		if (sortPair.length != PAIR_LENGTH)
		{
			throw new ConversionException("The sort parameter format [" + sortBlock + "] is not correct.");
		}
		final Sort sort = new Sort() //
				.withParameter(sortPair[0].trim()) //
				.withDirection(SortDirection.valueOf(sortPair[1].trim()));
		if (StringUtils.isEmpty(sort.getParameter()) || sort.getDirection() == null)
		{
			throw new ConversionException("The sort parameter cannot be empty.");
		}
		return sort;
	}
}
