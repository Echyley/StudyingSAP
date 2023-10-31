/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.b2b.company;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.b2b.model.B2BCostCenterModel;
import de.hybris.platform.b2b.util.B2BCommerceTestUtils;
import de.hybris.platform.catalog.jalo.CatalogManager;
import de.hybris.platform.commerceservices.search.pagedata.PageableData;
import de.hybris.platform.commerceservices.search.pagedata.SearchPageData;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.User;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.servicelayer.ServicelayerTest;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.i18n.I18NService;

import java.util.Collections;
import java.util.Locale;

import javax.annotation.Resource;

import org.junit.Before;
import org.junit.Test;


@IntegrationTest
public class B2BCommerceCostCenterServiceIntegrationTest extends ServicelayerTest
{
	private static final String RUSTIC_ADMIN_USER = "linda.wolf@rustic-hw.com";
	private static final String RUSTIC_CUSTOMER_USER = "mark.rivers@rustic-hw.com";

	@Resource
	private B2BCommerceCostCenterService b2bCommerceCostCenterService;

	@Resource
	private I18NService i18NService;

	@Resource
	private CommonI18NService commonI18NService;

	private User rusticAdminUser;
	private User rusticCustomerUser;

	@Before
	public void setup() throws Exception
	{
		createCoreData();
		createDefaultCatalog();
		CatalogManager.getInstance().createEssentialData(Collections.emptyMap(), null);

		importCsv("/impex/essentialdata_1_usergroups.impex", "UTF-8");
		importCsv("/impex/essentialdata_2_b2bcommerce.impex", "UTF-8");

		importCsv("/b2bcommerce/test/usergroups.impex", "UTF-8");

		i18NService.setCurrentLocale(Locale.ENGLISH);
		commonI18NService.setCurrentLanguage(commonI18NService.getLanguage("en"));
		commonI18NService.setCurrentCurrency(commonI18NService.getCurrency("USD"));

		// this to avoid search restrictions being applied to Users based on the current logged in user.
		rusticAdminUser = UserManager.getInstance().getUserByLogin(RUSTIC_ADMIN_USER);
		rusticCustomerUser = UserManager.getInstance().getUserByLogin(RUSTIC_CUSTOMER_USER);
	}

	@Test
	public void shouldGetCostCenterForCodeAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode("CostCenterA_Rustic");
		assertThat(costCenter).isNotNull();
		assertThat(costCenter.getName(Locale.ENGLISH)).isEqualTo("CostCenterA Rustic");
		assertThat(costCenter.getCurrency()).isNotNull();
		assertThat(costCenter.getCurrency().getIsocode()).isEqualTo("USD");
		assertThat(costCenter.getUnit()).isNotNull();
		assertThat(costCenter.getUnit().getUid()).isEqualTo("Rustic");
	}

	@Test
	public void shouldNotGetCostCenterForCodeAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode("CostCenterA_Rustic");
		assertThat(costCenter).isNull();
	}

	@Test
	public void shouldNotGetCostCenterNotExistIDAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode("notExist");
		assertThat(costCenter).isNull();
	}

	@Test
	public void shouldNotGetCostCenterNotExistIDAsB2BAdminFromDifferentUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode("CostCenterA_Pronto");
		assertThat(costCenter).isNull();
	}

	@Test
	public void shouldNotGetCostCenterNotExistIDAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode("notExist");
		assertThat(costCenter).isNull();
	}

	@Test
	public void shouldNotGetCostCenterNotExistIDAsB2BCustomerFromDifferentUnit()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final B2BCostCenterModel costCenter = b2bCommerceCostCenterService.getCostCenterForCode("CostCenterA_Pronto");
		assertThat(costCenter).isNull();
	}

	@Test
	public void shouldNotGetCostCenterNullID()
	{
		assertThatThrownBy(() -> b2bCommerceCostCenterService.getCostCenterForCode(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Value is required, null given for key: code");
	}

	@Test
	public void shouldGetPagedCostCentersAsB2BAdmin()
	{
		JaloSession.getCurrentSession().setUser(rusticAdminUser);

		final PageableData pageableData = B2BCommerceTestUtils.createPageableData(0, 10, "byName");
		final SearchPageData<B2BCostCenterModel> b2BCostCenters = b2bCommerceCostCenterService.getPagedCostCenters(pageableData);
		assertThat(b2BCostCenters).isNotNull();
		assertThat(b2BCostCenters.getResults()).hasSize(2)
				.extracting("code")
				.contains("CostCenterA_Rustic", "CostCenterB_Rustic");
	}

	@Test
	public void shouldNotGetPagedCostCentersAsB2BCustomer()
	{
		JaloSession.getCurrentSession().setUser(rusticCustomerUser);

		final PageableData pageableData = B2BCommerceTestUtils.createPageableData(0, 10, "byName");
		final SearchPageData<B2BCostCenterModel> b2BCostCenters = b2bCommerceCostCenterService.getPagedCostCenters(pageableData);
		assertThat(b2BCostCenters).isNotNull();
		assertThat(b2BCostCenters.getResults()).isNullOrEmpty();
	}

	@Test
	public void shouldNotGetPagedCostCentersNullAsPageableData()
	{
		assertThatThrownBy(() -> b2bCommerceCostCenterService.getPagedCostCenters(null))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("PageableData can not be null!");
	}
}
