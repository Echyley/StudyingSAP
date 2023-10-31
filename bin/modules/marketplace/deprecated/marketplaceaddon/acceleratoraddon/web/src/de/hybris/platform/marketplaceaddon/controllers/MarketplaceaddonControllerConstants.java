/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.marketplaceaddon.controllers;

import de.hybris.platform.cms2lib.model.components.BannerComponentModel;
import de.hybris.platform.cms2lib.model.components.ProductCarouselComponentModel;
import de.hybris.platform.cms2lib.model.components.RotatingImagesComponentModel;


/**
 */
public interface MarketplaceaddonControllerConstants
{

	final String ADDON_PREFIX = "addon:/marketplaceaddon";

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
			String ProductCarouselComponent = _Prefix + ProductCarouselComponentModel._TYPECODE + _Suffix;
			String RotatingImagesComponent = _Prefix + RotatingImagesComponentModel._TYPECODE + _Suffix;
			String BannerComponent = _Prefix + BannerComponentModel._TYPECODE + _Suffix;

		}
	}

	interface Views
	{
		interface Pages
		{
			interface Order
			{
				String OrderReviewPage = ADDON_PREFIX + "/pages/order/orderReviewPage";
			}

			interface Vendor
			{
				String VendorIndexPage = ADDON_PREFIX + "/pages/layout/vendorIndexLayoutPage";
				String VendorReviewsPage = ADDON_PREFIX + "/pages/store/vendorReviewsPage";
			}
		}
	}

}
