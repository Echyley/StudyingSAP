/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.sap.platform.sapcustomermasterlookupaddon.jalo;

import com.sap.platform.sapcustomermasterlookupaddon.constants.SapcustomerlookupaddonConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class SapcustomerlookupaddonManager extends GeneratedSapcustomerlookupaddonManager {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SapcustomerlookupaddonManager.class.getName());

    public static final SapcustomerlookupaddonManager getInstance() {
        ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
        return (SapcustomerlookupaddonManager) em.getExtension(SapcustomerlookupaddonConstants.EXTENSIONNAME);
    }

}
