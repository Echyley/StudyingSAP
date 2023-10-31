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
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

@IntegrationTest
public class DynamicLanguageRelatedAuditableActionTest extends ServicelayerBaseTest
{
	@Resource
	private HacDynamicLanguagesFacade hacDynamicLanguagesFacade;

	private AuditableActionHandler testAuditableActionHandler;
	private Supplier<AuditableActionHandler> originalActionHandler;

	private Optional<AuditableActions.Action> getAuditableAction(final String actionName)
	{

		final ArgumentCaptor<AuditableActions.Action> argCaptor = ArgumentCaptor.forClass(AuditableActions.Action.class);
		verify(testAuditableActionHandler, atLeastOnce()).auditAction(argCaptor.capture());

		return argCaptor.getAllValues()
		                .stream()
		                .filter(action -> actionName.equals(
				                action.getActionName()))
		                .findFirst();

	}

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
	public void shouldAuditScriptExecution()
	{
		//given
		final String scriptValue = "return Groovy Rocks!";

		//when
		hacDynamicLanguagesFacade.executeScript("groovy", scriptValue, false);

		//then
		final Optional<String> scriptSignatureToCompare = HacDynamicLanguagesFacade.generateSHA256Signature(
				HacDynamicLanguagesFacade.normalizeEOL(scriptValue));

		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("script execution started");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("commit", "false"),
				entry("engineName", "groovy"), entry("scriptSignature", scriptSignatureToCompare.get()));

	}

	@Test
	public void shouldAuditScriptCreate()
	{
		final String scriptValue = "return Groovy Rocks!";
		//when
		hacDynamicLanguagesFacade.saveScript("scriptCodeToCreate", scriptValue, "groovy");

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("script created");
		final Optional<String> scriptSignatureToCompare = HacDynamicLanguagesFacade.generateSHA256Signature(
				HacDynamicLanguagesFacade.normalizeEOL(scriptValue));
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("code", "scriptCodeToCreate"),
				entry("engineName", "groovy"), entry("scriptSignature", scriptSignatureToCompare.get()));
	}

	@Test
	public void shouldAuditScriptUpdate()
	{
		//given
		final String codeName = "scriptCodeToUpdate123456789";
		final String scriptValue = "return Groovy Rocks!";
		hacDynamicLanguagesFacade.saveScript(codeName, scriptValue, "groovy");
		//when
		hacDynamicLanguagesFacade.saveScript(codeName, scriptValue, "groovy");

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("script updated");
		final Optional<String> scriptSignatureToCompare = HacDynamicLanguagesFacade.generateSHA256Signature(
				HacDynamicLanguagesFacade.normalizeEOL(scriptValue));
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("code", codeName),
				entry("engineName", "groovy"), entry("scriptSignature", scriptSignatureToCompare.get()));
	}

	@Test
	public void shouldAuditScriptDelete()
	{
		//given
		final String scriptValue = "return Groovy Rocks!";
		hacDynamicLanguagesFacade.saveScript("scriptCodeToDelete", scriptValue, "groovy");

		//when
		hacDynamicLanguagesFacade.deleteScript("scriptCodeToDelete");

		//then
		final Optional<AuditableActions.Action> optionalAction = getAuditableAction("script removed");
		assertThat(optionalAction).isNotEmpty();
		assertThat(optionalAction.get().getActionAttributes()).containsOnly(entry("code", "scriptCodeToDelete"));
	}

}


