/**
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.hybris.merchandising.dto;

import de.hybris.platform.webservicescommons.dto.PaginationWsDTO;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * List of strategies.
 */
@Schema(name = "StrategyListWsDTO", description = "List of strategies.")
public class StrategyListWsDTO implements Serializable

{
	private static final long serialVersionUID = 1L;

	//The name is options to keep backward compatibility
	@Schema(name = "options", description = "List of strategies (id, label)")
	private List<DropdownElement> options;

	@Schema(name = "pagination", description = "Pagination items")
	private PaginationWsDTO pagination;

	public StrategyListWsDTO()
	{
		// default constructor
	}

	public List<DropdownElement> getOptions()
	{
		return options;
	}

	public void setOptions(final List<DropdownElement> options)
	{
		this.options = options;
	}

	public void setPagination(final PaginationWsDTO pagination)
	{
		this.pagination = pagination;
	}

	public PaginationWsDTO getPagination()
	{
		return pagination;
	}

}
