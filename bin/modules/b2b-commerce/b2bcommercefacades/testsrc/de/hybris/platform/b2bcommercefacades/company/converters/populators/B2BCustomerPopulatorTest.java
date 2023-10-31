/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.company.converters.populators;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.model.B2BUnitModel;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.services.B2BUnitService;
import de.hybris.platform.b2b.strategies.B2BUserGroupsLookUpStrategy;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.security.PrincipalGroupModel;
import de.hybris.platform.core.model.user.TitleModel;
import de.hybris.platform.core.model.user.UserGroupModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@UnitTest
@RunWith(MockitoJUnitRunner.class)
public class B2BCustomerPopulatorTest
{
	public static final String USER_ID = "uid";
	public static final String USER_NAME = "name";
	public static final String USER_EMAIL = "emailAddress";
	public static final String TITLE_CODE = "titleCode";
	public static final String UNIT_ID = "unitId";
	public static final String UNIT_NAME = "unitName";
	public static final String COST_CENTER_CODE = "costCenterCode";
	public static final String COST_CENTER_NAME = "costCenterName";
	public static final String USERGROUP = "usergroup";
	public static final String PERMISSION_GROUP_NAME = "permissionGroupName";
	public static final String PERMISSION_GROUP_UID = "permissionGroupUid";
	public static final Boolean IS_ACTIVE = Boolean.TRUE;

	private B2BCustomerPopulator b2BCustomerPopulator;
	private B2BCustomerModel source;
	private CustomerData target;

	@Mock
	private CurrencyModel testCurrency;

	@Mock
	private B2BUnitModel testUnit;

	@Mock
	private B2BUnitService<B2BUnitModel, UserModel> b2bUnitService;

	@Mock
	private CommonI18NService commonI18NService;

	@Mock
	private Converter<CurrencyModel, CurrencyData> currencyConverter;

	@Mock
	private B2BUserGroupsLookUpStrategy b2BUserGroupsLookUpStrategy;

	@Before
	public void setUp() throws Exception
	{
		source = mock(B2BCustomerModel.class);
		target = new CustomerData();

		b2BCustomerPopulator = new B2BCustomerPopulator();
		b2BCustomerPopulator.setB2bUnitService(b2bUnitService);
		b2BCustomerPopulator.setCurrencyConverter(currencyConverter);
		b2BCustomerPopulator.setCommonI18NService(commonI18NService);
		b2BCustomerPopulator.setB2BUserGroupsLookUpStrategy(b2BUserGroupsLookUpStrategy);
	}

	@Test
	public void shouldPopulate()
	{
		var testCurrencyData = mock(CurrencyData.class);
		var title = mock(TitleModel.class);
		given(title.getCode()).willReturn(TITLE_CODE);
		given(source.getUid()).willReturn(USER_ID);
		given(source.getName()).willReturn(USER_NAME);
		given(source.getEmail()).willReturn(USER_EMAIL);
		given(source.getActive()).willReturn(IS_ACTIVE);
		given(source.getTitle()).willReturn(title);
		given(currencyConverter.convert(nullable(CurrencyModel.class))).willReturn(testCurrencyData);

		b2BCustomerPopulator.populate(source, target);
		Assert.assertEquals("source and target code should match", source.getUid(), target.getUid());
		Assert.assertEquals("source and target name should match", source.getName(), target.getName());
		Assert.assertEquals("source and target email should match", source.getEmail(), target.getEmail());
		Assert.assertEquals("source and target name should match", source.getTitle().getCode(), target.getTitleCode());
		Assert.assertEquals("source and target name should match", source.getActive(), target.isActive());
		Assert.assertNotNull("target currency should not be null", target.getCurrency());
		Assert.assertEquals("source and target currency should match", testCurrencyData, target.getCurrency());
	}

