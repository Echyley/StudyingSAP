/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.getSelectedUsers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commercefacades.user.data.UserGroupData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class DefaultB2BUserGroupFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	protected static final String B2B_ADMIN_USER_FOR_MC = "MC Admin";
	protected static final String B2B_ADMIN_USER = "DC Admin";
	protected static final String B2B_CUSTOMER_USER = "DC S HH";

	// paging
	private static final PageableData PAGEABLE_DATA_0_5_BY_NAME = createPageableData(0, 5, "byName");

	@Resource
	private DefaultB2BUserGroupFacade defaultB2BUserGroupFacade;

	@Resource
	private DefaultB2BUnitFacade defaultB2BUnitFacade;

	@Resource
	private I18NService i18NService;

	protected User b2bAdminUser;
	protected User b2bCustomerUser;
	protected User b2bAdminUserForMC;

	@Before
	public void setUp() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();
		CatalogManager.getInstance().createEssentialData(Collections.emptyMap(), null);
		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		importCsv("/b2bcommercefacades/test/testOrganizations.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
		b2bAdminUser = UserManager.getInstance().getUserByLogin(B2B_ADMIN_USER);
		b2bCustomerUser = UserManager.getInstance().getUserByLogin(B2B_CUSTOMER_USER);
		b2bAdminUserForMC = UserManager.getInstance().getUserByLogin(B2B_ADMIN_USER_FOR_MC);

		setCurrentUser(b2bAdminUser);
	}

	protected void setCurrentUser(final User user)
	{
		JaloSession.getCurrentSession().setUser(user);
	}

	@Test
	public void shouldGetPagedCustomersForUserGroupAsB2BAdmin()
	{
		final SearchPageData<CustomerData> searchPageData =
			defaultB2BUserGroupFacade.getPagedCustomersForUserGroup(PAGEABLE_DATA_0_5_BY_NAME, "DC");

		assertThat(searchPageData).isNotNull().hasFieldOrPropertyWithValue("pagination.totalNumberOfResults", 10L);
		assertThat(searchPageData.getResults())
			.hasSize(5)
			.extracting("name")
			.contains("Bernie Big Boss", "Big Cheese", "Bobby Bargain", "Ed Whitacre", "John Ford");

		final List<CustomerData> selectedUsers = getSelectedUsers(searchPageData.getResults());
		assertThat(selectedUsers).hasSize(1)
								 .extracting("uid")
								 .contains("DC CEO");
	}

	@Test
	public void shouldNotGetPagedCustomersForUserGroupAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.getPagedCustomersForUserGroup(PAGEABLE_DATA_0_5_BY_NAME, "DC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No usergroup found for uid DC");
	}

	@Test
	public void shouldUpdateUserGroupAsB2BAdmin()
	{
		final B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull()
							 .hasFieldOrPropertyWithValue("unit.uid", "DC")
							 .hasFieldOrPropertyWithValue("name", null);

		// update some fields
		userGroup.setName("Europe Manager Permission Group");
		userGroup.setUnit(defaultB2BUnitFacade.getUnitForUid("DC Sales"));

		defaultB2BUserGroupFacade.updateUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", userGroup);

		final B2BUserGroupData updatedUserGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(updatedUserGroup).isNotNull()
									.hasFieldOrPropertyWithValue("unit.uid", "DC Sales")
									.hasFieldOrPropertyWithValue("name", "Europe Manager Permission Group");
	}

	@Test
	public void shouldNotUpdateUserGroupAsB2BCustomer()
	{
		final B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull()
							 .hasFieldOrPropertyWithValue("unit.uid", "DC")
							 .hasFieldOrPropertyWithValue("name", null);

		// update some fields
		userGroup.setName("Europe Manager Permission Group");
		userGroup.setUnit(defaultB2BUnitFacade.getUnitForUid("DC Sales"));

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.updateUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", userGroup) )
			.isInstanceOf(ModelSavingException.class)
			.hasMessageContaining("missing values for [Unit] in model B2BUserGroupModel");

		setCurrentUser(b2bAdminUser);
		final B2BUserGroupData updatedUserGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(updatedUserGroup).isNotNull()
									.hasFieldOrPropertyWithValue("unit.uid", "DC")
									.hasFieldOrPropertyWithValue("name", null);
	}

	@Test
	public void shouldCreateUserGroupAsB2BAdmin()
	{
		final B2BUserGroupData userGroup = new B2BUserGroupData();
		userGroup.setUid("DC New");
		userGroup.setName("New User Group");
		userGroup.setUnit(defaultB2BUnitFacade.getUnitForUid("DC"));

		defaultB2BUserGroupFacade.updateUserGroup("DC New", userGroup);

		final B2BUserGroupData newUserGroup = defaultB2BUserGroupFacade.getB2BUserGroup("DC New");
		assertThat(newUserGroup).isNotNull()
								.hasFieldOrPropertyWithValue("unit.uid", "DC")
								.hasFieldOrPropertyWithValue("name", "New User Group");
	}

	@Test
	public void shouldNotCreateUserGroupAsB2BCustomer()
	{
		final B2BUserGroupData userGroup = new B2BUserGroupData();
		userGroup.setUid("DC New");
		userGroup.setName("New User Group");
		userGroup.setUnit(defaultB2BUnitFacade.getUnitForUid("DC"));

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.updateUserGroup("DC New", userGroup) )
			.isInstanceOf(ModelSavingException.class)
			.hasMessageContaining("missing values for [Unit] in model B2BUserGroupModel");

		setCurrentUser(b2bAdminUser);
		final B2BUserGroupData newUserGroup = defaultB2BUserGroupFacade.getB2BUserGroup("DC New");
		assertThat(newUserGroup).isNull();
	}

	@Test
	public void shouldDisableUserGroupAsB2BAdmin()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).isNotEmpty();

		defaultB2BUserGroupFacade.disableUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");

		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).isNull();
	}

	@Test
	public void shouldNotDisableUserGroupAsB2BCustomer()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).isNotEmpty(); // empty groups means: disabled

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.disableUserGroup("EUROPE_MANAGER_PERM_GROUP_DC") )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("UserGroupModel with uid 'EUROPE_MANAGER_PERM_GROUP_DC' not found!");

		setCurrentUser(b2bAdminUser);
		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).isNotEmpty(); // empty groups means: disabled
	}

	@Test
	public void shouldDisableUserGroupNullUserGroupUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.disableUserGroup(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldDisableUserGroupNullUserGroupUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.disableUserGroup(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldRemoveUserGroupAsB2BAdmin()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();

		defaultB2BUserGroupFacade.removeUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");

		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNull();
	}

	@Test
	public void shouldNotRemoveUserGroupAsB2BCustomer()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.removeUserGroup("EUROPE_MANAGER_PERM_GROUP_DC") )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("UserGroupModel with uid 'EUROPE_MANAGER_PERM_GROUP_DC' not found!");

		setCurrentUser(b2bAdminUser);
		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
	}

	@Test
	public void shouldNotRemoveUserGroupNullUserGroupUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.removeUserGroup(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveUserGroupNullUserGroupUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.removeUserGroup(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldGetPagedUserDataAsB2BAdmin()
	{
		final SearchPageData<CustomerData> searchPageData =
			defaultB2BUserGroupFacade.getPagedUserData(PAGEABLE_DATA_0_5_BY_NAME);
		assertThat(searchPageData).isNotNull().hasFieldOrPropertyWithValue("pagination.totalNumberOfResults", 10L);
		assertThat(searchPageData.getResults())
			.hasSize(5)
			.extracting("name")
			.contains("Bernie Big Boss", "Big Cheese", "Bobby Bargain", "Ed Whitacre", "John Ford");
	}

	@Test
	public void shouldGetPagedUserDataAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final SearchPageData<CustomerData> searchPageData =
			defaultB2BUserGroupFacade.getPagedUserData(PAGEABLE_DATA_0_5_BY_NAME);
		assertThat(searchPageData).isNotNull().hasFieldOrPropertyWithValue("pagination.totalNumberOfResults", 1L);
		assertThat(searchPageData.getResults())
			.hasSize(1)
			.extracting("name")
			.contains("Uwe Seeler");
	}

	@Test
	public void shouldGetPagedB2BUserGroupsAsB2BAdmin()
	{
		final SearchPageData<B2BUserGroupData> searchPageData = defaultB2BUserGroupFacade.getPagedB2BUserGroups(PAGEABLE_DATA_0_5_BY_NAME);
		assertThat(searchPageData).isNotNull().hasFieldOrPropertyWithValue("pagination.totalNumberOfResults", 4L);
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC", "EUROPE_MANAGER_PERM_GROUP_DC");
	}

	@Test
	public void shouldNotGetPagedB2BUserGroupsAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final SearchPageData<B2BUserGroupData> searchPageData = defaultB2BUserGroupFacade.getPagedB2BUserGroups(PAGEABLE_DATA_0_5_BY_NAME);
		assertThat(searchPageData).isNotNull().hasFieldOrPropertyWithValue("pagination.totalNumberOfResults", 0L);
		assertThat(searchPageData.getResults()).isEmpty();
	}

	@Test
	public void shouldGetB2BUserGroupAsB2BAdmin()
	{
		final B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull()
							 .hasFieldOrPropertyWithValue("uid", "EUROPE_MANAGER_PERM_GROUP_DC");
	}

	@Test
	public void shouldNotGetB2BUserGroupAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNull();
	}

	@Test
	public void shouldGetB2BUserGroupNullUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.getB2BUserGroup(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}


	@Test
	public void shouldGetB2BUserGroupNullUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.getB2BUserGroup(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldAddMemberToUserGroupAsB2BAdmin()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers())
			.hasSize(1)
			.extracting("uid")
			.doesNotContain("DC S HH");

		final CustomerData user = defaultB2BUserGroupFacade.addMemberToUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", "DC S HH");
		assertThat(user).isNotNull()
						.hasFieldOrPropertyWithValue("uid", "DC S HH")
						.hasFieldOrPropertyWithValue("selected", true);

		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers())
			.hasSize(2)
			.extracting("uid")
			.contains("DC S HH");
	}

	@Test
	public void shouldNotAddMemberToUserGroupAsB2BCustomer()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers())
			.hasSize(1)
			.extracting("uid")
			.doesNotContain("DC S HH");

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.addMemberToUserGroup("EUROPE_MANAGER_PERM_GROUP_DC", "DC S HH") )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("UserGroupModel with uid 'EUROPE_MANAGER_PERM_GROUP_DC' not found!");

		setCurrentUser(b2bAdminUser);
		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers())
			.hasSize(1)
			.extracting("uid")
			.doesNotContain("DC S HH");
	}


	@Test
	public void shouldRemoveMemberFromUserGroupAsB2BAdmin()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers())
			.hasSize(1)
			.extracting("uid")
			.contains("DC Sales DE Boss");


		final CustomerData user =
			defaultB2BUserGroupFacade.removeMemberFromUserGroup("EUROPE_MANAGER_PERM_GROUP_DC",	"DC Sales DE Boss");
		assertThat(user).isNotNull()
						.hasFieldOrPropertyWithValue("uid", "DC Sales DE Boss")
						.hasFieldOrPropertyWithValue("selected", false);

		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers()).isNull();
	}

	@Test
	public void shouldNotRemoveMemberFromUserGroupAsB2BCustomer()
	{
		B2BUserGroupData userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers())
			.hasSize(1)
			.extracting("uid")
			.contains("DC Sales DE Boss");

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserGroupFacade.removeMemberFromUserGroup("EUROPE_MANAGER_PERM_GROUP_DC",	"DC Sales DE Boss") )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("UserGroupModel with uid 'EUROPE_MANAGER_PERM_GROUP_DC' not found!");

		setCurrentUser(b2bAdminUser);
		userGroup = defaultB2BUserGroupFacade.getB2BUserGroup("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull();
		assertThat(userGroup.getMembers())
			.hasSize(1)
			.extracting("uid")
			.contains("DC Sales DE Boss");
	}

	@Test
	public void shouldGetUserGroupDataForUidAsB2BAdmin()
	{
		final UserGroupData userGroup = defaultB2BUserGroupFacade.getUserGroupDataForUid("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNotNull()
							 .hasFieldOrPropertyWithValue("uid", "EUROPE_MANAGER_PERM_GROUP_DC");
	}

	@Test
	public void shouldNotGetUserGroupDataForUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final UserGroupData userGroup = defaultB2BUserGroupFacade.getUserGroupDataForUid("EUROPE_MANAGER_PERM_GROUP_DC");
		assertThat(userGroup).isNull();
	}

	@Test
	public void shouldNotGetUserGroupDataForUidForDifferentUnitAsB2BAdmin()
	{
		final UserGroupData userGroup = defaultB2BUserGroupFacade.getUserGroupDataForUid("MC_CEO_PERMISSIONS");
		assertThat(userGroup).isNull();
	}

	@Test
	public void shouldGetUserGroupDataForUidForDifferentUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final UserGroupData userGroup = defaultB2BUserGroupFacade.getUserGroupDataForUid("MC_CEO_PERMISSIONS");
		assertThat(userGroup).isNull();
	}
}
