/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.smartedit.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller(value = "cmsRootController")
public class RootController
{
	protected static final String SMART_EDIT_ROOT_PAGE = "index";

	@GetMapping(value = "/")
	@ResponseStatus(value = HttpStatus.OK)

	public String getSmartEditPage()
	{
		return SMART_EDIT_ROOT_PAGE;
	}
}
