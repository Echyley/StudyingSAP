/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.persistence.audit.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.PK;
import de.hybris.platform.processengine.model.ProcessTaskModel;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DBAuditEnablementServiceTest extends ServicelayerBaseTest
{
	@Resource
	private DBAuditEnablementService dbAuditEnablementService;
	@Resource
	private TypeService typeService;
	@Resource
	private ModelService modelService;
	@Resource
	private SessionService sessionService;

	private final PropertyConfigSwitcher flagForUserType = getConfigSwitcherForType("user");
	private final PropertyConfigSwitcher flagForProcessTask = getConfigSwitcherForType("processtask");

	private final PropertyConfigSwitcher flagForCapitalUser = getConfigSwitcherForType("User");
	private final PropertyConfigSwitcher flagForCapitalCatalog = getConfigSwitcherForType("CaTaLoG");

	private PK typePkForEmployee;

	@Before
	public void setUp()
	{
		typePkForEmployee = typeService.getComposedTypeForCode("Employee").getPk();
	}

	@After
	public void tearDown()
	{
		flagForUserType.switchBackToDefault();
		flagForProcessTask.switchBackToDefault();

		flagForCapitalUser.switchBackToDefault();
		flagForCapitalUser.switchBackToDefault();
		flagForCapitalCatalog.switchBackToDefault();

		dbAuditEnablementService.refreshConfiguredAuditTypes();
		dbAuditEnablementService.enableAuditInSession();
	}

	@Test
	public void shouldHaveAuditEnabledForTypeWhichIsNotDisabled()
	{
		// given
		final PK mediaTypePk = typeService.getComposedTypeForCode("Media").getPk();

		// when
		final boolean auditEnabled = dbAuditEnablementService.isAuditEnabledForType(mediaTypePk);

		// then
		assertThat(auditEnabled).isTrue();
	}

	@Test
	public void shouldHaveAuditDisabledForTypeIfAuditIsDisabled()
	{
		// given
		flagForProcessTask.switchToValue("true");
		dbAuditEnablementService.refreshConfiguredAuditTypes();
		final PK typePk = typeService.getComposedTypeForCode(ProcessTaskModel._TYPECODE).getPk();

		// when
		final boolean auditEnabled = dbAuditEnablementService.isAuditEnabledForType(typePk);

		// then
		assertThat(auditEnabled).isFalse();
	}

	@Test
	public void shouldHaveAuditDisabledForSubTypeWhenSupertpeIsSetInConfiguration()
	{
		// given
		flagForUserType.switchToValue("true");
		dbAuditEnablementService.refreshConfiguredAuditTypes();

		// when
		final boolean auditEnabled = dbAuditEnablementService.isAuditEnabledForType(typePkForEmployee);

		// then
		assertThat(auditEnabled).isFalse();
	}

	@Test
	public void shouldThrowExceptionIfAuditPropertyDefinedTwice()
	{
		flagForUserType.switchToValue("false");
		flagForCapitalUser.switchToValue("true");

		try
		{
			dbAuditEnablementService.refreshConfiguredAuditTypes();
			fail("Expected auditEnablementService to throw an exception");
		}
		catch (final Exception ex)
		{
			assertThat(ex).isInstanceOf(AuditConfigurationException.class);
			final AuditConfigurationException auditConfigException = (AuditConfigurationException) ex;
			assertThat(auditConfigException.getViolatingProperties()).containsOnly("dbaudit.User.disabled");
		}
	}

	@Test
	public void shouldThrowExceptionForNonLowercaseProperty()
	{
		flagForCapitalCatalog.switchToValue("true");

		try
		{
			dbAuditEnablementService.refreshConfiguredAuditTypes();
			fail("Expected auditEnablementService to throw an exception");
		}
		catch (final Exception ex)
		{
			assertThat(ex).isInstanceOf(AuditConfigurationException.class);
			final AuditConfigurationException auditConfigException = (AuditConfigurationException) ex;
			assertThat(auditConfigException.getViolatingProperties()).containsOnly("dbaudit.CaTaLoG.disabled");
		}
	}

	@Test
	public void shouldDisableDBAuditInSession()
	{
		// when
		dbAuditEnablementService.disableAuditInSession();

		// then
		assertThat(dbAuditEnablementService.skipActions()).isTrue();
	}

	@Test
	public void shouldEnableDBAuditInSessionWhenDisabledFirst()
	{
		// when
		dbAuditEnablementService.disableAuditInSession();
		dbAuditEnablementService.enableAuditInSession();

		// then
		assertThat(dbAuditEnablementService.skipActions()).isFalse();
	}

	@Test
	public void shouldNotDisableDBAuditInSessionForFakeValue()
	{
		// when
		sessionService.setAttribute("ctx.dbaudit.skip.actions", "SKIP_DB_AUDIT");

		// then
		assertThat(dbAuditEnablementService.skipActions()).isFalse();
	}

	private PropertyConfigSwitcher getConfigSwitcherForType(final String type)
	{
		return new PropertyConfigSwitcher("dbaudit." + type + ".disabled");
	}
}
