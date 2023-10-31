/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.audit.actions.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.AuditableActions;
import de.hybris.platform.core.PK;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionExecutionBody;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.testframework.assertions.assertj.TestLogListenerAssert;
import de.hybris.platform.testframework.assertions.assertj.TestLogListenerAssert.LogLevel;
import de.hybris.platform.testframework.log.TestLogListener;
import de.hybris.platform.testframework.seed.TestDataCreator;

import javax.annotation.Resource;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;

@IntegrationTest
public class Slf4jAuditableActionHandlerTest extends ServicelayerBaseTest
{

	@Resource
	private UserService userService;

	@Resource
	private SessionService sessionService;
	@Resource
	private ModelService modelService;
	TestDataCreator testDataCreator;

	private final TestLogListener testLogListener = new TestLogListener();

	@Before
	public void setUp()
	{
		testDataCreator = new TestDataCreator(modelService);

		testLogListener.attach();
	}

	@After
	public void tearDown()
	{
		testLogListener.detach();
	}


	@Test
	public void shouldLogAuditAction()
	{

		final Slf4jAuditableActionHandler slf4jAuditableActionHandler = new Slf4jAuditableActionHandler(userService);
		slf4jAuditableActionHandler.auditAction(AuditableActions.action("test"));

		TestLogListenerAssert.assertThat(testLogListener)
		                     .loggedFrom(Slf4jAuditableActionHandler.class)
		                     .withLogLevel(LogLevel.INFO)
		                     .withMessageContaining("test");

	}

	@Test
	public void shouldAuditUserIfProvided()
	{
		final String employeeUid = RandomStringUtils.randomAlphabetic(10);
		final UserModel testEmployee = testDataCreator.createEmployee(employeeUid, "Test Employee");

		final String testId = RandomStringUtils.randomAlphabetic(10);

		sessionService.executeInLocalView(new SessionExecutionBody()
		{
			@Override
			public void executeWithoutResult()
			{
				final Slf4jAuditableActionHandler slf4jAuditableActionHandler = new Slf4jAuditableActionHandler(userService);
				slf4jAuditableActionHandler.auditAction(AuditableActions.action(testId));
			}
		}, testEmployee);

		final PK testEmployeePk = testEmployee.getPk();

		TestLogListenerAssert.assertThat(testLogListener)
		                     .loggedFrom(Slf4jAuditableActionHandler.class)
		                     .withLogLevel(LogLevel.INFO)
		                     .withMessageContaining(testId)
		                     .withMDCContaining("user", testEmployeePk.toString());
	}


	@Test
	public void shouldHaveUnknownUserIfUserIsNotProvided()
	{
		final String testId = RandomStringUtils.randomAlphabetic(10);

		final Slf4jAuditableActionHandler slf4jAuditableActionHandler = new Slf4jAuditableActionHandler(userService);
		slf4jAuditableActionHandler.auditAction(AuditableActions.action(testId));

		TestLogListenerAssert.assertThat(testLogListener)
		                     .loggedFrom(Slf4jAuditableActionHandler.class)
		                     .withLogLevel(LogLevel.INFO)
		                     .withMessageContaining(testId)
		                     .withMDCContaining("user", "unknown");
	}

	@Test
	public void shouldLogAuditDataAsJson()
	{
		final String testId = RandomStringUtils.randomAlphabetic(10);
		final String attr1Val = RandomStringUtils.randomAlphabetic(10);
		final String attr2Val = RandomStringUtils.randomAlphabetic(10);

		final Slf4jAuditableActionHandler slf4jAuditableActionHandler = new Slf4jAuditableActionHandler(userService);
		slf4jAuditableActionHandler.auditAction(
				AuditableActions.withAttribute("attr1", attr1Val).withAttribute("attr2", attr2Val).action(testId));

		TestLogListenerAssert.assertThat(testLogListener)
		                     .loggedFrom(Slf4jAuditableActionHandler.class)
		                     .withLogLevel(LogLevel.INFO)
		                     .withMessageContaining(testId)
		                     .withMDCContaining("audit.data.attr1", attr1Val)
		                     .withMDCContaining("audit.data.attr2", attr2Val);

	}

	@Test
	public void shouldNotPolluteMDC()
	{
		final String testId = RandomStringUtils.randomAlphabetic(10);
		final String attr1Val = RandomStringUtils.randomAlphabetic(10);
		final String attr2Val = RandomStringUtils.randomAlphabetic(10);
		final String controlVal = RandomStringUtils.randomAlphabetic(10);

		MDC.put("someOtherMDCValue", controlVal);

		final int sizeBeforeAudit = MDC.getCopyOfContextMap().size();

		final Slf4jAuditableActionHandler slf4jAuditableActionHandler = new Slf4jAuditableActionHandler(userService);
		slf4jAuditableActionHandler.auditAction(
				AuditableActions.withAttribute("attr1", attr1Val).withAttribute("attr2", attr2Val).action(testId));


		Assertions.assertThat(MDC.getCopyOfContextMap()).hasSize(sizeBeforeAudit)
		          .doesNotContainKeys("audit.name", "audit.data.attr1", "audit.data.attr2", "audit.attr1", "audit.attr2", "audit")
		          .containsEntry("someOtherMDCValue", controlVal);
	}
}
