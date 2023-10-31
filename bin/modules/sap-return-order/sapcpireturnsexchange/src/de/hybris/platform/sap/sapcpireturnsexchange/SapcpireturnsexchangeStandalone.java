/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.sapcpireturnsexchange;

import de.hybris.platform.core.Registry;
import de.hybris.platform.util.RedeployUtilities;
import de.hybris.platform.util.Utilities;

/**
 * Demonstration of how to write a standalone application that can be run
 * directly from within eclipse or from the commandline.<br>
 * To run this from commandline, just use the following command:<br>
 * <code>
 * java -jar bootstrap/bin/ybootstrap.jar "new de.hybris.platform.sap.sapcpireturnsexchange.SapcpireturnsexchangeStandalone().run();"
 * </code> From eclipse, just run as Java Application. Note that you maybe need
 * to add all other projects like ext-commerce, ext-pim to the Launch
 * configuration classpath.
 */
public class SapcpireturnsexchangeStandalone {
    /**
     * Main class to be able to run it directly as a java program.
     *
     * @param args
     *            the arguments from commandline
     */
    public static void main(final String[] args) {
        new SapcpireturnsexchangeStandalone().run();
    }

    public void run() {
        Registry.activateStandaloneMode();
        Registry.activateMasterTenant();

        Utilities.printAppInfo();

        RedeployUtilities.shutdown();
    }
}
