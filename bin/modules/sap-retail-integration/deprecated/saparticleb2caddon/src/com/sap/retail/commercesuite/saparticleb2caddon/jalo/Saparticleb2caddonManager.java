/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.retail.commercesuite.saparticleb2caddon.jalo;

import com.sap.retail.commercesuite.saparticleb2caddon.constants.Saparticleb2caddonConstants;

import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;

@SuppressWarnings("PMD")
public class Saparticleb2caddonManager extends GeneratedSaparticleb2caddonManager
{

	public static final Saparticleb2caddonManager getInstance()
	{
		final ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (Saparticleb2caddonManager) em.getExtension(Saparticleb2caddonConstants.EXTENSIONNAME);
	}

}
