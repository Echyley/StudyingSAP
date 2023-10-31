/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.core;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.AbstractTenant.ShutDownMode;
import de.hybris.platform.core.AbstractTenant.State;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 *
 */
@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class MasterTenantUnitTest
{


	@Test
	public void testShutdownMasterDuringInitDoesNotCallShutdownForSlaves()
	{

		final SlaveTenant foo = Mockito.spy(new TestSlaveTenantStub("foo"));
		foo.setState(State.STARTED);
		//

		final SlaveTenant bar = Mockito.spy(new TestSlaveTenantStub("bar"));
		bar.setState(State.STARTED);


		final Map<String, SlaveTenant> tenants = new HashMap<String, SlaveTenant>();
		tenants.put("foo", foo);
		tenants.put("bar", bar);

		final MasterTenant master = new MasterTenant()
		{
			@Override
			protected java.util.Map<String, SlaveTenant> getSlaveTenantsMap()
			{
				return tenants;
			}

			@Override
			List<TenantListener> getTenantListeners()
			{
				return java.util.Collections.EMPTY_LIST;
			}
		};

		master.setState(State.STARTED);

		master.doShutDownDuringInitialization();

		Mockito.verify(foo, Mockito.times(0)).doShutDown();
		Mockito.verify(bar, Mockito.times(0)).doShutDown();

	}


	@Test
	public void testShutdownMasterCallsShutdownForSlaves()
	{

		final SlaveTenant foo = Mockito.spy(new TestSlaveTenantStub("foo"));
		foo.setState(State.STARTED);

		final SlaveTenant bar = Mockito.spy(new TestSlaveTenantStub("bar"));
		bar.setState(State.STARTED);

		final Map<String, SlaveTenant> tenants = new HashMap<String, SlaveTenant>();
		tenants.put("foo", foo);
		tenants.put("bar", bar);

		final MasterTenant master = new MasterTenant()
		{
			@Override
			protected java.util.Map<String, SlaveTenant> getSlaveTenantsMap()
			{
				return tenants;
			}

			@Override
			List<TenantListener> getTenantListeners()
			{
				return java.util.Collections.EMPTY_LIST;
			}
		};

		master.setState(State.STARTED);

		master.doShutDown();

		Mockito.verify(foo).shutDown(ShutDownMode.SYSTEM_SHUTDOWN);
		Mockito.verify(bar).shutDown(ShutDownMode.SYSTEM_SHUTDOWN);

	}

	@Test
	public void testShutdownDuringReconnectMasterCallsShutdownDuringReconnectForSlaves()
	{

		final SlaveTenant foo = Mockito.spy(new TestSlaveTenantStub("foo"));
		foo.setState(State.STARTED);
		BDDMockito.doReturn(null).when(foo).getMasterDataSource();

		this.setTenantConnectionCheck(foo, "true");
		foo.cannotConnect();

		final SlaveTenant bar = Mockito.spy(new TestSlaveTenantStub("bar"));
		bar.setState(State.STARTED);
		BDDMockito.doReturn(null).when(bar).getMasterDataSource();

		this.setTenantConnectionCheck(bar, "true");
		bar.cannotConnect();

		final Map<String, SlaveTenant> tenants = new HashMap<String, SlaveTenant>();
		tenants.put("foo", foo);
		tenants.put("bar", bar);

		final MasterTenant master = new MasterTenant()
		{
			@Override
			public HybrisDataSource getMasterDataSource()
			{
				return null;
			}

			@Override
			protected java.util.Map<String, SlaveTenant> getSlaveTenantsMap()
			{
				return tenants;
			}

			@Override
			List<TenantListener> getTenantListeners()
			{
				return java.util.Collections.EMPTY_LIST;
			}
		};
		this.setTenantConnectionCheck(master, "true");
		master.setState(State.STARTED);
		master.cannotConnect();//mark as cannot connect
	}

	private void setTenantConnectionCheck(AbstractTenant tenant, String value){
		tenant.getConfig().setParameter("tenant.connection.check.enabled", value);
	}

}
