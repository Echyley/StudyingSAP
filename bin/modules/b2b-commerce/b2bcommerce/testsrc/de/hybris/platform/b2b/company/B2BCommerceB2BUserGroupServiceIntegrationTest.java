/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BUserGroupModel;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collections;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class B2BCommerceB2BUserGroupServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String PRONTO_ADMIN_USER = "linda.wolf@pronto-hw.com";
	private static final String RUSTIC_ADMIN_USER = "linda.wolf@rustic-hw.com";
	private static final String PRONTO_CUSTOMER_USER = "mark.rivers@pronto-hw.com";
	private static final String RUSTIC_CUSTOMER_USER = "mark.rivers@rustic-hw.com";

	private static final String PRONTO_ENHANCED_GROUP = "enhancedGroup";
	private static final String PRONTO_COMMON_GROUP = "commonGroup";
	private static final String RUSTIC_PREMIUM_GROUP = "premiumGroup";
	private static final String RUSTIC_STANDARD_GROUP = "standardGroup";
	private static final String RUSTIC_LIMITED_GROUP = "limitedGroup";

	private static final PageableData PAGEABLE_DATA = B2BCommerceTestUtils.createPageableData();

	@Resource
	private B2BCommerceB2BUserGroupService b2bCommerceB2BUserGroupService;

	@Resource
	private I18NService i18NService;

	@Resource
	private CommonI18NService commonI18NService;

	private User prontoAdminUser;
	private User rusticAdminUser;
	private User prontoCustomerUser;
	private User rusticCustomerUser;

	@Before
	public void setUp() throws Exception
	{
		de.hybris.platform.servicelayer.ServicelayerTest.createCoreData();
		de.hybris.platform.servicelayer.ServicelayerTest.createDefaultCatalog();
		de.hybris.platform.catalog.jalo.CatalogManager.getInstance().createEssentialData(Collections.emptyMap(), null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");

		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));

		// this to avoid search restrictions being applied to Users based on the current logged in user.
		rusticAdminUser = UserManager.getInstance().getUserByLogin(RUSTIC_ADMIN_USER);
		prontoAdminUser = UserManager.getInstance().getUserByLogin(PRONTO_ADMIN_USER);
		rusticCustomerUser = UserManager.getInstance().getUserByLogin(RUSTIC_CUSTOMER_USER);
		prontoCustomerUser = UserManager.getInstance().getUserByLogin(PRONTO_CUSTOMER_USER);
	}

	// ----- getUserGroupForUID
	@Test
	public void testGetUserGroupAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_STANDARD_GROUP, B2BUserGroupModel.class);
		assertThat(userGroup).isNotNull();
	}

	@Test
	public void testGetUserGroupAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_PREMIUM_GROUP, B2BUserGroupModel.class);

		assertThat(userGroup).isNull();
	}

	@Test
	public void testB2bAdminDoesNotGetUserGroupFromDifferentUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BUserGroupModel b2BUserGroupModel =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(PRONTO_COMMON_GROUP, B2BUserGroupModel.class);

		assertThat(b2BUserGroupModel).isNull();
	}

	@Test
	public void testB2bCustomerDoesNotGetUserGroupFromDifferentUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUserGroupModel b2BUserGroupModel =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(PRONTO_COMMON_GROUP, B2BUserGroupModel.class);

		assertThat(b2BUserGroupModel).isNull();
	}

	@Test
	public void testB2bCustomerDoesNotGetUserGroupIfNotAssignedToIt()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BUserGroupModel b2BUserGroupModel =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_LIMITED_GROUP, B2BUserGroupModel.class);

		assertThat(b2BUserGroupModel).isNull();
	}

	// ----- getPagedB2BUserGroups
	@Test
	public void testGetUserGroupsAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final SearchPageData<B2BUserGroupModel> page =
			b2bCommerceB2BUserGroupService.getPagedB2BUserGroups(PAGEABLE_DATA);
		assertThat(page).isNotNull();
		assertThat(page.getResults())
				.extracting("uid")
				.containsExactlyInAnyOrder(RUSTIC_PREMIUM_GROUP, RUSTIC_STANDARD_GROUP, RUSTIC_LIMITED_GROUP);
	}

	@Test
	public void testGetUserGroupsAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final SearchPageData<B2BUserGroupModel> page =
			b2bCommerceB2BUserGroupService.getPagedB2BUserGroups(PAGEABLE_DATA);

		assertThat(page).isNotNull();
		assertThat(page.getResults()).isEmpty();
	}

	// ----- disableUserGroup
	@Test
	public void testDisableUserGroupAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		b2bCommerceB2BUserGroupService.disableUserGroup(RUSTIC_STANDARD_GROUP);
		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_STANDARD_GROUP, B2BUserGroupModel.class);

		assertThat(userGroup).isNotNull()
				.hasFieldOrPropertyWithValue("members", Collections.emptySet());
	}

	@Test
	public void testDisableUserGroupAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.disableUserGroup(RUSTIC_STANDARD_GROUP))
				.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testB2bCustomerDoesNotDisableUserGroupIfNotAssignedToIt()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.disableUserGroup(RUSTIC_LIMITED_GROUP))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testDisableUserGroupWithEmptyUidAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.disableUserGroup(StringUtils.EMPTY))
				.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testDisableUserGroupWithEmptyUidAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.disableUserGroup(StringUtils.EMPTY))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testDisableUserGroupWithUidNullAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.disableUserGroup(null))
				.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testDisableUserGroupWithUidNullAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.disableUserGroup(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	// ----- removeUserGroup
	@Test
	public void testRemoveUserGroupAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		b2bCommerceB2BUserGroupService.removeUserGroup(RUSTIC_STANDARD_GROUP);
		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_STANDARD_GROUP, B2BUserGroupModel.class);
		assertThat(userGroup).isNull();
	}

	@Test
	public void testRemoveUserGroupAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeUserGroup(RUSTIC_STANDARD_GROUP))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testB2bCustomerDoesNotRemoveUserGroupIfNotAssignedToIt()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeUserGroup(RUSTIC_LIMITED_GROUP))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testRemoveUserGroupWithEmptyUidAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeUserGroup(StringUtils.EMPTY))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testRemoveUserGroupWithEmptyUidAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeUserGroup(StringUtils.EMPTY))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testRemoveUserGroupWithUidNullAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeUserGroup(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testRemoveUserGroupWithUidNullAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeUserGroup(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	// ----- addUserToUserGroup
	@Test
	public void testAddUserToUserGroupAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		b2bCommerceB2BUserGroupService.addMemberToUserGroup(RUSTIC_STANDARD_GROUP, RUSTIC_CUSTOMER_USER);
		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_STANDARD_GROUP, B2BUserGroupModel.class);
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).extracting("uid").contains(RUSTIC_CUSTOMER_USER);
	}

	@Test
	public void testAddUserToUserGroupAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(RUSTIC_STANDARD_GROUP, PRONTO_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class);

		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_STANDARD_GROUP, B2BUserGroupModel.class);
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).extracting("uid").doesNotContain(PRONTO_CUSTOMER_USER);
	}

	@Test
	public void testB2bCustomerDoesNotAddUserToUserGroupIfNotAssignedToIt()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(PRONTO_COMMON_GROUP, PRONTO_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class);

		JaloSession.getCurrentSession().setUser(prontoAdminUser);
		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(PRONTO_COMMON_GROUP, B2BUserGroupModel.class);
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).extracting("uid").doesNotContain(PRONTO_CUSTOMER_USER);
	}

	@Test
	public void testAddUserToUserGroupWithEmptyUidAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(StringUtils.EMPTY, PRONTO_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testAddUserToUserGroupWithEmptyUidAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(StringUtils.EMPTY, PRONTO_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testAddUserToUserGroupWithUidNullAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(null, PRONTO_CUSTOMER_USER))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testAddUserToUserGroupWithUidNullAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(null, PRONTO_CUSTOMER_USER))
			.isInstanceOf(IllegalArgumentException.class);
	}


	// ----- removeUserFromUserGroup
	@Test
	public void testRemoveUserFromUserGroupAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		b2bCommerceB2BUserGroupService.removeMemberFromUserGroup(RUSTIC_PREMIUM_GROUP, RUSTIC_CUSTOMER_USER);
		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_PREMIUM_GROUP, B2BUserGroupModel.class);
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).extracting("uid").doesNotContain(RUSTIC_CUSTOMER_USER);
	}

	@Test
	public void testRemoveUserFromUserGroupAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeMemberFromUserGroup(RUSTIC_PREMIUM_GROUP, RUSTIC_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class);

		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		final B2BUserGroupModel userGroup =
			b2bCommerceB2BUserGroupService.getUserGroupForUID(RUSTIC_PREMIUM_GROUP, B2BUserGroupModel.class);
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).extracting("uid").contains(RUSTIC_CUSTOMER_USER);
	}

	@Test
	public void testB2bCustomerDoesNotRemoveUserFromUserGroupIfNotAssignedToIt()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeMemberFromUserGroup(PRONTO_COMMON_GROUP, PRONTO_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testRemoveUserFromUserGroupWithEmptyUidAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.removeMemberFromUserGroup(StringUtils.EMPTY, RUSTIC_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testRemoveUserFromUserGroupWithEmptyUidAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(StringUtils.EMPTY, RUSTIC_CUSTOMER_USER))
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void testRemoveUserFromUserGroupWithUidNullAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(null, RUSTIC_CUSTOMER_USER))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testRemoveUserFromUserGroupWithUidNullAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		assertThatThrownBy(() -> b2bCommerceB2BUserGroupService.addMemberToUserGroup(null, RUSTIC_CUSTOMER_USER))
			.isInstanceOf(IllegalArgumentException.class);
	}
}
