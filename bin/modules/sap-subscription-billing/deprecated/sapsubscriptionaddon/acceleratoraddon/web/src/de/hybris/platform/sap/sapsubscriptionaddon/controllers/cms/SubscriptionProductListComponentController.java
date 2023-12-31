/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapsubscriptionaddon.controllers.cms;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import de.hybris.platform.addonsupport.controllers.cms.AbstractCMSAddOnComponentController;
import de.hybris.platform.cms2.model.contents.components.AbstractCMSComponentModel;
import de.hybris.platform.sap.sapsubscriptionaddon.model.components.SubscriptionProductListComponentModel;
import de.hybris.platform.servicelayer.exceptions.AttributeNotSupportedException;
import de.hybris.platform.servicelayer.model.ModelService;

/**
 * Controller for Product List Component
 */
@Controller("SubscriptionProductListComponentController")
@RequestMapping(value = "/view/" + SubscriptionProductListComponentModel._TYPECODE + "Controller")
public class SubscriptionProductListComponentController extends AbstractCMSAddOnComponentController {

	private static final Logger LOGGER = Logger.getLogger(SubscriptionProductListComponentController.class);

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	protected void fillModel(HttpServletRequest request, Model model,
			AbstractCMSComponentModel component) {
		for (final String property : getCmsComponentService().getEditorProperties(component))
		{
			try
			{
				final Object value = modelService.getAttributeValue(component, property);
				model.addAttribute(property, value);
			}
			catch (final AttributeNotSupportedException ignore)
			{
				LOGGER.error(ignore);
			}
		}
	}
}
