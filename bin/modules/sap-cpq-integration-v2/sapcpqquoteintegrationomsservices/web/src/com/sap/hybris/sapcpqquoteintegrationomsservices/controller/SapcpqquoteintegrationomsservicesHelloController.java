/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegrationomsservices.controller;

import static com.sap.hybris.sapcpqquoteintegrationomsservices.constants.SapcpqquoteintegrationomsservicesConstants.PLATFORM_LOGO_CODE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sap.hybris.sapcpqquoteintegrationomsservices.service.SapcpqquoteintegrationomsservicesService;


@Controller
public class SapcpqquoteintegrationomsservicesHelloController
{
	@Autowired
	private SapcpqquoteintegrationomsservicesService sapcpqquoteintegrationomsservicesService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String printWelcome(final ModelMap model)
	{
		model.addAttribute("logoUrl", sapcpqquoteintegrationomsservicesService.getHybrisLogoUrl(PLATFORM_LOGO_CODE));
		return "welcome";
	}
}
