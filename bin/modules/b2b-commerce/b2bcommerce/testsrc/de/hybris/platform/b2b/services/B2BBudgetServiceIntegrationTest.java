/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.services;

import static de.hybris.platform.b2b.util.B2BCommerceTestUtils.createPageableData;
import static org.assertj.core.api.Assertions.assertThat;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.company.B2BCommerceCostCenterService;
import de.hybris.platform.b2b.model.B2BBudgetModel;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.model.B2BCustomerModel;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTransactionalTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;

@IntegrationTest
public class B2BBudgetServiceIntegrationTest extends ServicelayerTransactionalTest
{
	private static final String RUSTIC_CUSTOMER_USER = "mark.rivers@rustic-hw.com";
	private static final String RUSTIC_ADMIN_USER = "linda.wolf@rustic-hw.com";
	private static final String PRONTO_ADMIN_USER = "linda.wolf@pronto-hw.com";

	private static final String RUSTIC_BUDGET = "Rustic_Monthly_50K_USD";
	private static final String PRONTO_BUDGET = "Pronto_Monthly_50K_USD";

	@Resource
	private B2BBudgetService<B2BBudgetModel, B2BCustomerModel> b2BBudgetService;

	@Resource
	private I18NService i18NService;

	@Resource
	private CommonI18NService commonI18NService;

	@Resource
	private B2BCommerceCostCenterService b2bCommerceCostCenterService;

	private User rusticAdminUser;
	private User rusticCustomerUser;
	private User prontoAdminUser;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		createDefaultUsers();
		CatalogManager.getInstance().createEssentialData(Collections.emptyMap(), null);

		i18NService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));

		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");
		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");

		rusticAdminUser = UserManager.getInstance().getUserByLogin(RUSTIC_ADMIN_USER);
		rusticCustomerUser = UserManager.getInstance().getUserByLogin(RUSTIC_CUSTOMER_USER);
		prontoAdminUser = UserManager.getInstance().getUserByLogin(PRONTO_ADMIN_USER);
	}

	// getBudget
	@Test
	public void shouldGetBudgetAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BBudgetModel budgetModel = b2BBudgetService.getB2BBudgetForCode("Rustic_Monthly_2_5K_USD");
		assertThat(budgetModel).isNotNull().hasFieldOrPropertyWithValue("name", "Rustic Monthly 2.5K USD");
	}

	@Test
	public void shouldNotGetBudgetAsCustomerFromDifferentUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BBudgetModel budgetModel = b2BBudgetService.getB2BBudgetForCode(PRONTO_BUDGET);
		assertThat(budgetModel).isNull();
	}

	@Test
	public void shouldNotGetBudgetAsCustomerFromB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BBudgetModel budgetModel = b2BBudgetService.getB2BBudgetForCode(RUSTIC_BUDGET);
		assertThat(budgetModel).isNull();
	}

	@Test
	public void shouldGetBudgetAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BBudgetModel budgetModel = b2BBudgetService.getB2BBudgetForCode(RUSTIC_BUDGET);
		assertThat(budgetModel).isNotNull().hasFieldOrPropertyWithValue("name", "Rustic Monthly 50K USD");
	}

	@Test
	public void shouldNotGetBudgetAsB2BAdminFromDifferentUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BBudgetModel budgetModel = b2BBudgetService.getB2BBudgetForCode(PRONTO_BUDGET);
		assertThat(budgetModel).isNull();
	}

	// getUnits
	@Test
	public void shouldGetBudgetsAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final Set<B2BBudgetModel> budgets = b2BBudgetService.getB2BBudgets();
		assertThat(budgets).isNotEmpty().hasSize(1).extracting("code").contains("Rustic_Monthly_2_5K_USD");
	}

	@Test
	public void shouldGetBudgetsAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final Set<B2BBudgetModel> budgets = b2BBudgetService.getB2BBudgets();
		assertThat(budgets).isNotEmpty().hasSize(2).extracting("code").contains("Rustic_Monthly_50K_USD", "Rustic_Monthly_2_5K_USD");
	}

	// getCurrentBudgets
	@Test
	public void shouldGetCurrentBudgetsAsCustomerFromDifferentCostCenter()
	{
		JaloSession.getCurrentSession().setUser(prontoAdminUser);
		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode("CostCenterA_Pronto");
		assertThat(costCenter).isNotNull();

		JaloSession.getCurrentSession().setUser(rusticCustomerUser);
		final Collection<B2BBudgetModel> budgets = b2BBudgetService.getCurrentBudgets(costCenter);
		assertThat(budgets).isEmpty();
	}

	@Test
	public void shouldGetCurrentBudgetsAsB2BAdminFromDifferentCostCenter()
	{
		JaloSession.getCurrentSession().setUser(prontoAdminUser);
		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode("CostCenterA_Pronto");
		assertThat(costCenter).isNotNull();

		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		final Collection<B2BBudgetModel> budgets = b2BBudgetService.getCurrentBudgets(costCenter);
		assertThat(budgets).isEmpty();
	}

	// findPagedBudgets
	@Test
	public void shouldGetPagedBudgetsAsCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final SearchPageData<B2BBudgetModel> budgets = b2BBudgetService.findPagedBudgets(createPageableData());
		assertThat(budgets).isNotNull();
		assertThat(budgets.getResults()).isNotEmpty()
										.hasSize(1)
										.extracting("code")
										.contains("Rustic_Monthly_2_5K_USD");
	}

	@Test
	public void shouldGetPagedBudgetsAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);
		final SearchPageData<B2BBudgetModel> budgets = b2BBudgetService.findPagedBudgets(createPageableData());
		assertThat(budgets).isNotNull();
		assertThat(budgets.getResults()).isNotEmpty()
										.hasSize(2)
										.extracting("code")
										.contains("Rustic_Monthly_2_5K_USD", "Rustic_Monthly_50K_USD");
	}
}
