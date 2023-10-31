/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.group.populators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.security.PrincipalModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimGroup;
import de.hybris.platform.scimfacades.ScimGroupMember;
import de.hybris.platform.scimfacades.constants.ScimfacadesConstants;
import de.hybris.platform.scimfacades.utils.ScimGroupUtils;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ScimGroupPopulatorTest {

	@InjectMocks
	private final ScimGroupPopulator populator = new ScimGroupPopulator();

	@Mock
	private ScimUserGroupModel scimUserGroup;

	@Mock
	private UserModel user;


	private ScimGroup scimGroup;

	@Mock
	private UserGroupModel userGroup;

	@Test
	public void testPopulateWithMembers()
	{
		scimGroup = new ScimGroup();
		scimUserGroup = new ScimUserGroupModel();
		scimUserGroup.setScimUserGroup("external-id");
		scimUserGroup.setUserGroups(Collections.singletonList(userGroup));
		
		Mockito.lenient().when(user.getScimUserId()).thenReturn("test-user");
		Mockito.lenient().when(user.getDisplayName()).thenReturn("test-name");

		populator.populate(scimUserGroup, scimGroup);
		final ScimGroupMember newMember = new ScimGroupMember();
		final List<ScimGroupMember> scimGroupMembers = new ArrayList<> ();
		final Set<PrincipalModel> users = ScimGroupUtils.formMembersList(userGroup.getMembers());
		for(final PrincipalModel userDetail: users) {
			final UserModel user1 = (UserModel) userDetail;

			newMember.setValue(user1.getScimUserId());
			newMember.setDisplay(user1.getDisplayName());
			if(!checkIfMemberAlreadyExists(scimGroupMembers, newMember)) {
				scimGroupMembers.add(newMember);
			}
		}
		Assert.assertEquals("external-id", scimGroup.getId());
		Assert.assertEquals(scimGroupMembers, scimGroup.getMembers());
 		Assert.assertEquals(ScimfacadesConstants.META_VERSION, scimGroup.getMeta().getVersion());
		Assert.assertEquals(ScimfacadesConstants.GROUP_RESOURCE_TYPE, scimGroup.getMeta().getResourceType());
	}

	@Test
	public void testPopulateWithoutMembers()
	{
		scimGroup = new ScimGroup();
		scimUserGroup = new ScimUserGroupModel();
		scimUserGroup.setScimUserGroup("external-id");

		Mockito.lenient().when(user.getScimUserId()).thenReturn("test-user");
		Mockito.lenient().when(user.getDisplayName()).thenReturn("test-name");
		populator.populate(scimUserGroup, scimGroup);
		Assert.assertEquals("external-id", scimGroup.getId());
 		Assert.assertEquals(ScimfacadesConstants.META_VERSION, scimGroup.getMeta().getVersion());
		Assert.assertEquals(ScimfacadesConstants.GROUP_RESOURCE_TYPE, scimGroup.getMeta().getResourceType());

	}

	private boolean checkIfMemberAlreadyExists(final List<ScimGroupMember> scimGroupMembers, final ScimGroupMember newMember) {
		if(scimGroupMembers.size() > 0) {
			for(final ScimGroupMember scimGroupMember: scimGroupMembers) {
				if(scimGroupMember.getValue().equals(newMember.getValue())) {
					return true;
				}
			}
		}
		return false;
	}
}