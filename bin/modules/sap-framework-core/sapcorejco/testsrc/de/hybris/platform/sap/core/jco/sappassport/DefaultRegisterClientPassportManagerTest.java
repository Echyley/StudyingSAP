/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.core.jco.sappassport;

import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.sap.core.configuration.rfc.dao.SAPRFCDestinationDao;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.tenant.TenantService;


@UnitTest
public class DefaultRegisterClientPassportManagerTest
{
	private static final String TENANT_JUNIT = "junit";

	private static final String TENANT_MASTER = "master";

	@InjectMocks
	private DefaultRegisterClientPassportManager defaultRegisterClientPassportManager =new DefaultRegisterClientPassportManager();

	private TenantService tenantService = Mockito.mock(TenantService.class);
	private ConfigurationService configurationService = Mockito.mock(ConfigurationService.class);
	private SAPRFCDestinationDao rfcDestinationDao = Mockito.mock(SAPRFCDestinationDao.class);

	@Before
	public void setUp() 
	{
		defaultRegisterClientPassportManager.setConfigurationService(configurationService);
		defaultRegisterClientPassportManager.setTenantService(tenantService);
		defaultRegisterClientPassportManager.setRfcDestinationDao(rfcDestinationDao);
	}

	@Test
	public void testInitTenantMaster() 
	{
		when(tenantService.getCurrentTenantId()).thenReturn(TENANT_MASTER);
		defaultRegisterClientPassportManager.init();
		defaultRegisterClientPassportManager.destroy();
	}

	@Test
	public void testInitTenantJunit() 
	{
		when(tenantService.getCurrentTenantId()).thenReturn(TENANT_JUNIT);
		defaultRegisterClientPassportManager.init();
		defaultRegisterClientPassportManager.destroy();
	}


	@Test
	public void testInitAlreadyRegistered() 
	{
		when(tenantService.getCurrentTenantId()).thenReturn(TENANT_MASTER);
		defaultRegisterClientPassportManager.init();
		defaultRegisterClientPassportManager.init();
		defaultRegisterClientPassportManager.destroy();
		defaultRegisterClientPassportManager.destroy();
	}

	@Test
	public void testInitUnregister() 
	{
		when(tenantService.getCurrentTenantId()).thenReturn(TENANT_MASTER);
		defaultRegisterClientPassportManager.init();
	}
	
	@Test
	public void testDestroyUnregister() 
	{
		when(tenantService.getCurrentTenantId()).thenReturn(TENANT_MASTER);
		defaultRegisterClientPassportManager.destroy();
	}
}
