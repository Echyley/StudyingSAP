/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.opps;

import com.sap.retail.sapppspricing.RequestHelper;

public interface PPSClientBeanAccessor {
	
	/**
	 * @return object factory for client API DTOs
	 */
	ObjectFactory getObjectFactory();

	/**
	 * @return request helper
	 */
	RequestHelper getHelper();
	
}
