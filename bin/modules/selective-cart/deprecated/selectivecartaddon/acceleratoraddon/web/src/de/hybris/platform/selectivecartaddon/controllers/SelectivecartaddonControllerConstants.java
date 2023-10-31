/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.selectivecartaddon.controllers;

import de.hybris.platform.acceleratorcms.model.components.MiniCartComponentModel;
import de.hybris.platform.selectivecartaddon.model.components.SelectiveCartCMSComponentModel;



/**
 */
public interface SelectivecartaddonControllerConstants
{
	String ADDON_PREFIX = "addon:/selectivecartaddon";

	/**
	 * Class with action name constants
	 */
	interface Actions
	{
		interface Cms
		{
			String _Prefix = "/view/";
			String _Suffix = "Controller";

			/**
			 * Default CMS component controller
			 */
			String SelectiveCartCMSComponent = _Prefix + SelectiveCartCMSComponentModel._TYPECODE + _Suffix;
			String MiniCartComponent = _Prefix + MiniCartComponentModel._TYPECODE + _Suffix;
		}
	}

	/**
	 * Class with view name constants
	 */
	interface Views
	{
		interface Cms
		{
			String ComponentPrefix = "cms/";
		}

		interface Fragments
		{
			interface Cart
			{
				String MiniCartPanel = "fragments/cart/miniCartPanel";
				String CartPopup = "fragments/cart/cartPopup";
			}
		}
	}
}
