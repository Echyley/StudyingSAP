/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.persistence.audit.gateway.impl;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.bootstrap.util.LocaleHelper;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.persistence.audit.gateway.AuditRecord;
import de.hybris.platform.persistence.audit.gateway.AuditSearchQuery;
import de.hybris.platform.persistence.audit.gateway.ReadAuditGateway;
import de.hybris.platform.persistence.audit.gateway.WriteAuditGateway;
import de.hybris.platform.persistence.audit.internal.AuditEnablementService;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.testframework.PropertyConfigSwitcher;
import de.hybris.platform.testframework.seed.TestDataCreator;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class DefaultWriteAuditGatewayTest extends ServicelayerBaseTest
{
	@Resource(name = "auditingEnablementService")
	private AuditEnablementService auditEnablementService;

	@Resource
	private ReadAuditGateway readAuditGateway;

	@Resource
	private WriteAuditGateway writeAuditGateway;

	@Resource
	private ModelService modelService;

	private final PropertyConfigSwitcher pcsAuditingAllTypesEnabled = new PropertyConfigSwitcher("auditing.alltypes.enabled");
	private final PropertyConfigSwitcher pcsAuditUserEnabled = new PropertyConfigSwitcher(
			"audit." + UserModel._TYPECODE.toLowerCase(LocaleHelper.getPersistenceLocale()) + ".enabled");
	private TestDataCreator testDataCreator;

	@Before
	public void setUp()
	{
		testDataCreator = new TestDataCreator(modelService);
	}

	@After
	public void tearDown()
	{
		pcsAuditingAllTypesEnabled.switchBackToDefault();
		pcsAuditUserEnabled.switchBackToDefault();
	}

	@Test
	public void shouldNotSkipRemovingAuditRecordsWhenAuditEnabled() throws Exception
	{
		//given
		pcsAuditingAllTypesEnabled.switchToValue("true");
		pcsAuditUserEnabled.switchToValue("true");
		auditEnablementService.refreshConfiguredAuditTypes();
		final UserModel user = testDataCreator.createUser("ronald", "Ronald Reagan");
		final AuditSearchQuery query = AuditSearchQuery.forType(UserModel._TYPECODE).withPkSearchRules(user.getPk()).build();
		writeAuditGateway.removeAuditRecordsForType(UserModel._TYPECODE, user.getPk());

		// when
		final List<AuditRecord> auditRecords = readAuditGateway.search(query).collect(Collectors.toList());

		// then
		assertThat(auditRecords).isEmpty();
	}

	@Test
	public void shouldSkipRemovingAuditRecordsWhenAuditDisabled() throws Exception
	{
		//given
		pcsAuditingAllTypesEnabled.switchToValue("true");
		pcsAuditUserEnabled.switchToValue("true");
		auditEnablementService.refreshConfiguredAuditTypes();
		final UserModel user = testDataCreator.createUser("ronald", "Ronald Reagan");
		final AuditSearchQuery query = AuditSearchQuery.forType(UserModel._TYPECODE).withPkSearchRules(user.getPk()).build();

		pcsAuditingAllTypesEnabled.switchToValue("false");
		pcsAuditUserEnabled.switchToValue("false");
		auditEnablementService.refreshConfiguredAuditTypes();

		writeAuditGateway.removeAuditRecordsForType(UserModel._TYPECODE, user.getPk());

		// when
		final List<AuditRecord> auditRecords = readAuditGateway.search(query).collect(Collectors.toList());

		// then
		assertThat(auditRecords).isNotEmpty();
	}
}
