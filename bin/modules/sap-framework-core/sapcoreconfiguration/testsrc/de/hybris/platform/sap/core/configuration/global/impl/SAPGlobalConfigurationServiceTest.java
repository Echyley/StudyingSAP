/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.configuration.global.impl;

import static org.junit.Assert.assertEquals;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.sap.core.configuration.model.SAPGlobalConfigurationModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.interceptor.impl.DefaultInterceptorRegistry;
import de.hybris.platform.servicelayer.interceptor.impl.InterceptorMapping;
import de.hybris.platform.servicelayer.model.ModelService;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Test for global configuration service.
 */
@IntegrationTest
public class SAPGlobalConfigurationServiceTest extends ServicelayerTransactionalTest
{

	@Resource(name = "sapCoreSAPGlobalConfigurationService")
	private SAPGlobalConfigurationServiceImpl classUnderTest;

	/**
	 * Variable for the model service.
	 */

	@Resource
	private ModelService modelService;

	/**
	 * Interceptor Registry.
	 */
	@Resource(name = "interceptorRegistry")
	protected DefaultInterceptorRegistry interceptorRegistry; //NOPMD

	/**
	 * Mandatory Attributes Validator Mapping.
	 */
	@Resource(name = "mandatoryAttributesValidatorMapping")
	protected InterceptorMapping interceptorMappingForMandatoryAttributesValidator; //NOPMD

	/**
	 * Test SAPGlobalConfigurationModel.
	 */
	private SAPGlobalConfigurationModel globaConfigModel = null;

	/**
	 * Test creation time.
	 */
	private Date creationTime = null;


	@SuppressWarnings("javadoc")
	@Before
	public void setUp()
	{
		// unregister the mandatoryAttributesValidator because the SAPGlobalConfiguration is also defined in the sapmodel
		// extension with some mandatory fields. These mandatory fields won't be filled within a core component because
		// they are belonging to the application.
		interceptorRegistry.unregisterInterceptor(interceptorMappingForMandatoryAttributesValidator);

		globaConfigModel = new SAPGlobalConfigurationModel();
		modelService.save(globaConfigModel);
		creationTime = globaConfigModel.getCreationtime();
	}

	@SuppressWarnings("javadoc")
	@After
	public void tearDown()
	{
		modelService.remove(globaConfigModel);

		interceptorRegistry.registerInterceptor(interceptorMappingForMandatoryAttributesValidator);
	}

	@SuppressWarnings("javadoc")
	@Test
	public void testSAPGlobalConfigurationGet()
	{
		assertEquals(creationTime, classUnderTest.getProperty("creationtime"));
	}

}
