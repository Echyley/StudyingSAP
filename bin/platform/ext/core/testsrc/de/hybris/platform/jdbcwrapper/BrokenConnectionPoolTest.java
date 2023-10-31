package de.hybris.platform.jdbcwrapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.fail;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.Registry;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class BrokenConnectionPoolTest extends ServicelayerBaseTest
{
	HybrisDataSource dataSource;
	JUnitConnectionErrorCheckingJDBCConnectionPool pool;
	List<JUnitConnectionImpl> generatedConnections;

	@Before
	public void setUp()
	{

		dataSource = Registry.getCurrentTenant().getDataSource();
		pool = createConnectionPool(dataSource);
		generatedConnections = generateConnectionsInPool(pool, 5);
	}

	@After
	public void tearDown()
	{
		pool.close();
	}

	@Test
	public void testConnectionPoolGetsRidOfBrokenConnectionsWhenOneConnectionReportsConnectionError() throws Exception
	{
		final JUnitConnectionImpl connectionToBeClosedBeforeStatementExecution = (JUnitConnectionImpl) pool.borrowConnection();
		final String sqlQuery = getSQLQuery(dataSource);
		final PreparedStatement dummyStatement = connectionToBeClosedBeforeStatementExecution.prepareStatement(sqlQuery);

		generatedConnections.get(1).destroy();
		generatedConnections.get(2).destroy();
		generatedConnections.get(3).destroy();

		connectionToBeClosedBeforeStatementExecution.destroy();
		final long errorCounterBeforeError = pool.getConnectionStatusErrorCounter();

		assertThatThrownBy(dummyStatement::execute).isInstanceOf(SQLException.class);
		assertThat(pool.getConnectionStatusErrorCounter()).isGreaterThan(errorCounterBeforeError);

		pool.returnConnection(connectionToBeClosedBeforeStatementExecution);
		assertThat(pool.getNumIdle()).isEqualTo(4);

		final ConnectionImpl lastRemainingConnection = (ConnectionImpl) pool.borrowConnection();
		assertThat(pool.getNumIdle()).isZero();
		assertThat(pool.getNumActive()).isEqualTo(1);
		assertThat(lastRemainingConnection).isEqualTo(generatedConnections.get(0));
	}

	public JUnitConnectionErrorCheckingJDBCConnectionPool createConnectionPool(final HybrisDataSource dataSource)
	{
		final GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
		final JUnitConnectionStatus connectionStatus = new JUnitConnectionStatus();
		final JUnitJDBCConnectionFactory factory;

		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);

		//Creating an instance of a different class for hsqldb to override the isDatabaseRunningInSameProcess method to always return false
		//since it is assumed, that if it is running in the same process, the connections cannot be broken
		if(dataSource.getDatabaseName().equals("hsqldb"))
		{
			factory = new JUnitJDBCConnectionFactoryForHSQLTesting(dataSource, connectionStatus);
		}
		else
		{
			factory = new JUnitJDBCConnectionFactory(dataSource, connectionStatus);
		}

		final JUnitConnectionErrorCheckingJDBCConnectionPool pool;
		pool = new JUnitConnectionErrorCheckingJDBCConnectionPool(factory, poolConfig, connectionStatus);

		pool.setDumpStackOnConnectionError(dataSource.getTenant().getConfig().getBoolean(
				DBConstants.POOL_DUMP_STACK_ON_CONNECTION_ERROR, DBConstants.DEFAULT_POOL_DUMP_STACK_ON_CONNECTION_ERROR));
		return pool;
	}

	public List<JUnitConnectionImpl> generateConnectionsInPool(final JUnitConnectionErrorCheckingJDBCConnectionPool pool,
	                                                           final int numberOfConnectionsToOpen)
	{
		final List<JUnitConnectionImpl> generatedConnections = new ArrayList<>();
		try
		{
			for (int i = 0; i < numberOfConnectionsToOpen; ++i)
			{
				final JUnitConnectionImpl currentConnection = (JUnitConnectionImpl) pool.borrowConnection();
				generatedConnections.add(currentConnection);
			}
		}
		catch (final Exception e)
		{
			fail("Couldn't generate connections in pool", e);
		}

		generatedConnections.forEach(pool::returnConnection);

		return generatedConnections;
	}

	private String getSQLQuery(final HybrisDataSource dataSource)
	{
		final String SQL_QUERY = "SELECT * FROM ";
		final String TABLE_NAME = "composedTypes";
		final String TABLE_PREFIX = dataSource.getTablePrefix();

		return SQL_QUERY + TABLE_PREFIX + TABLE_NAME;
	}

	private class JUnitJDBCConnectionFactoryForHSQLTesting extends JUnitJDBCConnectionFactory
	{

		public JUnitJDBCConnectionFactoryForHSQLTesting(final HybrisDataSource dataSource,
		                                         final ConnectionStatus connectionStatus)
		{
			super(dataSource, connectionStatus);
		}

		@Override
		public boolean isDatabaseRunningInSameProcess()
		{
			return false;
		}

	}
}
