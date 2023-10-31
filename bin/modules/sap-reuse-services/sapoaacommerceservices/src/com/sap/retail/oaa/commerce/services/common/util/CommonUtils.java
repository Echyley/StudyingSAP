/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.oaa.commerce.services.common.util;

import de.hybris.platform.core.model.order.AbstractOrderModel;

public interface CommonUtils {

	boolean isCAREnabled();
	
	boolean isCAREnabled(AbstractOrderModel abstractOrderModel);

	boolean isCOSEnabled();
	
	boolean isCOSEnabled(AbstractOrderModel orderModel);

}