	@Test
	public void shouldPopulateUnit()
	{
		var costCenters = new ArrayList<B2BCostCenterModel>();
		var costCenter = mock(B2BCostCenterModel.class);
		given(costCenter.getCode()).willReturn(COST_CENTER_CODE);
		given(costCenter.getName()).willReturn(COST_CENTER_NAME);
		costCenters.add(costCenter);
		given(testUnit.getUid()).willReturn(UNIT_ID);
		given(testUnit.getLocName()).willReturn(UNIT_NAME);
		given(testUnit.getActive()).willReturn(IS_ACTIVE);
		given(testUnit.getCostCenters()).willReturn(costCenters);
		given(b2bUnitService.getParent(source)).willReturn(testUnit);

		b2BCustomerPopulator.populate(source, target);
		Assert.assertNotNull("target unit should not be null", target.getUnit());
		Assert.assertEquals("source and target unit id should match", UNIT_ID, target.getUnit().getUid());
		Assert.assertEquals("source and target unit name should match", UNIT_NAME, target.getUnit().getName());
		Assert.assertEquals("source and target unit name should match", IS_ACTIVE, target.getUnit().isActive());
		Assert.assertNotNull("target unit cost centers should not be null", target.getUnit().getCostCenters());
		Assert.assertEquals("source and target unit cost centers should match", 1, target.getUnit().getCostCenters().size());
		var targetCostCenter = target.getUnit().getCostCenters().get(0);
		Assert.assertEquals("source and target unit cost centers should match", COST_CENTER_CODE, targetCostCenter.getCode());
		Assert.assertEquals("source and target unit cost centers should match", COST_CENTER_NAME, targetCostCenter.getName());
	}

	@Test
	public void shouldPopulateRolesAndGroups()
	{
		// roles
		var roleGroups = new ArrayList<String>();
		var roleGroup = mock(UserGroupModel.class);
		given(roleGroup.getUid()).willReturn(USERGROUP);
		roleGroups.add(USERGROUP);
		given(b2BUserGroupsLookUpStrategy.getUserGroups()).willReturn(roleGroups);

		//permissionGroups
		var permissionGroups = new HashSet<PrincipalGroupModel>();
		var permissionGroup = mock(B2BUserGroupModel.class);
		given(permissionGroup.getName()).willReturn(PERMISSION_GROUP_NAME);
		given(permissionGroup.getUid()).willReturn(PERMISSION_GROUP_UID);
		given(permissionGroup.getUnit()).willReturn(testUnit);
		permissionGroups.add(roleGroup);
		permissionGroups.add(permissionGroup);
		given(source.getGroups()).willReturn(permissionGroups);

		given(testUnit.getUid()).willReturn(UNIT_ID);
		given(testUnit.getLocName()).willReturn(UNIT_NAME);
		given(testUnit.getActive()).willReturn(IS_ACTIVE);

		b2BCustomerPopulator.populate(source, target);

		// roles
		Assert.assertNotNull("target roles should not be null", target.getRoles());
		Assert.assertEquals("source and target unit roles should match", 1, target.getRoles().size());
		Assert.assertEquals("source and target unit roles should match", USERGROUP, target.getRoles().iterator().next());

		// permission groups
		Assert.assertNotNull("target PermissionGroups should not be null", target.getPermissionGroups());
		Assert.assertEquals("source and target unit PermissionGroups should match", 1, target.getPermissionGroups().size());
		var permission = target.getPermissionGroups().iterator().next();
		Assert.assertEquals("source and target unit PermissionGroups should match", PERMISSION_GROUP_NAME, permission.getName());
		Assert.assertEquals("source and target unit PermissionGroups should match", PERMISSION_GROUP_UID, permission.getUid());
		Assert.assertNotNull("target permission unit should not be null", permission.getUnit());
		Assert.assertEquals("source and target PermissionGroups unit should match", UNIT_ID, permission.getUnit().getUid());
		Assert.assertEquals("source and target PermissionGroups unit should match", UNIT_NAME, permission.getUnit().getName());
		Assert.assertEquals("source and target PermissionGroups unit should match", IS_ACTIVE, permission.getUnit().isActive());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfSourceIsNull()
	{
		b2BCustomerPopulator.populate(null, target);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotPopulateIfTargetIsNull()
	{
		b2BCustomerPopulator.populate(source, null);
	}

}
