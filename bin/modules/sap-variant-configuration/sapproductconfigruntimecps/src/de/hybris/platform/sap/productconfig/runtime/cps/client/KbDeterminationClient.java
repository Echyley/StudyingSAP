/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.runtime.cps.client;

import com.hybris.charon.annotations.Control;
import com.hybris.charon.annotations.OAuth;


/**
 * Decorating the {@link KbDeterminationClientBase} with enforced authorization via OAuth.
 */
@OAuth
@Control(timeout = "${sapproductconfigruntimecps.charon.timeout:15000}")
public interface KbDeterminationClient extends KbDeterminationClientBase
{
	// empty - just to enfore authorization via OAuth
}
