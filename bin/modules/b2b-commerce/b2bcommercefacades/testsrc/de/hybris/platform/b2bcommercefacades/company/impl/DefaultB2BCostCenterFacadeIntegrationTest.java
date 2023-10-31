/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.dao.B2BCostCenterDao;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2bcommercefacades.company.data.B2BCostCenterData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BSelectionData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BCostCenterFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	protected static final String B2B_ADMIN_USER_FOR_MC = "MC Admin";
	protected static final String B2B_ADMIN_USER = "DC Admin";
	protected static final String B2B_CUSTOMER_USER = "DC S HH";

	private static final String ORIGINAL_CODE = "DC 2.5";
	private static final String NEW_CODE = "sampleB2BCostCenter";
	private static final String NEW_NAME = "sample B2B Cost Center";
	private static final String NEW_ISO_CODE = "EUR";
	private static final String NEW_UNIT_UID = "DC Sales Hamburg";
	private static final String NEW_BUDGET = "DC BUDGET EUR 5K";
	private static final String ORIGINAL_BUDGET = "DC BUDGET USD 1M";

	@Resource
	private DefaultB2BCostCenterFacade defaultB2BCostCenterFacade;

	@Resource
	private B2BCostCenterDao b2bCostCenterDao;

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
	public void shouldGetCostCentersAsB2BAdmin()
	{
		final List<? extends B2BCostCenterData> costCenters = defaultB2BCostCenterFacade.getCostCenters();
		assertThat(costCenters).isNotNull()
							   .hasSize(9)
							   .extracting("code")
							   .contains("DC 0", "DC 1.1", "DC 1.2", "DC 2.1", "DC 2.2", "DC 2.3", "DC 2.4", "DC 2.5", "DC 2.6");
	}

	@Test
	public void shouldGetCostCentersAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final List<? extends B2BCostCenterData> costCenters = defaultB2BCostCenterFacade.getCostCenters();
		assertThat(costCenters).isNotNull().hasSize(1).extracting("code").contains("DC 2.4");
	}

	@Test
	public void shouldGetActiveCostCentersAsB2BAdmin()
	{
		final List<? extends B2BCostCenterData> costCenters = defaultB2BCostCenterFacade.getActiveCostCenters();
		assertThat(costCenters).isNotNull()
							   .hasSize(8)
							   .extracting("code")
							   .contains("DC 0", "DC 1.1", "DC 1.2", "DC 2.1", "DC 2.2", "DC 2.4", "DC 2.5", "DC 2.6");
	}

	@Test
	public void shouldGetActiveCostCentersAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final List<? extends B2BCostCenterData> costCenters = defaultB2BCostCenterFacade.getActiveCostCenters();
		assertThat(costCenters).isNotNull()
							   .hasSize(1)
							   .extracting("code")
							   .contains("DC 2.4");
	}

	@Test
	public void shouldGetCostCenterDataForCodeAsB2BAdmin()
	{
		final B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull();
		assertThat(costCenter.getB2bBudgetData())
			.isNotNull()
			.hasSize(1)
			.extracting("code")
			.contains(ORIGINAL_BUDGET);
	}

	@Test
	public void shouldGetCostCenterDataForCodeAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetCostCenterDataForCodeForNotExistingCodeAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.getCostCenterDataForCode("Not exist") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetCostCenterDataForCodeForNotExistingCodeAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.getCostCenterDataForCode("Not exist") )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetCostCenterDataForCodeFromOtherUnitAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.getCostCenterDataForCode("MC 0") )
			.isInstanceOf(IllegalArgumentException.class);

		setCurrentUser(b2bAdminUserForMC);
		assertThat(defaultB2BCostCenterFacade.getCostCenterDataForCode("MC 0")).isNotNull();
	}

	@Test
	public void shouldNotGetCostCenterDataForCodeFromOtherUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.getCostCenterDataForCode("MC 0") )
			.isInstanceOf(IllegalArgumentException.class);

		setCurrentUser(b2bAdminUserForMC);
		assertThat(defaultB2BCostCenterFacade.getCostCenterDataForCode("MC 0")).isNotNull();
	}

	@Test
	public void shouldSearchAsB2BAdmin()
	{
		final SearchPageData<B2BCostCenterData> searchResult =
			defaultB2BCostCenterFacade.search(null,	createPageableData(0, 20, "byUnitName"));
		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getResults())
			.isNotEmpty()
			.hasSize(9)
			.extracting("code")
			.contains("DC 0", "DC 1.1", "DC 1.2", "DC 2.1", "DC 2.2", "DC 2.3", "DC 2.4", "DC 2.5", "DC 2.6");
	}

	@Test
	public void shouldSearchAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final SearchPageData<B2BCostCenterData> searchResult =
			defaultB2BCostCenterFacade.search(null,	createPageableData(0, 20, "byUnitName"));
		assertThat(searchResult).isNotNull();
		assertThat(searchResult.getResults())
			.hasSize(1)
			.extracting("code")
			.contains("DC 2.4");
	}

	@Test
	public void shouldUpdateCostCenterAsB2BAdmin()
	{
		// check property values before update
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "DC 2.5")
							  .hasFieldOrPropertyWithValue("name", "DC 2.5")
							  .hasFieldOrPropertyWithValue("currency.isocode", "USD")
							  .hasFieldOrPropertyWithValue("unit.uid", "DC Sales Detroit");

		// update
		defaultB2BCostCenterFacade.updateCostCenter(
				createB2BCostCenterData(ORIGINAL_CODE, ORIGINAL_CODE, NEW_NAME, NEW_ISO_CODE, createUnit(NEW_UNIT_UID)));

		// check property values after update
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("name", NEW_NAME)
							  .hasFieldOrPropertyWithValue("currency.isocode", NEW_ISO_CODE)
							  .hasFieldOrPropertyWithValue("unit.uid", NEW_UNIT_UID);
	}

	@Test
	public void shouldNotUpdateCostCenterAsB2BCustomer()
	{
		// check property values before update
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "DC 2.5")
							  .hasFieldOrPropertyWithValue("name", "DC 2.5")
							  .hasFieldOrPropertyWithValue("currency.isocode", "USD")
							  .hasFieldOrPropertyWithValue("unit.uid", "DC Sales Detroit");

		// update
		setCurrentUser(b2bCustomerUser);
		defaultB2BCostCenterFacade.updateCostCenter(
			createB2BCostCenterData(ORIGINAL_CODE, ORIGINAL_CODE, NEW_NAME, NEW_ISO_CODE, createUnit(NEW_UNIT_UID)));

		// check property values after update
		setCurrentUser(b2bAdminUser);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "DC 2.5")
							  .hasFieldOrPropertyWithValue("name", "DC 2.5")
							  .hasFieldOrPropertyWithValue("currency.isocode", "USD")
							  .hasFieldOrPropertyWithValue("unit.uid", "DC Sales Detroit");
	}

	@Test
	public void shouldNotUpdateCostCenterFromOtherUnitAsB2BAdmin()
	{
		// check property values before update
		setCurrentUser(b2bAdminUserForMC);
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode("MC 0");
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "MC 0")
							  .hasFieldOrPropertyWithValue("name", "MC 0")
							  .hasFieldOrPropertyWithValue("currency.isocode", "EUR");

		// update
		setCurrentUser(b2bAdminUser);
		defaultB2BCostCenterFacade.updateCostCenter(createB2BCostCenterData("MC 0", "MC 0", "NEW MC", "USD", createUnit("MC")));

		// check property values after update
		setCurrentUser(b2bAdminUserForMC);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode("MC 0");
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "MC 0")
							  .hasFieldOrPropertyWithValue("name", "MC 0")
							  .hasFieldOrPropertyWithValue("currency.isocode", "EUR");
	}

	@Test
	public void shouldNotUpdateCostCenterFromOtherUnitAsB2BCustomer()
	{
		// check property values before update
		setCurrentUser(b2bAdminUserForMC);
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode("MC 0");
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "MC 0")
							  .hasFieldOrPropertyWithValue("name", "MC 0")
							  .hasFieldOrPropertyWithValue("currency.isocode", "EUR");

		// update
		setCurrentUser(b2bCustomerUser);
		defaultB2BCostCenterFacade.updateCostCenter(createB2BCostCenterData("MC 0", "MC 0", "NEW MC", "USD", createUnit("MC")));

		// check property values after update
		setCurrentUser(b2bAdminUserForMC);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode("MC 0");
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("code", "MC 0")
							  .hasFieldOrPropertyWithValue("name", "MC 0")
							  .hasFieldOrPropertyWithValue("currency.isocode", "EUR");
	}

	@Test
	public void shouldNotUpdateCostCenterWhenCodeIsNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.updateCostCenter(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotUpdateCostCenterWhenCodeIsNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.updateCostCenter(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldAddCostCenterAsB2BAdmin()
	{
		// check before adding
		B2BCostCenterModel costCenter = b2bCostCenterDao.findByCode(NEW_CODE);
		assertThat(costCenter).isNull();

		// add
		defaultB2BCostCenterFacade
				.addCostCenter(createB2BCostCenterData(NEW_CODE, NEW_CODE, NEW_NAME, NEW_ISO_CODE, createUnit(NEW_UNIT_UID)));

		// check after adding
		costCenter = b2bCostCenterDao.findByCode(NEW_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("name", NEW_NAME)
							  .hasFieldOrPropertyWithValue("currency.isocode", NEW_ISO_CODE)
							  .hasFieldOrPropertyWithValue("unit.uid", NEW_UNIT_UID);
	}

	@Test
	public void shouldNotAddCostCenterWhenCodeIsNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.addCostCenter(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotAddCostCenterWhenCodeIsNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.addCostCenter(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldEnableDisableCostCenterAsB2BAdmin()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("active", true);

		// disable
		defaultB2BCostCenterFacade.enableDisableCostCenter(ORIGINAL_CODE, false);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("active", false);

		// enable
		defaultB2BCostCenterFacade.enableDisableCostCenter(ORIGINAL_CODE, true);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("active", true);
	}

	@Test
	public void shouldNotEnableOrDisableCostCenterAsB2BCustomer()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("active", true);

		// disable
		setCurrentUser(b2bCustomerUser);
		defaultB2BCostCenterFacade.enableDisableCostCenter(ORIGINAL_CODE, false);

		setCurrentUser(b2bAdminUser);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("active", true);

		// enable
		setCurrentUser(b2bCustomerUser);
		defaultB2BCostCenterFacade.enableDisableCostCenter(ORIGINAL_CODE, true);

		setCurrentUser(b2bAdminUser);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull()
							  .hasFieldOrPropertyWithValue("active", true);
	}

	@Test
	public void shouldSelectBudgetForCostCenterAsB2BAdmin()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull();
		assertThat(costCenter.getB2bBudgetData())
			.isNotNull()
			.hasSize(1)
			.extracting("code")
			.contains(ORIGINAL_BUDGET);

		final B2BSelectionData selectionData = defaultB2BCostCenterFacade.selectBudgetForCostCenter(ORIGINAL_CODE, NEW_BUDGET);
		assertThat(selectionData).isNotNull()
								 .hasFieldOrPropertyWithValue("id", NEW_BUDGET)
								 .hasFieldOrPropertyWithValue("selected", true);

		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter.getB2bBudgetData())
			.isNotNull()
			.hasSize(2)
			.extracting("code")
			.contains(ORIGINAL_BUDGET, NEW_BUDGET);
	}

	@Test
	public void shouldSelectBudgetForCostCenterAsB2BCustomer()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull();
		assertThat(costCenter.getB2bBudgetData())
			.isNotNull()
			.hasSize(1)
			.extracting("code")
			.contains(ORIGINAL_BUDGET);

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.selectBudgetForCostCenter(ORIGINAL_CODE, NEW_BUDGET) )
			.isInstanceOf(NullPointerException.class);

		setCurrentUser(b2bAdminUser);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter.getB2bBudgetData())
			.isNotNull()
			.hasSize(1)
			.extracting("code")
			.contains(ORIGINAL_BUDGET);
	}

	@Test
	public void shouldDeSelectBudgetForCostCenterAsB2BCustomer()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull();
		assertThat(costCenter.getB2bBudgetData())
			.isNotNull()
			.hasSize(1)
			.extracting("code")
			.contains(ORIGINAL_BUDGET);

		setCurrentUser(b2bCustomerUser);
		assertThatThrownBy( () -> defaultB2BCostCenterFacade.deSelectBudgetForCostCenter(ORIGINAL_CODE, ORIGINAL_BUDGET) )
			.isInstanceOf(NullPointerException.class);

		setCurrentUser(b2bAdminUser);
		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter.getB2bBudgetData())
			.isNotNull()
			.hasSize(1)
			.extracting("code")
			.contains(ORIGINAL_BUDGET);
	}

	@Test
	public void shouldDeSelectBudgetForCostCenterAsB2BAdmin()
	{
		B2BCostCenterData costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter).isNotNull();
		assertThat(costCenter.getB2bBudgetData())
			.isNotNull()
			.hasSize(1)
			.extracting("code")
			.contains(ORIGINAL_BUDGET);

		final B2BSelectionData selectionData = defaultB2BCostCenterFacade.deSelectBudgetForCostCenter(ORIGINAL_CODE, ORIGINAL_BUDGET);
		assertThat(selectionData).isNotNull()
								 .hasFieldOrPropertyWithValue("id", ORIGINAL_BUDGET)
								 .hasFieldOrPropertyWithValue("selected", false);

		costCenter = defaultB2BCostCenterFacade.getCostCenterDataForCode(ORIGINAL_CODE);
		assertThat(costCenter.getB2bBudgetData()).isNotNull().isEmpty();
	}

	private B2BUnitData createUnit(final String newUnitUid)
	{
		final B2BUnitData unit = new B2BUnitData();
		unit.setUid(newUnitUid);
		return unit;
	}

	protected B2BCostCenterData createB2BCostCenterData(final String originalCode, final String code, final String name,
			final String isoCode, final B2BUnitData unit)
	{
		final B2BCostCenterData b2BCostCenterData = new B2BCostCenterData();
		b2BCostCenterData.setOriginalCode(originalCode);
		b2BCostCenterData.setCode(code);
		b2BCostCenterData.setName(name);
		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode(isoCode);
		b2BCostCenterData.setCurrency(currencyData);
		b2BCostCenterData.setUnit(unit);

		return b2BCostCenterData;
	}
}
