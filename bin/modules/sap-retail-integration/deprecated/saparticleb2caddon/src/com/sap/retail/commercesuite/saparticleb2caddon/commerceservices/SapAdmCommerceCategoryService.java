/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticleb2caddon.commerceservices;

import java.util.Collection;

import org.apache.commons.lang3.StringUtils;

import com.sap.retail.commercesuite.saparticlemodel.util.ArticleCommonUtils;
import com.sap.retail.commercesuite.saparticlemodel.util.impl.DefaultArticleCommonUtils;

/**
*
*/

import de.hybris.platform.category.model.CategoryModel;
import de.hybris.platform.commerceservices.category.impl.DefaultCommerceCategoryService;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;

/**
 * Add ERP Classification categories to the supported categories list, handled
 * by DefeultCommerceCategoryService.java. Related incident "Navigation via
 * bread crumbs leads to 404 HHTP Response Code" is described in the link below
 * https://support.wdf.sap.corp/sap/support/message/1670268238
 */
public class SapAdmCommerceCategoryService extends DefaultCommerceCategoryService {
	private ArticleCommonUtils articleCommonUtils;
	
	

	@Override
	public CategoryModel getCategoryForCode(final String code) {
		if (getArticleCommonUtils().isCAREnabled() || getArticleCommonUtils().isCOSEnabled()) {
			// Get all the categories for the specified code
			final Collection<CategoryModel> categoriesForCode = getCategoryService().getCategoriesForCode(code);

			// Start original Code : filter the categories and return the first not in a
			// classification system
			// Start error correction
			// filter and get the first non empty category
			for (final CategoryModel categoryModel : categoriesForCode) {
				if (categoryModel != null) {
					return categoryModel;
				}

			}

			throw new UnknownIdentifierException("Category with code '" + code
					+ "' not found! (Active session catalogversions: " + getCatalogVersionsString() + ")");
		}
		// End error correction
		else {

			return super.getCategoryForCode(code);
		}
	}



	public ArticleCommonUtils getArticleCommonUtils() {
		return articleCommonUtils;
	}



	public void setArticleCommonUtils(ArticleCommonUtils articleCommonUtils) {
		this.articleCommonUtils = articleCommonUtils;
	}
	
}
