/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.client;



import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.OAuth;


/**
 * Decorating the {@link MasterDataClientBase} with enforced authorization via OAuth.
 */
@OAuth
@Control(timeout = "${sapproductconfigruntimecps.charon.timeout:15000}")
public interface MasterDataClient extends MasterDataClientBase
{
	// empty - just to enfore authorization via OAuth
}
