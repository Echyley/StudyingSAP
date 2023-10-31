/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.company.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.security.PrincipalModel;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@UnitTest
public class DefaultB2BGroupCycleValidatorTest
{

	private DefaultB2BGroupCycleValidator b2bGroupCycleValidator;

	@Before
	public void setup()
	{
		b2bGroupCycleValidator = new DefaultB2BGroupCycleValidator();
	}

	@Test
	public void shouldValidateGroupsCycleNotDetected()
	{
		final PrincipalGroupModel group = mock(PrincipalGroupModel.class);
		final PrincipalGroupModel groupMember = mock(PrincipalGroupModel.class);

		Set<PrincipalGroupModel> groups = new HashSet<>();
		groups.add(mock(PrincipalGroupModel.class));
		when(group.getGroups()).thenReturn(groups);

		Set<PrincipalGroupModel> groups2 = new HashSet<>();
		groups2.add(mock(PrincipalGroupModel.class));
		when(groupMember.getGroups()).thenReturn(groups2);

		Assert.assertTrue(b2bGroupCycleValidator.validateGroups(group, groupMember));
	}

	@Test
	public void shouldValidateGroupsCycleDetected()
	{
		final PrincipalGroupModel group = mock(PrincipalGroupModel.class);
		final PrincipalGroupModel groupMember = mock(PrincipalGroupModel.class);

		Set<PrincipalGroupModel> groups = new HashSet<>();
		groups.add(groupMember);
		when(group.getGroups()).thenReturn(groups);

		Set<PrincipalGroupModel> groups2 = new HashSet<>();
		groups2.add(group);
		when(groupMember.getGroups()).thenReturn(groups2);

		Assert.assertFalse(b2bGroupCycleValidator.validateGroups(group, groupMember));
	}

	@Test
	public void shouldValidateMembersCycleNotDetected()
	{
		final PrincipalGroupModel group = mock(PrincipalGroupModel.class);
		PrincipalModel member = mock(PrincipalModel.class);

		Set<PrincipalGroupModel> groups = new HashSet<>();
		groups.add(mock(PrincipalGroupModel.class));
		when(group.getGroups()).thenReturn(groups);

		Set<PrincipalModel> members = new HashSet<>();
		members.add(mock(PrincipalModel.class));
		when(group.getMembers()).thenReturn(members);

		Assert.assertTrue(b2bGroupCycleValidator.validateMembers(group, member));
	}

	@Test
	public void shouldValidateMembersCycleDetected()
	{
		final PrincipalGroupModel group = mock(PrincipalGroupModel.class);
		PrincipalGroupModel member = mock(PrincipalGroupModel.class);

		Set<PrincipalGroupModel> groups = new HashSet<>();
		groups.add(member);
		when(group.getGroups()).thenReturn(groups);

		Set<PrincipalModel> members = new HashSet<>();
		members.add(member);
		when(group.getMembers()).thenReturn(members);

		Assert.assertFalse(b2bGroupCycleValidator.validateMembers(group, member));
	}
}
