/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.sapppspricing.opps;

import com.sap.retail.sapppspricing.RequestHelper;

public class PPSClientBeanAccessorImpl implements PPSClientBeanAccessor{
	
	private ObjectFactory objectFactory;
	private RequestHelper helper;
	
	
	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}
	public void setHelper(RequestHelper helper) {
		this.helper = helper;
	}
	@Override
	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}
	@Override
	public RequestHelper getHelper() {
		return helper;
	}

	
}
