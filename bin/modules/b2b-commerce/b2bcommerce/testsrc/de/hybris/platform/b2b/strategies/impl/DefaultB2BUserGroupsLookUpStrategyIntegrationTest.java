/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.strategies.impl;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.constants.B2BConstants;
import de.hybris.platform.servicelayer.ServicelayerBaseTest;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.platform.servicelayer.config.ConfigurationService;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BUserGroupsLookUpStrategyIntegrationTest extends ServicelayerBaseTest
{

	private static final String UNIT_ORDER_VIEWER_GROUP_UID = "unitorderviewergroup";
	private static final String[] defaultGroupUids = { "b2badmingroup", "b2bcustomergroup", "b2bmanagergroup", "b2bapprovergroup" };

	@Resource
	private ConfigurationService configurationService;
	@Resource
	private DefaultB2BUserGroupsLookUpStrategy b2bUserGroupsLookUpStrategy;

	private List<String> defaultUserGroups;

	@Before
	public void setUp() throws Exception
	{
		defaultUserGroups = Arrays.asList(defaultGroupUids);
	}

	@Test
	public void shouldGetUserGroups()
	{
		final List<String> userGroups = b2bUserGroupsLookUpStrategy.getUserGroups();

		assertThat(userGroups).isNotNull().containsAll(defaultUserGroups);
		assertThat(userGroups.size()).isGreaterThanOrEqualTo(defaultUserGroups.size());
	}

	@Test
	public void shouldGetOrderViewerGroupWhenUnitLevelOrdersEnabled()
	{
		configurationService.getConfiguration().setProperty(B2BConstants.UNITLEVELORDERS_ENABLED, "true");

		final List<String> userGroups = b2bUserGroupsLookUpStrategy.getUserGroups();

		assertThat(userGroups).isNotNull().containsAll(defaultUserGroups).contains(UNIT_ORDER_VIEWER_GROUP_UID);
	}
}
