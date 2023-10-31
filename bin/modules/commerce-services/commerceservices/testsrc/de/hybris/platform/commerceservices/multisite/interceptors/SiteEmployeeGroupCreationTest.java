/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.multisite.interceptors;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.model.user.SiteEmployeeGroupModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.config.impl.HybrisConfiguration;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

import org.fest.util.Collections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@IntegrationTest
public class SiteEmployeeGroupCreationTest extends ServicelayerTransactionalTest
{

	private static final String DEFAULT_BASE_SITE_UID = "DefaultBaseSiteUid";
	private static final String DEFAULT_SITE_EMPLOYEE_GROUP_UID = "DefaultSiteEmployeeGroupUid";
	private static final String DEFAULT_CONFIGURED_PARENT_GROUP_NAME = "parentUserGroup";

	@Resource
	private ModelService modelService;
	@Resource
	private ConfigurationService configurationService;
	@Resource
	private UserService userService;

	private BaseSiteModel defaultBaseSiteModel;

	private boolean delimiterParsingDisabledBefore;

	@Before
	public void setUp()
	{
		delimiterParsingDisabledBefore = ((HybrisConfiguration) configurationService.getConfiguration()).isDelimiterParsingDisabled();

		defaultBaseSiteModel = modelService.create(BaseSiteModel.class);
		defaultBaseSiteModel.setUid(DEFAULT_BASE_SITE_UID);
		defaultBaseSiteModel.setDataIsolationEnabled(true);

		modelService.save(defaultBaseSiteModel);

		final UserGroupModel upperUserGroup = modelService.create(UserGroupModel.class);
		upperUserGroup.setUid(DEFAULT_CONFIGURED_PARENT_GROUP_NAME);

		modelService.save(upperUserGroup);
	}

	@After
	public void tearDown()
	{
		((HybrisConfiguration) configurationService.getConfiguration()).setDelimiterParsingDisabled(delimiterParsingDisabledBefore);
	}

	@Test
	public void testPropertyNotConfiguredShouldNotCreateUpperGroup()
	{
		// when
		final SiteEmployeeGroupModel siteEmployeeGroupModel = modelService.create(SiteEmployeeGroupModel.class);

		siteEmployeeGroupModel.setUid(DEFAULT_SITE_EMPLOYEE_GROUP_UID);
		siteEmployeeGroupModel.setVisibleSite(defaultBaseSiteModel);

		modelService.save(siteEmployeeGroupModel);

		// then
		final UserGroupModel fetchedUserGroup = userService.getUserGroupForUID(DEFAULT_SITE_EMPLOYEE_GROUP_UID);

		assertTrue(fetchedUserGroup instanceof SiteEmployeeGroupModel);
		assertTrue(Collections.isEmpty(fetchedUserGroup.getGroups()));
	}

	@Test
	public void testPropertyConfiguredShouldCreateUpperGroup()
	{
		// given
		configurationService.getConfiguration()
				.setProperty(SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT, DEFAULT_CONFIGURED_PARENT_GROUP_NAME);

		// when
		final SiteEmployeeGroupModel siteEmployeeGroupModel = modelService.create(SiteEmployeeGroupModel.class);

		siteEmployeeGroupModel.setUid(DEFAULT_SITE_EMPLOYEE_GROUP_UID);
		siteEmployeeGroupModel.setVisibleSite(defaultBaseSiteModel);

		modelService.save(siteEmployeeGroupModel);

		// then
		final UserGroupModel fetchedUserGroup = userService.getUserGroupForUID(DEFAULT_SITE_EMPLOYEE_GROUP_UID);

		assertTrue(fetchedUserGroup instanceof SiteEmployeeGroupModel);
		final Set<PrincipalGroupModel> groups = fetchedUserGroup.getGroups();

		assertEquals(1, groups.size());
		final PrincipalGroupModel firstGroup = (PrincipalGroupModel) groups.toArray()[0];
		assertEquals(DEFAULT_CONFIGURED_PARENT_GROUP_NAME, firstGroup.getUid());
	}

