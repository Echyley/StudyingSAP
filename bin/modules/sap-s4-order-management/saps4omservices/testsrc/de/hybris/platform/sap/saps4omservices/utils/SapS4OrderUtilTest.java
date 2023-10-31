/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.sap.saps4omservices.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.sap.core.configuration.model.SAPConfigurationModel;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class SapS4OrderUtilTest {

	@Spy
	@InjectMocks
	SapS4OrderUtil s4OrderUtil;

	@Mock
	UserService userService;

	@Mock
	B2BUnitService<B2BUnitModel, B2BCustomerModel> b2bUnitService;

	@Mock
	BaseStoreService baseStoreService;

	@Before
	public void init() {

		s4OrderUtil.setUserService(userService);
		s4OrderUtil.setB2bUnitService(b2bUnitService);
		s4OrderUtil.setBaseStoreService(baseStoreService);

	}

	@Test
	public void testGetSoldToParty() {

		B2BCustomerModel b2bCustomer = spy(B2BCustomerModel.class);
		B2BUnitModel parent = spy(B2BUnitModel.class);
		parent.setUid("1234");

		when(b2bUnitService.getParent(b2bCustomer)).thenReturn(parent);

		assertEquals(parent.getUid(),s4OrderUtil.getSoldToParty(b2bCustomer));
		
		

	}

	@Test
	public void testGetSoldToPartyWithSalesOrgInParent() {

		B2BCustomerModel b2bCustomer = spy(B2BCustomerModel.class);
		B2BUnitModel parent = spy(B2BUnitModel.class);
		parent.setUid("1234_100_10");
		String b2bUnitId = "1234";

		when(b2bUnitService.getParent(b2bCustomer)).thenReturn(parent);

		assertEquals(b2bUnitId,s4OrderUtil.getSoldToParty(b2bCustomer));
		
		assert true;
	}

	@Test
	public void testGetSoldToPartyForB2C() {

		CustomerModel customer = spy(CustomerModel.class);
		SAPConfigurationModel sapConfig = spy(SAPConfigurationModel.class);
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		sapConfig.setSapcommon_referenceCustomer("1234");

		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfig);

		assertEquals(sapConfig.getSapcommon_referenceCustomer(),s4OrderUtil.getSoldToParty(customer));

	}

	@Test
	public void testGetSoldToPartyForNull() {

		B2BCustomerModel b2bCustomer = spy(B2BCustomerModel.class);
		B2BUnitModel parent = spy(B2BUnitModel.class);
		parent.setUid("1234");

		when(userService.getCurrentUser()).thenReturn(b2bCustomer);
		when(b2bUnitService.getParent(b2bCustomer)).thenReturn(parent);

		assertEquals(parent.getUid(),s4OrderUtil.getSoldToParty(null));

	}
	
	@Test
	public void testGetCommonTransactionType() {
		
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		SAPConfigurationModel sapConfig = spy(SAPConfigurationModel.class);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		when(baseStore.getSAPConfiguration()).thenReturn(sapConfig);
		when(sapConfig.getSapcommon_transactionType()).thenReturn("OR");
		
		assertEquals("OR", s4OrderUtil.getCommonTransactionType());
	}
	
	@Test
	public void testGetCommonTransactionTypeWhenBaseStoreNull() {
		
		when(baseStoreService.getCurrentBaseStore()).thenReturn(null);
		
		assertNull(s4OrderUtil.getCommonTransactionType());
	}
	
	@Test
	public void testGetCommonTransactionTypeWhenSapConfigNull() {
		
		BaseStoreModel baseStore = spy(BaseStoreModel.class);
		when(baseStoreService.getCurrentBaseStore()).thenReturn(baseStore);
		when(baseStore.getSAPConfiguration()).thenReturn(null);
		
		assertNull(s4OrderUtil.getCommonTransactionType());
	}


}