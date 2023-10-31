/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.dao.impl;

import static org.junit.Assert.assertTrue;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.dao.B2BRegistrationDao;
import de.hybris.platform.b2b.util.B2BRegistrationTestsUtil;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BRegistrationDaoIntegrationTest extends ServicelayerTest
{
	@Resource
	private B2BRegistrationDao b2bRegistrationDao;

	private String impexContent;
	private List<String> userGroups;

	private static final String PATH = "/b2bcommerce/test/";
	private static final String IMPEX_TEST_FILE = "testDefaultB2BRegistrationDaoIntegrationTest.impex";

	private static final int UID_INDEX = 1;
	private static final int USER_GROUP_INDEX = 3;
	private static final String INEXISTANT_USER_GROUP = "inexistantUserGroup";


	@Before
	public void setUp() throws Exception
	{
		importCsv(PATH + IMPEX_TEST_FILE, "UTF-8");
		impexContent = B2BRegistrationTestsUtil.impexFileToString(PATH + IMPEX_TEST_FILE);

		userGroups = B2BRegistrationTestsUtil.getUserGroupsFromImpex(impexContent, UID_INDEX);
	}


	/*
	 * Make sure no user is associated to userGroup INEXISTANT_USER_GROUP in file IMPEX_TEST_FILE.
	 */
	@Test
	public void testGetEmployeesInUserGroup()
	{

		List<EmployeeModel> employees;
		List<String> uidsFromDao;
		List<String> uidsFromImpex;

		for (final String userGroup : userGroups)
		{
			uidsFromImpex = B2BRegistrationTestsUtil.getEmployeesUidsFromImpex(impexContent, userGroup, UID_INDEX, USER_GROUP_INDEX);

			employees = b2bRegistrationDao.getEmployeesInUserGroup(userGroup);
			uidsFromDao = B2BRegistrationTestsUtil.employeesToUids(employees);

			assertTrue("Dao should return same employees as those in impex file",
					B2BRegistrationTestsUtil.compareLists(uidsFromImpex, uidsFromDao));
		}

		//Testing inexistant userGroup		
		employees = b2bRegistrationDao.getEmployeesInUserGroup(INEXISTANT_USER_GROUP);
		assertTrue("Dao should not return any employee for userGroup " + INEXISTANT_USER_GROUP, employees.isEmpty());
	}

}
