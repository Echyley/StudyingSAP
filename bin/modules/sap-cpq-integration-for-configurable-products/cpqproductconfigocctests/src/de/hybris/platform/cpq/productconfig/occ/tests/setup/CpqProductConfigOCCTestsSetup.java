/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cpq.productconfig.occ.tests.setup;

import de.hybris.bootstrap.config.ConfigUtil;
import de.hybris.bootstrap.config.ExtensionInfo;
import de.hybris.bootstrap.config.PlatformConfig;
import de.hybris.platform.commerceservices.dataimport.impl.CoreDataImportService;
import de.hybris.platform.commerceservices.dataimport.impl.SampleDataImportService;
import de.hybris.platform.commerceservices.setup.AbstractSystemSetup;
import de.hybris.platform.core.initialization.SystemSetupParameter;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;


/**
 * CPQ OCC Test implementation of {@link AbstractSystemSetup}
 */
public class CpqProductConfigOCCTestsSetup extends AbstractSystemSetup
{
	private CoreDataImportService coreDataImportService;
	private SampleDataImportService sampleDataImportService;
	private UserService userService;

	/**
	 * import data required for CPQ OCC integration tests
	 */
	public void loadData()
	{
		final UserModel currentUser = getUserService().getCurrentUser();
		final EmployeeModel adminUser = getUserService().getAdminUser();
		getUserService().setCurrentUser(adminUser);
		getSetupImpexService().importImpexFile(
				"/cpqproductconfigocctests/import/sampledata/productCatalogs/testConfigureCatalog/cpqProductConfig_basic_testDataStandalone.impex",
				false);

		getSetupImpexService().importImpexFile(
				"/cpqproductconfigocctests/import/sampledata/productCatalogs/testConfigureCatalog/cpqProductConfigUsers.impex",
				false);

		getSetupImpexService().importImpexFile(
				"/cpqproductconfigocctests/import/sampledata/productCatalogs/testConfigureCatalog/cpqProductConfigSapConfiguration.impex",
				false);

		getSetupImpexService().importImpexFile(
				"/cpqproductconfigocctests/import/sampledata/productCatalogs/testConfigureCatalog/cpqProductConfigOutboundOMMOrderOMSOrder.impex",
				false);

		getSetupImpexService().importImpexFile(
				"/cpqproductconfigocctests/import/sampledata/productCatalogs/testConfigureCatalog/process.impex", false);

		if (isExtensionInSetup("promotionengineservices"))
		{
			// to avoid log is flooded with
			// java.lang.IllegalStateException: No rule engine context could be derived for order/cart
			getSetupImpexService().importImpexFile("/cpqproductconfigservices/test/testDataDummyPromotion.impex", false);
		}

		getUserService().setCurrentUser(currentUser);
	}

	public boolean isExtensionInSetup(final String extension)
	{
		final PlatformConfig platformConfig = ConfigUtil.getPlatformConfig(Utilities.class);
		final ExtensionInfo extensionInfo = platformConfig.getExtensionInfo(extension);
		return (extensionInfo != null);
	}

	@Override
	public List<SystemSetupParameter> getInitializationOptions()
	{
		final List<SystemSetupParameter> params = new ArrayList<>();

		params.add(createBooleanSystemSetupParameter(CoreDataImportService.IMPORT_CORE_DATA, "Import Core Data", true));
		params.add(createBooleanSystemSetupParameter(SampleDataImportService.IMPORT_SAMPLE_DATA, "Import Sample Data", true));

		return params;
	}

	public CoreDataImportService getCoreDataImportService()
	{
		return coreDataImportService;
	}

	public void setCoreDataImportService(final CoreDataImportService coreDataImportService)
	{
		this.coreDataImportService = coreDataImportService;
	}

	public SampleDataImportService getSampleDataImportService()
	{
		return sampleDataImportService;
	}

	public void setSampleDataImportService(final SampleDataImportService sampleDataImportService)
	{
		this.sampleDataImportService = sampleDataImportService;
	}

	protected UserService getUserService()
	{
		return userService;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public boolean isGetCPQConfigurationIdForQuoteEntryEndpointDisabled()
	{
		boolean disabled = false;
		final String disabledEndpoints = Config.getParameter("commercewebservices.api.restrictions.disabled.endpoints");
		if (StringUtils.isNotEmpty(disabledEndpoints))
		{
			final String[] disabledEndpointsArray = disabledEndpoints.split(",");
			disabled = Arrays.stream(disabledEndpointsArray).anyMatch("getCPQConfigurationIdForQuoteEntry"::equals);
		}
		return disabled;
	}

}
