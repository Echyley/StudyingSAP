/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2bcommercefacades.company.impl;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.services.B2BBudgetService;
import de.hybris.platform.b2bcommercefacades.company.data.B2BBudgetData;
import de.hybris.platform.b2bcommercefacades.company.data.B2BUnitData;
import de.hybris.platform.b2bcommercefacades.search.data.BudgetSearchStateData;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commercefacades.storesession.data.CurrencyData;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class DefaultB2BBudgetFacadeIntegrationTest extends ServicelayerTransactionalTest
{
	protected static final String B2B_ADMIN_USER = "DC Admin";
	protected static final String B2B_CUSTOMER_USER = "DC S HH";

	private static final String BUDGET_CODE = "TEST BUDGET";
	private static final String NEW_BUDGET_CODE = "NEW TEST BUDGET";
	private static final String DISABLED_BUDGET_CODE = "DISABLED TEST BUDGET";
	private static final String TEST_COST_CENTER = "DC 2.6";
	private static final String TEST_UNIT_CODE = "DC Test Center";

	private static final String MC_BUDGET_CODE = "MC BUDGET EUR 1M";

	@Resource
	private DefaultB2BBudgetFacade defaultB2BBudgetFacade;

	@Resource
	private B2BBudgetService b2BBudgetService;

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
	public void shouldGetBudgetDataForCodeAsB2BAdmin()
	{
		final B2BBudgetData budgetData = defaultB2BBudgetFacade.getBudgetDataForCode(BUDGET_CODE);
		assertThat(budgetData).isNotNull();
		assertThat(budgetData.getCostCenterNames()).hasSize(1);
	}

	@Test
	public void shouldNotGetBudgetDataForCodeAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);

		final B2BBudgetData budgetData = defaultB2BBudgetFacade.getBudgetDataForCode(BUDGET_CODE);
		assertThat(budgetData).isNull();
	}

	@Test
	public void shouldNotGetBudgetDataForCodeNullAsB2BAdmin()
	{
		assertThatThrownBy( () -> defaultB2BBudgetFacade.getBudgetDataForCode(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetBudgetDataForCodeNullAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);

		assertThatThrownBy( () -> defaultB2BBudgetFacade.getBudgetDataForCode(null) )
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void shouldNotGetBudgetDataForCodeForDifferentUnitAsB2BAdmin()
	{
		final B2BBudgetData budgetData = defaultB2BBudgetFacade.getBudgetDataForCode(MC_BUDGET_CODE);
		assertThat(budgetData).isNull();
	}

	@Test
	public void shouldNotGetBudgetDataForCodeForDifferentUnitAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final B2BBudgetData budgetData = defaultB2BBudgetFacade.getBudgetDataForCode(MC_BUDGET_CODE);
		assertThat(budgetData).isNull();
	}

	@Test
	public void shouldUpdateBudgetAsB2BAdmin()
	{
		final B2BBudgetData b2bBudgetData = createBudgetData(BUDGET_CODE, BUDGET_CODE);

		defaultB2BBudgetFacade.updateBudget(b2bBudgetData);

		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(BUDGET_CODE);
		validateBudgetModel(b2bBudgetData, b2BBudgetModel);
	}

	@Test
	public void shouldNotUpdateBudgetAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final B2BBudgetData b2bBudgetData = createBudgetData(BUDGET_CODE, BUDGET_CODE);
		defaultB2BBudgetFacade.updateBudget(b2bBudgetData);

		setCurrentUser(b2bAdminUser);
		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(BUDGET_CODE);
		assertThat(b2BBudgetModel.getBudget().stripTrailingZeros()).isNotEqualTo(b2bBudgetData.getBudget().stripTrailingZeros());
	}

	@Test
	public void shouldAddBudgetAsB2BAdmin()
	{
		final B2BBudgetData b2bBudgetData = createBudgetData(NEW_BUDGET_CODE, NEW_BUDGET_CODE);

		defaultB2BBudgetFacade.addBudget(b2bBudgetData);

		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(NEW_BUDGET_CODE);
		validateBudgetModel(b2bBudgetData, b2BBudgetModel);
	}

	@Test
	public void shouldNotAddBudgetAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final B2BBudgetData b2bBudgetData = createBudgetData("FOO BUDGET", "FOO BUDGET");

		assertThatThrownBy( () -> defaultB2BBudgetFacade.addBudget(b2bBudgetData) )
			.isInstanceOf(ModelSavingException.class);
	}

	@Test
	public void shouldEnableBudgetAsB2BAdmin()
	{
		defaultB2BBudgetFacade.enableDisableBudget(DISABLED_BUDGET_CODE, true);

		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(DISABLED_BUDGET_CODE);
		assertThat(b2BBudgetModel).isNotNull()
								  .hasFieldOrPropertyWithValue("active", true);
	}

	@Test
	public void shouldNotEnableBudgetAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		defaultB2BBudgetFacade.enableDisableBudget(DISABLED_BUDGET_CODE, true);

		setCurrentUser(b2bAdminUser);
		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(DISABLED_BUDGET_CODE);
		assertThat(b2BBudgetModel).isNotNull()
								  .hasFieldOrPropertyWithValue("active", false);
	}

	@Test
	public void shouldDisableBudgetAsB2BAdmin()
	{
		defaultB2BBudgetFacade.enableDisableBudget(BUDGET_CODE, false);

		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(BUDGET_CODE);
		assertThat(b2BBudgetModel).isNotNull()
								  .hasFieldOrPropertyWithValue("active", false);
	}

	@Test
	public void shouldNotDisableBudgetAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		defaultB2BBudgetFacade.enableDisableBudget(BUDGET_CODE, false);

		setCurrentUser(b2bAdminUser);
		final B2BBudgetModel b2BBudgetModel = b2BBudgetService.getB2BBudgetForCode(BUDGET_CODE);
		assertThat(b2BBudgetModel).isNotNull()
								  .hasFieldOrPropertyWithValue("active", true);
	}

	@Test
	public void shouldSearchAsB2BAdmin()
	{
		final PageableData pageableData = createPageableData(1, 3, "byName");
		final SearchPageData<B2BBudgetData> budgets = defaultB2BBudgetFacade.search(null, pageableData);
		assertThat(budgets).isNotNull();
		assertThat(budgets.getResults()).hasSize(3)
										.extracting("code")
										.contains("DC BUDGET USD 1M", "DISABLED TEST BUDGET", "TEST BUDGET");
		assertThat(budgets.getPagination().getTotalNumberOfResults()).isEqualTo(6);
	}

	@Test
	public void shouldNotSearchAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final PageableData pageableData = createPageableData(1, 3, "byName");
		final SearchPageData<B2BBudgetData> budgets = defaultB2BBudgetFacade.search(null, pageableData);
		assertThat(budgets).isNotNull();
		assertThat(budgets.getResults()).isNullOrEmpty();
	}

	@Test
	public void shouldSearchCostCenterAsB2BAdmin()
	{
		final BudgetSearchStateData searchState = new BudgetSearchStateData();
		searchState.setCostCenterCode(TEST_COST_CENTER);
		final PageableData pageableData = createPageableData(1, 2, "byName");
		final SearchPageData<B2BBudgetData> budgets = defaultB2BBudgetFacade.search(searchState, pageableData);
		assertThat(budgets).isNotNull();
		assertThat(budgets.getResults()).hasSize(2)
										.extracting("code")
										.contains("DC BUDGET GBP 1M", "DC BUDGET USD 1M");
		assertThat(budgets.getPagination().getTotalNumberOfResults()).isEqualTo(6);
	}

	@Test
	public void shouldNotSearchCostCenterAsB2BCustomer()
	{
		setCurrentUser(b2bCustomerUser);
		final BudgetSearchStateData searchState = new BudgetSearchStateData();
		searchState.setCostCenterCode(TEST_COST_CENTER);
		final PageableData pageableData = createPageableData(1, 2, "byName");
		assertThatThrownBy( () -> defaultB2BBudgetFacade.search(searchState, pageableData) )
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Parameter source cannot be null.");
	}

	protected B2BBudgetData createBudgetData(final String code, final String name)
	{
		final B2BBudgetData b2bBudgetData = new B2BBudgetData();
		b2bBudgetData.setOriginalCode(code);
		b2bBudgetData.setCode(code);
		b2bBudgetData.setName(name);
		b2bBudgetData.setBudget(BigDecimal.TEN);
		final B2BUnitData b2bUnitData = new B2BUnitData();
		b2bUnitData.setUid(TEST_UNIT_CODE);
		b2bUnitData.setActive(true);
		b2bBudgetData.setUnit(b2bUnitData);
		final CurrencyData currencyData = new CurrencyData();
		currencyData.setIsocode("USD");
		b2bBudgetData.setCurrency(currencyData);
		final Date date = new Date();
		b2bBudgetData.setStartDate(date);
		b2bBudgetData.setEndDate(date);
		b2bBudgetData.setActive(true);

		return b2bBudgetData;
	}

	protected void validateBudgetModel(final B2BBudgetData b2bBudgetData, final B2BBudgetModel b2bBudgetModel)
	{
		assertThat(b2bBudgetModel).isNotNull()
								  .hasFieldOrPropertyWithValue("code", b2bBudgetData.getCode())
								  .hasFieldOrPropertyWithValue("name", b2bBudgetData.getName())
								  .hasFieldOrPropertyWithValue("currency.isocode", b2bBudgetData.getCurrency().getIsocode());

		assertThat(b2bBudgetModel.getBudget().stripTrailingZeros()).isEqualTo(b2bBudgetData.getBudget().stripTrailingZeros());

		assertThat(b2bBudgetModel.getDateRange()).hasFieldOrPropertyWithValue("start", b2bBudgetData.getStartDate())
												 .hasFieldOrPropertyWithValue("end", b2bBudgetData.getEndDate());
	}
}
