/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.test;

import static org.assertj.core.api.Assertions.assertThat;
import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.jdbcwrapper.ConnectionImpl;
import de.hybris.platform.jdbcwrapper.HybrisDataSource;
import de.hybris.platform.jdbcwrapper.JUnitConnectionImpl;
import de.hybris.platform.testframework.HybrisJUnit4Test;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.TestUtils;
import de.hybris.platform.tx.TransactionConnectionBrokenException;
import de.hybris.platform.tx.DefaultTransaction;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;

import java.sql.SQLException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoSession;

@IntegrationTest
public class TransactionDBOutageTest extends HybrisJUnit4Test
{

	private final PropertyConfigSwitcher exceptionForBrokenConnectionEnabled = new PropertyConfigSwitcher(
			Transaction.CFG_EXECUTE_THROWS_EXCEPTION_FOR_BROKEN_CONNECTION);

	private MockitoSession mockito;

	@Before
	public void setUp()
	{
		mockito = Mockito.mockitoSession().initMocks(this).startMocking();
	}

	@After
	public void cleanUp()
	{
		mockito.finishMocking();
		exceptionForBrokenConnectionEnabled.switchBackToDefault();
	}

	@Test
	public void shouldThrowConnectionClosedExceptionDuringCommitWhenEnabled() throws Exception
	{
		exceptionForBrokenConnectionEnabled.switchToValue("true");
		checkIfThereIsNoTransactionRunning();
		DefaultTransactionWithModifiedConnection transaction = null;
		try
		{
			transaction = new DefaultTransactionWithModifiedConnection();
			transaction.activateAsCurrentTransaction();
			try
			{
				Transaction.current().execute(new TransactionBody()
				{
					@Override
					public Object execute() throws Exception
					{
						throw new TestPermittedException();
					}
				}, TestPermittedException.class);
				Assert.fail("Expected an TransactionConnectionBrokenException to be thrown");
			}
			catch (final TransactionConnectionBrokenException te)
			{
				assertThat(te.getCause()).isInstanceOf(TestPermittedException.class);
			}
		}
		finally
		{
			resetTestMode(transaction);
		}
	}

	private void resetTestMode(final DefaultTransactionWithModifiedConnection transaction)
	{
		if (transaction != null && transaction.getJUnitConnection() != null)
		{
			transaction.getJUnitConnection().resetTestMode();
		}
	}


	@Test
	public void shouldThrowConnectionClosedExceptionDuringRollbackWhenEnabled() throws Exception
	{
		exceptionForBrokenConnectionEnabled.switchToValue("true");
		checkIfThereIsNoTransactionRunning();
		DefaultTransactionWithModifiedConnection transaction = null;
		try
		{
			transaction = new DefaultTransactionWithModifiedConnection();
			transaction.activateAsCurrentTransaction();
			try
			{
				Transaction.current().execute(new TransactionBody()
				{
					@Override
					public Object execute() throws Exception
					{
						throw new TestUnPermittedException();
					}
				});
				Assert.fail("Expected an TransactionConnectionBrokenException to be thrown");
			}
			catch (final TransactionConnectionBrokenException te)
			{
				assertThat(te.getCause()).isInstanceOf(TestUnPermittedException.class);
			}
		}
		finally
		{
			resetTestMode(transaction);
		}
	}

	@Test(expected = TestPermittedException.class)
	public void shouldNotThrowConnectionClosedExceptionDuringCommit() throws Exception
	{
		exceptionForBrokenConnectionEnabled.switchToValue("false");
		TestUtils.disableFileAnalyzer(
				"expecting logged exception from TransactionDBOutageTest.shouldNotThrowConnectionClosedExceptionDuringCommit");
		checkIfThereIsNoTransactionRunning();
		DefaultTransactionWithModifiedConnection transaction = null;
		try
		{
			transaction = new DefaultTransactionWithModifiedConnection();
			transaction.activateAsCurrentTransaction();

			Transaction.current().execute(new TransactionBody()
			{
				@Override
				public Object execute() throws Exception
				{
					throw new TestPermittedException();
				}
			}, TestPermittedException.class);

		}
		finally
		{
			resetTestMode(transaction);
			TestUtils.enableFileAnalyzer();
		}
	}


	@Test(expected = TestUnPermittedException.class)
	public void shouldNotThrowConnectionClosedExceptionDuringRollback() throws Exception
	{
		exceptionForBrokenConnectionEnabled.switchToValue("false");
		TestUtils.disableFileAnalyzer(
				"expecting logged exception from TransactionDBOutageTest.shouldNotThrowConnectionClosedExceptionDuringRollback");
		checkIfThereIsNoTransactionRunning();
		DefaultTransactionWithModifiedConnection transaction = null;
		try
		{
			transaction = new DefaultTransactionWithModifiedConnection();
			transaction.activateAsCurrentTransaction();
			Transaction.current().execute(new TransactionBody()
			{
				@Override
				public Object execute()
				{
					throw new TestUnPermittedException();
				}
			});
		}
		finally
		{
			resetTestMode(transaction);
			TestUtils.enableFileAnalyzer();
		}
	}

	@Test(expected = TransactionConnectionBrokenException.class)
	public void shouldThrowConnectionClosedExceptionWhenConnectionIsBrokenAndThereIsNoOtherExceptionDuringExecute()
			throws Exception
	{
		exceptionForBrokenConnectionEnabled.switchToValue("true");
		checkIfThereIsNoTransactionRunning();
		DefaultTransactionWithModifiedConnection transaction = null;
		try
		{
			transaction = new DefaultTransactionWithModifiedConnection();
			transaction.activateAsCurrentTransaction();

			Transaction.current().execute(new TransactionBody()
			{
				@Override
				public Object execute() throws Exception
				{
					return null;
				}
			});
		}
		finally
		{
			resetTestMode(transaction);
		}
	}

