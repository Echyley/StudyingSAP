/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.audit.actions.AuditableActionHandler;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.util.Optional;
import java.util.function.Supplier;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

@IntegrationTest
public class AuditableActionsTest extends ServicelayerBaseTest
{

	private AuditableActionHandler testAuditableActionHandler;

	private Supplier<AuditableActionHandler> originalActionHandler;

	@Before
	public void setUp() throws Exception
	{
		testAuditableActionHandler = Mockito.mock(AuditableActionHandler.class);
		originalActionHandler = AuditableActions.getAuditableActionHandlerFactory();
		AuditableActions.setAuditableActionHandlerFactory(() -> testAuditableActionHandler);
	}

	@After
	public void tearDown() throws Exception
	{
		AuditableActions.setAuditableActionHandlerFactory(originalActionHandler);
	}

	@Test
	public void shouldAuditActionAsBuilder()
	{
		final String actionName = "test-" + RandomStringUtils.randomAlphabetic(15);
		final AuditableActions.NamedActionBuilder builder = AuditableActions.withName(actionName).withAttribute("attr", "val");
		AuditableActions.audit(builder);

		final ArgumentCaptor<AuditableActions.Action> argCaptor = ArgumentCaptor.forClass(AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(argCaptor.capture());

		final Optional<AuditableActions.Action> optionalAction = argCaptor.getAllValues()
		                                                                  .stream()
		                                                                  .filter(action -> actionName.equals(
				                                                                  action.getActionName()))
		                                                                  .findFirst();

		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("attr", "val"));
	}

	@Test
	public void shouldSkipOptionalAttributeForNullValue()
	{
		final String actionName = "test-" + RandomStringUtils.randomAlphabetic(15);
		final AuditableActions.NamedActionBuilder builder = AuditableActions.withName(actionName)
		                                                                    .withOptionalAttribute("optional", null)
		                                                                    .withAttribute("attr", "val")
		                                                                    .withOptionalAttribute("optionalNotNull", "val");

		AuditableActions.audit(builder);

		final ArgumentCaptor<AuditableActions.Action> argCaptor = ArgumentCaptor.forClass(AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(argCaptor.capture());

		final Optional<AuditableActions.Action> optionalAction = argCaptor.getAllValues()
		                                                                  .stream()
		                                                                  .filter(action -> actionName.equals(
				                                                                  action.getActionName()))
		                                                                  .findFirst();

		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("attr", "val"),
				entry("optionalNotNull", "val"));
	}

	@Test
	public void shouldAuditActionAsAction()
	{
		final String actionName = "test-" + RandomStringUtils.randomAlphabetic(15);
		final AuditableActions.Action action = AuditableActions.withAttribute("attr", "value").action(actionName);
		AuditableActions.audit(action);

		verify(testAuditableActionHandler).auditAction(action);
	}


	@Test
	public void shouldNotPropagateExceptionsThrownByActionHandler()
	{
		final String actionName = "test-" + RandomStringUtils.randomAlphabetic(15);
		final AuditableActions.Action action = AuditableActions.withAttribute("attr", "value").action(actionName);

		Mockito.doThrow(RuntimeException.class).when(testAuditableActionHandler).auditAction(action);

		AuditableActions.audit(action);

		verify(testAuditableActionHandler).auditAction(action);
	}

	@Test
	public void shouldFailOnNullAction()
	{
		assertThatThrownBy(() -> AuditableActions.audit((AuditableActions.Action) null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void shouldFailOnNullActionBuilder()
	{
		assertThatThrownBy(() -> AuditableActions.audit((AuditableActions.NamedActionBuilder) null)).isInstanceOf(
				NullPointerException.class);
	}

	@Test
	public void shouldFailOnNullActionName()
	{
		testActionName(null);
	}

	@Test
	public void shouldFailOnEmptyActionName()
	{
		testActionName(StringUtils.EMPTY);
	}

	@Test
	public void shouldFailOnBlankActionName()
	{
		testActionName("     ");
	}

	private static void testActionName(final String actionName)
	{
		assertThatThrownBy(() -> AuditableActions.action(actionName)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldFailToBuildActionWithNullActionName()
	{
		testActionFromBuilder(null);
	}

	private static void testActionFromBuilder(final String actionName)
	{
		final AuditableActions.ActionBuilder builder = AuditableActions.builder();
		assertThatThrownBy(() -> builder.action(actionName)).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldFailToBuildActionWithEmptyActionName()
	{
		testActionFromBuilder(StringUtils.EMPTY);
	}

	@Test
	public void shouldFailToBuildActionWithBlankActionName()
	{
		testActionFromBuilder("     ");
	}

	@Test
	public void shouldFailToBuildActionFromNamedActionBuilderWithNullActionName()
	{
		testActionFromNamedActionBuilder(null);
	}

	@Test
	public void shouldFailToBuildActionFromNamedActionBuilderWithEmptyActionName()
	{
		testActionFromNamedActionBuilder(StringUtils.EMPTY);
	}

	@Test
	public void shouldFailToBuildActionFromNamedActionBuilderWithBlankActionName()
	{
		testActionFromNamedActionBuilder("    ");
	}

	private static void testActionFromNamedActionBuilder(final String actionName)
	{
		final AuditableActions.NamedActionBuilder builder = AuditableActions.builder().withName(actionName);
		assertThatThrownBy(builder::action).isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldFailToAddAttributeWithNullNameToActionBuilder()
	{
		final AuditableActions.ActionBuilder builder = AuditableActions.builder();
		assertThatThrownBy(() -> builder.withAttribute(null, "val")).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void shouldFailToAddAttributeWithNullNameToNamedActionBuilder()
	{
		final AuditableActions.NamedActionBuilder builder = AuditableActions.withName("name");
		assertThatThrownBy(() -> builder.withAttribute(null, "val")).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void shouldFailToRemoveAttributeWithNullNameToActionBuilder()
	{
		final AuditableActions.ActionBuilder builder = AuditableActions.builder();
		assertThatThrownBy(() -> builder.withoutAttribute(null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void shouldFailToRemoveAttributeWithNullNameToNamedActionBuilder()
	{
		final AuditableActions.NamedActionBuilder builder = AuditableActions.withName("name");
		assertThatThrownBy(() -> builder.withoutAttribute(null)).isInstanceOf(NullPointerException.class);
	}

	@Test
	public void shouldHandleNullValueOfActionAttribute()
	{
		final String testId = RandomStringUtils.randomAlphabetic(10);
		final AuditableActions.Action action = AuditableActions.withName(testId).withAttribute("nullAttr", null).action();

		assertThat(action.getActionAttributes()).containsEntry("nullAttr", AuditableActions.Action.NULL_VALUE);
	}

}
