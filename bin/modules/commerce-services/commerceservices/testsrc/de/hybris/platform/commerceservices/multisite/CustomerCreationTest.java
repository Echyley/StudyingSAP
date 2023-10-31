/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.commerceservices.multisite;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.EmployeeModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@IntegrationTest
public class CustomerCreationTest extends ServicelayerTransactionalTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerCreationTest.class);

	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;
	@Resource
	private BaseSiteService baseSiteService;
	@Resource
	private MultiSiteUidDecorationService multiSiteUidDecorationService;

	@Test(expected = ModelSavingException.class)
	public void testCreateCustomerWithUnDataIsolatedSiteWillFailed()
	{
		//given
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);
		baseSiteModel.setUid("testBaseSiteUID");
		baseSiteModel.setDataIsolationEnabled(false);
		modelService.save(baseSiteModel);

		final CustomerModel customer1 = modelService.create(CustomerModel.class);
		customer1.setUid("testCustomerUnDataIsolatedSite");
		customer1.setSite(baseSiteModel);
		//when
		modelService.save(customer1);
		//then
		fail("Can not create a Customer attached to an unDataIsolated site.");
	}

	@Test(expected = ModelSavingException.class)
	public void testCreateUserGroupWithSameUIDWillFailed()
	{
		final UserGroupModel group1 = modelService.create(UserGroupModel.class);
		final UserGroupModel group2 = modelService.create(UserGroupModel.class);

		group1.setUid("testSameGroupUID");
		group2.setUid("testSameGroupUID");

		modelService.save(group1);
		modelService.save(group2);

		fail("UserGroup with same UID and null site will not be saved.");
	}

	@Test(expected = ModelSavingException.class)
	public void testCreateEmployeeWithSameUIDWillFailed()
	{
		final EmployeeModel employee1 = modelService.create(EmployeeModel.class);
		final EmployeeModel employee2 = modelService.create(EmployeeModel.class);

		employee1.setUid("testSameEmployeeUID");
		employee2.setUid("testSameEmployeeUID");

		modelService.save(employee1);
		modelService.save(employee2);

		fail("Employee with same UID and null site will not be saved.");
	}

	@Test(expected = ModelSavingException.class)
	public void testCreateCustomerWithSameUIDNullSiteWillFailed()
	{
		final CustomerModel customer1 = modelService.create(CustomerModel.class);
		final CustomerModel customer2 = modelService.create(CustomerModel.class);

		customer1.setUid("testSameCustomerUIDNullSite");
		customer1.setSite(null);
		customer2.setUid("testSameCustomerUIDNullSite");
		customer2.setSite(null);

		modelService.save(customer1);
		modelService.save(customer2);

		fail("Customer with same UID and null site will not be saved.");
	}

	@Test
	public void testCreateCustomerWithDiffUIDNullSiteWillSuccess()
	{
		final String customerUID1 = "test1@sap.com";
		final String customerUID2 = "test2@sap.com";

		setCurrentBaseSite(null, false);

		final CustomerModel customer1 = modelService.create(CustomerModel.class);
		final CustomerModel customer2 = modelService.create(CustomerModel.class);

		customer1.setUid(customerUID1);
		customer2.setUid(customerUID2);

		modelService.save(customer1);
		modelService.save(customer2);

		final UserModel user1 = userService.getUserForUID(customerUID1);
		assertTrue(user1 instanceof CustomerModel);
		assertEquals(customerUID1, user1.getUid());
		final UserModel user2 = userService.getUserForUID(customerUID2);
		assertTrue(user2 instanceof CustomerModel);
		assertEquals(customerUID2, user2.getUid());
	}

	@Test(expected = ModelSavingException.class)
	public void testCreateCustomerWithSameUIDAndSiteWillFailed()
	{
		setCurrentBaseSite("testBaseSiteUID", true);

		final CustomerModel customer1 = modelService.create(CustomerModel.class);
		final CustomerModel customer2 = modelService.create(CustomerModel.class);

		customer1.setUid("testSameCustomerUIDNullSite");
		customer2.setUid("testSameCustomerUIDNullSite");

		modelService.save(customer1);
		modelService.save(customer2);

		fail("Customer with same UID and null site will not be saved.");
	}

	@Test
	public void testCreateCustomerWithDiffUIDSiteWillSuccess()
	{
		final String customerUID1 = "test1@sap.com";
		final String customerUID2 = "test2@sap.com";
		final String customerUID3 = "test1@sap.com";
		final String customerUID4 = "test1@sap.com";
		final String baseSiteUID1 = "testBaseSiteUID1";
		final String baseSiteUID2 = "testBaseSiteUID2";

		setCurrentBaseSite(baseSiteUID1, true);
		final CustomerModel customer1 = modelService.create(CustomerModel.class);
		final CustomerModel customer2 = modelService.create(CustomerModel.class);

		customer1.setUid(customerUID1);
		customer2.setUid(customerUID2);

		modelService.save(customer1);
		modelService.save(customer2);

		// test different UID and same site will be saved successfully
		final UserModel user1 = userService.getUserForUID(customerUID1);
		assertTrue(user1 instanceof CustomerModel);
		assertEquals(multiSiteUidDecorationService.decorate(customerUID1, baseSiteUID1), user1.getUid());
		assertNotNull(((CustomerModel) user1).getSite());
		assertEquals(baseSiteUID1, ((CustomerModel) user1).getSite().getUid());

		final UserModel user2 = userService.getUserForUID(customerUID2);
		assertTrue(user2 instanceof CustomerModel);
		assertEquals(multiSiteUidDecorationService.decorate(customerUID2, baseSiteUID1), user2.getUid());
		assertNotNull(((CustomerModel) user2).getSite());
		assertEquals(baseSiteUID1, ((CustomerModel) user2).getSite().getUid());

		// test same UID and different site will be saved successfully
		setCurrentBaseSite(baseSiteUID2, true);
		final CustomerModel customer3 = modelService.create(CustomerModel.class);

		customer3.setUid(customerUID3);
		modelService.save(customer3);

		final UserModel user3 = userService.getUserForUID(customerUID3);
		assertTrue(user3 instanceof CustomerModel);
		assertEquals(multiSiteUidDecorationService.decorate(customerUID3, baseSiteUID2), user3.getUid());
		assertNotNull(((CustomerModel) user3).getSite());
		assertEquals(baseSiteUID2, ((CustomerModel) user3).getSite().getUid());

		// test same UID and null site will be saved successfully
		setCurrentBaseSite(null, true);
		final CustomerModel customer4 = modelService.create(CustomerModel.class);

		customer4.setUid(customerUID4);
		modelService.save(customer4);

		final UserModel user4 = userService.getUserForUID(customerUID4);
		assertTrue(user4 instanceof CustomerModel);
		assertEquals(customerUID4, user4.getUid());
		assertNull(((CustomerModel) user4).getSite());
	}

	@Test
	public void testCustomerUidSensitive()
	{
		verifyCustomerUidSensitive("test@sap.com|basesiteid", "test@sap.com", "test@sap.com", "test@sap.com", "basesiteid");
		verifyCustomerUidSensitive("test@sap.com|baseSiteId", "tEst@saP.com", "test@sap.com", "tEst@saP.com", "baseSiteId");
		verifyCustomerUidSensitive("test@sap.com|basesiteid|baseSiteId", "tEst@saP.com|basesiteId", "test@sap.com|basesiteid",
				"tEst@saP.com|basesiteId", "baseSiteId");
		verifyCustomerUidSensitive("test@sap.com|baseSiteId", "tEst@saP.com", "test@sap.com", "tEst@saP.com|baseSiteId",
				"baseSiteId");

		verifyCustomerUidSensitive("test@sap.com", "tEst@saP.com", "test@sap.com", "tEst@saP.com", null);
		verifyCustomerUidSensitive("test@sap.com|basesiteid", "tEst@saP.com|baseSiteId", "test@sap.com|basesiteid",
				"tEst@saP.com|baseSiteId", null);
	}

	@Test
	public void testUndecoratedUidDerivedFromUIDWhenCustomerCreation() {
		// given
		final BaseSiteModel baseSiteModel = modelService.create(BaseSiteModel.class);
		baseSiteModel.setUid("testBaseSiteUID");
		baseSiteModel.setDataIsolationEnabled(true);
		modelService.save(baseSiteModel);

		final CustomerModel unisolatedCustomer = modelService.create(CustomerModel.class);
		unisolatedCustomer.setUid("unisolatedCustomer@sap.com");
		unisolatedCustomer.setOriginalUid("unisolatedCustomer@sap.com");
		unisolatedCustomer.setUndecoratedUid("unisolatedCustomer@sap.com");

		final CustomerModel isolatedCustomer = modelService.create(CustomerModel.class);
		isolatedCustomer.setUid("isolatedCustomer@sap.com");
		isolatedCustomer.setOriginalUid("isolatedCustomer@sap.com");
		isolatedCustomer.setUndecoratedUid("differentCustomer@sap.com");
		isolatedCustomer.setSite(baseSiteModel);

		// when
		modelService.save(unisolatedCustomer);
		modelService.save(isolatedCustomer);

		// then
		final CustomerModel unisolatedCustomerInDB = (CustomerModel)userService.getUserForUID("unisolatedcustomer@sap.com");
		final CustomerModel isolatedCustomerInDB = (CustomerModel)userService.getUserForUID("isolatedcustomer@sap.com|testBaseSiteUID");

		assertEquals("unisolatedCustomer@sap.com", unisolatedCustomerInDB.getOriginalUid());
		assertEquals("unisolatedcustomer@sap.com", unisolatedCustomerInDB.getUndecoratedUid());
		assertEquals("isolatedCustomer@sap.com", isolatedCustomerInDB.getOriginalUid());
		assertEquals("isolatedcustomer@sap.com", isolatedCustomerInDB.getUndecoratedUid());
	}

	private void verifyCustomerUidSensitive(final String expectedUid, final String expectedOriginalUid,
			final String expectedUndecoratedUid, final String plainCustomerUid, final String baseSiteUid)
	{
		// when
		final CustomerModel customerModel = modelService.create(CustomerModel.class);
		customerModel.setUid(plainCustomerUid);

		final boolean createNewBaseSite;
		final BaseSiteModel baseSiteModel;
		if (baseSiteUid != null)
		{
			baseSiteModel = modelService.create(BaseSiteModel.class);
			baseSiteModel.setUid(baseSiteUid);
			baseSiteModel.setDataIsolationEnabled(true);

			modelService.save(baseSiteModel);

			customerModel.setSite(baseSiteModel);

			createNewBaseSite = true;
		}
		else
		{
			createNewBaseSite = false;
			baseSiteModel = null;
		}

		// then
		modelService.save(customerModel);

		// expect
		final UserModel user = userService.getUserForUID(expectedUid);
		assertTrue(user instanceof CustomerModel);
		assertEquals(expectedOriginalUid, ((CustomerModel) user).getOriginalUid());
		assertEquals(expectedUndecoratedUid, ((CustomerModel) user).getUndecoratedUid());
		if (createNewBaseSite)
		{
			assertNotNull(((CustomerModel) user).getSite());
		}
		else
		{
			assertNull(((CustomerModel) user).getSite());
		}

		// rollback
		modelService.remove(user);
		if (createNewBaseSite)
		{
			modelService.remove(baseSiteModel);
		}
	}

	protected void setCurrentBaseSite(final String uid, final boolean dataIsolationEnabled)
	{
		if (uid == null)
		{
			baseSiteService.setCurrentBaseSite((BaseSiteModel) null, false);
		}
		else
		{
			final BaseSiteModel curBaseSite = modelService.create(BaseSiteModel.class);
			curBaseSite.setUid(uid);
			curBaseSite.setDataIsolationEnabled(dataIsolationEnabled);

			modelService.save(curBaseSite);

			baseSiteService.setCurrentBaseSite(curBaseSite, false);
		}
	}
}
