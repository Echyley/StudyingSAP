/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapquoteintegrationoms.service;

public interface SapquoteintegrationomsService
{
	String getHybrisLogoUrl(String logoCode);

	void createLogo(String logoCode);
}