	@Test(expected = TransactionConnectionBrokenException.class)
	public void shouldThrowConnectionBrokenExceptionWhenRollbackFailedForFlushDelayedStore() throws Exception
	{
		exceptionForBrokenConnectionEnabled.switchToValue("true");
		checkIfThereIsNoTransactionRunning();
		DefaultTransactionWithModifiedConnection transaction = null;
		try
		{
			transaction = new DefaultTransactionWithModifiedConnection()
			{
				@Override
				public void flushDelayedStore()
				{
					throw new RuntimeException("Exception during flushDelayedStore");
				}

			};
			transaction.activateAsCurrentTransaction();
			Transaction.current().execute(new TransactionBody()
			{
				@Override
				public Object execute() throws Exception
				{
					return null;
				}
			});
		}
		finally
		{
			resetTestMode(transaction);
		}
	}

	@Test(expected = TransactionConnectionBrokenException.class)
	public void shouldThrowConnectionBrokenExceptionForRollbackOnly() throws Exception
	{
		exceptionForBrokenConnectionEnabled.switchToValue("true");
		checkIfThereIsNoTransactionRunning();
		DefaultTransactionWithModifiedConnection transaction = null;
		try
		{
			transaction = new DefaultTransactionWithModifiedConnection();
			transaction.activateAsCurrentTransaction();

			Transaction.current().execute(new TransactionBody()
			{
				@Override
				public Object execute() throws Exception
				{
					Transaction.current().setRollbackOnly();
					return null;
				}
			});
		}
		finally
		{
			resetTestMode(transaction);
		}
	}

	@Test(expected = TransactionConnectionBrokenException.class)
	public void shouldThrowConnectionBrokenExceptionForNestedTransactions() throws Exception
	{
		exceptionForBrokenConnectionEnabled.switchToValue("true");
		checkIfThereIsNoTransactionRunning();
		DefaultTransactionWithModifiedConnection transaction = null;
		try
		{
			transaction = new DefaultTransactionWithModifiedConnection();
			transaction.activateAsCurrentTransaction();

			Transaction.current().execute(new TransactionBody()
			{
				@Override
				public Object execute() throws Exception
				{
					Transaction.current().execute(new TransactionBody()
					{
						@Override
						public Object execute() throws Exception
						{

							return null;
						}
					});
					return null;
				}
			});
		}
		finally
		{
			resetTestMode(transaction);
		}
	}

	@Test
	public void shouldThrowTransactionConnectionBrokenWhenGetConnectionToBindReturnNullConnection() throws Exception
	{
		final Transaction modifiedTransaction = new DefaultTransaction()
		{
			@Override
			protected ConnectionImpl getConnectionToBindWithRetry(final HybrisDataSource dataSource)
			{
				return null;
			}
		};
		modifiedTransaction.activateAsCurrentTransaction();
		Assert.assertThrows(TransactionConnectionBrokenException.class, () -> Transaction.current().begin());
	}

	@Test
	public void shouldThrowTransactionConnectionBrokenWhenGetConnectionToBindFail()
	{
		final Transaction modifiedTransaction = new DefaultTransaction()
		{
			@Override
			protected ConnectionImpl getConnectionToBindWithRetry(final HybrisDataSource dataSource)
			{
				throw new RuntimeException("Exception during getConnectionToBindWithRetry");
			}
		};
		modifiedTransaction.activateAsCurrentTransaction();
		Assert.assertThrows(TransactionConnectionBrokenException.class, () -> Transaction.current().begin());
	}

	@Test
	public void shouldThrowTransactionConnectionBrokenWhenSetTxBoundUserTAThrowsException() throws SQLException
	{

		final ConnectionImpl mockedConnection = Mockito.mock(ConnectionImpl.class);

		Mockito.doThrow(new SQLException("Connection is closed")).when(mockedConnection).setTxBoundUserTA(Mockito.any());
		Mockito.doReturn(false).when(mockedConnection).isClosed();

		final Transaction modifiedTransaction = new DefaultTransaction()
		{

			@Override
			protected ConnectionImpl getConnectionToBindWithRetry(final HybrisDataSource dataSource)
			{
				return mockedConnection;
			}
		};
		modifiedTransaction.activateAsCurrentTransaction();
		final Exception exception = Assert.assertThrows(TransactionConnectionBrokenException.class,
				() -> Transaction.current().begin());
		assertThat(exception.getMessage()).contains("Connection is closed");
	}

	private void checkIfThereIsNoTransactionRunning()
	{
		assertThat(Transaction.current().getOpenTransactionCount()).isZero();
		assertThat(Transaction.current().isRunning()).isFalse();
	}

	private static class TestUnPermittedException extends RuntimeException
	{

	}

	private static class TestPermittedException extends Exception
	{

	}

	private static class DefaultTransactionWithModifiedConnection extends DefaultTransaction
	{
		JUnitConnectionImpl modifiedConnection;

		@Override
		protected ConnectionImpl getConnectionToBind(final HybrisDataSource dataSource) throws SQLException
		{
			final JUnitConnectionImpl connection = (JUnitConnectionImpl) super.getConnectionToBind(dataSource);
			connection.setCommitMode(JUnitConnectionImpl.CommitMode.COMMIT_OR_ROLLBACK_ERROR_AND_CLOSE_CONNECTION);
			modifiedConnection = connection;
			return connection;
		}

		JUnitConnectionImpl getJUnitConnection()
		{
			return modifiedConnection;
		}
	}
}
