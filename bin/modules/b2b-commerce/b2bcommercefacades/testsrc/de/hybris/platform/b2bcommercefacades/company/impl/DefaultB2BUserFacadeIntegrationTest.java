/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static de.hybris.platform.b2bcommercefacades.util.B2BCommercefacadesTestUtils.getSelectedUserGroups;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.b2b.services.impl.DefaultB2BCustomerService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUserGroupData;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.exceptions.UnknownIdentifierException;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

@IntegrationTest
public class DefaultB2BUserFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	protected static final String B2B_ADMIN_USER = "DC Admin";
	protected static final String B2B_CUSTOMER_USER = "DC S HH";

	// b2b customers
	private static final String DC_SALES_DE_BOSS = "DC Sales DE Boss";
	private static final String DC_S_DET = "DC S Det";
	private static final String DC_S_HH = "DC S HH";

	// b2b units
	private static final String DC_SALES_NOTTINGHAM = "DC Sales Nottingham";
	private static final String DC_SALES_DETROIT = "DC Sales Detroit";

	// b2b permission groups
	private static final String EUROPE_MANAGER_PERM_GROUP_DC = "EUROPE_MANAGER_PERM_GROUP_DC";


	// paging
	private static final String BY_UNIT_NAME = "byUnitName";
	private static final PageableData PAGEABLE_DATA_0_20_BY_UNIT_NAME = createPageableData(0, 20, BY_UNIT_NAME);

	@Resource
	private DefaultB2BUserFacade defaultB2BUserFacade;

	@Resource
	private DefaultB2BCustomerService defaultB2BCustomerService;

	@Resource
	private DefaultB2BUnitFacade defaultB2BUnitFacade;

	@Resource
	private UserService userService;

	@Resource
	private I18NService i18NService;

	protected User b2bAdminUser;
	protected User b2bCustomerUser;

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
		setCurrentUser(b2bAdminUser);
	}

	protected void setCurrentUser(final User user)
	{
		JaloSession.getCurrentSession().setUser(user);
	}

	@Test
	public void shouldGetPagedCustomersAsB2BAdmin()
	{
		final SearchPageData<CustomerData> searchPageData = defaultB2BUserFacade.getPagedCustomers(PAGEABLE_DATA_0_20_BY_UNIT_NAME);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(10)
			.extracting("name")
			.contains("Bernie Big Boss", "Big Cheese", "Bobby Bargain", "Ed Whitacre", "John Ford", "Nimda Admin",
				"Otto Meier", "Robin Hood", "Robin Hood MUC", "Uwe Seeler");
	}

	@Test
	public void shouldNotGetPagedCustomersAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final SearchPageData<CustomerData> searchPageData = defaultB2BUserFacade.getPagedCustomers(PAGEABLE_DATA_0_20_BY_UNIT_NAME);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults()).hasSize(1)
											   .extracting("uid")
											   .contains("DC S HH");
	}

	@Test
	public void shouldGetParentUnitForCustomerAsB2BAdmin()
	{
		final B2BUnitData parentUnit = defaultB2BUserFacade.getParentUnitForCustomer(DC_S_DET);
		assertThat(parentUnit).isNotNull().hasFieldOrPropertyWithValue("uid", DC_SALES_DETROIT);
	}

	@Test
	public void shouldNotGetParentUnitForCustomerAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getParentUnitForCustomer(DC_S_DET) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC S Det'");
	}

	@Test
	public void shouldNotGetParentUnitForNullCustomerAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.getParentUnitForCustomer(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetParentUnitForNullCustomerAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getParentUnitForCustomer(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldUpdateCustomerUpdateExistingCustomerAsB2BAdmin()
	{
		final CustomerData customer = defaultB2BUserFacade.getCustomerForUid(DC_S_DET);
		assertThat(customer).isNotNull()
							.hasFieldOrPropertyWithValue("titleCode", "mr")
							.hasFieldOrPropertyWithValue("name", "Ed Whitacre")
							.hasFieldOrPropertyWithValue("active", true)
							.hasFieldOrPropertyWithValue("unit.uid", DC_SALES_DETROIT)
							.hasFieldOrPropertyWithValue("roles", Collections.singletonList("b2bcustomergroup"));

		// no actual updates, but fields are not populated and are mandatory for the update
		customer.setEmail("DC.S.Det@gmail.com");
		customer.setDisplayUid(DC_S_DET);

		// update some fields
		customer.setFirstName("Edward");
		customer.setLastName("Whiteacre Jr.");
		customer.setActive(false);
		customer.setUnit(defaultB2BUnitFacade.getUnitForUid("DC Sales US"));
		customer.getRoles().add("b2bmanagergroup");

		// update customer
		defaultB2BUserFacade.updateCustomer(customer);

		final CustomerData updatedCustomer = defaultB2BUserFacade.getCustomerForUid(DC_S_DET);
		assertThat(updatedCustomer).isNotNull()
								   .hasFieldOrPropertyWithValue("titleCode", "mr")
								   .hasFieldOrPropertyWithValue("name", "Edward Whiteacre Jr.")
								   .hasFieldOrPropertyWithValue("active", true)    // the facade does not update this field.
								   .hasFieldOrPropertyWithValue("unit.uid", "DC Sales US");
		assertThat(updatedCustomer.getRoles()).hasSize(2).contains("b2bmanagergroup", "b2bcustomergroup");
	}

	@Test
	public void shouldNotUpdateCustomerUpdateExistingCustomerAsB2BCustomer()
	{
		setCurrentUser(b2bAdminUser);
		final CustomerData customer = defaultB2BUserFacade.getCustomerForUid(DC_S_DET);
		assertThat(customer).isNotNull()
							.hasFieldOrPropertyWithValue("titleCode", "mr")
							.hasFieldOrPropertyWithValue("name", "Ed Whitacre")
							.hasFieldOrPropertyWithValue("active", true)
							.hasFieldOrPropertyWithValue("unit.uid", DC_SALES_DETROIT)
							.hasFieldOrPropertyWithValue("roles", Lists.newArrayList("b2bcustomergroup"));

		// no actual updates, but fields are not populated and are mandatory for the update
		customer.setEmail("DC.S.Det@gmail.com");
		customer.setDisplayUid(DC_S_DET);

		// update some fields
		customer.setFirstName("Edward");
		customer.setLastName("Whiteacre Jr.");
		customer.setActive(false);
		customer.setUnit(defaultB2BUnitFacade.getUnitForUid("DC Sales US"));
		customer.getRoles().add("b2bmanagergroup");

		// update customer
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(customer) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC S Det'");

		setCurrentUser(b2bAdminUser);
		final CustomerData updatedCustomer = defaultB2BUserFacade.getCustomerForUid(DC_S_DET);
		assertThat(updatedCustomer).isNotNull()
								   .hasFieldOrPropertyWithValue("titleCode", "mr")
								   .hasFieldOrPropertyWithValue("name", "Ed Whitacre")
								   .hasFieldOrPropertyWithValue("active", true)
								   .hasFieldOrPropertyWithValue("unit.uid", DC_SALES_DETROIT)
								   .hasFieldOrPropertyWithValue("roles", Lists.newArrayList("b2bcustomergroup"));
	}

	@Test
	public void shouldUpdateCustomerCreateNewCustomerAsB2BAdmin()
	{
		final CustomerData customer = new CustomerData();
		// no actual updates, but fields are not populated and are mandatory for the update
		customer.setEmail("DC.S.Det.2@gmail.com");
		customer.setDisplayUid("DC S Det 2");

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName("New");
		customer.setLastName("Customer");
		customer.setActive(true);
		customer.setUnit(defaultB2BUnitFacade.getUnitForUid(DC_SALES_DETROIT));
		final List<String> roles = new ArrayList<>();
		roles.add("b2bcustomergroup");
		customer.setRoles(roles);

		// create a new customer
		defaultB2BUserFacade.updateCustomer(customer);

		final CustomerData newCustomer = defaultB2BUserFacade.getCustomerForUid("DC S Det 2".toLowerCase()); // reverse populator makes uid lowercase
		assertThat(newCustomer).isNotNull()
							   .hasFieldOrPropertyWithValue("titleCode", "mr")
							   .hasFieldOrPropertyWithValue("name", "New Customer")
							   .hasFieldOrPropertyWithValue("active", true)
							   .hasFieldOrPropertyWithValue("unit.uid", DC_SALES_DETROIT)
							   .hasFieldOrPropertyWithValue("roles", Lists.newArrayList("b2bcustomergroup"));
	}

	@Test
	public void shouldNotUpdateCustomerCreateNewCustomerAsB2BCustomer()
	{
		setCurrentUser(b2bAdminUser);
		final CustomerData customer = new CustomerData();
		// no actual updates, but fields are not populated and are mandatory for the update
		customer.setEmail("DC.S.Det.2@gmail.com");
		customer.setDisplayUid("DC S Det 2");

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName("New");
		customer.setLastName("Customer");
		customer.setActive(true);


		customer.setUnit(defaultB2BUnitFacade.getUnitForUid(DC_SALES_DETROIT));
		final List<String> roles = new ArrayList<>();
		roles.add("b2bcustomergroup");
		customer.setRoles(roles);

		// create a new customer
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(customer) )
			.isInstanceOf(ModelSavingException.class)
			.hasMessageContaining("All B2B Customers must be a member of a B2B Unit");

		setCurrentUser(b2bAdminUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getCustomerForUid("dc s det 2") )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'dc s det 2'");
	}

	@Test
	public void shouldUpdateCustomerTitleCodeNullAsB2BAdmin()
	{
		final CustomerData customer = new CustomerData();
		// no actual updates, but fields are not populated and are mandatory for the update
		customer.setEmail("DC.S.Det.2@gmail.com");
		customer.setDisplayUid("DC S Det 2");

		// set fields
		customer.setTitleCode(null);
		customer.setFirstName("Newer");
		customer.setLastName("FavCustomer");
		customer.setUnit(defaultB2BUnitFacade.getUnitForUid(DC_SALES_NOTTINGHAM));

		// create a new customer
		defaultB2BUserFacade.updateCustomer(customer);

		final CustomerData newCustomer = defaultB2BUserFacade.getCustomerForUid("DC S Det 2".toLowerCase()); // reverse populator makes uid lowercase
		assertThat(newCustomer).isNotNull()
							   .hasFieldOrPropertyWithValue("titleCode", null)
							   .hasFieldOrPropertyWithValue("name", "Newer FavCustomer")
							   .hasFieldOrPropertyWithValue("active", true)
							   .hasFieldOrPropertyWithValue("unit.uid", DC_SALES_NOTTINGHAM)
							   .hasFieldOrPropertyWithValue("roles", Lists.newArrayList("b2bcustomergroup"));
	}

	@Test
	public void shouldNotUpdateCustomerTitleCodeNullAsB2BCustomer()
	{
		final CustomerData customer = new CustomerData();
		// no actual updates, but fields are not populated and are mandatory for the update
		customer.setEmail("DC.S.Det.2@gmail.com");
		customer.setDisplayUid("DC S Det 2");

		// set fields
		customer.setTitleCode(null);
		customer.setFirstName("Newer");
		customer.setLastName("FavCustomer");
		customer.setUnit(defaultB2BUnitFacade.getUnitForUid(DC_SALES_NOTTINGHAM));

		// create a new customer
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(customer) )
			.isInstanceOf(ModelSavingException.class)
			.hasMessageContaining("All B2B Customers must be a member of a B2B Unit");

		setCurrentUser(b2bAdminUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getCustomerForUid("dc s det 2") )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'dc s det 2'");
	}

	@Test
	public void shouldNotUpdateCustomerCustomerDataNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotUpdateCustomerCustomerDataNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotUpdateCustomerFirstNameEmptyAsB2BAdmin()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName(StringUtils.EMPTY);
		customer.setLastName("Customer");

		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(customer) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotUpdateCustomerFirstNameEmptyAsB2BCustomer()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName(StringUtils.EMPTY);
		customer.setLastName("Customer");

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(customer) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotUpdateCustomerLastNameEmptyAsB2BAdmin()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName("New");
		customer.setLastName(StringUtils.EMPTY);

		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(customer) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotUpdateCustomerLastNameEmptyAsB2BCustomer()
	{
		final CustomerData customer = new CustomerData();

		// set fields
		customer.setTitleCode("mr");
		customer.setFirstName("New");
		customer.setLastName(StringUtils.EMPTY);

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.updateCustomer(customer) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldResetCustomerPasswordAsB2BAdmin()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull();
		assertThat(customer.getEncodedPassword()).isNull();

		defaultB2BUserFacade.resetCustomerPassword(DC_S_DET, "updatedPassword");

		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer.getEncodedPassword()).isNotNull();
	}


	@Test
	public void shouldResetCustomerPasswordAsB2BCustomer()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull();
		assertThat(customer.getEncodedPassword()).isNull();

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.resetCustomerPassword(DC_S_DET, "updatedPassword") )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC S Det'");

		setCurrentUser(b2bAdminUser);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer.getEncodedPassword()).isNull();
	}

	@Test
	public void shouldNotResetCustomerPasswordAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.resetCustomerPassword(null, "updatedPassword") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotResetCustomerPasswordAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.resetCustomerPassword(null, "updatedPassword") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotResetCustomerPassword2AsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.resetCustomerPassword(DC_S_DET, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotResetCustomerPassword2AsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.resetCustomerPassword(DC_S_DET, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldDisableEnableCustomerAsB2BAdmin()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull()
							.hasFieldOrPropertyWithValue("active", true);

		// disable
		defaultB2BUserFacade.disableCustomer(DC_S_DET);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull()
							.hasFieldOrPropertyWithValue("active", false);

		// enable
		defaultB2BUserFacade.enableCustomer(DC_S_DET);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull()
							.hasFieldOrPropertyWithValue("active", true);
	}

	@Test
	public void shouldNotDisableOrEnableCustomerAsB2BCustomer()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull()
							.hasFieldOrPropertyWithValue("active", true);

		// disable
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.disableCustomer(DC_S_DET) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC S Det'");

		setCurrentUser(b2bAdminUser);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull()
							.hasFieldOrPropertyWithValue("active", true);

		// enable
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.enableCustomer("DC S MUC") )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC S MUC'");


		setCurrentUser(b2bAdminUser);
		customer = defaultB2BCustomerService.getUserForUID("DC S MUC");
		assertThat(customer).isNotNull()
							.hasFieldOrPropertyWithValue("active", false);
	}

	@Test
	public void shouldNotDisableCustomerAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.disableCustomer(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotDisableCustomerAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.disableCustomer(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotDisableCustomerWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.disableCustomer(StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotDisableCustomerWithEmptyCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.disableCustomer(StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotEnableCustomerAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.enableCustomer(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotEnableCustomerAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.enableCustomer(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotEnableCustomerWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.enableCustomer(StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotEnableCustomerWithEmptyCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.enableCustomer(StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldAddAndRemoveUserRoleAsB2BAdmin()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull();
		assertThat(customer.getGroups())
			.hasSize(2)
			.extracting("uid")
			.contains(DC_SALES_DETROIT)
			.doesNotContain(DC_SALES_NOTTINGHAM);

		defaultB2BUserFacade.addUserRole(DC_S_DET, DC_SALES_NOTTINGHAM);
		defaultB2BUserFacade.removeUserRole(DC_S_DET, DC_SALES_DETROIT);

		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull();
		assertThat(customer.getGroups())
			.hasSize(2)
			.extracting("uid")
			.contains(DC_SALES_NOTTINGHAM)
			.doesNotContain(DC_SALES_DETROIT);
	}

	@Test
	public void shouldNotAddOrRemoveUserRoleAsB2BCustomer()
	{
		B2BCustomerModel customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull();
		assertThat(customer.getGroups())
			.hasSize(2)
			.extracting("uid")
			.contains(DC_SALES_DETROIT)
			.doesNotContain(DC_SALES_NOTTINGHAM);

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.addUserRole(DC_S_DET, DC_SALES_NOTTINGHAM) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC S Det'");
		assertThatThrownBy( () -> defaultB2BUserFacade.removeUserRole(DC_S_DET, DC_SALES_DETROIT) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC S Det'");

		setCurrentUser(b2bAdminUser);
		customer = defaultB2BCustomerService.getUserForUID(DC_S_DET);
		assertThat(customer).isNotNull();
		assertThat(customer.getGroups())
			.hasSize(2)
			.extracting("uid")
			.contains(DC_SALES_DETROIT)
			.doesNotContain(DC_SALES_NOTTINGHAM);
	}

	@Test
	public void shouldNotAddUserRoleCustomerUidNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.addUserRole(null, "b2bmanagergroup") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddUserRoleCustomerUidNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.addUserRole(null, "b2bmanagergroup") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddUserRoleRoleUidNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.addUserRole(DC_S_DET, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddUserRoleRoleUidNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.addUserRole(DC_S_DET, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddUserRoleWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.addUserRole(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotAddUserRoleWithEmptyCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.addUserRole(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotRemoveUserRoleCustomerUidNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.removeUserRole(null, "b2bcustomergroup") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveUserRoleCustomerUidNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.removeUserRole(null, "b2bcustomergroup") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveUserRoleRoleUidNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.removeUserRole(DC_S_DET, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveUserRoleRoleUidNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.removeUserRole(DC_S_DET, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveUserRoleWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.removeUserRole(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotRemoveUserRoleWithEmptyCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.removeUserRole(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldGetPagedB2BUserGroupsForCustomerAsB2BAdmin()
	{
		final SearchPageData<B2BUserGroupData> searchPageData =
			defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("EUROPE_MANAGER_PERM_GROUP_DC", "UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC");

		final List<B2BUserGroupData> selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults()); // some customers are imported by system initialization
		assertThat(selectedB2BUserGroups).hasSize(1).extracting("uid").contains(EUROPE_MANAGER_PERM_GROUP_DC);
	}

	@Test
	public void shouldNotGetPagedB2BUserGroupsForCustomerAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC Sales DE Boss'");
	}

	@Test
	public void shouldNotGetPagedB2BUserGroupsForNullCustomerAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetPagedB2BUserGroupsForNullCustomerAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetPagedB2BUserGroupsForCustomerWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotGetPagedB2BUserGroupsForCustomerWithEmptyCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldAddB2BUserGroupToCustomerAndRemoveB2BUserGroupFromCustomerGroupsAsB2BAdmin()
	{
		SearchPageData<B2BUserGroupData> searchPageData =
			defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("EUROPE_MANAGER_PERM_GROUP_DC", "UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC");
		List<B2BUserGroupData> selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).isEmpty();

		// add
		defaultB2BUserFacade.addB2BUserGroupToCustomer(DC_S_HH, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC", "EUROPE_MANAGER_PERM_GROUP_DC");

		selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).hasSize(1).extracting("uid").contains(EUROPE_MANAGER_PERM_GROUP_DC);

		// remove
		defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(DC_S_HH, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC", "EUROPE_MANAGER_PERM_GROUP_DC");

		selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).isEmpty();
	}

	@Test
	public void shouldNotAddB2BUserGroupToCustomerOrRemoveB2BUserGroupFromCustomerGroupsAsB2BCustomer()
	{
		SearchPageData<B2BUserGroupData> searchPageData =
			defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("EUROPE_MANAGER_PERM_GROUP_DC", "UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC");
		List<B2BUserGroupData> selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).isEmpty();

		// add
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.addB2BUserGroupToCustomer(DC_S_HH, EUROPE_MANAGER_PERM_GROUP_DC) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("UserGroupModel with uid 'EUROPE_MANAGER_PERM_GROUP_DC' not found!");

		setCurrentUser(b2bAdminUser);
		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_S_HH);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("EUROPE_MANAGER_PERM_GROUP_DC", "UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC");

		selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).isEmpty();

		// remove
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(DC_SALES_DE_BOSS, EUROPE_MANAGER_PERM_GROUP_DC) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC Sales DE Boss'");

		setCurrentUser(b2bAdminUser);
		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC", "EUROPE_MANAGER_PERM_GROUP_DC");

		selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).hasSize(1).extracting("uid").contains(EUROPE_MANAGER_PERM_GROUP_DC);
	}

	@Test
	public void shouldNotAddB2BUserGroupToCustomerNullCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.addB2BUserGroupToCustomer(null, EUROPE_MANAGER_PERM_GROUP_DC) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddB2BUserGroupToCustomerNullCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.addB2BUserGroupToCustomer(null, EUROPE_MANAGER_PERM_GROUP_DC) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddB2BUserGroupToCustomerNullUserGroupUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.addB2BUserGroupToCustomer(DC_S_HH, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddB2BUserGroupToCustomerNullUserGroupUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.addB2BUserGroupToCustomer(DC_S_HH, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddB2BUserGroupToCustomerWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.addB2BUserGroupToCustomer(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotAddB2BUserGroupToCustomerWithEmptyCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.addB2BUserGroupToCustomer(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsNullCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(null, EUROPE_MANAGER_PERM_GROUP_DC) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsNullCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(null, EUROPE_MANAGER_PERM_GROUP_DC) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsNullUserGroupUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(DC_S_HH, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsNullUserGroupUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(DC_S_HH, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotRemoveB2BUserGroupFromCustomerGroupsWithEmptyCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.removeB2BUserGroupFromCustomerGroups(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldDeselectB2BUserGroupFromCustomerAsB2BAdmin()
	{
		SearchPageData<B2BUserGroupData> searchPageData =
			defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("EUROPE_MANAGER_PERM_GROUP_DC", "UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC");

		List<B2BUserGroupData> selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).hasSize(1).extracting("uid").contains(EUROPE_MANAGER_PERM_GROUP_DC);

		// deselect
		defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(DC_SALES_DE_BOSS, EUROPE_MANAGER_PERM_GROUP_DC);

		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC", "EUROPE_MANAGER_PERM_GROUP_DC");

		selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).isEmpty();
	}

	@Test
	public void shouldNotDeselectB2BUserGroupFromCustomerAsB2BCustomer()
	{
		SearchPageData<B2BUserGroupData> searchPageData =
			defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("EUROPE_MANAGER_PERM_GROUP_DC", "UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC");

		List<B2BUserGroupData> selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).hasSize(1).extracting("uid").contains(EUROPE_MANAGER_PERM_GROUP_DC);

		// deselect
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(DC_SALES_DE_BOSS, EUROPE_MANAGER_PERM_GROUP_DC) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC Sales DE Boss'");

		setCurrentUser(b2bAdminUser);
		searchPageData = defaultB2BUserFacade.getPagedB2BUserGroupsForCustomer(PAGEABLE_DATA_0_20_BY_UNIT_NAME, DC_SALES_DE_BOSS);
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(4)
			.extracting("uid")
			.contains("EUROPE_MANAGER_PERM_GROUP_DC", "UK_MANAGER_GROUP_PERMISSIONS_DC", "DC_CEO_PERMISSIONS", "US_MANAGER_PERM_GROUP_DC");

		selectedB2BUserGroups = getSelectedUserGroups(searchPageData.getResults());
		assertThat(selectedB2BUserGroups).hasSize(1).extracting("uid").contains(EUROPE_MANAGER_PERM_GROUP_DC);
	}

	@Test
	public void shouldNotDeselectB2BUserGroupFromCustomerNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(null, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotDeselectB2BUserGroupFromCustomerNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(null, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotDeselectB2BUserGroupFromCustomerWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotDeselectB2BUserGroupFromCustomerWithEmptyCustomerUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.deselectB2BUserGroupFromCustomer(StringUtils.EMPTY, StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldGetCustomerForUidAsB2BAdmin()
	{
		final CustomerData customer = defaultB2BUserFacade.getCustomerForUid(DC_SALES_DE_BOSS);
		assertThat(customer).isNotNull().hasFieldOrPropertyWithValue("uid", DC_SALES_DE_BOSS);
	}

	@Test
	public void shouldNotGetCustomerForUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getCustomerForUid(DC_SALES_DE_BOSS) )
			.isInstanceOf(UnknownIdentifierException.class)
			.hasMessage("Cannot find user with uid 'DC Sales DE Boss'");
	}

	@Test
	public void shouldNotGetCustomerForUidNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.getCustomerForUid(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetCustomerForUidNullAsB2Customer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getCustomerForUid(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetCustomerForUidWithEmptyCustomerUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BUserFacade.getCustomerForUid(StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}

	@Test
	public void shouldNotGetCustomerForUidWithEmptyCustomerUidAsB2Customer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BUserFacade.getCustomerForUid(StringUtils.EMPTY) )
			.isInstanceOf(UnknownIdentifierException.class);
	}
}
