/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static de.hybris.platform.commerceservices.constants.CommerceServicesConstants.ORG_UNIT_PATH_GENERATION_ENABLED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.company.B2BCommerceUnitService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitNodeData;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.commerceservices.model.OrgUnitModel;
import de.hybris.platform.commerceservices.organization.services.impl.OrgUnitHierarchyException;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.I18NService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.util.Config;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

/**
 * Integration tests for {@link DefaultB2BUnitFacade}.
 */
@IntegrationTest
public class DefaultB2BUnitFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	/**
	 * {@link PageableData} object, getting the <i>first result page</i> with <i>page size 10</i> and <i>sort by
	 * name</i>.
	 */
	private static final PageableData DEFAULT_PAGEABLE_DATA = createPageableData(0, 10, "byName");
	protected static final String B2B_ADMIN_USER = "DC Admin";
	protected static final String B2B_CUSTOMER_USER = "DC S HH";
	protected static final String B2B_ADMIN_USER_FOR_MC = "MC Admin";

	@Resource
	private DefaultB2BUnitFacade b2bUnitFacade;

	@Resource
	private B2BCommerceUnitService b2bCommerceUnitService;

	@Resource
	private UserService userService;

	@Resource
	private I18NService i18NService;

	protected User b2bAdminUserForMC;
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
		b2bAdminUserForMC = UserManager.getInstance().getUserByLogin(B2B_ADMIN_USER_FOR_MC);
		setCurrentUser(b2bAdminUser);
	}

	protected void setCurrentUser(final User user)
	{
		JaloSession.getCurrentSession().setUser(user);
	}

	@Test
	public void shouldGetPagedCustomersForUnitAsB2BAdmin()
	{
		final SearchPageData<CustomerData> searchPageData =
			b2bUnitFacade.getPagedCustomersForUnit(DEFAULT_PAGEABLE_DATA, "DC Sales Detroit");

		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(1)
			.extracting("name")
			.contains("Ed Whitacre");
	}

	@Test
	public void shouldNotGetPagedCustomersForUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedCustomersForUnit(DEFAULT_PAGEABLE_DATA, "DC Sales Detroit") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid DC Sales Detroit");
	}

	@Test
	public void shouldNotGetPagedCustomersForUnitFromDifferentUnitAsB2BAdmin()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedCustomersForUnit(DEFAULT_PAGEABLE_DATA, "MC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid MC");
	}

	@Test
	public void shouldNotGetPagedCustomersForUnitFromDifferentUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedCustomersForUnit(DEFAULT_PAGEABLE_DATA, "MC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid MC");
	}

	@Test
	public void shouldNotGetPagedCustomersForUnitNullUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.getPagedCustomersForUnit(DEFAULT_PAGEABLE_DATA, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetPagedCustomersForUnitNullUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedCustomersForUnit(DEFAULT_PAGEABLE_DATA, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldGetPagedAdministratorsForUnitAsB2BAdmin()
	{
		// this returns all customers of the unit with the 'selected' flag set for admins of the unit
		final SearchPageData<CustomerData> searchPageData =
			b2bUnitFacade.getPagedAdministratorsForUnit(DEFAULT_PAGEABLE_DATA, "DC");

		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(2)
			.extracting("name", "selected")
			.contains(tuple("Bernie Big Boss", false), tuple("Nimda Admin", true));
	}

	@Test
	public void shouldNotGetPagedAdministratorsForUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedAdministratorsForUnit(DEFAULT_PAGEABLE_DATA, "DC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid DC");
	}

	@Test
	public void shouldNotGetPagedAdministratorsForUnitFromDifferentUnitAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.getPagedAdministratorsForUnit(DEFAULT_PAGEABLE_DATA, "MC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid MC");
	}

	@Test
	public void shouldNotGetPagedAdministratorsForUnitFromDifferentUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedAdministratorsForUnit(DEFAULT_PAGEABLE_DATA, "MC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid MC");
	}

	@Test
	public void shouldNotGetPagedAdministratorsForUnitNullUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.getPagedAdministratorsForUnit(DEFAULT_PAGEABLE_DATA, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetPagedAdministratorsForUnitNullUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedAdministratorsForUnit(DEFAULT_PAGEABLE_DATA, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldGetPagedManagersForUnitAsB2BAdmin()
	{
		// this returns all customers of the unit with the 'selected' flag set for managers of the unit
		final SearchPageData<CustomerData> searchPageData = b2bUnitFacade.getPagedManagersForUnit(DEFAULT_PAGEABLE_DATA, "DC");

		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(2)
			.extracting("name", "selected")
			.contains(tuple("Bernie Big Boss", true), tuple("Nimda Admin", false));
	}

	@Test
	public void shouldNotGetPagedManagersForUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedManagersForUnit(DEFAULT_PAGEABLE_DATA, "DC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid DC");
	}

	@Test
	public void shouldNotGetPagedManagersForUnitFromDifferentUnitAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.getPagedManagersForUnit(DEFAULT_PAGEABLE_DATA, "MC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid MC");
	}

	@Test
	public void shouldNotGetPagedManagersForUnitFromDifferentUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedManagersForUnit(DEFAULT_PAGEABLE_DATA, "MC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("No unit found for uid MC");
	}

	@Test
	public void shouldNotGetPagedManagersForUnitNullUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.getPagedManagersForUnit(DEFAULT_PAGEABLE_DATA, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetPagedManagersForUnitNullUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getPagedManagersForUnit(DEFAULT_PAGEABLE_DATA, null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldDisableAndEnableUnitAsB2BAdmin()
	{
		b2bUnitFacade.disableUnit("DC");

		B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull().hasFieldOrPropertyWithValue("active", false);

		b2bUnitFacade.enableUnit("DC");

		unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull().hasFieldOrPropertyWithValue("active", true);
	}

	@Test
	public void shouldNotDisableOrEnableUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.disableUnit("DC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter unit can not be null");

		setCurrentUser(b2bAdminUser);
		B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull().hasFieldOrPropertyWithValue("active", true); // it is still enabled

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.enableUnit("DC") )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter unit can not be null");

		setCurrentUser(b2bAdminUser);
		unitData = b2bUnitFacade.getUnitForUid("DC Sales Munich");  // it is still disabled
		assertThat(unitData).isNotNull().hasFieldOrPropertyWithValue("active", false);
	}

	@Test
	public void shouldNotDisableUnitNullUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.disableUnit(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotDisableUnitNullUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.disableUnit(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotEnableUnitNullUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.enableUnit(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotEnableUnitNullUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.enableUnit(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldGetAllowedParentUnitsAsB2BAdmin()
	{
		final List<B2BUnitNodeData> allowedParentUnits = b2bUnitFacade.getAllowedParentUnits("DC Sales Detroit");
		assertThat(allowedParentUnits).isNotNull()
									  .hasSize(12)
									  .extracting("id")
									  .contains("DC Sales US", "DC Sales", "DC Sales Hamburg", "DC Sales London",
										  "DC Sales UK", "DC Production Center UK", "DC Sales DE", "DC",
										  "DC Sales Nottingham", "DC Test Center", "DC Sales Edinburgh", "DC Sales Hamburg");
	}

	@Test
	public void shouldNotGetAllowedParentUnitsAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.getAllowedParentUnits("DC Sales Detroit") )
			.isInstanceOf(IllegalArgumentException.class).hasMessage("Unit can not be null!");
	}

	@Test
	public void shouldGetAllActiveUnitsOfOrganizationAsB2BAdmin()
	{
		// this will get results based on the current session user (DC Admin)
		final List<String> activeUnits = b2bUnitFacade.getAllActiveUnitsOfOrganization();
		assertThat(activeUnits).isNotNull()
							   .hasSize(12)
							   .contains("DC Production Center UK", "DC", "DC Test Center", "DC Sales Edinburgh",
								   "DC Sales London", "DC Sales Nottingham", "DC Sales DE", "DC Sales US",
								   "DC Sales Detroit", "DC Sales", "DC Sales UK", "DC Sales Hamburg");
	}

	@Test
	public void shouldGetAllActiveUnitsOfOrganizationAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final List<String> activeUnits = b2bUnitFacade.getAllActiveUnitsOfOrganization();
		assertThat(activeUnits).isNotNull()
							   .hasSize(1)
							   .contains("DC Sales Hamburg");
	}

	@Test
	public void shouldGetPagedUserDataForUnitAsB2BAdmin()
	{
		SearchPageData<CustomerData> searchPageData = b2bUnitFacade.getPagedUserDataForUnit(DEFAULT_PAGEABLE_DATA, "DC");
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(2)
			.extracting("name", "selected")
			.contains(tuple("Bernie Big Boss", false), tuple("Nimda Admin", false));

		searchPageData = b2bUnitFacade.getPagedUserDataForUnit(DEFAULT_PAGEABLE_DATA, "DC Sales Detroit");
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults())
			.hasSize(1)
			.extracting("name", "selected")
			.contains(tuple("Ed Whitacre", false));
	}

	@Test
	public void shouldNotGetPagedUserDataForUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		SearchPageData<CustomerData> searchPageData = b2bUnitFacade.getPagedUserDataForUnit(DEFAULT_PAGEABLE_DATA, "DC");
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults()).isEmpty();

		searchPageData = b2bUnitFacade.getPagedUserDataForUnit(DEFAULT_PAGEABLE_DATA, "DC Sales Detroit");
		assertThat(searchPageData).isNotNull();
		assertThat(searchPageData.getResults()).isEmpty();
	}

	@Test
	public void shouldAddAddressToUnitAsB2BAdmin()
	{
		// check the number of addresses before adding a new one
		B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("1 DC Street");

		// add a new address
		final AddressData addressData = new AddressData();
		addressData.setLine1("MY_LINE1");
		b2bUnitFacade.addAddressToUnit(addressData, "DC");

		// check the number of addresses after adding a new one
		unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(2)
			.extracting("line1")
			.contains("1 DC Street", "MY_LINE1");
	}

	@Test
	public void testAddAddressToUnitAsB2BCustomer()
	{
		// check the number of addresses before adding a new one
		B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("1 DC Street");

		// add a new address
		setCurrentUser(b2bCustomerUser);

		final AddressData addressData = new AddressData();
		addressData.setLine1("MY_LINE1");
		assertThatThrownBy( () -> b2bUnitFacade.addAddressToUnit(addressData, "DC") )
			.isInstanceOf(NullPointerException.class);

		// check the number of addresses after adding a new one
		setCurrentUser(b2bAdminUser);

		unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("1 DC Street");
	}

	@Test
	public void shouldRemoveAddressFromUnitAsB2BAdmin()
	{
		// check the number of addresses before removal
		B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("1 DC Street");
		final AddressData address = unitData.getAddresses().get(0);

		// remove an address
		b2bUnitFacade.removeAddressFromUnit("DC", address.getId());

		// check the number of addresses after removal
		unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses()).isNullOrEmpty();
	}

	@Test
	public void shouldNotRemoveAddressFromUnitAsB2BCustomer()
	{
		// check the number of addresses before removal
		B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("1 DC Street");
		final AddressData address = unitData.getAddresses().get(0);

		// remove an address
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.removeAddressFromUnit("DC", address.getId()) )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter B2BUnit can not be null");

		// check the number of addresses after removal
		setCurrentUser(b2bAdminUser);
		unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("1 DC Street");
	}

	@Test
	public void shouldNotRemoveAddressFromUnitNullUnitUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.removeAddressFromUnit(null, "addressId") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveAddressFromUnitNullUnitUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.removeAddressFromUnit(null, "addressId") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveAddressFromUnitNullAddressIdAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.removeAddressFromUnit("DC", null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotRemoveAddressFromUnitNullAddressIdAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.removeAddressFromUnit("DC", null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldEditAddressOfUnitAsB2BAdmin()
	{
		// check the number of addresses before removal
		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("1 DC Street");

		// edit line1 for the first address of the unit
		final AddressData address = unitData.getAddresses().get(0);
		final String newLine1 = "New Line 1";
		assertThat(address.getLine1()).isNotEqualTo(newLine1);
		address.setLine1(newLine1);

		b2bUnitFacade.editAddressOfUnit(address, "DC");

		// verify the address has been updated
		final AddressModel updatedAddress =
			b2bCommerceUnitService.getAddressForCode(b2bCommerceUnitService.getUnitForUid("DC"), address.getId());
		assertThat(updatedAddress).isNotNull()
								  .hasFieldOrPropertyWithValue("line1", newLine1);
	}

	@Test
	public void shouldNotEditAddressOfUnitAsB2BCustomer()
	{
		// check the number of addresses before removal
		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid("DC");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("1 DC Street");

		// edit line1 for the first address of the unit
		setCurrentUser(b2bCustomerUser);

		final AddressData address = unitData.getAddresses().get(0);
		final String newLine1 = "New Line 1";
		assertThat(address.getLine1()).isNotEqualTo(newLine1);
		address.setLine1(newLine1);

		assertThatThrownBy( () -> b2bUnitFacade.editAddressOfUnit(address, "DC") )
			.isInstanceOf(NullPointerException.class);

		// verify the address has not been updated
		setCurrentUser(b2bAdminUser);
		final AddressModel updatedAddress =
			b2bCommerceUnitService.getAddressForCode(b2bCommerceUnitService.getUnitForUid("DC"), address.getId());
		assertThat(updatedAddress).isNotNull()
								  .hasFieldOrPropertyWithValue("line1", "1 DC Street");
	}

	@Test
	public void shouldEditAddressOfUnitFromDifferentUnitAsB2BAdmin()
	{
		// check the number of addresses before removal
		setCurrentUser(b2bAdminUserForMC);
		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid("MC Sales Hamburg");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("7 MC Sales Hamburg Street");

		// edit line1 for the first address of the unit
		final AddressData address = unitData.getAddresses().get(0);
		final String newLine1 = "New Line 1";
		assertThat(address.getLine1()).isNotEqualTo(newLine1);
		address.setLine1(newLine1);

		setCurrentUser(b2bAdminUser);
		assertThatThrownBy( () -> b2bUnitFacade.editAddressOfUnit(address, "MC Sales Hamburg") )
			.isInstanceOf(NullPointerException.class);

		// verify the address has been updated
		setCurrentUser(b2bAdminUserForMC);
		final AddressModel updatedAddress =
			b2bCommerceUnitService.getAddressForCode(b2bCommerceUnitService.getUnitForUid("MC Sales Hamburg"), address.getId());
		assertThat(updatedAddress).isNotNull()
								  .hasFieldOrPropertyWithValue("line1", "7 MC Sales Hamburg Street");
	}

	@Test
	public void shouldNotEditAddressOfUnitFromDifferentUnitAsB2BCustomer()
	{
		// check the number of addresses before removal
		setCurrentUser(b2bAdminUserForMC);
		final B2BUnitData unitData = b2bUnitFacade.getUnitForUid("MC Sales Hamburg");
		assertThat(unitData).isNotNull();
		assertThat(unitData.getAddresses())
			.hasSize(1)
			.extracting("line1")
			.contains("7 MC Sales Hamburg Street");

		// edit line1 for the first address of the unit
		final AddressData address = unitData.getAddresses().get(0);
		final String newLine1 = "New Line 1";
		assertThat(address.getLine1()).isNotEqualTo(newLine1);
		address.setLine1(newLine1);

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.editAddressOfUnit(address, "MC Sales Hamburg") )
			.isInstanceOf(NullPointerException.class);

		// verify the address has been updated
		setCurrentUser(b2bAdminUserForMC);
		final AddressModel updatedAddress =
			b2bCommerceUnitService.getAddressForCode(b2bCommerceUnitService.getUnitForUid("MC Sales Hamburg"), address.getId());
		assertThat(updatedAddress).isNotNull()
								  .hasFieldOrPropertyWithValue("line1", "7 MC Sales Hamburg Street");
	}

	@Test
	public void shouldNotEditAddressOfUnitNullAddressAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.editAddressOfUnit(null, "DC") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotEditAddressOfUnitNullAddressAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.editAddressOfUnit(null, "DC") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotEditAddressOfUnitNullUnitUidAsB2BAdmin()
	{
		assertThatThrownBy( () -> b2bUnitFacade.editAddressOfUnit(new AddressData(), null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotEditAddressOfUnitNullUnitUidAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> b2bUnitFacade.editAddressOfUnit(new AddressData(), null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldUpdateOrCreateBusinessUnitAsB2BAdmin() throws ImpExException
	{
		importCsv("/b2bcommercefacades/test/b2bUnitTestData_addPath.impex", "UTF-8");
		final B2BUnitData parentUnit = b2bUnitFacade.getUnitForUid("DC");

		// create a new business unit
		final B2BUnitData unit = new B2BUnitData();
		final String newUnitId = "DC New Unit";
		unit.setUid(newUnitId);
		unit.setName("New Unit");
		unit.setUnit(parentUnit);
		b2bUnitFacade.updateOrCreateBusinessUnit(unit.getUid(), unit);

		// assert unit has been created
		final B2BUnitData newUnit = b2bUnitFacade.getUnitForUid(newUnitId);
		assertThat(newUnit).isNotNull()
						   .hasFieldOrPropertyWithValue("name", "New Unit");
		assertThat(getUnitForUid(newUnitId)).hasFieldOrPropertyWithValue("path", "/DC/" + newUnitId);

		// update the unit
		unit.setName("Updated Unit");
		b2bUnitFacade.updateOrCreateBusinessUnit(unit.getUid(), unit);

		// assert unit has been created
		final B2BUnitData updatedUnit = b2bUnitFacade.getUnitForUid(newUnitId);
		assertThat(updatedUnit).isNotNull()
							   .hasFieldOrPropertyWithValue("name", "Updated Unit");
	}

	@Test
	public void shouldNotUpdateOrCreateBusinessUnitAsB2BCustomer() throws ImpExException
	{
		importCsv("/b2bcommercefacades/test/b2bUnitTestData_addPath.impex", "UTF-8");
		final B2BUnitData parentUnit = b2bUnitFacade.getUnitForUid("DC");
		assertThat(parentUnit).isNotNull();

		// create a new business unit
		setCurrentUser(b2bCustomerUser);

		final B2BUnitData unit = new B2BUnitData();
		final String newUnitId = "DC New Unit";
		unit.setUid(newUnitId);
		unit.setName("New Unit");
		unit.setUnit(parentUnit);
		assertThatThrownBy( () -> b2bUnitFacade.updateOrCreateBusinessUnit(unit.getUid(), unit) )
			.isInstanceOf(OrgUnitHierarchyException.class)
			.hasMessage("Update of unit paths failed");

		// assert unit has not been created
		setCurrentUser(b2bAdminUser);
		final B2BUnitData newUnit = b2bUnitFacade.getUnitForUid(newUnitId);
		assertThat(newUnit).isNull();

		// update the unit
		final B2BUnitData oldUnit = b2bUnitFacade.getUnitForUid("DC");
		setCurrentUser(b2bCustomerUser);

		unit.setName("Updated Unit");
		assertThatThrownBy( () -> b2bUnitFacade.updateOrCreateBusinessUnit(oldUnit.getUid(), oldUnit) )
			.isInstanceOf(OrgUnitHierarchyException.class)
			.hasMessage("Update of unit paths failed");

		// assert unit has not been updated
		setCurrentUser(b2bAdminUser);
		final B2BUnitData updatedUnit = b2bUnitFacade.getUnitForUid(oldUnit.getUid());
		assertThat(updatedUnit).isNotNull()
							   .hasFieldOrPropertyWithValue("name", null);  // there is no name in impex file.
	}

	@Test
	public void testUpdateOrCreateBusinessUnitWithSkipPathGenerationAsB2BAdmin()
	{
		Config.setParameter(ORG_UNIT_PATH_GENERATION_ENABLED, "false");
		final B2BUnitData parentUnit = b2bUnitFacade.getUnitForUid("DC");

		// create a new business unit
		final B2BUnitData unit = new B2BUnitData();
		final String newUnitId = "DC New Unit";
		unit.setUid(newUnitId);
		unit.setName("New Unit");
		unit.setUnit(parentUnit);
		b2bUnitFacade.updateOrCreateBusinessUnit(unit.getUid(), unit);

		// assert unit has been created
		final B2BUnitData newUnit = b2bUnitFacade.getUnitForUid(newUnitId);
		assertThat(newUnit).isNotNull()
						   .hasFieldOrPropertyWithValue("name", "New Unit");
		assertThat(getUnitForUid(newUnitId)).hasFieldOrPropertyWithValue("path", null);
	}

	@Test
	public void shouldGetParentUnitAsB2BAdmin()
	{
		final B2BUnitData parentUnit = b2bUnitFacade.getParentUnit();
		assertThat(parentUnit).isNotNull()
							  .hasFieldOrPropertyWithValue("uid", "DC");
	}


	@Test
	public void shouldGetParentUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final B2BUnitData parentUnit = b2bUnitFacade.getParentUnit();
		assertThat(parentUnit).isNotNull()
							  .hasFieldOrPropertyWithValue("uid", "DC Sales Hamburg");
	}

	@Test
	public void shouldGetParentUniNodeAsB2BAdmin()
	{
		final B2BUnitNodeData parentUnit = b2bUnitFacade.getParentUnitNode();
		assertThat(parentUnit).isNotNull()
							  .hasFieldOrPropertyWithValue("id", "DC");
	}

	@Test
	public void shouldGetParentUniNodeAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final B2BUnitNodeData parentUnit = b2bUnitFacade.getParentUnitNode();
		assertThat(parentUnit).isNotNull()
							  .hasFieldOrPropertyWithValue("id", "DC Sales Hamburg");
	}

	protected OrgUnitModel getUnitForUid(final String uid)
	{
		final OrgUnitModel unit = userService.getUserGroupForUID(uid, OrgUnitModel.class);
		assertThat(unit).isNotNull();
		return unit;
	}
}
