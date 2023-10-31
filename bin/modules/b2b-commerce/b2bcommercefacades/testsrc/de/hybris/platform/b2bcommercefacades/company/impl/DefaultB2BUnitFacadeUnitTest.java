/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.company.impl;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.exceptions.ClassMismatchException;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultB2BUnitFacadeUnitTest
{
	@InjectMocks
	private DefaultB2BUnitFacade b2bUnitFacade;

	@Mock
	private UserService userService;

	@Test
	public void testGetParentUnitNodeAsB2BAdminThrowsException()
	{
		when(userService.getCurrentUser()).thenReturn(mock(UserModel.class));

		assertThatThrownBy( () -> b2bUnitFacade.getParentUnitNode() )
			.isInstanceOf(ClassMismatchException.class);
	}
}
