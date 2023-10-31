/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.commerceservices.multisite;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@IntegrationTest
public class CustomerUpdateTest extends ServicelayerTransactionalTest
{
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerUpdateTest.class);

	@Resource
	private ModelService modelService;
	@Resource
	private UserService userService;

	private BaseSiteModel dataIsolatedSiteA;
	private CustomerModel unisolatedCustomer;
	private CustomerModel isolatedCustomer;
	private static final String UNISOLATED_CUSTOMER_UID = "unisolatedCustomer@sap.com";
	private static final String ISOLATED_CUSTOMER_UID = "isolatedCustomer@sap.com";
	private static final String SITEA_UID = "dataIsolatedSiteA";

	@Before
	public void setUp()
	{
		dataIsolatedSiteA = modelService.create(BaseSiteModel.class);
		dataIsolatedSiteA.setUid(SITEA_UID);
		dataIsolatedSiteA.setDataIsolationEnabled(true);
		modelService.save(dataIsolatedSiteA);

		unisolatedCustomer = modelService.create(CustomerModel.class);
		unisolatedCustomer.setUid(UNISOLATED_CUSTOMER_UID);
		modelService.save(unisolatedCustomer);

		isolatedCustomer = modelService.create(CustomerModel.class);
		isolatedCustomer.setUid(ISOLATED_CUSTOMER_UID);
		isolatedCustomer.setSite(dataIsolatedSiteA);
		modelService.save(isolatedCustomer);
	}

	@Test
	public void testUpdateCustomerUidWhenAttachedSiteIsNullWillSucceed()
	{
		//given
		unisolatedCustomer.setUid("unisolatedCustomer@sap.com|dataIsolatedSiteA");
		//when
		modelService.save(unisolatedCustomer);
		//then
		final UserModel testUser1 = userService.getUserForUID("unisolatedcustomer@sap.com|dataisolatedsitea");
		assertTrue(testUser1 instanceof CustomerModel);
		assertEquals("unisolatedcustomer@sap.com|dataisolatedsitea", testUser1.getUid());
		assertEquals("unisolatedCustomer@sap.com|dataIsolatedSiteA", ((CustomerModel) testUser1).getOriginalUid());
		assertEquals("unisolatedcustomer@sap.com|dataisolatedsitea", ((CustomerModel) testUser1).getUndecoratedUid());
		assertNull(((CustomerModel) testUser1).getSite());
	}

	@Test(expected = ModelSavingException.class)
	public void testUpdateCustomerUidCauseAttachedSiteInconsistentWillFailedCase1()
	{
		//given
		isolatedCustomer.setUid("isolatedCustomer@sap.com");
		//when
		modelService.save(isolatedCustomer);
		//then
		fail("When update Customer.uid, it must follow the namespacing syntax: <email>|<base site identifier>.");
	}

	@Test(expected = ModelSavingException.class)
	public void testUpdateCustomerUidCauseAttachedSiteInconsistentWillFailedCase2()
	{
		//given
		isolatedCustomer.setUid("isolatedCustomer@sap.com|dataIsolatedSiteB");
		//when
		modelService.save(isolatedCustomer);
		//then
		fail("When update Customer.uid, it must follow the namespacing syntax: <email>|<base site identifier>, and the value of <base site identifier> in Customer.uid must be consistent with the value of Customer.site");
	}

	@Test
	public void testUpdateCustomerEmailWillSucceed()
	{
		final String fuzzyEmail = "Fuzzy@demo.com";
		isolatedCustomer.setOriginalUid(fuzzyEmail);
		isolatedCustomer.setUid(StringUtils.lowerCase(fuzzyEmail));
		modelService.save(isolatedCustomer);

		final String customerSaveUid = StringUtils.lowerCase(fuzzyEmail) + '|' + SITEA_UID;
		final CustomerModel c2InDB = (CustomerModel) userService.getUserForUID(customerSaveUid);
		assertEquals(customerSaveUid, c2InDB.getUid());
		assertEquals(fuzzyEmail, c2InDB.getOriginalUid());
		assertEquals(StringUtils.lowerCase(fuzzyEmail), c2InDB.getUndecoratedUid());
	}

	@Test
	public void testUpdateCustomerUidKeepAttachedSiteConsistentWillSucceed()
	{
		//given
		isolatedCustomer.setUid("isolatedCustomer@sap.com|testContext|dataIsolatedSiteA");
		//when
		modelService.save(isolatedCustomer);
		//then
		final CustomerModel cInDB = (CustomerModel) userService.getUserForUID(
				"isolatedcustomer@sap.com|testcontext|dataIsolatedSiteA");
		assertEquals("isolatedcustomer@sap.com|testcontext|dataIsolatedSiteA", cInDB.getUid());
		assertEquals("isolatedCustomer@sap.com|testContext", cInDB.getOriginalUid());
		assertEquals("isolatedcustomer@sap.com|testcontext", cInDB.getUndecoratedUid());
		assertNotNull(cInDB.getSite());
		assertEquals(SITEA_UID, cInDB.getSite().getUid());
	}

	@Test
	public void testUpdateCustomerUidCaseShouldUpdateOriginalUid() {
		//given
		isolatedCustomer.setUid("isolatedCUSTOMER@SAP.com|dataIsolatedSiteA");
		//when
		modelService.save(isolatedCustomer);
		//then
		final CustomerModel cInDB = (CustomerModel) userService.getUserForUID(
				"isolatedcustomer@sap.com|dataIsolatedSiteA");
		assertEquals("isolatedcustomer@sap.com|dataIsolatedSiteA", cInDB.getUid());
		assertEquals("isolatedCUSTOMER@SAP.com", cInDB.getOriginalUid());
		assertEquals("isolatedcustomer@sap.com", cInDB.getUndecoratedUid());
		assertNotNull(cInDB.getSite());
		assertEquals(SITEA_UID, cInDB.getSite().getUid());
	}

	@Test
	public void testUpdateCustomerOriginalUidKeepOtherAttributeConsistent()
	{
		//given
		unisolatedCustomer.setUid("");
		unisolatedCustomer.setOriginalUid("newUpdated@sap.com");
		isolatedCustomer.setUid("");
		isolatedCustomer.setOriginalUid("newUpdated@sap.com");
		// when
		modelService.save(unisolatedCustomer);
		modelService.save(isolatedCustomer);
		// then
		final CustomerModel unIsolatedCustomerInDB = (CustomerModel) userService.getUserForUID("newupdated@sap.com");
		assertEquals("newupdated@sap.com", unIsolatedCustomerInDB.getUid());
		assertEquals("newUpdated@sap.com", unIsolatedCustomerInDB.getOriginalUid());
		assertEquals("newupdated@sap.com", unIsolatedCustomerInDB.getUndecoratedUid());
		assertNull(unIsolatedCustomerInDB.getSite());

		final CustomerModel isolatedCustomerInDB = (CustomerModel) userService.getUserForUID(
				"newupdated@sap.com|dataIsolatedSiteA");
		assertEquals("newupdated@sap.com|dataIsolatedSiteA", isolatedCustomerInDB.getUid());
		assertEquals("newUpdated@sap.com", isolatedCustomerInDB.getOriginalUid());
		assertEquals("newupdated@sap.com", isolatedCustomerInDB.getUndecoratedUid());
		assertNotNull(isolatedCustomerInDB.getSite());
		assertEquals(SITEA_UID, isolatedCustomerInDB.getSite().getUid());
	}

	@Test
	public void testUpdateCustomerOriginalUidWithBaseSiteKeepOtherAttributeConsistent()
	{
		//given
		isolatedCustomer.setUid("");
		isolatedCustomer.setOriginalUid("newUpdated@sap.com|dataIsolatedSiteA");
		// when
		modelService.save(isolatedCustomer);

		// then
		final CustomerModel cInDB = (CustomerModel) userService.getUserForUID("newupdated@sap.com|dataIsolatedSiteA");
		assertEquals("newupdated@sap.com|dataIsolatedSiteA", cInDB.getUid());
		assertEquals("newUpdated@sap.com", cInDB.getOriginalUid());
		assertEquals("newupdated@sap.com", cInDB.getUndecoratedUid());
		assertNotNull(cInDB.getSite());
		assertEquals(SITEA_UID, cInDB.getSite().getUid());
	}

	@Test
	public void testUpdateCustomerOrigialUidToUpperCase()
	{
		// given
		unisolatedCustomer.setUid(UNISOLATED_CUSTOMER_UID.toUpperCase());
		unisolatedCustomer.setOriginalUid(UNISOLATED_CUSTOMER_UID.toUpperCase());
		isolatedCustomer.setUid(ISOLATED_CUSTOMER_UID.toUpperCase());
		isolatedCustomer.setOriginalUid(ISOLATED_CUSTOMER_UID.toUpperCase());
		// when
		modelService.save(unisolatedCustomer);
		modelService.save(isolatedCustomer);

		// then
		final CustomerModel unisolatedCustomerInDB = (CustomerModel) userService.getUserForUID("unisolatedcustomer@sap.com");
		assertEquals("unisolatedcustomer@sap.com", unisolatedCustomerInDB.getUid());
		assertEquals("UNISOLATEDCUSTOMER@SAP.COM", unisolatedCustomerInDB.getOriginalUid());
		assertEquals("unisolatedcustomer@sap.com", unisolatedCustomerInDB.getUndecoratedUid());
		assertNull(unisolatedCustomerInDB.getSite());
		final CustomerModel isolatedCustomerInDB = (CustomerModel) userService.getUserForUID(
				"isolatedcustomer@sap.com|dataIsolatedSiteA");
		assertEquals("isolatedcustomer@sap.com|dataIsolatedSiteA", isolatedCustomerInDB.getUid());
		assertEquals("ISOLATEDCUSTOMER@SAP.COM", isolatedCustomerInDB.getOriginalUid());
		assertEquals("isolatedcustomer@sap.com", isolatedCustomerInDB.getUndecoratedUid());
		assertNotNull(isolatedCustomerInDB.getSite());
		assertEquals(SITEA_UID, isolatedCustomerInDB.getSite().getUid());
	}

	@Test
	public void testDifferentUndecoratedUidWillBeIgnored()
	{
		// when
		isolatedCustomer.setUndecoratedUid("newUpdated@sap.com");
		unisolatedCustomer.setUndecoratedUid("newUpdated@sap.com");

		// given
		modelService.save(isolatedCustomer);
		modelService.save(unisolatedCustomer);

		// then
		final CustomerModel unCInDB = (CustomerModel) userService.getUserForUID("unisolatedcustomer@sap.com");
		assertEquals(StringUtils.lowerCase(UNISOLATED_CUSTOMER_UID), unCInDB.getUid());
		assertEquals(UNISOLATED_CUSTOMER_UID, unCInDB.getOriginalUid());
		assertEquals(StringUtils.lowerCase(UNISOLATED_CUSTOMER_UID), unCInDB.getUndecoratedUid());

		final CustomerModel isoCInDB = (CustomerModel) userService.getUserForUID("isolatedcustomer@sap.com|dataIsolatedSiteA");
		assertEquals(ISOLATED_CUSTOMER_UID, isoCInDB.getOriginalUid());
		assertEquals(StringUtils.lowerCase(ISOLATED_CUSTOMER_UID), isoCInDB.getUndecoratedUid());
		assertNotNull(isoCInDB.getSite());
		assertEquals(SITEA_UID, isoCInDB.getSite().getUid());
	}
}