	@Test
	public void testPropertyConfiguredWrongShouldNotCreateUpperGroup()
	{
		// given
		configurationService.getConfiguration().setProperty(SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT, "notExist");

		// when
		final SiteEmployeeGroupModel siteEmployeeGroupModel = modelService.create(SiteEmployeeGroupModel.class);

		siteEmployeeGroupModel.setUid(DEFAULT_SITE_EMPLOYEE_GROUP_UID);
		siteEmployeeGroupModel.setVisibleSite(defaultBaseSiteModel);

		modelService.save(siteEmployeeGroupModel);

		// then
		final UserGroupModel fetchedUserGroup = userService.getUserGroupForUID(DEFAULT_SITE_EMPLOYEE_GROUP_UID);

		assertTrue(fetchedUserGroup instanceof SiteEmployeeGroupModel);
		assertTrue(Collections.isEmpty(fetchedUserGroup.getGroups()));
	}

	@Test
	public void testPropertyConfiguredShouldAddUpperGroupToGroups()
	{
		// given
		final String existUpperUserGroupUid = "existParentGroup";
		configurationService.getConfiguration()
				.setProperty(SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT, DEFAULT_CONFIGURED_PARENT_GROUP_NAME);

		final UserGroupModel existUpperUserGroup = modelService.create(UserGroupModel.class);
		existUpperUserGroup.setUid(existUpperUserGroupUid);

		modelService.save(existUpperUserGroup);

		// when
		final SiteEmployeeGroupModel siteEmployeeGroupModel = modelService.create(SiteEmployeeGroupModel.class);

		siteEmployeeGroupModel.setUid(DEFAULT_SITE_EMPLOYEE_GROUP_UID);
		siteEmployeeGroupModel.setVisibleSite(defaultBaseSiteModel);
		Set<PrincipalGroupModel> existGroups = siteEmployeeGroupModel.getGroups();
		if (existGroups == null)
		{
			existGroups = new HashSet<>();
			siteEmployeeGroupModel.setGroups(existGroups);
		}
		existGroups.add(existUpperUserGroup);

		modelService.save(siteEmployeeGroupModel);

		// then
		final UserGroupModel fetchedUserGroup = userService.getUserGroupForUID(DEFAULT_SITE_EMPLOYEE_GROUP_UID);

		assertTrue(fetchedUserGroup instanceof SiteEmployeeGroupModel);
		final Set<PrincipalGroupModel> groups = fetchedUserGroup.getGroups();

		assertEquals(2, groups.stream().filter(
						group -> group.getUid().equals(existUpperUserGroupUid) || group.getUid().equals(DEFAULT_CONFIGURED_PARENT_GROUP_NAME))
				.count());
	}

	@Test
	public void testPropertyConfiguredMultiGroupsShouldAddToGroups()
	{
		// given
		final String notExistGroupName = "notExistGroup";
		final String secondGroupName = "secondPGroup";
		final StringJoiner joiner = new StringJoiner(",");
		joiner.add(DEFAULT_CONFIGURED_PARENT_GROUP_NAME).add(notExistGroupName).add(secondGroupName);

		((HybrisConfiguration) configurationService.getConfiguration()).setDelimiterParsingDisabled(true);

		configurationService.getConfiguration().setProperty(SITE_EMPLOYEE_GROUP_GROUPS_DEFAULT, joiner.toString());

		final UserGroupModel secondParentGroup = modelService.create(UserGroupModel.class);
		secondParentGroup.setUid(secondGroupName);

		modelService.save(secondParentGroup);

		// when
		final SiteEmployeeGroupModel siteEmployeeGroupModel = modelService.create(SiteEmployeeGroupModel.class);

		siteEmployeeGroupModel.setUid(DEFAULT_SITE_EMPLOYEE_GROUP_UID);
		siteEmployeeGroupModel.setVisibleSite(defaultBaseSiteModel);

		modelService.save(siteEmployeeGroupModel);

		// then
		final UserGroupModel fetchedUserGroup = userService.getUserGroupForUID(DEFAULT_SITE_EMPLOYEE_GROUP_UID);

		assertTrue(fetchedUserGroup instanceof SiteEmployeeGroupModel);
		final Set<PrincipalGroupModel> groups = fetchedUserGroup.getGroups();

		assertEquals(2, groups.stream().filter(
						group -> group.getUid().equals(secondGroupName) || group.getUid().equals(DEFAULT_CONFIGURED_PARENT_GROUP_NAME))
				.count());
	}
}
