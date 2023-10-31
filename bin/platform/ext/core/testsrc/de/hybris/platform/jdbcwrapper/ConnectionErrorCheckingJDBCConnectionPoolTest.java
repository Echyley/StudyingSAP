/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.jdbcwrapper;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.DataSourceImplFactory;
import de.hybris.platform.core.Tenant;
import de.hybris.platform.servicelayer.tenant.MockTenant;
import de.hybris.platform.util.Config.SystemSpecificParams;
import de.hybris.platform.util.config.ConfigIntf;
import de.hybris.platform.util.config.FastHashMapConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for jdbc connect retry mechanism.
 */
@UnitTest
public class ConnectionErrorCheckingJDBCConnectionPoolTest
{
	private ConnectionErrorCheckingJDBCConnectionPool createPool(final int amountOfErrors)
	{
		final Tenant currentTenant = new MockTenant("test")
		{
			@Override
			public ConfigIntf getConfig()
			{
				return new FastHashMapConfig(Collections.emptyMap());
			}
		};

		final Map<String, String> params = new TreeMap<>();
		params.put(SystemSpecificParams.DB_USERNAME, "junit-heini");
		params.put(SystemSpecificParams.DB_PASSWORD, "ignored");
		params.put(SystemSpecificParams.DB_URL, "jdbc:hsqldb:mem:susi");
		params.put(SystemSpecificParams.DB_DRIVER, "org.hsqldb.jdbcDriver");
		params.put(SystemSpecificParams.DB_TABLEPREFIX, "o");
		final HybrisDataSource dataSource = new DataSourceImplFactory().createDataSource(//
				"testing", //
				currentTenant, //
				params, //
				false);

		return new ConnectionErrorCheckingJDBCConnectionPool(//
				new CorruptedJDBCConnectionFactory(dataSource, amountOfErrors), //
				new GenericObjectPoolConfig(), //
				new ConnectionStatus());
	}

	/**
	 * (regression test) no retry --> single connection creation error must throw exception
	 */
	@Test(expected = SQLException.class)
	public void testBackwardCompatibility() throws Exception
	{
		final ConnectionErrorCheckingJDBCConnectionPool pool = this.createPool(1);
		pool.setConnectRetries(0);
		pool.borrowConnection();
	}

	/**
	 * (regression test) no retry --> single connection creation error must throw exception
	 */
	@Test(expected = SQLException.class)
	public void testBackwardCompatibility2() throws Exception
	{
		this.createPool(1).borrowConnection();
	}

	/**
	 * retry success --> 3 retries configured + single connection error must not throw any exception + connection must be
	 * returned
	 */
	@Test
	public void testRetrySuccess() throws Exception
	{
		final ConnectionErrorCheckingJDBCConnectionPool pool = this.createPool(1);
		pool.setConnectRetries(3);
		Assert.assertNotNull("No exception thrown.", pool.borrowConnection());
	}

	@Test
	public void testReallyHighRetrySuccess() throws Exception
	{
		final ConnectionErrorCheckingJDBCConnectionPool pool = this.createPool(18);
		pool.setConnectRetries(32);
		Assert.assertNotNull("No exception thrown.", pool.borrowConnection());
	}

	/**
	 * retry too low --> 1 retry configured + two connection creation error must throw exception again
	 */
	@Test(expected = SQLException.class)
	public void testRetryTooLow() throws Exception
	{
		final ConnectionErrorCheckingJDBCConnectionPool pool = this.createPool(2);
		pool.setConnectRetries(1);
		pool.borrowConnection();
	}

	private class CorruptedJDBCConnectionFactory extends JDBCConnectionFactory
	{
		private final AtomicInteger failureCount;

		private CorruptedJDBCConnectionFactory(final HybrisDataSource hybrisDataSource, final int failureCount)
		{
			super(hybrisDataSource);
			this.failureCount = new AtomicInteger(failureCount);
		}

		@Override
		public PooledObject<Connection> makeObject() throws SQLException
		{
			if (this.failureCount.getAndDecrement() > 0)
			{
				throw new SimulatedSQLException();
			}
			return super.makeObject();
		}
	}

	private static class SimulatedSQLException extends SQLException
	{
		SimulatedSQLException()
		{
			super("Simulating jdbc connection error.");
		}
	}
}
