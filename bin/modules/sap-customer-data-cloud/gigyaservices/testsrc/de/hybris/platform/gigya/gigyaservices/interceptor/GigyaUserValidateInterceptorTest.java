/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.gigya.gigyaservices.interceptor;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.gigya.gigyaservices.login.GigyaLoginService;
import de.hybris.platform.servicelayer.interceptor.InterceptorContext;
import de.hybris.platform.servicelayer.interceptor.InterceptorException;
import de.hybris.platform.servicelayer.user.UserService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class GigyaUserValidateInterceptorTest
{

	@InjectMocks
	private final GigyaUserValidateInterceptor interceptor = new GigyaUserValidateInterceptor() {
		
		protected String getGigyaMandatoryAttributes() {
			return "name";
		}
	};

	@Mock
	private CustomerModel gigyaUser;

	@Mock
	private InterceptorContext interceptorContext;

	@Mock
	private UserService userService;

	@Mock
	private GigyaLoginService gigyaLoginService;

	@Mock
	private EmployeeModel employee;


	/**
	 * | gigyaUidExists - true | gigyaUserNew - false | gigyaUserModified - true | modifyingUser - user |
	 * mandatoryAttributesModified - false |
	 */
	@Test
	public void testOnValidateWhenGigyaUidExistsForANewGigyaUser() throws InterceptorException
	{
		Mockito.lenient().when(gigyaUser.getGyUID()).thenReturn("gigya-uid");

		Mockito.lenient().when(interceptorContext.isNew(gigyaUser)).thenReturn(Boolean.TRUE);

		interceptor.onValidate(gigyaUser, interceptorContext);

		Mockito.verifyZeroInteractions(gigyaLoginService);
	}

	/**
	 * | gigyaUidExists - true | gigyaUserNew - false | gigyaUserModified - true | modifyingUser - user |
	 * mandatoryAttributesModified - false |
	 */
	@Test
	public void testOnValidateWhenGigyaUserUpdatesHisProfile() throws InterceptorException
	{
		Mockito.lenient().when(gigyaUser.getGyUID()).thenReturn("gigya-uid");
		Mockito.lenient().when(interceptorContext.isNew(gigyaUser)).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(interceptorContext.isModified(gigyaUser)).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(gigyaUser);

		interceptor.onValidate(gigyaUser, interceptorContext);

		Mockito.verifyZeroInteractions(gigyaLoginService);
	}

	/**
	 * | gigyaUidExists - true | gigyaUserNew - false | gigyaUserModified - true | modifyingUser - employee |
	 * mandatoryAttributesModified - false |
	 */
	@Test
	public void testOnValidateWhenBackofficeUserUpdatesGigyaUsersProfileWithNonMandatoryAttributes() throws InterceptorException
	{
		Mockito.lenient().when(gigyaUser.getGyUID()).thenReturn("gigya-uid");
		Mockito.lenient().when(interceptorContext.isNew(gigyaUser)).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(interceptorContext.isModified(gigyaUser)).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(employee);

		interceptor.onValidate(gigyaUser, interceptorContext);

		Mockito.verifyZeroInteractions(gigyaLoginService);
	}

	/**
	 * | gigyaUidExists - true | gigyaUserNew - false | gigyaUserModified - true | modifyingUser - employee |
	 * mandatoryAttributesModified - true |
	 */
	@Test
	public void testOnValidateWhenBackofficeUserUpdatesGigyaUsersProfileWithMandatoryAttributes() throws InterceptorException
	{
		Mockito.lenient().when(gigyaUser.getGyUID()).thenReturn("gigya-uid");
		Mockito.lenient().when(interceptorContext.isNew(gigyaUser)).thenReturn(Boolean.FALSE);
		Mockito.lenient().when(interceptorContext.isModified(gigyaUser)).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(userService.getCurrentUser()).thenReturn(employee);
		Mockito.lenient().when(interceptorContext.isModified(gigyaUser, "name")).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(gigyaLoginService.sendUserToGigya(gigyaUser)).thenReturn(Boolean.TRUE);
		Mockito.lenient().when(gigyaUser.getGyApiKey()).thenReturn("gigya-api-key");

		interceptor.onValidate(gigyaUser, interceptorContext);

		Mockito.verify(gigyaLoginService).sendUserToGigya(gigyaUser);
	}

}
