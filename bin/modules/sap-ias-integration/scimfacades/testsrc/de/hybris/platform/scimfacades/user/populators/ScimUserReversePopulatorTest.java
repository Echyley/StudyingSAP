/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.scimfacades.user.populators;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.scimfacades.ScimUser;
import de.hybris.platform.scimfacades.ScimUserEmail;
import de.hybris.platform.scimfacades.ScimUserGroup;
import de.hybris.platform.scimfacades.ScimUserName;
import de.hybris.platform.scimservices.model.ScimUserGroupModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.internal.dao.GenericDao;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class ScimUserReversePopulatorTest
{

	@InjectMocks
	private final ScimUserReversePopulator populator = new ScimUserReversePopulator();

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private GenericDao<ScimUserGroupModel> scimUserGroupGenericDao;

	@Mock
	private CurrencyModel currency;

	@Mock
	private LanguageModel language;

	@Mock
	private UserGroupModel userGroup;


	@Mock
	private ScimUserGroupModel scimUserGroup;

	@Test
	public void testPopulateWithGroups()
	{
		ScimUser scimUser = new ScimUser();
		EmployeeModel employee = new EmployeeModel();

		scimUser.setId("id");
		final ScimUserEmail email = new ScimUserEmail();
		email.setPrimary(true);
		email.setValue("sample@email.com");
		scimUser.setEmails(Collections.singletonList(email));

		final ScimUserName name = new ScimUserName();
		name.setFamilyName("familyName");
		scimUser.setName(name);

		scimUser.setActive(Boolean.TRUE);

		Mockito.when(commonI18NService.getCurrentCurrency()).thenReturn(currency);
		Mockito.when(commonI18NService.getCurrentLanguage()).thenReturn(language);

		final ScimUserGroup group = new ScimUserGroup();
		group.setValue("group-value");
		scimUser.setGroups(Collections.singletonList(group));

		Mockito.when(scimUserGroupGenericDao.find(Mockito.anyMap())).thenReturn(Collections.singletonList(scimUserGroup));
		Mockito.when(scimUserGroup.getUserGroups()).thenReturn(Collections.singletonList(userGroup));

		populator.populate(scimUser, employee);

		Assert.assertEquals("id", employee.getScimUserId());
		Assert.assertEquals("sample@email.com", employee.getUid());
		Assert.assertEquals("familyName", employee.getName());
		Assert.assertEquals(currency, employee.getSessionCurrency());
		Assert.assertEquals(language, employee.getSessionLanguage());
		Assert.assertTrue(employee.getGroups().contains(userGroup));
	}

}
