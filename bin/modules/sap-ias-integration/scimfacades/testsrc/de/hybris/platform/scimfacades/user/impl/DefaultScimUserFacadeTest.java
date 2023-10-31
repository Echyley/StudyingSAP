/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.user.impl;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimservices.exceptions.ScimException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class DefaultScimUserFacadeTest
{

	private static final String EXTERNAL_ID = "external-id";

	@InjectMocks
	private final DefaultScimUserFacade scimUserFacade = new DefaultScimUserFacade();

	@Mock
	private FlexibleSearchService flexibleSearchService;

	@Mock
	private UserModel userModel;

	@Mock
	private Converter<EmployeeModel, ScimUser> scimUserConverter;

	@Mock
	private EmployeeModel employeeModel;

	@Mock
	private ModelService modelService;

	@Mock
	private Converter<ScimUser, EmployeeModel> scimUserReverseConverter;


	@Mock
	private GenericDao<UserModel> scimUserGenericDao;

	@Test
	public void testCreateUserForUserTypeEmployee()
	{
		final ScimUser scimUser = new ScimUser();
		scimUser.setId(EXTERNAL_ID);
		scimUser.setUserType("employee");

		Mockito.lenient().when(modelService.create(EmployeeModel.class)).thenReturn(employeeModel);
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(employeeModel));
		Mockito.lenient().when(scimUserConverter.convert(employeeModel)).thenReturn(scimUser);

		final ScimUser returnedScimUser = scimUserFacade.createUser(scimUser);

		Mockito.verify(scimUserReverseConverter).convert(scimUser, employeeModel);
		Mockito.verify(modelService).save(employeeModel);
		Assert.assertEquals(scimUser, returnedScimUser);
	}

	@Test(expected = ScimException.class)
	public void testCreateUserForUserTypeInvalid()
	{
		final ScimUser scimUser = new ScimUser();
		scimUser.setId(EXTERNAL_ID);
		scimUser.setUserType("invalid");

		scimUserFacade.createUser(scimUser);
	}

	@Test
	public void testUpdateUserWhenUserExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(employeeModel));

		final ScimUser scimUser = new ScimUser();
		scimUser.setId(EXTERNAL_ID);
		Mockito.lenient().when(scimUserConverter.convert(employeeModel)).thenReturn(scimUser);

		final ScimUser returnedScimUser = scimUserFacade.updateUser(EXTERNAL_ID, scimUser);

		Mockito.verify(scimUserReverseConverter).convert(scimUser, employeeModel);
		Mockito.verify(modelService).save(employeeModel);
		Assert.assertEquals(scimUser, returnedScimUser);
	}

	@Test(expected = ScimException.class)
	public void testUpdateUserWhenUserDoesntExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimUserFacade.updateUser(EXTERNAL_ID, new ScimUser());
	}

	@Test
	public void testGetUserWhenUserExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(employeeModel));

		scimUserFacade.getUser(EXTERNAL_ID);

		Mockito.verify(scimUserConverter).convert(employeeModel);
	}

	@Test(expected = ScimException.class)
	public void testGetUserWhenUserDoesntExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimUserFacade.getUser(EXTERNAL_ID);
	}

	@Test
	public void testGetUserForScimUserIdWhenModelExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(userModel));

		Assert.assertEquals(userModel, scimUserFacade.getUserForScimUserId(EXTERNAL_ID));
	}

	@Test
	public void testGetUserForScimUserIdWhenModelDoesntExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenThrow(new ModelNotFoundException("exception"));

		Assert.assertNull(scimUserFacade.getUserForScimUserId(EXTERNAL_ID));
	}

	@Test
	public void testDeleteUserWhenUserExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(userModel));

		Assert.assertTrue(scimUserFacade.deleteUser(EXTERNAL_ID));
	}

	@Test(expected = ScimException.class)
	public void testDeleteUserWhenUserDoesntExists()
	{
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(null);

		scimUserFacade.deleteUser(EXTERNAL_ID);
	}
	
	@Test
	public void testGetUsers()
	{
		final ScimUser scimUser = new ScimUser();
		scimUser.setId(EXTERNAL_ID);
		scimUser.setUserType("employee");
		Mockito.lenient().when(modelService.create(EmployeeModel.class)).thenReturn(employeeModel);
		Mockito.lenient().when(flexibleSearchService.getModelsByExample(Mockito.any())).thenReturn(Collections.singletonList(employeeModel));
		Mockito.lenient().when(scimUserConverter.convert(employeeModel)).thenReturn(scimUser);
		Mockito.lenient().when(scimUserReverseConverter.convert(scimUser)).thenReturn(employeeModel);
		Mockito.lenient().when(scimUserGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(employeeModel));
		
		scimUserFacade.createUser(scimUser);
		scimUserFacade.getUsers();
		Mockito.verify(scimUserConverter).convert(employeeModel);
		Mockito.verify(scimUserReverseConverter).convert(scimUser, employeeModel);
	}
	
	@Test
	public void testGetUsersForOneUser()
	{
		final ScimUser scimUser = new ScimUser();
		scimUser.setId("sample@email.com");
		scimUser.setUserType("employee");
		
		EmployeeModel employeeModelval =new EmployeeModel();
		employeeModelval.setScimUserId("external-id");
		employeeModelval.setName("firstName lastName");
		employeeModelval.setUid("sample@email.com");
		employeeModelval.setLoginDisabled(Boolean.FALSE);
		
		Mockito.lenient().when(scimUserGenericDao.find()).thenReturn(Collections.singletonList(employeeModelval));

		Mockito.lenient().when(scimUserConverter.convert(employeeModelval)).thenReturn(scimUser);
 		
		List<ScimUser> ScimUserList= scimUserFacade.getUsers();
		System.out.println("ScimUserList: "+ScimUserList.get(0).getId());
		Assert.assertEquals("sample@email.com",ScimUserList.get(0).getId());
	}

}
