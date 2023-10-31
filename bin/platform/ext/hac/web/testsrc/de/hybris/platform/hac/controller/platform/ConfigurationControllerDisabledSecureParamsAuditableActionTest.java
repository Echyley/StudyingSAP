/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.controller.platform;

import de.hybris.bootstrap.annotations.IntegrationTest;

@IntegrationTest
public class ConfigurationControllerDisabledSecureParamsAuditableActionTest
		extends ConfigurationControllerSecureParamsAuditableActionTest
{

	@Override
	protected void configureAuditFlag()
	{
		auditPropertyValues.switchToValue("false");
	}

	protected String getAuditValue()
	{
		return "auditDisabled";
	}

}
