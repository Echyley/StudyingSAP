/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.group.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimGroup;
import de.hybris.platform.scimfacades.ScimGroupMember;
import de.hybris.platform.scimfacades.utils.ScimGroupUtils;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

@IntegrationTest
public class DefaultScimGroupFacadeIntegrationTest extends ServicelayerTransactionalTest {

	private ScimGroup group;

	@Resource
	private DefaultScimGroupFacade scimGroupFacade;

	private static final String GROUP_ID = "group-id";
	private static final String NEW_GROUP_ID = "newGroup";

	@Resource
	private FlexibleSearchService flexibleSearchService;

	@Resource
	private ModelService modelService;

	private static final Logger LOG = Logger.getLogger(DefaultScimGroupFacadeIntegrationTest.class);

	@Before
	public void setUp() throws ScimException
	{
		LOG.info("Default Scim Group Facade Integration Test");
		group = new ScimGroup();
		group.setId(GROUP_ID);
		scimGroupFacade.createGroup(group);
	}

	@Test
	public void shouldCreateGroup() {
		final ScimGroup newGroup = createNewGroup();
		final ScimUserGroupModel exampleGroupModel = new ScimUserGroupModel();
		exampleGroupModel.setScimUserGroup(NEW_GROUP_ID);
		final List<ScimUserGroupModel> groupModels = flexibleSearchService.getModelsByExample(exampleGroupModel);
		if (CollectionUtils.isNotEmpty(groupModels)) {
			Assert.assertEquals(newGroup.getId(), groupModels.get(0).getScimUserGroup());			
		}

	}

	@Test
	public void shouldGetGroup() {
		final ScimGroup returnedGroup = scimGroupFacade.getGroup(GROUP_ID);
		Assert.assertEquals(group.getId(), returnedGroup.getId());
	}

	@Test
	public void shouldGetAllGroups() {
		createNewGroup();
		final String[] ids = {NEW_GROUP_ID, GROUP_ID};
		final List<ScimGroup> returnedScimGroups = scimGroupFacade.getGroups();
		for(final ScimGroup returnedGroup: returnedScimGroups) {
			Assert.assertTrue(Arrays.asList(ids).contains(returnedGroup.getId()));
		}
	}

	@Test
	public void shouldDeleteGroup() {
		scimGroupFacade.deleteGroup(GROUP_ID);
		final ScimUserGroupModel exampleGroupModel = new ScimUserGroupModel();
		exampleGroupModel.setScimUserGroup(GROUP_ID);
		Assert.assertTrue(flexibleSearchService.getModelsByExample(exampleGroupModel).isEmpty());
	}

	@Test
	public void shouldUpdateGroup() {
		boolean memberExists = false;

		final UserGroupModel exampleUserGroupModel = new UserGroupModel("admingroup");
		final List<UserGroupModel> userGroupModels = flexibleSearchService.getModelsByExample(exampleUserGroupModel);

		final ScimUserGroupModel exampleGroupModel = new ScimUserGroupModel();
		exampleGroupModel.setScimUserGroup(GROUP_ID);
		ScimUserGroupModel groupModel = flexibleSearchService.getModelByExample(exampleGroupModel);

		groupModel.setUserGroups(userGroupModels);
		modelService.save(groupModel);

		final ScimGroupMember scimGroupMember = new ScimGroupMember();
		scimGroupMember.setValue("newMember");
		final List<ScimGroupMember> groupMembers = new ArrayList<> ();
		groupMembers.add(scimGroupMember);
		group.setMembers(groupMembers);

		scimGroupFacade.updateGroup(GROUP_ID, group);

		groupModel = flexibleSearchService.getModelByExample(exampleGroupModel);

		Assert.assertEquals(GROUP_ID, groupModel.getScimUserGroup());

		final Collection<UserGroupModel> members = groupModel.getUserGroups();
		if (!members.isEmpty()){
			for(final UserGroupModel userGroupModel : members) {
				final Set<PrincipalModel> users = ScimGroupUtils.formMembersList(userGroupModel.getMembers());
				for(final PrincipalModel userDetail: users) {
					final UserModel user = (UserModel) userDetail;
					final String returnedMemberId = user.getScimUserId();
					if("newMember".equalsIgnoreCase(returnedMemberId)) {
						memberExists = true;
						Assert.assertTrue(memberExists);
						break;
					}
				}
			}
	 	}
	}

	private ScimGroup createNewGroup() {
		final ScimGroup newGroup = new ScimGroup();
		newGroup.setId(NEW_GROUP_ID);
		return scimGroupFacade.createGroup(newGroup);
	}
}