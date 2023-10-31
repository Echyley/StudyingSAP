/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.util.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.sap.retail.oaa.commerce.services.common.util.CommonUtils;
import com.sap.retail.oaa.commerce.services.constants.SapoaacommerceservicesConstants;
import com.sap.retail.oaa.model.constants.SapoaamodelConstants;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.sap.core.configuration.SAPConfigurationService;
import de.hybris.platform.store.services.BaseStoreService;

public class DefaultCommonUtils implements CommonUtils {

	private static final Logger LOG = Logger.getLogger(DefaultCommonUtils.class);
	private BaseStoreService baseStoreService;

	@Override
	public boolean isCAREnabled() {
		boolean isCARInUse = false;

		if (checkSAPConfiguration() && StringUtils.isNotEmpty(
				getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapoaa_salesChannel())) {
			isCARInUse = true;

		}

		return isCARInUse;

	}

	@Override
	public boolean isCOSEnabled() {
		boolean isCOSInUse = false;
		if (checkSAPConfiguration() && StringUtils.isNotEmpty(
				getBaseStoreService().getCurrentBaseStore().getSAPConfiguration().getSapcos_casStrategyId())) {
			isCOSInUse = true;
		}
		return isCOSInUse;
	}

	@Override
	public boolean isCAREnabled(AbstractOrderModel orderModel) {
		boolean isCARInUse = false;

		if (checkSAPConfigurationUsingOrder(orderModel)
				&& StringUtils.isNotEmpty(orderModel.getStore().getSAPConfiguration().getSapoaa_salesChannel())) {
			isCARInUse = true;

		}

		return isCARInUse;
	}

	@Override
	public boolean isCOSEnabled(AbstractOrderModel orderModel) {
		boolean isCOSInUse = false;
		if (checkSAPConfigurationUsingOrder(orderModel)
				&& StringUtils.isNotEmpty(orderModel.getStore().getSAPConfiguration().getSapcos_casStrategyId())) {
			isCOSInUse = true;
		}
		return isCOSInUse;
	}

	private boolean checkSAPConfigurationUsingOrder(AbstractOrderModel orderModel) {
		if (null != orderModel.getStore() && null != orderModel.getStore().getSAPConfiguration()) {
			return true;
		}
		return false;
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
