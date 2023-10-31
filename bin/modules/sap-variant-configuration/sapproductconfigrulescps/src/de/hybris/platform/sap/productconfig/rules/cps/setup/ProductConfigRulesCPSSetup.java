/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.productconfig.rules.cps.setup;

import de.hybris.platform.core.initialization.SystemSetup;
import de.hybris.platform.core.initialization.SystemSetup.Process;
import de.hybris.platform.core.initialization.SystemSetup.Type;
import de.hybris.platform.core.initialization.SystemSetupContext;
import de.hybris.platform.sap.productconfig.rules.cps.constants.SapproductconfigrulescpsConstants;
import de.hybris.platform.sap.productconfig.rules.setup.ProductConfigRulesSetup;


/**
 * This Setup implementation loads multiple language files for the CPS specific rules implementation.
 *
 * It is expected, that the language files are delivered as properties file, following the file name format
 * <strong>sapproductconfigrulescps-impexsupport_[<i>two character language code</i>].properties</strong>, and are
 * placed in the resources/localization folder.
 *
 * The properties files are used as parameter sets for the essential Impex file (
 * <strong>essentialdata-sapproductconfigrulescps_languages.impex</strong>).
 * 
 * @deprecated since 22.11 release due to using of the standard essentialdata impex logic
 */
@SystemSetup(extension = SapproductconfigrulescpsConstants.EXTENSIONNAME)
@Deprecated(since = "2211", forRemoval = true)
public class ProductConfigRulesCPSSetup extends ProductConfigRulesSetup
{
	/**
	 * The method processes first the base definitions for the CPS rules, and updates the definitions values with the
	 * language specific property files. Each language property file will trigger an import for the language essential
	 * Impex.
	 *
	 * @param context
	 *           System context, provided by the initialize or update run
	 */
	@Override
	@SystemSetup(type = Type.ESSENTIAL, process = Process.ALL)
	public void processEssentialFiles(final SystemSetupContext context)
	{
		super.processEssentialFiles(context);
	}

	@Override
	protected String getExtensionName()
	{
		return SapproductconfigrulescpsConstants.EXTENSIONNAME;
	}
}
