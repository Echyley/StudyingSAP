/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticlemodel.util.impl;

import static com.sap.retail.commercesuite.saparticlemodel.constants.SaparticlemodelConstants.COSSTRATEGYID;
import static com.sap.retail.commercesuite.saparticlemodel.constants.SaparticlemodelConstants.SALESCHANNEL;

import org.apache.commons.lang3.StringUtils;

import com.sap.retail.commercesuite.saparticlemodel.util.ArticleCommonUtils;

import de.hybris.platform.store.services.BaseStoreService;



/*
 * The class DefaultArticleCommonUtils is used to 
 * provide methods to check whether it is CAR enabled or COS enabled. 
 * 
 */

public class DefaultArticleCommonUtils implements ArticleCommonUtils {

	private BaseStoreService baseStoreService;

	@Override
	public boolean isCAREnabled() {
		boolean isCARInUse = false;

		if (checkSAPConfiguration() && StringUtils.isNotEmpty(
				getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getProperty(SALESCHANNEL))) {
			isCARInUse = true;

		}

		return isCARInUse;

	}

	@Override
	public boolean isCOSEnabled() {
		boolean isCOSInUse = false;
		if (checkSAPConfiguration() && StringUtils.isNotEmpty(
				getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getProperty(COSSTRATEGYID))) {
			isCOSInUse = true;
		}
		return isCOSInUse;
	}

	private boolean checkSAPConfiguration() {
		if (null != getBaseStoreService() && null != getBaseStoreService().getCurrentBaseStore()
				&& null != getBaseStoreService().getCurrentBaseStore().getSAPConfiguration()) {
			return true;
		}
		return false;
	}

	public BaseStoreService getBaseStoreService() {
		return baseStoreService;
	}

	public void setBaseStoreService(BaseStoreService baseStoreService) {
		this.baseStoreService = baseStoreService;
	}

}
