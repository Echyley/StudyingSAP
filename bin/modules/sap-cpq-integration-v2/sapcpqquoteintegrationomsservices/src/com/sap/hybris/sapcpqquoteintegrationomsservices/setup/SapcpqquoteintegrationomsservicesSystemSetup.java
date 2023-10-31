/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.hybris.sapcpqquoteintegrationomsservices.setup;

import static com.sap.hybris.sapcpqquoteintegrationomsservices.constants.SapcpqquoteintegrationomsservicesConstants.PLATFORM_LOGO_CODE;

import de.hybris.platform.core.initialization.SystemSetup;

import java.io.InputStream;

import com.sap.hybris.sapcpqquoteintegrationomsservices.constants.SapcpqquoteintegrationomsservicesConstants;
import com.sap.hybris.sapcpqquoteintegrationomsservices.service.SapcpqquoteintegrationomsservicesService;


@SystemSetup(extension = SapcpqquoteintegrationomsservicesConstants.EXTENSIONNAME)
public class SapcpqquoteintegrationomsservicesSystemSetup
{
	private final SapcpqquoteintegrationomsservicesService sapcpqquoteintegrationomsservicesService;

	public SapcpqquoteintegrationomsservicesSystemSetup(final SapcpqquoteintegrationomsservicesService sapcpqquoteintegrationomsservicesService)
	{
		this.sapcpqquoteintegrationomsservicesService = sapcpqquoteintegrationomsservicesService;
	}

	@SystemSetup(process = SystemSetup.Process.INIT, type = SystemSetup.Type.ESSENTIAL)
	public void createEssentialData()
	{
		sapcpqquoteintegrationomsservicesService.createLogo(PLATFORM_LOGO_CODE);
	}

	private InputStream getImageStream()
	{
		return SapcpqquoteintegrationomsservicesSystemSetup.class.getResourceAsStream("/sapcpqquoteintegrationomsservices/sap-hybris-platform.png");
	}
}
