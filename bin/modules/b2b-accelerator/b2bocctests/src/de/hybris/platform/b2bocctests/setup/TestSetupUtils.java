/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bocctests.setup;

import de.hybris.platform.commerceservices.constants.CommerceServicesConstants;
import de.hybris.platform.commerceservices.order.strategies.impl.DefaultQuoteUserTypeIdentificationStrategy;
import de.hybris.platform.commerceservices.setup.SetupImpexService;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.util.FieldUtils;


/**
 * Utility class to be used in test suites to manage tests (e.g. start server, load data).
 */
public class TestSetupUtils extends de.hybris.platform.commercewebservicestests.setup.TestSetupUtils
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TestSetupUtils.class);

	private static String buyerGroup;
	private static String sellerGroup;
	private static String approverGroup;

	public static void loadExtensionDataInJunit()
	{
		Registry.setCurrentTenantByID("junit");
		loginAdmin();
		loadExtensionData();
		defineQuoteRoles();
		defineQuoteThresholds();
	}

	private static void loadExtensionData()
	{
		final B2BOCCTestSetup b2bOccTestSetup = Registry.getApplicationContext().getBean("b2BOCCTestSetup", B2BOCCTestSetup.class);
		b2bOccTestSetup.loadData();
	}

	private static void loginAdmin()
	{
		final UserService userService = Registry.getApplicationContext().getBean("userService", UserService.class);
		userService.setCurrentUser(userService.getAdminUser());

	}

	private static void defineQuoteRoles()
	{
		final DefaultQuoteUserTypeIdentificationStrategy defaultQuoteUserTypeIdentificationStrategy = Registry.getApplicationContext()
				.getBean("defaultQuoteUserTypeIdentificationStrategy", DefaultQuoteUserTypeIdentificationStrategy.class);

		try
		{
			buyerGroup = FieldUtils.getFieldValue(defaultQuoteUserTypeIdentificationStrategy, "buyerGroup").toString();
			sellerGroup = FieldUtils.getFieldValue(defaultQuoteUserTypeIdentificationStrategy, "sellerGroup").toString();
			approverGroup = FieldUtils.getFieldValue(defaultQuoteUserTypeIdentificationStrategy, "sellerApproverGroup").toString();
		}
		catch (final IllegalAccessException e)
		{
			LOGGER.error(e.getMessage(), e);
		}

		defaultQuoteUserTypeIdentificationStrategy.setBuyerGroup("b2bcustomergroup");
		defaultQuoteUserTypeIdentificationStrategy.setSellerGroup("b2bsellergroup");
		defaultQuoteUserTypeIdentificationStrategy.setSellerApproverGroup("b2bapprovergroup");
	}

	private static void defineQuoteThresholds()
	{
		Config.setParameter(CommerceServicesConstants.QUOTE_REQUEST_INITIATION_THRESHOLD, "1500");
		Config.setParameter(CommerceServicesConstants.QUOTE_APPROVAL_THRESHOLD, "7500");
	}

	public static void cleanupDataInJunit()
	{
		final DefaultQuoteUserTypeIdentificationStrategy defaultQuoteUserTypeIdentificationStrategy = Registry.getApplicationContext()
				.getBean("defaultQuoteUserTypeIdentificationStrategy", DefaultQuoteUserTypeIdentificationStrategy.class);

		defaultQuoteUserTypeIdentificationStrategy.setBuyerGroup(buyerGroup);
		defaultQuoteUserTypeIdentificationStrategy.setSellerGroup(sellerGroup);
		defaultQuoteUserTypeIdentificationStrategy.setSellerApproverGroup(approverGroup);
	}

	public static void updateCaptchaCheckEnabled(boolean enable)
	{
		final SetupImpexService setupImpexService = Registry.getApplicationContext()
				.getBean("setupImpexService", SetupImpexService.class);
		if (enable)
		{
			setupImpexService.importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/captcha/captchaEnabled.impex", false);
		}
		else
		{
			setupImpexService.importImpexFile("/b2bocctests/import/sampledata/wsCommerceOrg/captcha/captchaDisabled.impex", false);
		}
	}
}
