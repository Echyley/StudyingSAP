/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.hac.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.audit.AuditableActionsUtil;
import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.hac.data.LoggerConfigData;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.apache.logging.log4j.Level;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

@IntegrationTest
public class LogRelatedAuditableActionTest extends ServicelayerBaseTest
{
	private final HacLog4JFacade log4JFacade = new HacLog4JFacade();
	private AuditableActionHandler testAuditableActionHandler;
	private Supplier<AuditableActionHandler> originalActionHandler;

	@Before
	public void setUp()
	{
		testAuditableActionHandler = mock(AuditableActionHandler.class);
		originalActionHandler = AuditableActionsUtil.getAuditableActionHandlerFactory();
		AuditableActionsUtil.setAuditableActionHandlerFactory(() -> testAuditableActionHandler);
	}

	@After
	public void tearDown()
	{
		AuditableActionsUtil.setAuditableActionHandlerFactory(originalActionHandler);
	}

	@Test
	public void shouldAuditNewLoggerCreationWithLogLevel()
	{
		//given
		final String loggerName = LogRelatedAuditableActionTest.class.getName() + "testLogger";
		Optional<LoggerConfigData> loggerConfig = getLoggerConfig(loggerName);
		assertThat(loggerConfig.isPresent()).isFalse();

		// when
		log4JFacade.changeLogLevel(loggerName, Level.ERROR.name());

		//then
		loggerConfig = getLoggerConfig(loggerName);
		assertThat(loggerConfig.isPresent()).isTrue();
		final ArgumentCaptor<AuditableActions.Action> actionArgumentCaptor = ArgumentCaptor.forClass(
				AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(actionArgumentCaptor.capture());
		final AuditableActions.Action capturedAction = actionArgumentCaptor.getValue();
		assertThat(capturedAction.getActionName()).isEqualTo("new logger created");
		assertThat(capturedAction.getActionAttributes()).containsOnly(entry("logLevel", "ERROR"),
				entry("loggerName", loggerName));
	}

	@Test
	public void shouldAuditLogLevelChangeForExistingLogger()
	{
		//given
		final String loggerName = LogRelatedAuditableActionTest.class.getName() + "testLogger2";
		Optional<LoggerConfigData> loggerConfig = getLoggerConfig(loggerName);
		assertThat(loggerConfig.isPresent()).isFalse();
		log4JFacade.changeLogLevel(loggerName, Level.ERROR.name());
		loggerConfig = getLoggerConfig(loggerName);
		assertThat(loggerConfig.isPresent()).isTrue();

		//when
		log4JFacade.changeLogLevel(loggerName, Level.DEBUG.name());

		//then
		loggerConfig = getLoggerConfig(loggerName);
		assertThat(loggerConfig.isPresent()).isTrue();
		final ArgumentCaptor<AuditableActions.Action> actionArgumentCaptor = ArgumentCaptor.forClass(
				AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(actionArgumentCaptor.capture());
		final AuditableActions.Action capturedAction = actionArgumentCaptor.getValue();
		assertThat(capturedAction.getActionName()).isEqualTo("log level changed");
		assertThat(capturedAction.getActionAttributes()).containsOnly(entry("newLogLevel", "DEBUG"),
				entry("prevLogLevel", "ERROR"),
				entry("loggerName", loggerName));
	}


	private Optional<LoggerConfigData> getLoggerConfig(final String loggerName)
	{
		final List<LoggerConfigData> loggers = log4JFacade.getLoggers();
		return loggers.stream().filter(i -> i.getName().equals(loggerName)).findFirst();
	}

}
