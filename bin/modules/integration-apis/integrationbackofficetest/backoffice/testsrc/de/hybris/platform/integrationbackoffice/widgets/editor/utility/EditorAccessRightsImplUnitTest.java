/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.editor.utility;


import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.lenient;


import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.integrationbackoffice.widgets.common.utility.EditorAccessRightsImpl;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class EditorAccessRightsImplUnitTest
{
	@InjectMocks
	private EditorAccessRightsImpl editorAccessRights;

	@Mock(lenient = true)
	private UserService userService;
	@Mock(lenient = true)
	private UserModel userModel;
	@Mock(lenient = true)
	private PrincipalGroupModel role1;
	@Mock(lenient = true)
	private PrincipalGroupModel role2;

	private List<String> adminRoles;
	private Set<PrincipalGroupModel> groups;

	@Before
	public void setup()
	{

		adminRoles = new ArrayList<>();
		editorAccessRights.setUserService(userService);

		groups = new HashSet<>();
		groups.add(role1);
		groups.add(role2);

		lenient().when(userService.getCurrentUser()).thenReturn(userModel);
		lenient().when(userModel.getGroups()).thenReturn(groups);
		lenient().when(role1.getUid()).thenReturn("Test1");
		lenient().when(role2.getUid()).thenReturn("Test2");

	}

	@Test
	public void isUserAdminTestTrue()
	{
		adminRoles.add("Test1");
		adminRoles.add("Test555");

		editorAccessRights.setAdminRoles(adminRoles);

		assertTrue(editorAccessRights.isUserAdmin());
	}

	@Test
	public void isUserAdminTestFalse()
	{
		adminRoles.add("Test99999");
		adminRoles.add("Test");

		editorAccessRights.setAdminRoles(adminRoles);

		assertFalse(editorAccessRights.isUserAdmin());
	}

	@Test
	public void isUserAdminTestNull()
	{
		editorAccessRights.setAdminRoles(null);

		assertFalse(editorAccessRights.isUserAdmin());
	}

	@Test
	public void isUserAdminTestEmptyAdmin()
	{
		editorAccessRights.setAdminRoles(adminRoles);

		assertFalse(editorAccessRights.isUserAdmin());
	}

	@Test
	public void isUserAdminTestEmptyCurrent()
	{
		adminRoles.add("Test1");
		adminRoles.add("Test555");
		Set<PrincipalGroupModel> emptyGroups = new HashSet<>();
		lenient().when(userModel.getGroups()).thenReturn(emptyGroups);

		assertFalse(editorAccessRights.isUserAdmin());
	}

	@Test
	public void setCurrentListboxAttributesHandlesNull()
	{
		editorAccessRights.setAdminRoles(null);
		assertEquals("Set an empty list if it's set null", Collections.emptyList(), editorAccessRights.getAdminRoles());
	}

	@Test
	public void setAdminRolesDoesNotStoreReference()
	{
		adminRoles.addAll(List.of("test1", "test2"));
		editorAccessRights.setAdminRoles(adminRoles);
		adminRoles.add("notMatters");
		assertNotEquals("Two AdminRoles contain different elements.", adminRoles, editorAccessRights.getAdminRoles());
		assertNotSame("The references are different.", adminRoles, editorAccessRights.getAdminRoles());
	}

	@Test
	public void getAdminRolesDoesNotLeakReference()
	{
		editorAccessRights.setAdminRoles(List.of("notMatters"));
		assertEquals(1, editorAccessRights.getAdminRoles().size());

		// Getter won't leak reference. It returns an unmodifiable list.
		List<String> rolesList = editorAccessRights.getAdminRoles();
		assertThatThrownBy(() -> rolesList.add("notMatters"))
				.isInstanceOf(UnsupportedOperationException.class);
	}
}
